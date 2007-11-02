package org.ncbo.stanford.domain.generated;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboOntologyCategory entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboOntologyCategory
 * @author MyEclipse Persistence Tools
 */

public class NcboOntologyCategoryDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory
			.getLog(NcboOntologyCategoryDAO.class);

	// property constants

	protected void initDao() {
		// do nothing
	}

	public void save(NcboOntologyCategory transientInstance) {
		log.debug("saving NcboOntologyCategory instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboOntologyCategory persistentInstance) {
		log.debug("deleting NcboOntologyCategory instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboOntologyCategory findById(java.lang.Integer id) {
		log.debug("getting NcboOntologyCategory instance with id: " + id);
		try {
			NcboOntologyCategory instance = (NcboOntologyCategory) getHibernateTemplate()
					.get(
							"org.ncbo.stanford.domain.generated.NcboOntologyCategory",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboOntologyCategory instance) {
		log.debug("finding NcboOntologyCategory instance by example");
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
		log.debug("finding NcboOntologyCategory instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboOntologyCategory as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all NcboOntologyCategory instances");
		try {
			String queryString = "from NcboOntologyCategory";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboOntologyCategory merge(NcboOntologyCategory detachedInstance) {
		log.debug("merging NcboOntologyCategory instance");
		try {
			NcboOntologyCategory result = (NcboOntologyCategory) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboOntologyCategory instance) {
		log.debug("attaching dirty NcboOntologyCategory instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboOntologyCategory instance) {
		log.debug("attaching clean NcboOntologyCategory instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboOntologyCategoryDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboOntologyCategoryDAO) ctx.getBean("NcboOntologyCategoryDAO");
	}
}