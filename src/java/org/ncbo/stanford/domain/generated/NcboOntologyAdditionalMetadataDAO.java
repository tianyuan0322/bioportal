package org.ncbo.stanford.domain.generated;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboOntologyAdditionalMetadata entities. Transaction control of the save(),
 * update() and delete() operations can directly support Spring
 * container-managed transactions or they can be augmented to handle
 * user-managed Spring transactions. Each of these methods provides additional
 * information for how to configure it for the desired type of transaction
 * control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboOntologyAdditionalMetadata
 * @author MyEclipse Persistence Tools
 */

public class NcboOntologyAdditionalMetadataDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory
			.getLog(NcboOntologyAdditionalMetadataDAO.class);
	// property constants
	public static final String ADDITIONAL_METADATA_VALUE = "additionalMetadataValue";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboOntologyAdditionalMetadata transientInstance) {
		log.debug("saving NcboOntologyAdditionalMetadata instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboOntologyAdditionalMetadata persistentInstance) {
		log.debug("deleting NcboOntologyAdditionalMetadata instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboOntologyAdditionalMetadata findById(java.lang.Integer id) {
		log.debug("getting NcboOntologyAdditionalMetadata instance with id: "
				+ id);
		try {
			NcboOntologyAdditionalMetadata instance = (NcboOntologyAdditionalMetadata) getHibernateTemplate()
					.get(
							"org.ncbo.stanford.domain.generated.NcboOntologyAdditionalMetadata",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboOntologyAdditionalMetadata instance) {
		log.debug("finding NcboOntologyAdditionalMetadata instance by example");
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
		log
				.debug("finding NcboOntologyAdditionalMetadata instance with property: "
						+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboOntologyAdditionalMetadata as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByAdditionalMetadataValue(Object additionalMetadataValue) {
		return findByProperty(ADDITIONAL_METADATA_VALUE,
				additionalMetadataValue);
	}

	public List findAll() {
		log.debug("finding all NcboOntologyAdditionalMetadata instances");
		try {
			String queryString = "from NcboOntologyAdditionalMetadata";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboOntologyAdditionalMetadata merge(
			NcboOntologyAdditionalMetadata detachedInstance) {
		log.debug("merging NcboOntologyAdditionalMetadata instance");
		try {
			NcboOntologyAdditionalMetadata result = (NcboOntologyAdditionalMetadata) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboOntologyAdditionalMetadata instance) {
		log.debug("attaching dirty NcboOntologyAdditionalMetadata instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboOntologyAdditionalMetadata instance) {
		log.debug("attaching clean NcboOntologyAdditionalMetadata instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboOntologyAdditionalMetadataDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboOntologyAdditionalMetadataDAO) ctx
				.getBean("NcboOntologyAdditionalMetadataDAO");
	}
}