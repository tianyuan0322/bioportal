/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.AbstractConceptBean;
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
	

	
	
	public AbstractConceptBean findConcept() {
		
		ClassBean cb = new ClassBean();
//		cb.setId(5);
//		cb.setLabel("SAMPLECONCEPT");
//		
//		PropertyBean pb = new PropertyBean();
//		pb.setId(2);
//		pb.setLabel("Definition");
//		
//		cb.addRelation(pb, "this is a concept A");
		
		
		
		
		
		
		
		return cb;
	}
	
	public ClassBean findRoot(int ontologyId){
		return ontologyRetrievalManager.findRootConcept(ontologyId);
	}
	
	
	
	public ClassBean findConcept(String id, int ontologyId) {
		return ontologyRetrievalManager.findConcept(id, ontologyId);
	}

	public ArrayList<ClassBean> findPathToRoot(String id, int ontologyId) {
		return ontologyRetrievalManager.findPathToRoot(id, ontologyId);
	}

	public ClassBean findParent(String id, int ontologyId) {
		return ontologyRetrievalManager.findParent(id, ontologyId);
	}

	public ArrayList<ClassBean> findChildren(String id, int ontologyId) {
		return ontologyRetrievalManager.findChildren(id, ontologyId);
	}

	public ArrayList<ClassBean> findConceptNameExact(String query,
			ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptNameExact(query, ontologyIds);
	}

	public ArrayList<ClassBean> findConceptNameStartsWith(String query,
			ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptNameStartsWith(query, ontologyIds);
	}

	public ArrayList<ClassBean> findConceptNameContains(String query,
			ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptNameContains(query, ontologyIds);
	}

	public ArrayList<ClassBean> findConceptPropertyExact(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptPropertyExact(property, query, ontologyIds);
	}

	public ArrayList<ClassBean> findConceptPropertyStartsWith(
			String property, String query, ArrayList<Integer> ontologyIds) {
		return ontologyRetrievalManager.findConceptPropertyStartsWith(property, query, ontologyIds);
	}

	public ArrayList<ClassBean> findConceptPropertyContains(String property,
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
