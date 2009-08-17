package org.ncbo.stanford.manager.metadata;

import java.util.List;

import org.ncbo.stanford.bean.CategoryBean;

/**
 * An interface for all API specific user metadata managers to conform to. This allows
 * for a seamless Spring injection.
 * 
 * @author Csongor Nyulas
 * 
 */
public interface OntologyCategoryMetadataManager {

	/**
	 * Saves the user information specified by the categoryBean.
	 * 
	 * @param categoryBean
	 * @throws Exception
	 */
	public void saveOntologyCategory(CategoryBean categoryBean) throws Exception;

	
	/**
	 * Retrieves the categoryBean representing an ontology category for a specific category id.
	 * 
	 * @param categoryId
	 */
	public CategoryBean findCategoryById(Integer categoryId);
	
	/**
	 * Returns the list of categoryBeans, one for each category in the metadata.
	 *  
	 * @return the list of category beans
	 * @throws Exception 
	 */
	public List<CategoryBean> findAllCategories();
}
