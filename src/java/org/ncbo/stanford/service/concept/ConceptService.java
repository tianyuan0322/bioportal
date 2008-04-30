/**
 * 
 */
package org.ncbo.stanford.service.concept;

import java.util.List;

import org.ncbo.stanford.bean.concept.ClassBean;

/**
 * @author nickgriffith
 *
 */
public interface ConceptService {
	
	
		
	
	public ClassBean findRoot(int ontologyId) throws Exception;
	
	
	public ClassBean findConcept(int ontologyId, String id) throws Exception;
	
	public List<ClassBean> findPathToRoot(int ontologyId, String id) throws Exception;
	
	//public List<ClassBean> findParent(int ontologyId, String id) throws Exception;
	
	//public List<ClassBean> findChildren(int ontologyId, String id) throws Exception;
	
	public List<ClassBean> findConceptNameExact(List<Integer> ontologyIds, String query);
	
	public List<ClassBean> findConceptNameStartsWith(List<Integer> ontologyIds, String query);
	
	public List<ClassBean> findConceptNameContains(List<Integer> ontologyIds, String query);
	
	public List<ClassBean> findConceptPropertyExact(List<Integer> ontologyIds, String property, String query);
	
	public List<ClassBean> findConceptPropertyStartsWith(List<Integer> ontologyIds, String property, String query);
	
	public List<ClassBean> findConceptPropertyContains(List<Integer> ontologyIds, String property, String query);
}
