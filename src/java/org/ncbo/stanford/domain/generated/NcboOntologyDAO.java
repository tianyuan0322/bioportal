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
 * NcboOntology entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboOntology
 * @author MyEclipse Persistence Tools
 */

public class NcboOntologyDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(NcboOntologyDAO.class);
	// property constants
	public static final String OBO_FOUNDRY_ID = "oboFoundryId";
	public static final String IS_MANUAL = "isManual";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboOntology transientInstance) {
		log.debug("saving NcboOntology instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboOntology persistentInstance) {
		log.debug("deleting NcboOntology instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboOntology findById(java.lang.Integer id) {
		log.debug("getting NcboOntology instance with id: " + id);
		try {
			NcboOntology instance = (NcboOntology) getHibernateTemplate().get(
					"org.ncbo.stanford.domain.generated.NcboOntology", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboOntology instance) {
		log.debug("finding NcboOntology instance by example");
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
		log.debug("finding NcboOntology instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboOntology as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByOboFoundryId(Object oboFoundryId) {
		return findByProperty(OBO_FOUNDRY_ID, oboFoundryId);
	}

	public List findByIsManual(Object isManual) {
		return findByProperty(IS_MANUAL, isManual);
	}

	public List findAll() {
		log.debug("finding all NcboOntology instances");
		try {
			String queryString = "from NcboOntology";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboOntology merge(NcboOntology detachedInstance) {
		log.debug("merging NcboOntology instance");
		try {
			NcboOntology result = (NcboOntology) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboOntology instance) {
		log.debug("attaching dirty NcboOntology instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboOntology instance) {
		log.debug("attaching clean NcboOntology instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboOntologyDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboOntologyDAO) ctx.getBean("NcboOntologyDAO");
	}
}