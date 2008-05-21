/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadataDAO;

/**
 * @author mdorf
 * 
 */
public class CustomNcboOntologyMetadataDAO extends NcboOntologyMetadataDAO {

	private static final Log log = LogFactory
			.getLog(CustomNcboOntologyLoadQueueDAO.class);

	/**
	 * 
	 */
	public CustomNcboOntologyMetadataDAO() {
		super();
	}

	/**
	 * @param transientInstance
	 * @return
	 */
	public NcboOntologyMetadata saveNcboOntologyMetadata(
			NcboOntologyLoadQueue transientInstance) {
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
