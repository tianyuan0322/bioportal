/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.domain.generated.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboOntologyDAO;

/**
 * @author Michael Dorf
 * 
 */
public class CustomNcboOntologyDAO extends NcboOntologyDAO {

	private static final Log log = LogFactory
			.getLog(CustomNcboOntologyDAO.class);

	/**
	 * 
	 */
	public CustomNcboOntologyDAO() {
		super();
	}

	/**
	 * @param transientInstance
	 * @return
	 */
	public NcboOntology saveOntology(NcboOntology transientInstance) {
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
