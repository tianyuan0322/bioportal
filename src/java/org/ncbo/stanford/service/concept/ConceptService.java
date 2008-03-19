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
	
	
		
	
	public ClassBean findRoot(int ontologyId) throws Exception;
	
	
	public ClassBean findConcept(int ontologyId, String id) throws Exception;
	
	public ArrayList<ClassBean> findPathToRoot(int ontologyId, String id) throws Exception;
	
	public ArrayList<ClassBean> findParent(int ontologyId, String id) throws Exception;
	
	public ArrayList<ClassBean> findChildren(int ontologyId, String id) throws Exception;
	
	public ArrayList<ClassBean> findConceptNameExact(ArrayList<Integer> ontologyIds, String query);
	
	public ArrayList<ClassBean> findConceptNameStartsWith(ArrayList<Integer> ontologyIds, String query);
	
	public ArrayList<ClassBean> findConceptNameContains(ArrayList<Integer> ontologyIds, String query);
	
	public ArrayList<ClassBean> findConceptPropertyExact(ArrayList<Integer> ontologyIds, String property, String query);
	
	public ArrayList<ClassBean> findConceptPropertyStartsWith(ArrayList<Integer> ontologyIds, String property, String query);
	
	public ArrayList<ClassBean> findConceptPropertyContains(ArrayList<Integer> ontologyIds, String property, String query);
}
