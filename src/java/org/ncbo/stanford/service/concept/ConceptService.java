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
	
	public ConceptBean findConcept(String id, int ontologyId);
	
	public ArrayList<ConceptBean> findPathToRoot(String id, int ontologyId);
	
	public ConceptBean findParent(String id, int ontologyId);
	
	public ArrayList<ConceptBean> findChildren(String id, int ontologyId);
	
	public ArrayList<ConceptBean> findConceptNameExact(String query, ArrayList<Integer> ontologyIds);
	
	public ArrayList<ConceptBean> findConceptNameStartsWith(String query, ArrayList<Integer> ontologyIds);
	
	public ArrayList<ConceptBean> findConceptNameContains(String query, ArrayList<Integer> ontologyIds);
	
	public ArrayList<ConceptBean> findConceptPropertyExact(String property, String query, ArrayList<Integer> ontologyIds);
	
	public ArrayList<ConceptBean> findConceptPropertyStartsWith(String property, String query, ArrayList<Integer> ontologyIds);
	
	public ArrayList<ConceptBean> findConceptPropertyContains(String property, String query, ArrayList<Integer> ontologyIds);
}
