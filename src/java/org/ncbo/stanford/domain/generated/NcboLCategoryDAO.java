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
 * NcboLCategory entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboLCategory
 * @author MyEclipse Persistence Tools
 */

public class NcboLCategoryDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(NcboLCategoryDAO.class);
	// property constants
	public static final String NAME = "name";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboLCategory transientInstance) {
		log.debug("saving NcboLCategory instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboLCategory persistentInstance) {
		log.debug("deleting NcboLCategory instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboLCategory findById(java.lang.Integer id) {
		log.debug("getting NcboLCategory instance with id: " + id);
		try {
			NcboLCategory instance = (NcboLCategory) getHibernateTemplate()
					.get("org.ncbo.stanford.domain.generated.NcboLCategory", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboLCategory instance) {
		log.debug("finding NcboLCategory instance by example");
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
		log.debug("finding NcboLCategory instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboLCategory as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByName(Object name) {
		return findByProperty(NAME, name);
	}

	public List findAll() {
		log.debug("finding all NcboLCategory instances");
		try {
			String queryString = "from NcboLCategory";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboLCategory merge(NcboLCategory detachedInstance) {
		log.debug("merging NcboLCategory instance");
		try {
			NcboLCategory result = (NcboLCategory) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboLCategory instance) {
		log.debug("attaching dirty NcboLCategory instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboLCategory instance) {
		log.debug("attaching clean NcboLCategory instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboLCategoryDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboLCategoryDAO) ctx.getBean("NcboLCategoryDAO");
	}
}