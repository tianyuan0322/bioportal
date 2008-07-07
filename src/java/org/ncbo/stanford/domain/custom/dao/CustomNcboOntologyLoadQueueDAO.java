/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueueDAO;
import org.ncbo.stanford.enumeration.StatusEnum;

/**
 * @author Michael Dorf
 * 
 */
public class CustomNcboOntologyLoadQueueDAO extends NcboOntologyLoadQueueDAO {

	private static final Log log = LogFactory
			.getLog(CustomNcboOntologyLoadQueueDAO.class);

	/**
	 * 
	 */
	public CustomNcboOntologyLoadQueueDAO() {
		super();
	}

	/**
	 * Finds a record in the queue corresponding to a given ontology version
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public NcboOntologyLoadQueue findByOntologyVersionId(
			Integer ontologyVersionId) {
		return (NcboOntologyLoadQueue) getSession()
				.createCriteria(
						"org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue")
				.add(Expression.eq("ncboOntologyVersion.id", ontologyVersionId))
				.uniqueResult();
	}

	/**
	 * 
	 * Retuns a list of ontologies to be loaded and parsed
	 * 
	 * @return List of NcboOntologyLoadQueue
	 */
	@SuppressWarnings("unchecked")
	public List<NcboOntologyLoadQueue> getOntologiesToLoad() {
		return getSession().createCriteria(
				"org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue")
				.add(
						Expression.eq("ncboLStatus.id",
								StatusEnum.STATUS_WAITING.getStatus()))
				.addOrder(Order.asc("id")).list();
	}

	/**
	 * @param transientInstance
	 * @return
	 */
	public NcboOntologyLoadQueue saveNcboOntologyLoadQueue(
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
