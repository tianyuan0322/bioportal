package org.ncbo.stanford.domain.custom.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Expression;
import org.ncbo.stanford.domain.generated.NcboOntologyAcl;
import org.ncbo.stanford.domain.generated.NcboOntologyAclDAO;

/**
 * @author mdorf
 * 
 */
public class CustomNcboOntologyAclDAO extends NcboOntologyAclDAO {

	private static final Log log = LogFactory
			.getLog(CustomNcboOntologyAclDAO.class);

	/**
	 * 
	 */
	public CustomNcboOntologyAclDAO() {
		super();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getAllOntologyIds() {
		try {
			String queryString = "SELECT distinct ontologyId FROM NcboOntologyAcl ORDER BY ontologyId";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("error getting all ontologyIds from Acl " + re);
			throw re;
		}
	}

	/**
	 * Returns a single ACL record using a combo of userId and ontologyId
	 * 
	 * @param userId
	 * @param ontologyId
	 * @return NcboOntologyAcl
	 */
	public NcboOntologyAcl findByUserIdAndOntologyId(Integer userId,
			Integer ontologyId) {
		return (NcboOntologyAcl) getSession().createCriteria(
				"org.ncbo.stanford.domain.generated.NcboOntologyAcl").add(
				Expression.eq("ncboUser.id", userId)).add(
				Expression.eq("ontologyId", ontologyId)).uniqueResult();
	}

	/**
	 * @param transientInstance
	 * @return
	 */
	public void deleteOntologyAcl(NcboOntologyAcl transientInstance) {
		try {
			getHibernateTemplate().delete(transientInstance);
			getHibernateTemplate().flush();
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}
}
