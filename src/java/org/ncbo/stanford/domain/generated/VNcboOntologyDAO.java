package org.ncbo.stanford.domain.generated;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * VNcboOntology entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.VNcboOntology
 * @author MyEclipse Persistence Tools
 */

public class VNcboOntologyDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(VNcboOntologyDAO.class);

	// property constants

	protected void initDao() {
		// do nothing
	}

	public void save(VNcboOntology transientInstance) {
		log.debug("saving VNcboOntology instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(VNcboOntology persistentInstance) {
		log.debug("deleting VNcboOntology instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public VNcboOntology findById(
			org.ncbo.stanford.domain.generated.VNcboOntologyId id) {
		log.debug("getting VNcboOntology instance with id: " + id);
		try {
			VNcboOntology instance = (VNcboOntology) getHibernateTemplate()
					.get("org.ncbo.stanford.domain.generated.VNcboOntology", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(VNcboOntology instance) {
		log.debug("finding VNcboOntology instance by example");
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
		log.debug("finding VNcboOntology instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from VNcboOntology as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all VNcboOntology instances");
		try {
			String queryString = "from VNcboOntology";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public VNcboOntology merge(VNcboOntology detachedInstance) {
		log.debug("merging VNcboOntology instance");
		try {
			VNcboOntology result = (VNcboOntology) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(VNcboOntology instance) {
		log.debug("attaching dirty VNcboOntology instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(VNcboOntology instance) {
		log.debug("attaching clean VNcboOntology instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static VNcboOntologyDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (VNcboOntologyDAO) ctx.getBean("VNcboOntologyDAO");
	}
}