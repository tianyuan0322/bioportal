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
 * NcboAppText entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboAppText
 * @author MyEclipse Persistence Tools
 */

public class NcboAppTextDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(NcboAppTextDAO.class);
	// property constants
	public static final String DESCRIPTION = "description";
	public static final String TEXT_CONTENT = "textContent";
	public static final String LAST_MODIFIER = "lastModifier";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboAppText transientInstance) {
		log.debug("saving NcboAppText instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboAppText persistentInstance) {
		log.debug("deleting NcboAppText instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboAppText findById(java.lang.String id) {
		log.debug("getting NcboAppText instance with id: " + id);
		try {
			NcboAppText instance = (NcboAppText) getHibernateTemplate().get(
					"org.ncbo.stanford.domain.generated.NcboAppText", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboAppText instance) {
		log.debug("finding NcboAppText instance by example");
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
		log.debug("finding NcboAppText instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from NcboAppText as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByDescription(Object description) {
		return findByProperty(DESCRIPTION, description);
	}

	public List findByTextContent(Object textContent) {
		return findByProperty(TEXT_CONTENT, textContent);
	}

	public List findByLastModifier(Object lastModifier) {
		return findByProperty(LAST_MODIFIER, lastModifier);
	}

	public List findAll() {
		log.debug("finding all NcboAppText instances");
		try {
			String queryString = "from NcboAppText";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboAppText merge(NcboAppText detachedInstance) {
		log.debug("merging NcboAppText instance");
		try {
			NcboAppText result = (NcboAppText) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboAppText instance) {
		log.debug("attaching dirty NcboAppText instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboAppText instance) {
		log.debug("attaching clean NcboAppText instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboAppTextDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboAppTextDAO) ctx.getBean("NcboAppTextDAO");
	}
}