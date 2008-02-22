/**
 * 
 */
package org.ncbo.stanford.service.concept;

import java.util.ArrayList;

import org.ncbo.stanford.bean.concept.AbstractConceptBean;
import org.ncbo.stanford.bean.concept.ClassBean;

/**
 * @author nickgriffith
 *
 */
public interface ConceptService {
	
	
	
	
	public AbstractConceptBean findConcept();
	
	
	
	
	
	
	public ClassBean findConcept(String id, int ontologyId);
	
	public ArrayList<ClassBean> findPathToRoot(String id, int ontologyId);
	
	public ClassBean findParent(String id, int ontologyId);
	
	public ArrayList<ClassBean> findChildren(String id, int ontologyId);
	
	public ArrayList<ClassBean> findConceptNameExact(String query, ArrayList<Integer> ontologyIds);
	
	public ArrayList<ClassBean> findConceptNameStartsWith(String query, ArrayList<Integer> ontologyIds);
	
	public ArrayList<ClassBean> findConceptNameContains(String query, ArrayList<Integer> ontologyIds);
	
	public ArrayList<ClassBean> findConceptPropertyExact(String property, String query, ArrayList<Integer> ontologyIds);
	
	public ArrayList<ClassBean> findConceptPropertyStartsWith(String property, String query, ArrayList<Integer> ontologyIds);
	
	public ArrayList<ClassBean> findConceptPropertyContains(String property, String query, ArrayList<Integer> ontologyIds);
}
