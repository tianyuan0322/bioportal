package org.ncbo.stanford.domain.generated;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboUserRole entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboUserRole
 * @author MyEclipse Persistence Tools
 */

public class NcboUserRoleDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(NcboUserRoleDAO.class);

	// property constants

	protected void initDao() {
		// do nothing
	}

	public void save(NcboUserRole transientInstance) {
		log.debug("saving NcboUserRole instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboUserRole persistentInstance) {
		log.debug("deleting NcboUserRole instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboUserRole findById(java.lang.Integer id) {
		log.debug("getting NcboUserRole instance with id: " + id);
		try {
			NcboUserRole instance = (NcboUserRole) getHibernateTemplate().get(
					"org.ncbo.stanford.domain.generated.NcboUserRole", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboUserRole instance) {
		log.debug("finding NcboUserRole instance by example");
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
		log.debug("finding NcboUserRole instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboUserRole as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all NcboUserRole instances");
		try {
			String queryString = "from NcboUserRole";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboUserRole merge(NcboUserRole detachedInstance) {
		log.debug("merging NcboUserRole instance");
		try {
			NcboUserRole result = (NcboUserRole) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboUserRole instance) {
		log.debug("attaching dirty NcboUserRole instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboUserRole instance) {
		log.debug("attaching clean NcboUserRole instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboUserRoleDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboUserRoleDAO) ctx.getBean("NcboUserRoleDAO");
	}
}