package org.ncbo.stanford.domain.generated;

import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboLUsageEventType entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboLUsageEventType
 * @author MyEclipse Persistence Tools
 */

public class NcboLUsageEventTypeDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory
			.getLog(NcboLUsageEventTypeDAO.class);
	// property constants
	public static final String EVENT_NAME = "eventName";
	public static final String EVENT_DESCRIPTION = "eventDescription";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboLUsageEventType transientInstance) {
		log.debug("saving NcboLUsageEventType instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboLUsageEventType persistentInstance) {
		log.debug("deleting NcboLUsageEventType instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboLUsageEventType findById(java.lang.Integer id) {
		log.debug("getting NcboLUsageEventType instance with id: " + id);
		try {
			NcboLUsageEventType instance = (NcboLUsageEventType) getHibernateTemplate()
					.get(
							"org.ncbo.stanford.domain.generated.NcboLUsageEventType",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboLUsageEventType instance) {
		log.debug("finding NcboLUsageEventType instance by example");
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
		log.debug("finding NcboLUsageEventType instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboLUsageEventType as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByEventName(Object eventName) {
		return findByProperty(EVENT_NAME, eventName);
	}

	public List findByEventDescription(Object eventDescription) {
		return findByProperty(EVENT_DESCRIPTION, eventDescription);
	}

	public List findAll() {
		log.debug("finding all NcboLUsageEventType instances");
		try {
			String queryString = "from NcboLUsageEventType";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboLUsageEventType merge(NcboLUsageEventType detachedInstance) {
		log.debug("merging NcboLUsageEventType instance");
		try {
			NcboLUsageEventType result = (NcboLUsageEventType) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboLUsageEventType instance) {
		log.debug("attaching dirty NcboLUsageEventType instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboLUsageEventType instance) {
		log.debug("attaching clean NcboLUsageEventType instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboLUsageEventTypeDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboLUsageEventTypeDAO) ctx.getBean("NcboLUsageEventTypeDAO");
	}
}