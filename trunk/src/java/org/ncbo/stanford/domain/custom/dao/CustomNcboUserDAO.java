/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Expression;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;

/**
 * @author mdorf
 * 
 */
public class CustomNcboUserDAO extends NcboUserDAO {

	private static final Log log = LogFactory.getLog(CustomNcboUserDAO.class);

	/**
	 * 
	 */
	public CustomNcboUserDAO() {
		super();
	}

	/**
	 * @param transientInstance
	 * @return
	 */
	public NcboUser saveUser(NcboUser transientInstance) {
		try {
			Integer newId = (Integer) getHibernateTemplate().save(
					transientInstance);
			getHibernateTemplate().flush();

			return this.findById(newId);
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public NcboUser getUserByUsername(String username) {
		return (NcboUser) getSession().createCriteria(
				"org.ncbo.stanford.domain.generated.NcboUser").add(
				Expression.eq("username", username)).uniqueResult();
	}
}
