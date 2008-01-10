/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ConceptBean;
import org.ncbo.stanford.manager.OntologyAndConceptManager;
import org.ncbo.stanford.service.concept.ConceptService;

/**
 * @author Nick Griffith
 * 
 * 
 */
public class ConceptServiceImpl implements ConceptService {
	private static final Log log = LogFactory.getLog(ConceptServiceImpl.class);
	
	private OntologyAndConceptManager ontConceptMgr = null;
	
	public ConceptBean findConcept(String id, int ontologyId) {
		return ontConceptMgr.findConcept(id, ontologyId);
	}

	public ArrayList<ConceptBean> findPathToRoot(String id, int ontologyId) {
		return ontConceptMgr.findPathToRoot(id, ontologyId);
	}

	public ConceptBean findParent(String id, int ontologyId) {
		return ontConceptMgr.findParent(id, ontologyId);
	}

	public ArrayList<ConceptBean> findChildren(String id, int ontologyId) {
		return ontConceptMgr.findChildren(id, ontologyId);
	}

	public ArrayList<ConceptBean> findConceptNameExact(String query,
			ArrayList<Integer> ontologyIds) {
		return ontConceptMgr.findConceptNameExact(query, ontologyIds);
	}

	public ArrayList<ConceptBean> findConceptNameStartsWith(String query,
			ArrayList<Integer> ontologyIds) {
		return ontConceptMgr.findConceptNameStartsWith(query, ontologyIds);
	}

	public ArrayList<ConceptBean> findConceptNameContains(String query,
			ArrayList<Integer> ontologyIds) {
		return ontConceptMgr.findConceptNameContains(query, ontologyIds);
	}

	public ArrayList<ConceptBean> findConceptPropertyExact(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return ontConceptMgr.findConceptPropertyExact(property, query, ontologyIds);
	}

	public ArrayList<ConceptBean> findConceptPropertyStartsWith(
			String property, String query, ArrayList<Integer> ontologyIds) {
		return ontConceptMgr.findConceptPropertyStartsWith(property, query, ontologyIds);
	}

	public ArrayList<ConceptBean> findConceptPropertyContains(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return ontConceptMgr.findConceptPropertyContains(property, query, ontologyIds);
	}

	/**
	 * @return the ontologyAndConceptManager
	 */
	public OntologyAndConceptManager getOntologyAndConceptManager() {
		return ontConceptMgr;
	}

	/**
	 * @param ontologyAndConceptManager
	 *            the ontologyAndConceptManager to set
	 */
	public void setOntologyAndConceptManager(
			OntologyAndConceptManager ontologyAndConceptManager) {
		this.ontConceptMgr = ontologyAndConceptManager;
	}
}
