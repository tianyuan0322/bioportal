/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
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

	private OntologyRetrievalManager getRetrievalManager(VNcboOntology ontology) {
		String formatHandler = ontologyFormatHandlerMap.get(ontology
				.getFormat());
		return ontologyRetrievalHandlerMap.get(formatHandler);
	}

	//
	// Non interface methods
	//

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
}
