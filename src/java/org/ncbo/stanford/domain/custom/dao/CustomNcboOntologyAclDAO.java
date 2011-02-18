package org.ncbo.stanford.domain.custom.dao;

import java.util.List;

import org.ncbo.stanford.domain.generated.NcboOntologyAclDAO;

/**
 * @author mdorf
 * 
 */
public class CustomNcboOntologyAclDAO extends NcboOntologyAclDAO {

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
			System.out.println("error getting all ontologyIds from Acl " + re);
			throw re;
		}
	}
}
