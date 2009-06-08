/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyIdBean;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.manager.metadata.OntologyViewMetadataManager;
import org.ncbo.stanford.manager.obs.OBSManager;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.service.concept.ConceptService;
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

	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	private Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap = new HashMap<String, OntologyRetrievalManager>(
			0);
	private OBSManager obsManager;	
	private OntologyMetadataManager ontologyMetadataManagerProtege;
	

	/**
	 * Get the root concept for the specified ontology.
	 */
	public ClassBean findRootConcept(Integer ontologyVersionId)
			throws Exception {
		OntologyBean ontology = ontologyMetadataManagerProtege.findOntologyOrOntologyViewById(ontologyVersionId);

		return getRetrievalManager(ontology).findRootConcept(ontology);
	}

	public ClassBean findConcept(Integer ontologyVersionId, String conceptId)
			throws Exception {
		OntologyBean ontology = ontologyMetadataManagerProtege.findOntologyOrOntologyViewById(ontologyVersionId);

		return getRetrievalManager(ontology).findConcept(ontology, conceptId);
	}
	
	public ClassBean findPathFromRoot(Integer ontologyVersionId,
			String conceptId, boolean light) throws Exception {
		OntologyBean ontology = ontologyMetadataManagerProtege.findOntologyOrOntologyViewById(ontologyVersionId);

		return getRetrievalManager(ontology).findPathFromRoot(ontology,
				conceptId, light);
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

	private OntologyRetrievalManager getRetrievalManager(OntologyBean ontology) {
		String formatHandler = ontologyFormatHandlerMap.get(ontology
				.getFormat());
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
	 * @param ontologyMetadataManagerProtege the ontologyMetadataManagerProtege to set
	 */
	public void setOntologyMetadataManagerProtege(
			OntologyMetadataManager ontologyMetadataManagerProtege) {
		this.ontologyMetadataManagerProtege = ontologyMetadataManagerProtege;
	}
}
