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
 * NcboLNotificationType entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboLNotificationType
 * @author MyEclipse Persistence Tools
 */

public class NcboLNotificationTypeDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory
			.getLog(NcboLNotificationTypeDAO.class);
	// property constants
	public static final String TYPE = "type";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboLNotificationType transientInstance) {
		log.debug("saving NcboLNotificationType instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboLNotificationType persistentInstance) {
		log.debug("deleting NcboLNotificationType instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboLNotificationType findById(java.lang.Integer id) {
		log.debug("getting NcboLNotificationType instance with id: " + id);
		try {
			NcboLNotificationType instance = (NcboLNotificationType) getHibernateTemplate()
					.get(
							"org.ncbo.stanford.domain.generated.NcboLNotificationType",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboLNotificationType instance) {
		log.debug("finding NcboLNotificationType instance by example");
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
		log.debug("finding NcboLNotificationType instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboLNotificationType as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByType(Object type) {
		return findByProperty(TYPE, type);
	}

	public List findAll() {
		log.debug("finding all NcboLNotificationType instances");
		try {
			String queryString = "from NcboLNotificationType";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboLNotificationType merge(NcboLNotificationType detachedInstance) {
		log.debug("merging NcboLNotificationType instance");
		try {
			NcboLNotificationType result = (NcboLNotificationType) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboLNotificationType instance) {
		log.debug("attaching dirty NcboLNotificationType instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboLNotificationType instance) {
		log.debug("attaching clean NcboLNotificationType instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboLNotificationTypeDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboLNotificationTypeDAO) ctx
				.getBean("NcboLNotificationTypeDAO");
	}
}