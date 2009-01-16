/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.domain.generated.NcboOntologyVersionMetadata;
import org.ncbo.stanford.domain.generated.NcboOntologyVersionMetadataDAO;

/**
 * @author mdorf
 * 
 */
public class CustomNcboOntologyVersionMetadataDAO extends
		NcboOntologyVersionMetadataDAO {

	private static final Log log = LogFactory
			.getLog(CustomNcboOntologyLoadQueueDAO.class);

	/**
	 * 
	 */
	public CustomNcboOntologyVersionMetadataDAO() {
		super();
	}

	/**
	 * @param transientInstance
	 * @return
	 */
	public NcboOntologyVersionMetadata saveNcboOntologyMetadata(
			NcboOntologyVersionMetadata transientInstance) {
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
