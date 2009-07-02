package org.ncbo.stanford.domain.generated;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboUsageLog entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboUsageLog
 * @author MyEclipse Persistence Tools
 */

public class NcboUsageLogDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(NcboUsageLogDAO.class);
	// property constants
	public static final String HASH_CODE = "hashCode";
	public static final String APPLICATION_ID = "applicationId";
	public static final String REQUEST_URL = "requestUrl";
	public static final String HTTP_METHOD = "httpMethod";
	public static final String RESOURCE_PARAMETERS = "resourceParameters";
	public static final String REQUEST_PARAMETERS = "requestParameters";
	public static final String USER_ID = "userId";
	public static final String HIT_COUNT = "hitCount";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboUsageLog transientInstance) {
		log.debug("saving NcboUsageLog instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboUsageLog persistentInstance) {
		log.debug("deleting NcboUsageLog instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboUsageLog findById(java.lang.Integer id) {
		log.debug("getting NcboUsageLog instance with id: " + id);
		try {
			NcboUsageLog instance = (NcboUsageLog) getHibernateTemplate().get(
					"org.ncbo.stanford.domain.generated.NcboUsageLog", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboUsageLog instance) {
		log.debug("finding NcboUsageLog instance by example");
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
		log.debug("finding NcboUsageLog instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboUsageLog as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByHashCode(Object hashCode) {
		return findByProperty(HASH_CODE, hashCode);
	}

	public List findByApplicationId(Object applicationId) {
		return findByProperty(APPLICATION_ID, applicationId);
	}

	public List findByRequestUrl(Object requestUrl) {
		return findByProperty(REQUEST_URL, requestUrl);
	}

	public List findByHttpMethod(Object httpMethod) {
		return findByProperty(HTTP_METHOD, httpMethod);
	}

	public List findByResourceParameters(Object resourceParameters) {
		return findByProperty(RESOURCE_PARAMETERS, resourceParameters);
	}

	public List findByRequestParameters(Object requestParameters) {
		return findByProperty(REQUEST_PARAMETERS, requestParameters);
	}

	public List findByUserId(Object userId) {
		return findByProperty(USER_ID, userId);
	}

	public List findByHitCount(Object hitCount) {
		return findByProperty(HIT_COUNT, hitCount);
	}

	public List findAll() {
		log.debug("finding all NcboUsageLog instances");
		try {
			String queryString = "from NcboUsageLog";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboUsageLog merge(NcboUsageLog detachedInstance) {
		log.debug("merging NcboUsageLog instance");
		try {
			NcboUsageLog result = (NcboUsageLog) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboUsageLog instance) {
		log.debug("attaching dirty NcboUsageLog instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboUsageLog instance) {
		log.debug("attaching clean NcboUsageLog instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboUsageLogDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboUsageLogDAO) ctx.getBean("NcboUsageLogDAO");
	}
}