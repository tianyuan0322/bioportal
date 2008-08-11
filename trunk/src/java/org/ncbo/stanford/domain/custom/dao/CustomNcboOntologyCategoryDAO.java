/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.domain.generated.NcboOntologyCategory;
import org.ncbo.stanford.domain.generated.NcboOntologyCategoryDAO;

/**
 * @author mdorf
 * 
 */
public class CustomNcboOntologyCategoryDAO extends NcboOntologyCategoryDAO {

	private static final Log log = LogFactory
			.getLog(CustomNcboOntologyCategoryDAO.class);

	/**
	 * 
	 */
	public CustomNcboOntologyCategoryDAO() {
		super();
	}

	/**
	 * @param transientInstance
	 * @return
	 */
	public NcboOntologyCategory saveOntologyCategory(
			NcboOntologyCategory transientInstance) {
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
}
