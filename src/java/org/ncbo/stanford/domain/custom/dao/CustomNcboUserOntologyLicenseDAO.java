package org.ncbo.stanford.domain.custom.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Expression;
import org.ncbo.stanford.domain.generated.NcboUserOntologyLicense;
import org.ncbo.stanford.domain.generated.NcboUserOntologyLicenseDAO;

/**
 * @author mdorf
 * 
 */
public class CustomNcboUserOntologyLicenseDAO extends
		NcboUserOntologyLicenseDAO {

	private static final Log log = LogFactory
			.getLog(CustomNcboUserOntologyLicenseDAO.class);

	/**
	 * 
	 */
	public CustomNcboUserOntologyLicenseDAO() {
		super();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getAllOntologyIds() {
		try {
			String queryString = "SELECT distinct ontologyId FROM NcboUserOntologyLicense ORDER BY ontologyId";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log
					.error("error getting all ontologyIds from the user ontology license table "
							+ re);
			throw re;
		}
	}

	/**
	 * Returns a single user ontology license record using a combo of userId and
	 * ontologyId
	 * 
	 * @param userId
	 * @param ontologyId
	 * @return NcboUserOntologyLicense
	 */
	public NcboUserOntologyLicense findByUserIdAndOntologyId(Integer userId,
			Integer ontologyId) {
		return (NcboUserOntologyLicense) getSession().createCriteria(
				"org.ncbo.stanford.domain.generated.NcboUserOntologyLicense")
				.add(Expression.eq("ncboUser.id", userId)).add(
						Expression.eq("ontologyId", ontologyId)).uniqueResult();
	}

	/**
	 * @param transientInstance
	 * @return
	 */
	public void deleteUserOntologyLicense(
			NcboUserOntologyLicense transientInstance) {
		try {
			getHibernateTemplate().delete(transientInstance);
			getHibernateTemplate().flush();
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}
}
