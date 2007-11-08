/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import org.hibernate.criterion.Expression;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;

/**
 * @author mdorf
 * 
 */
public class CustomNcboUserDAO extends NcboUserDAO {

	/**
	 * 
	 */
	public CustomNcboUserDAO() {
		super();
	}

	public NcboUser getUserByUsername(String username) {
		return (NcboUser) getSession().createCriteria(
				"org.ncbo.stanford.domain.generated.NcboUser").add(
				Expression.eq("username", username)).uniqueResult();
	}
}
