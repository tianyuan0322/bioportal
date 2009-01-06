package org.ncbo.stanford.domain.generated;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboAdminApplication entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboAdminApplication
 * @author MyEclipse Persistence Tools
 */

public class NcboAdminApplicationDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory
			.getLog(NcboAdminApplicationDAO.class);
	// property constants
	public static final String APPLICATION_ID = "applicationId";
	public static final String APPLICATION_NAME = "applicationName";
	public static final String APPLICATION_DESCRIPTION = "applicationDescription";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboAdminApplication transientInstance) {
		log.debug("saving NcboAdminApplication instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboAdminApplication persistentInstance) {
		log.debug("deleting NcboAdminApplication instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboAdminApplication findById(java.lang.Integer id) {
		log.debug("getting NcboAdminApplication instance with id: " + id);
		try {
			NcboAdminApplication instance = (NcboAdminApplication) getHibernateTemplate()
					.get(
							"org.ncbo.stanford.domain.generated.NcboAdminApplication",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboAdminApplication instance) {
		log.debug("finding NcboAdminApplication instance by example");
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
		log.debug("finding NcboAdminApplication instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboAdminApplication as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByApplicationId(Object applicationId) {
		return findByProperty(APPLICATION_ID, applicationId);
	}

	public List findByApplicationName(Object applicationName) {
		return findByProperty(APPLICATION_NAME, applicationName);
	}

	public List findByApplicationDescription(Object applicationDescription) {
		return findByProperty(APPLICATION_DESCRIPTION, applicationDescription);
	}

	public List findAll() {
		log.debug("finding all NcboAdminApplication instances");
		try {
			String queryString = "from NcboAdminApplication";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboAdminApplication merge(NcboAdminApplication detachedInstance) {
		log.debug("merging NcboAdminApplication instance");
		try {
			NcboAdminApplication result = (NcboAdminApplication) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboAdminApplication instance) {
		log.debug("attaching dirty NcboAdminApplication instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboAdminApplication instance) {
		log.debug("attaching clean NcboAdminApplication instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboAdminApplicationDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboAdminApplicationDAO) ctx.getBean("NcboAdminApplicationDAO");
	}
}