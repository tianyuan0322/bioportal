/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ConceptBean;
import org.ncbo.stanford.manager.OntologyRetrievalManager;
import org.ncbo.stanford.service.concept.ConceptService;

/**
 * @author Nick Griffith
 * 
 * 
 */
public class ConceptServiceImpl implements ConceptService {
	
	private static final Log log = LogFactory.getLog(ConceptServiceImpl.class);
	
	private OntologyRetrievalManager ontologyRetrievalManager;
	
	
	public ConceptBean findConcept(String id, int ontologyId) {
		return ontologyRetrievalManager.findConcept(id, ontologyId);
	}

	public ArrayList<ConceptBean> findPathToRoot(String id, int ontologyId) {
		return ontologyRetrievalManager.findPathToRoot(id, ontologyId);
	}

	public ConceptBean findParent(String id, int ontologyId) {
		return ontologyRetrievalManager.findParent(id, ontologyId);
	}

	public ArrayList<ConceptBean> findChildren(String id, int ontologyId) {
		return ontologyRetrievalManager.findChildren(id, ontologyId);
	}

	public ArrayList<ConceptBean> findConceptNameExact(String query,
			ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptNameExact(query, ontologyIds);
	}

	public ArrayList<ConceptBean> findConceptNameStartsWith(String query,
			ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptNameStartsWith(query, ontologyIds);
	}

	public ArrayList<ConceptBean> findConceptNameContains(String query,
			ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptNameContains(query, ontologyIds);
	}

	public ArrayList<ConceptBean> findConceptPropertyExact(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptPropertyExact(property, query, ontologyIds);
	}

	public ArrayList<ConceptBean> findConceptPropertyStartsWith(
			String property, String query, ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptPropertyStartsWith(property, query, ontologyIds);
	}

	public ArrayList<ConceptBean> findConceptPropertyContains(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptPropertyContains(property, query, ontologyIds);
	}

	/**
	 * @return the ontologyRetrievalManager
	 */
	public OntologyRetrievalManager getOntologyRetrievalManager() {
		return ontologyRetrievalManager;
	}

	/**
	 * @param ontologyRetrievalManager the ontologyRetrievalManager to set
	 */
	public void setOntologyRetrievalManager(
			OntologyRetrievalManager ontologyRetrievalManager) {
		this.ontologyRetrievalManager = ontologyRetrievalManager;
	}
}
