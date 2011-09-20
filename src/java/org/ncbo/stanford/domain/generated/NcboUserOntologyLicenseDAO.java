package org.ncbo.stanford.domain.generated;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboUserOntologyLicense entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboUserOntologyLicense
 * @author MyEclipse Persistence Tools
 */

public class NcboUserOntologyLicenseDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory
			.getLog(NcboUserOntologyLicenseDAO.class);
	// property constants
	public static final String ONTOLOGY_ID = "ontologyId";
	public static final String LICENSE_TEXT = "licenseText";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboUserOntologyLicense transientInstance) {
		log.debug("saving NcboUserOntologyLicense instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboUserOntologyLicense persistentInstance) {
		log.debug("deleting NcboUserOntologyLicense instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboUserOntologyLicense findById(java.lang.Integer id) {
		log.debug("getting NcboUserOntologyLicense instance with id: " + id);
		try {
			NcboUserOntologyLicense instance = (NcboUserOntologyLicense) getHibernateTemplate()
					.get(
							"org.ncbo.stanford.domain.generated.NcboUserOntologyLicense",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboUserOntologyLicense instance) {
		log.debug("finding NcboUserOntologyLicense instance by example");
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
		log.debug("finding NcboUserOntologyLicense instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboUserOntologyLicense as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByOntologyId(Object ontologyId) {
		return findByProperty(ONTOLOGY_ID, ontologyId);
	}

	public List findByLicenseText(Object licenseText) {
		return findByProperty(LICENSE_TEXT, licenseText);
	}

	public List findAll() {
		log.debug("finding all NcboUserOntologyLicense instances");
		try {
			String queryString = "from NcboUserOntologyLicense";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboUserOntologyLicense merge(
			NcboUserOntologyLicense detachedInstance) {
		log.debug("merging NcboUserOntologyLicense instance");
		try {
			NcboUserOntologyLicense result = (NcboUserOntologyLicense) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboUserOntologyLicense instance) {
		log.debug("attaching dirty NcboUserOntologyLicense instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboUserOntologyLicense instance) {
		log.debug("attaching clean NcboUserOntologyLicense instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboUserOntologyLicenseDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboUserOntologyLicenseDAO) ctx
				.getBean("NcboUserOntologyLicenseDAO");
	}
}