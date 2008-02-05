package org.ncbo.stanford.domain.generated;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboOntologyLoadQueue entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue
 * @author MyEclipse Persistence Tools
 */

public class NcboOntologyLoadQueueDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory
			.getLog(NcboOntologyLoadQueueDAO.class);
	// property constants
	public static final String ERROR_MESSAGE = "errorMessage";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboOntologyLoadQueue transientInstance) {
		log.debug("saving NcboOntologyLoadQueue instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboOntologyLoadQueue persistentInstance) {
		log.debug("deleting NcboOntologyLoadQueue instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboOntologyLoadQueue findById(java.lang.Integer id) {
		log.debug("getting NcboOntologyLoadQueue instance with id: " + id);
		try {
			NcboOntologyLoadQueue instance = (NcboOntologyLoadQueue) getHibernateTemplate()
					.get(
							"org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboOntologyLoadQueue instance) {
		log.debug("finding NcboOntologyLoadQueue instance by example");
		try {
			List results = getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding NcboOntologyLoadQueue instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboOntologyLoadQueue as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByErrorMessage(Object errorMessage) {
		return findByProperty(ERROR_MESSAGE, errorMessage);
	}

	public List findAll() {
		log.debug("finding all NcboOntologyLoadQueue instances");
		try {
			String queryString = "from NcboOntologyLoadQueue";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboOntologyLoadQueue merge(NcboOntologyLoadQueue detachedInstance) {
		log.debug("merging NcboOntologyLoadQueue instance");
		try {
			NcboOntologyLoadQueue result = (NcboOntologyLoadQueue) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboOntologyLoadQueue instance) {
		log.debug("attaching dirty NcboOntologyLoadQueue instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboOntologyLoadQueue instance) {
		log.debug("attaching clean NcboOntologyLoadQueue instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboOntologyLoadQueueDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboOntologyLoadQueueDAO) ctx
				.getBean("NcboOntologyLoadQueueDAO");
	}
}