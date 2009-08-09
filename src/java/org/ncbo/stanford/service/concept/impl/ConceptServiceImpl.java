/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyIdBean;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.manager.obs.OBSManager;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.constants.ApplicationConstants;
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
	private static final Integer MAX_CHILD_COUNT = 100;

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	private Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap = new HashMap<String, OntologyRetrievalManager>(
			0);
	private OBSManager obsManager;

	/**
	 * Get the root concept for the specified ontology.
	 */
	public ClassBean findRootConcept(Integer ontologyVersionId)
			throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);

		if (ontology == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersionId + ")");
		}

		return getRetrievalManager(ontology).findRootConcept(ontology);
	}

	public ClassBean findConcept(Integer ontologyVersionId, String conceptId)
			throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);

		if (ontology == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersionId + ")");
		}

		return getRetrievalManager(ontology).findConcept(ontology, conceptId);
	}

	@SuppressWarnings("unchecked")
	public ClassBean findPathFromRoot(Integer ontologyVersionId,
			String conceptId, boolean light) throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);

		if (ontology == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersionId + ")");
		}

		ClassBean path = getRetrievalManager(ontology).findPathFromRoot(
				ontology, conceptId, light);

		// temporary fix to remove long list of siblings
		if (!light) {
			removeExtraSiblings((ArrayList<ClassBean>) path
					.getRelation((Object) ApplicationConstants.SUB_CLASS),
					conceptId, 0);
		}

		return path;
	}

	@SuppressWarnings("unchecked")
	private void removeExtraSiblings(ArrayList<ClassBean> subClasses, String conceptId,
			Integer parentChildCount) {
		for (ClassBean subClass : subClasses) {
			ArrayList<ClassBean> sub = (ArrayList<ClassBean>) subClass
					.getRelation((Object) ApplicationConstants.SUB_CLASS);

			subClass.removeRelation((Object) "CHD");

			if (sub != null) {
				Integer childCount = (Integer) subClass
						.getRelation((Object) ApplicationConstants.CHILD_COUNT);
				removeExtraSiblings(sub, conceptId, childCount);
			}
		}

		if (parentChildCount > MAX_CHILD_COUNT) {
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
		String ontologyVersionId = obsManager
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
		String ontologyVersionId = obsManager
				.findLatestOntologyVersion(ontologyId.getOntologyId());

		return obsManager.findChildren(ontologyVersionId, conceptId, level,
				offset, limit);
	}

	public List<ClassBean> findRootPaths(
			OntologyVersionIdBean ontologyVersionId, String conceptId,
			Integer offset, Integer limit) throws Exception {
		return obsManager.findRootPaths(ontologyVersionId
				.getOntologyVersionId(), conceptId, offset, limit);
	}

	public List<ClassBean> findRootPaths(OntologyIdBean ontologyId,
			String conceptId, Integer offset, Integer limit) throws Exception {
		String ontologyVersionId = obsManager
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
		String ontologyVersionId = obsManager
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
		String ontologyVersionId = obsManager
				.findLatestOntologyVersion(ontologyId.getOntologyId());

		return obsManager.findLeaves(ontologyVersionId, conceptId, offset,
				limit);
	}

	//
	// Non interface methods
	//

	@SuppressWarnings("unused")
	private String findLatestActiveOntologyVersionId(OntologyIdBean ontologyId)
			throws OntologyNotFoundException {
		String ontologyVersionId = null;

		// if UMLS, just pass through the id, else, find the latest version
		if (ontologyId.isUmls()) {
			ontologyVersionId = ontologyId.getOntologyId();
		} else {
			VNcboOntology ontology = ncboOntologyVersionDAO
					.findLatestActiveOntologyVersion(Integer
							.parseInt(ontologyId.getOntologyId()));

			if (ontology == null) {
				throw new OntologyNotFoundException();
			}

			ontologyVersionId = ontology.getId().toString();
		}

		return ontologyVersionId;
	}

	private OntologyRetrievalManager getRetrievalManager(VNcboOntology ontology) {
		String formatHandler = ontologyFormatHandlerMap.get(ontology
				.getFormat());
		return ontologyRetrievalHandlerMap.get(formatHandler);
	}

	/**
	 * @param ncboOntologyVersionDAO
	 *            the ncboOntologyVersionDAO to set
	 */
	public void setNcboOntologyVersionDAO(
			CustomNcboOntologyVersionDAO ncboOntologyVersionDAO) {
		this.ncboOntologyVersionDAO = ncboOntologyVersionDAO;
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
}
