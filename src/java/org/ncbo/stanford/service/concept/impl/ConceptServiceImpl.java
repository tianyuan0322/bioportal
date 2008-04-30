/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
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
	

	
		
	
	public ClassBean findRoot(int ontologyId) throws Exception {
		return ontologyRetrievalManager.findRootConcept(ontologyId);
	}
	
	
	
	public ClassBean findConcept(int ontologyId, String id ) throws Exception {
		return ontologyRetrievalManager.findConcept(ontologyId, id);
	}

	public List<ClassBean> findPathToRoot(int ontologyId, String id) throws Exception {
		return ontologyRetrievalManager.findPathToRoot(ontologyId, id);
	}

	/*
	public List<ClassBean> findParent(int ontologyId, String id) throws Exception {
		return ontologyRetrievalManager.findParent(ontologyId, id);
	}

	public List<ClassBean> findChildren(int ontologyId, String id ) throws Exception {
		return ontologyRetrievalManager.findChildren(ontologyId, id);
	}*/

	public List<ClassBean> findConceptNameExact(List<Integer> ontologyIds, String query
	) {
		return ontologyRetrievalManager.findConceptNameExact(ontologyIds, query );
	}

	public List<ClassBean> findConceptNameStartsWith(List<Integer> ontologyIds, String query
	) {
		return ontologyRetrievalManager.findConceptNameStartsWith(ontologyIds, query);
	}

	public List<ClassBean> findConceptNameContains(List<Integer> ontologyIds, String query
			) {
		return ontologyRetrievalManager.findConceptNameContains(ontologyIds, query );
	}

	public List<ClassBean> findConceptPropertyExact(List<Integer> ontologyIds, String property,
			String query) {
		return ontologyRetrievalManager.findConceptPropertyExact(ontologyIds, property, query );
	}

	public List<ClassBean> findConceptPropertyStartsWith(List<Integer> ontologyIds, String property,
			String query) {
		return ontologyRetrievalManager.findConceptPropertyStartsWith(ontologyIds, property, query);
	}

	public List<ClassBean> findConceptPropertyContains(List<Integer> ontologyIds, String property,
			String query) {
		return ontologyRetrievalManager.findConceptPropertyContains(ontologyIds, property, query);
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
