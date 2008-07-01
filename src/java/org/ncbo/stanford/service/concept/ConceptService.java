/**
 * 
 */
package org.ncbo.stanford.service.concept;

import java.util.List;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;

/**
 * @author nickgriffith
 *
 */
public interface ConceptService {
	
	
		
	
	public ClassBean findRoot(Integer ontologyId) throws Exception;
	
	
	public ClassBean findConcept(Integer ontologyId, String conceptId) throws Exception;
	
	public ClassBean findPathToRoot(Integer ontologyId, String conceptId,boolean light) throws Exception;
	
	//public List<ClassBean> findParent(int ontologyId, String id) throws Exception;
	
	//public List<ClassBean> findChildren(int ontologyId, String id) throws Exception;
	
	public List<SearchResultBean> findConceptNameExact(List<Integer> ontologyIds, String query);
	
	public List<SearchResultBean> findConceptNameStartsWith(List<Integer> ontologyIds, String query);
	
	public List<SearchResultBean> findConceptNameContains(List<Integer> ontologyIds, String query);
	
	public List<SearchResultBean> findConceptPropertyExact(List<Integer> ontologyIds, String property, String query);
	
	public List<SearchResultBean> findConceptPropertyStartsWith(List<Integer> ontologyIds, String property, String query);
	
	public List<SearchResultBean> findConceptPropertyContains(List<Integer> ontologyIds, String query);
}
