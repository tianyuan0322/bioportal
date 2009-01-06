/**
 * 
 */
package org.ncbo.stanford.service.concept;

import org.ncbo.stanford.bean.concept.ClassBean;

/**
 * @author nickgriffith
 * 
 */
public interface ConceptService {

	public ClassBean findRootConcept(Integer ontologyVersionId)
			throws Exception;

	public ClassBean findConcept(Integer ontologyVersionId, String conceptId)
			throws Exception;

	public ClassBean findPathFromRoot(Integer ontologyVersionId,
			String conceptId, boolean light) throws Exception;

/*	public List<SearchResultBean> findConceptNameExact(
			List<Integer> ontologyVersionIds, String query);

	public List<SearchResultBean> findConceptNameStartsWith(
			List<Integer> ontologyVersionIds, String query);

	public List<SearchResultBean> findConceptNameContains(
			List<Integer> ontologyVersionIds, String query);

	public List<SearchResultBean> findConceptPropertyExact(
			List<Integer> ontologyVersionIds, String property, String query);

	public List<SearchResultBean> findConceptPropertyStartsWith(
			List<Integer> ontologyVersionIds, String property, String query);

	public List<SearchResultBean> findConceptPropertyContains(
			List<Integer> ontologyVersionIds, String query);
*/
}
