/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyIdBean;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.exception.OntologyDeprecatedException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.manager.obs.OBSManager;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Nick Griffith
 * @author Michael Dorf
 * 
 */
@Transactional(readOnly = true)
public class ConceptServiceImpl implements ConceptService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ConceptServiceImpl.class);

	private static final String DUMMY_CONCEPT_ID = "0";
	private static final String DUMMY_CONCEPT_LABEL = "*** Too many children...";

	protected Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	protected Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap = new HashMap<String, OntologyRetrievalManager>(
			0);
	protected OBSManager obsManager;
	protected OntologyMetadataManager ontologyMetadataManager;

	/**
	 * Get the root concept for the specified ontology.
	 */
	@SuppressWarnings("unchecked")
	public ClassBean findRootConcept(Integer ontologyVersionId,
			Integer maxNumChildren, boolean light) throws Exception {
		OntologyBean ontology = ontologyMetadataManager
				.findOntologyOrViewVersionById(ontologyVersionId);

		if (ontology == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersionId + ")");
		} else if (ontology.getStatusId().equals(StatusEnum.STATUS_DEPRECATED.getStatus())) {
			throw new OntologyDeprecatedException(
					OntologyDeprecatedException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersionId + ")");			
		}

		ClassBean concept = getRetrievalManager(ontology).findRootConcept(
				ontology, light);

		// temporary fix to remove long list of siblings
		if (concept != null && maxNumChildren != null) {
			removeExtraSiblingsOneIteration(
					(ArrayList<ClassBean>) concept
							.getRelation((Object) ApplicationConstants.SUB_CLASS),
					null,
					(Integer) concept
							.getRelation((Object) ApplicationConstants.CHILD_COUNT),
					maxNumChildren);
		}

		return concept;
	}

	@SuppressWarnings("unchecked")
	public ClassBean findConcept(Integer ontologyVersionId, String conceptId,
			Integer maxNumChildren, boolean light, boolean noRelations,
			boolean isIncludeInstances) throws Exception {
		OntologyBean ontology = ontologyMetadataManager
				.findOntologyOrViewVersionById(ontologyVersionId);

		if (ontology == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersionId + ")");
		} else if (ontology.getStatusId().equals(StatusEnum.STATUS_DEPRECATED.getStatus())) {
			throw new OntologyDeprecatedException(
					OntologyDeprecatedException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersionId + ")");			
		}

		ClassBean concept = getRetrievalManager(ontology).findConcept(ontology,
				conceptId, light, noRelations, isIncludeInstances);

		// temporary fix to remove long list of siblings
		if (concept != null && maxNumChildren != null) {
			removeExtraSiblingsOneIteration(
					(ArrayList<ClassBean>) concept
							.getRelation((Object) ApplicationConstants.SUB_CLASS),
					conceptId,
					(Integer) concept
							.getRelation((Object) ApplicationConstants.CHILD_COUNT),
					maxNumChildren);
		}

		return concept;
	}

	public InstanceBean findInstanceById(Integer ontologyVerId,
			String instanceId) throws Exception {
		OntologyBean ontology = ontologyMetadataManager
				.findOntologyOrViewVersionById(ontologyVerId);

		if (ontology == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVerId + ")");
		}
		return getRetrievalManager(ontology).findInstanceById(ontology,
				instanceId);
	}

	@SuppressWarnings("unchecked")
	public ClassBean findPathFromRoot(Integer ontologyVersionId,
			String conceptId, boolean light, Integer maxNumChildren)
			throws Exception {
		OntologyBean ontology = ontologyMetadataManager
				.findOntologyOrViewVersionById(ontologyVersionId);

		if (ontology == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersionId + ")");
		}

		ClassBean path = getRetrievalManager(ontology).findPathFromRoot(
				ontology, conceptId, light);

		// temporary fix to remove long list of siblings
		if (path != null && maxNumChildren != null && !light) {
			// long start = System.currentTimeMillis();
			// System.out.println("Start: " + start);
			removeExtraSiblingsOneIteration(
					(ArrayList<ClassBean>) path
							.getRelation((Object) ApplicationConstants.SUB_CLASS),
					conceptId,
					(Integer) path
							.getRelation((Object) ApplicationConstants.CHILD_COUNT),
					maxNumChildren);
			// long stop = System.currentTimeMillis(); // stop timing
			// System.out.println("Stop: " + stop);
			// System.out.println("Finished removing siblings in "
			// + (stop - start) / 1000F + " miliseconds.\n");
		}

		return path;
	}

	@SuppressWarnings("unchecked")
	private void removeExtraSiblingsOneIteration(
			ArrayList<ClassBean> subClasses, String conceptId,
			Integer parentChildCount, Integer maxNumChildren) {
		if (subClasses == null) {
			return;
		}

		ListIterator<ClassBean> listIterator = subClasses.listIterator();
		boolean removed = false;

		while (listIterator.hasNext()) {
			ClassBean sibling = listIterator.next();
			ArrayList<ClassBean> sub = (ArrayList<ClassBean>) sibling
					.getRelation((Object) ApplicationConstants.SUB_CLASS);

			// removing this relation, as it duplicates the SUB_CLASS relation
			sibling.removeRelation((Object) "CHD");

			if (sub != null) {
				Integer childCount = (Integer) sibling
						.getRelation((Object) ApplicationConstants.CHILD_COUNT);
				removeExtraSiblingsOneIteration(sub, conceptId, childCount,
						maxNumChildren);
			}

			if (parentChildCount > maxNumChildren && sub == null
					&& !sibling.getId().equalsIgnoreCase(conceptId)) {
				listIterator.remove();
				removed = true;
			}

			if (!listIterator.hasNext() && removed) {
				ClassBean dummyClass = new ClassBean();
				dummyClass.setId(DUMMY_CONCEPT_ID);
				dummyClass.setLabel(DUMMY_CONCEPT_LABEL);
				listIterator.add(dummyClass);
			}
		}
	}

	@SuppressWarnings( { "unchecked", "unused" })
	private void removeExtraSiblingsTwoIterations(
			ArrayList<ClassBean> subClasses, String conceptId,
			Integer parentChildCount, Integer maxNumChildren) {
		if (subClasses == null) {
			return;
		}

		for (ClassBean subClass : subClasses) {
			ArrayList<ClassBean> sub = (ArrayList<ClassBean>) subClass
					.getRelation((Object) ApplicationConstants.SUB_CLASS);
			// removing this relation, as it duplicates the SUB_CLASS relation
			subClass.removeRelation((Object) "CHD");

			if (sub != null) {
				Integer childCount = (Integer) subClass
						.getRelation((Object) ApplicationConstants.CHILD_COUNT);
				removeExtraSiblingsTwoIterations(sub, conceptId, childCount,
						maxNumChildren);
			}
		}

		if (parentChildCount > maxNumChildren) {
			ListIterator<ClassBean> listIterator = subClasses.listIterator();

			while (listIterator.hasNext()) {
				ClassBean next = listIterator.next();
				ArrayList<ClassBean> sub = (ArrayList<ClassBean>) next
						.getRelation((Object) ApplicationConstants.SUB_CLASS);

				if (!next.getId().equalsIgnoreCase(conceptId) && sub == null) {
					listIterator.remove();
				}
			}

			ClassBean dummyClass = new ClassBean();
			dummyClass.setId(DUMMY_CONCEPT_ID);
			dummyClass.setLabel(DUMMY_CONCEPT_LABEL);
			subClasses.add(dummyClass);
		}
	}

	public List<ClassBean> findParents(OntologyVersionIdBean ontologyVersionId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception {
		return obsManager.findParents(ontologyVersionId.getOntologyVersionId(),
				conceptId, level, offset, limit);
	}

	public List<ClassBean> findParents(OntologyIdBean ontologyId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception {
		Integer ontologyVersionId = obsManager
				.findLatestOntologyVersion(ontologyId.getOntologyId());

		return obsManager.findParents(ontologyVersionId, conceptId, level,
				offset, limit);
	}

	public List<ClassBean> findChildren(
			OntologyVersionIdBean ontologyVersionId, String conceptId,
			Integer level, Integer offset, Integer limit) throws Exception {
		return obsManager.findChildren(
				ontologyVersionId.getOntologyVersionId(), conceptId, level,
				offset, limit);
	}

	public List<ClassBean> findChildren(OntologyIdBean ontologyId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception {
		Integer ontologyVersionId = obsManager
				.findLatestOntologyVersion(ontologyId.getOntologyId());

		return obsManager.findChildren(ontologyVersionId, conceptId, level,
				offset, limit);
	}

	public Set<String> findChildrenConceptIds(OntologyIdBean ontologyId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception {
		Integer ontologyVersionId = obsManager
				.findLatestOntologyVersion(ontologyId.getOntologyId());

		return obsManager.findChildrenConceptIds(ontologyVersionId, conceptId,
				level, offset, limit);
	}

	public List<ClassBean> findRootPaths(
			OntologyVersionIdBean ontologyVersionId, String conceptId,
			Integer offset, Integer limit) throws Exception {
		return obsManager.findRootPaths(ontologyVersionId
				.getOntologyVersionId(), conceptId, offset, limit);
	}

	public List<ClassBean> findRootPaths(OntologyIdBean ontologyId,
			String conceptId, Integer offset, Integer limit) throws Exception {
		Integer ontologyVersionId = obsManager
				.findLatestOntologyVersion(ontologyId.getOntologyId());

		return obsManager.findRootPaths(ontologyVersionId, conceptId, offset,
				limit);
	}

	public List<ClassBean> findSiblings(
			OntologyVersionIdBean ontologyVersionId, String conceptId,
			Integer level, Integer offset) throws Exception {
		return obsManager.findSiblings(
				ontologyVersionId.getOntologyVersionId(), conceptId, level,
				offset);
	}

	public List<ClassBean> findSiblings(OntologyIdBean ontologyId,
			String conceptId, Integer level, Integer offset) throws Exception {
		Integer ontologyVersionId = obsManager
				.findLatestOntologyVersion(ontologyId.getOntologyId());

		return obsManager.findSiblings(ontologyVersionId, conceptId, level,
				offset);
	}

	public List<ClassBean> findLeaves(OntologyVersionIdBean ontologyVersionId,
			String conceptId, Integer offset, Integer limit) throws Exception {
		return obsManager.findLeaves(ontologyVersionId.getOntologyVersionId(),
				conceptId, offset, limit);
	}

	public List<ClassBean> findLeaves(OntologyIdBean ontologyId,
			String conceptId, Integer offset, Integer limit) throws Exception {
		Integer ontologyVersionId = obsManager
				.findLatestOntologyVersion(ontologyId.getOntologyId());

		return obsManager.findLeaves(ontologyVersionId, conceptId, offset,
				limit);
	}

	public Page<ClassBean> findAllConcepts(Integer ontologyVersionId,
			Integer pageSize, Integer pageNum) throws Exception {
		// get ontologyBean from versionId
		OntologyBean ontology = ontologyMetadataManager
				.findOntologyOrViewVersionById(ontologyVersionId);

		if (ontology == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersionId + ")");
		}

		return getRetrievalManager(ontology).findAllConcepts(ontology,
				pageSize, pageNum);
	}

	//
	// Non interface methods
	//

	protected OntologyRetrievalManager getRetrievalManager(OntologyBean ontology) {
		String formatHandler = ontologyFormatHandlerMap.get(ontology
				.getFormat().toUpperCase());
		return ontologyRetrievalHandlerMap.get(formatHandler);
	}

	/**
	 * @param ontologyRetrievalHandlerMap
	 *            the ontologyRetrievalHandlerMap to set
	 */
	public void setOntologyRetrievalHandlerMap(
			Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap) {
		this.ontologyRetrievalHandlerMap = ontologyRetrievalHandlerMap;
	}

	/**
	 * @param ontologyFormatHandlerMap
	 *            the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}

	/**
	 * @param obsManager
	 *            the obsManager to set
	 */
	public void setObsManager(OBSManager obsManager) {
		this.obsManager = obsManager;
	}

	/**
	 * @param ontologyMetadataManager
	 *            the ontologyMetadataManager to set
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}
}
