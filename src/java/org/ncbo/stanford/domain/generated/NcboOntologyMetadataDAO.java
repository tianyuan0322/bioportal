package org.ncbo.stanford.domain.generated;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * NcboOntologyMetadata entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see org.ncbo.stanford.domain.generated.NcboOntologyMetadata
 * @author MyEclipse Persistence Tools
 */

public class NcboOntologyMetadataDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory
			.getLog(NcboOntologyMetadataDAO.class);
	// property constants
	public static final String DISPLAY_LABEL = "displayLabel";
	public static final String FORMAT = "format";
	public static final String CONTACT_NAME = "contactName";
	public static final String CONTACT_EMAIL = "contactEmail";
	public static final String HOMEPAGE = "homepage";
	public static final String DOCUMENTATION = "documentation";
	public static final String PUBLICATION = "publication";
	public static final String URN = "urn";
	public static final String CODING_SCHEME = "codingScheme";
	public static final String IS_FOUNDRY = "isFoundry";

	protected void initDao() {
		// do nothing
	}

	public void save(NcboOntologyMetadata transientInstance) {
		log.debug("saving NcboOntologyMetadata instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(NcboOntologyMetadata persistentInstance) {
		log.debug("deleting NcboOntologyMetadata instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NcboOntologyMetadata findById(java.lang.Integer id) {
		log.debug("getting NcboOntologyMetadata instance with id: " + id);
		try {
			NcboOntologyMetadata instance = (NcboOntologyMetadata) getHibernateTemplate()
					.get(
							"org.ncbo.stanford.domain.generated.NcboOntologyMetadata",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(NcboOntologyMetadata instance) {
		log.debug("finding NcboOntologyMetadata instance by example");
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
		log.debug("finding NcboOntologyMetadata instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from NcboOntologyMetadata as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByDisplayLabel(Object displayLabel) {
		return findByProperty(DISPLAY_LABEL, displayLabel);
	}

	public List findByFormat(Object format) {
		return findByProperty(FORMAT, format);
	}

	public List findByContactName(Object contactName) {
		return findByProperty(CONTACT_NAME, contactName);
	}

	public List findByContactEmail(Object contactEmail) {
		return findByProperty(CONTACT_EMAIL, contactEmail);
	}

	public List findByHomepage(Object homepage) {
		return findByProperty(HOMEPAGE, homepage);
	}

	public List findByDocumentation(Object documentation) {
		return findByProperty(DOCUMENTATION, documentation);
	}

	public List findByPublication(Object publication) {
		return findByProperty(PUBLICATION, publication);
	}

	public List findByUrn(Object urn) {
		return findByProperty(URN, urn);
	}

	public List findByCodingScheme(Object codingScheme) {
		return findByProperty(CODING_SCHEME, codingScheme);
	}

	public List findByIsFoundry(Object isFoundry) {
		return findByProperty(IS_FOUNDRY, isFoundry);
	}

	public List findAll() {
		log.debug("finding all NcboOntologyMetadata instances");
		try {
			String queryString = "from NcboOntologyMetadata";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public NcboOntologyMetadata merge(NcboOntologyMetadata detachedInstance) {
		log.debug("merging NcboOntologyMetadata instance");
		try {
			NcboOntologyMetadata result = (NcboOntologyMetadata) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(NcboOntologyMetadata instance) {
		log.debug("attaching dirty NcboOntologyMetadata instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(NcboOntologyMetadata instance) {
		log.debug("attaching clean NcboOntologyMetadata instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static NcboOntologyMetadataDAO getFromApplicationContext(
			ApplicationContext ctx) {
		return (NcboOntologyMetadataDAO) ctx.getBean("NcboOntologyMetadataDAO");
	}
}