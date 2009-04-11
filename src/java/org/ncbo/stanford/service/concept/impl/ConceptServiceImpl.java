/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyIdBean;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
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

		return getRetrievalManager(ontology).findRootConcept(ontology);
	}

	public ClassBean findConcept(Integer ontologyVersionId, String conceptId)
			throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);

		return getRetrievalManager(ontology).findConcept(ontology, conceptId);
	}

	public ClassBean findPathFromRoot(Integer ontologyVersionId,
			String conceptId, boolean light) throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);

		return getRetrievalManager(ontology).findPathFromRoot(ontology,
				conceptId, light);
	}

	public List<ClassBean> findParents(OntologyVersionIdBean ontologyVersionId,
			String conceptId) throws Exception {
		return obsManager.findParents(ontologyVersionId.getOntologyVersionId(),
				conceptId);
	}

	public List<ClassBean> findChildren(
			OntologyVersionIdBean ontologyVersionId, String conceptId)
			throws Exception {
		return obsManager.findChildren(
				ontologyVersionId.getOntologyVersionId(), conceptId);
	}

	public List<ClassBean> findParents(OntologyIdBean ontologyId,
			String conceptId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ClassBean> findRootPaths(
			OntologyVersionIdBean ontologyVersionId, String conceptId)
			throws Exception {
		return obsManager.findRootPaths(ontologyVersionId
				.getOntologyVersionId(), conceptId);
	}

	public List<ClassBean> findSiblings(OntologyVersionIdBean ontologyVersionId,
			String conceptId, String level) throws Exception {
		return obsManager.findSiblings(ontologyVersionId.getOntologyVersionId(),
				conceptId, level);
	}

	public List<ClassBean> findLeaves(
			OntologyVersionIdBean ontologyVersionId, String conceptId)
			throws Exception {
		return obsManager.findLeaves(ontologyVersionId
				.getOntologyVersionId(), conceptId);
	}

	//
	// Non interface methods
	//

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
