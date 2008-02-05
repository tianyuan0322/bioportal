package org.ncbo.stanford.domain.generated;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboOntologyVersion entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboOntologyVersion
 * @author MyEclipse Persistence Tools
 */

public class NcboOntologyVersionDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory
			.getLog(NcboOntologyVersionDAO.class);
	// property constants
	public static final String ONTOLOGY_ID = "ontologyId";
	public static final String INTERNAL_VERSION_NUMBER = "internalVersionNumber";
	public static final String VERSION_NUMBER = "versionNumber";
	public static final String VERSION_STATUS = "versionStatus";
	public static final String FILE_PATH = "filePath";
	public static final String IS_CURRENT = "isCurrent";
	public static final String IS_REMOTE = "isRemote";
	public static final String IS_REVIEWED = "isReviewed";
	public static final String STATUS_ID = "statusId";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboOntologyVersion transientInstance) {
		log.debug("saving NcboOntologyVersion instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboOntologyVersion persistentInstance) {
		log.debug("deleting NcboOntologyVersion instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboOntologyVersion findById(java.lang.Integer id) {
		log.debug("getting NcboOntologyVersion instance with id: " + id);
		try {
			NcboOntologyVersion instance = (NcboOntologyVersion) getHibernateTemplate()
					.get(
							"org.ncbo.stanford.domain.generated.NcboOntologyVersion",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboOntologyVersion instance) {
		log.debug("finding NcboOntologyVersion instance by example");
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
		log.debug("finding NcboOntologyVersion instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboOntologyVersion as model where model."
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

	public List findByInternalVersionNumber(Object internalVersionNumber) {
		return findByProperty(INTERNAL_VERSION_NUMBER, internalVersionNumber);
	}

	public List findByVersionNumber(Object versionNumber) {
		return findByProperty(VERSION_NUMBER, versionNumber);
	}

	public List findByVersionStatus(Object versionStatus) {
		return findByProperty(VERSION_STATUS, versionStatus);
	}

	public List findByFilePath(Object filePath) {
		return findByProperty(FILE_PATH, filePath);
	}

	public List findByIsCurrent(Object isCurrent) {
		return findByProperty(IS_CURRENT, isCurrent);
	}

	public List findByIsRemote(Object isRemote) {
		return findByProperty(IS_REMOTE, isRemote);
	}

	public List findByIsReviewed(Object isReviewed) {
		return findByProperty(IS_REVIEWED, isReviewed);
	}

	public List findByStatusId(Object statusId) {
		return findByProperty(STATUS_ID, statusId);
	}

	public List findAll() {
		log.debug("finding all NcboOntologyVersion instances");
		try {
			String queryString = "from NcboOntologyVersion";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboOntologyVersion merge(NcboOntologyVersion detachedInstance) {
		log.debug("merging NcboOntologyVersion instance");
		try {
			NcboOntologyVersion result = (NcboOntologyVersion) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboOntologyVersion instance) {
		log.debug("attaching dirty NcboOntologyVersion instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboOntologyVersion instance) {
		log.debug("attaching clean NcboOntologyVersion instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboOntologyVersionDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboOntologyVersionDAO) ctx.getBean("NcboOntologyVersionDAO");
	}
}