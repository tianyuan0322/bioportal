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
 * NcboLAdditionalMetadata entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboLAdditionalMetadata
 * @author MyEclipse Persistence Tools
 */

public class NcboLAdditionalMetadataDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory
			.getLog(NcboLAdditionalMetadataDAO.class);
	// property constants
	public static final String NAME = "name";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboLAdditionalMetadata transientInstance) {
		log.debug("saving NcboLAdditionalMetadata instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboLAdditionalMetadata persistentInstance) {
		log.debug("deleting NcboLAdditionalMetadata instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboLAdditionalMetadata findById(java.lang.Integer id) {
		log.debug("getting NcboLAdditionalMetadata instance with id: " + id);
		try {
			NcboLAdditionalMetadata instance = (NcboLAdditionalMetadata) getHibernateTemplate()
					.get(
							"org.ncbo.stanford.domain.generated.NcboLAdditionalMetadata",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboLAdditionalMetadata instance) {
		log.debug("finding NcboLAdditionalMetadata instance by example");
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
		log.debug("finding NcboLAdditionalMetadata instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboLAdditionalMetadata as model where model."
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
		log.debug("finding all NcboLAdditionalMetadata instances");
		try {
			String queryString = "from NcboLAdditionalMetadata";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboLAdditionalMetadata merge(
			NcboLAdditionalMetadata detachedInstance) {
		log.debug("merging NcboLAdditionalMetadata instance");
		try {
			NcboLAdditionalMetadata result = (NcboLAdditionalMetadata) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboLAdditionalMetadata instance) {
		log.debug("attaching dirty NcboLAdditionalMetadata instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboLAdditionalMetadata instance) {
		log.debug("attaching clean NcboLAdditionalMetadata instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboLAdditionalMetadataDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboLAdditionalMetadataDAO) ctx
				.getBean("NcboLAdditionalMetadataDAO");
	}
}