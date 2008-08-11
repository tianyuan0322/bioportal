/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import java.util.List;

import org.hibernate.criterion.Expression;
import org.ncbo.stanford.domain.generated.NcboLCategory;
import org.ncbo.stanford.domain.generated.NcboLCategoryDAO;

/**
 * @author Michael Dorf
 * 
 */
public class CustomNcboLCategoryDAO extends NcboLCategoryDAO {

	/**
	 * 
	 */
	public CustomNcboLCategoryDAO() {
		super();
	}

	/**
	 * Returns a list of categories that correspond to the given obo foundry
	 * names
	 * 
	 * @param oboFoundryNames
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NcboLCategory> findCategoriesByOBOFoundryNames(
			String[] oboFoundryNames) {
		return getSession().createCriteria(NcboLCategory.class).add(
				Expression.in("oboFoundryName", oboFoundryNames)).list();
	}
}
