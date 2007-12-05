/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ConceptBean;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.ontology.impl.OntologyServiceImpl;

/**
 * @author Nick Griffith
 *
 */
public class ConceptServiceImpl implements ConceptService{
	
	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);
	
	
    public ConceptBean findConcept(String id, int ontologyId){
    	return new ConceptBean();
    }
    
	public ArrayList<ConceptBean> findPathToRoot(String id, int ontologyId){
		return new ArrayList();
	}
	
	public ConceptBean findParent(String id, int ontologyId){
		return new ConceptBean();
	}
	
	public ArrayList<ConceptBean> findChildren(String id, int ontologyId){
		return new ArrayList();
	}
	
	public ArrayList<ConceptBean> findConceptNameExact(String query, ArrayList<Integer> ontologyIds){
		return new ArrayList();
	}
	
	public ArrayList<ConceptBean> findConceptNameStartsWith(String query, ArrayList<Integer> ontologyIds){
		return new ArrayList();
	}
	
	public ArrayList<ConceptBean> findConceptNameContains(String query, ArrayList<Integer> ontologyIds){
		return new ArrayList();
	}
	
	public ArrayList<ConceptBean> findConceptPropertyExact(String property, String query, ArrayList<Integer> ontologyIds){
		return new ArrayList();
	}
	
	public ArrayList<ConceptBean> findConceptPropertyStartsWith(String property, String query, ArrayList<Integer> ontologyIds){
		return new ArrayList();
	}
	
	public ArrayList<ConceptBean> findConceptPropertyContains(String property, String query, ArrayList<Integer> ontologyIds){
		return new ArrayList();
	}
	
}
	


