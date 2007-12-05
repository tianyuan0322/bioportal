/**
 * 
 */
package org.ncbo.stanford.service.concept;

import java.util.ArrayList;

import org.ncbo.stanford.bean.ConceptBean;

/**
 * @author nickgriffith
 *
 */
public interface ConceptService {
	
	ConceptBean findConcept(String id, int ontologyId);
	
	ArrayList<ConceptBean> findPathToRoot(String id, int ontologyId);
	
	ConceptBean findParent(String id, int ontologyId);
	
	ArrayList<ConceptBean> findChildren(String id, int ontologyId);
	
	ArrayList<ConceptBean> findConceptNameExact(String query, ArrayList<Integer> ontologyIds);
	
	ArrayList<ConceptBean> findConceptNameStartsWith(String query, ArrayList<Integer> ontologyIds);
	
	ArrayList<ConceptBean> findConceptNameContains(String query, ArrayList<Integer> ontologyIds);
	
	ArrayList<ConceptBean> findConceptPropertyExact(String property, String query, ArrayList<Integer> ontologyIds);
	
	ArrayList<ConceptBean> findConceptPropertyStartsWith(String property, String query, ArrayList<Integer> ontologyIds);
	
	ArrayList<ConceptBean> findConceptPropertyContains(String property, String query, ArrayList<Integer> ontologyIds);
}
