package org.ncbo.stanford.domain.generated;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboOntologyFile entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboOntologyFile
 * @author MyEclipse Persistence Tools
 */

public class NcboOntologyFileDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(NcboOntologyFileDAO.class);
	// property constants
	public static final String ONTOLOGY_VERSION_ID = "ontologyVersionId";
	public static final String FILENAME = "filename";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboOntologyFile transientInstance) {
		log.debug("saving NcboOntologyFile instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboOntologyFile persistentInstance) {
		log.debug("deleting NcboOntologyFile instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboOntologyFile findById(java.lang.Integer id) {
		log.debug("getting NcboOntologyFile instance with id: " + id);
		try {
			NcboOntologyFile instance = (NcboOntologyFile) getHibernateTemplate()
					.get("org.ncbo.stanford.domain.generated.NcboOntologyFile",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboOntologyFile instance) {
		log.debug("finding NcboOntologyFile instance by example");
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
		log.debug("finding NcboOntologyFile instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboOntologyFile as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByOntologyVersionId(Object ontologyVersionId) {
		return findByProperty(ONTOLOGY_VERSION_ID, ontologyVersionId);
	}

	public List findByFilename(Object filename) {
		return findByProperty(FILENAME, filename);
	}

	public List findAll() {
		log.debug("finding all NcboOntologyFile instances");
		try {
			String queryString = "from NcboOntologyFile";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboOntologyFile merge(NcboOntologyFile detachedInstance) {
		log.debug("merging NcboOntologyFile instance");
		try {
			NcboOntologyFile result = (NcboOntologyFile) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboOntologyFile instance) {
		log.debug("attaching dirty NcboOntologyFile instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboOntologyFile instance) {
		log.debug("attaching clean NcboOntologyFile instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboOntologyFileDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboOntologyFileDAO) ctx.getBean("NcboOntologyFileDAO");
	}
}