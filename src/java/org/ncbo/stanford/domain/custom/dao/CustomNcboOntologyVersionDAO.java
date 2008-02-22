/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.domain.generated.NcboOntologyVersionDAO;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * @author Michael Dorf
 * 
 */
@SuppressWarnings("unchecked")
public class CustomNcboOntologyVersionDAO extends NcboOntologyVersionDAO {

	private static final Log log = LogFactory
			.getLog(CustomNcboOntologyVersionDAO.class);

	/**
	 * 
	 */
	public CustomNcboOntologyVersionDAO() {
		super();
	}

	/**
	 * Find all unique latest current versions of ontologies
	 * 
	 * @return list of ontologies
	 */
	public List<NcboOntology> findLatestOntologyVersions() {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.getNamedQuery("NcboOntologyVersionDAO.GET_LATEST_ONTOLOGY_VERSIONS_QUERY");
				query.setByte("isCurrent", ApplicationConstants.TRUE);

				return query.list();
			}
		});
	}

	/**
	 * @param transientInstance
	 * @return
	 */
	public NcboOntologyVersion saveOntologyVersion(
			NcboOntologyVersion transientInstance) {
		try {
			return this.findById((Integer) getHibernateTemplate().save(
					transientInstance));
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

}
