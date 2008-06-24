/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboOntologyCategory;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
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
	 * Finds a given version of ontology
	 * 
	 * @return ontology object
	 */
	public NcboOntology findOntologyVersion(final Integer ontologyVersionId) {
		NcboOntology ontology = (NcboOntology) getSession().createCriteria(
				NcboOntology.class).add(Expression.eq("id", ontologyVersionId))
				.uniqueResult();

		NcboOntologyVersion ontologyVersion = findById(ontologyVersionId);

		if (ontologyVersion != null) {
			Set<NcboOntologyFile> files = ontologyVersion
					.getNcboOntologyFiles();
			Set<NcboOntologyCategory> categories = ontologyVersion
					.getNcboOntologyCategories();

			for (NcboOntologyFile file : files) {
				ontology.addFilename(file.getFilename());
			}

			for (NcboOntologyCategory cat : categories) {
				ontology.addCategoryId(cat.getNcboLCategory().getId());
			}
		}

		return ontology;
	}

	/**
	 * Search Ontology metadata
	 * 
	 * @return List of ontology objects
	 */
	public List<NcboOntology> searchOntologyMetadata(String query) {
		List<NcboOntology> ontologies = getSession().createCriteria(
				NcboOntology.class).add(
				Expression.or(
						Expression.like("publication", "%" + query + "%"),
						Expression.or(Expression.like("homepage", "%" + query
								+ "%"), Expression.or(Expression.like(
								"contactEmail", "%" + query + "%"), Expression
								.or(Expression.like("contactName", "%" + query
										+ "%"), Expression.or(Expression.like(
										"displayLabel", "%" + query + "%"),
										Expression.like("format", "%" + query
												+ "%"))))))).list();

		return ontologies;
	}

	/**
	 * Finds the latest version of a given ontology based on the obo foundry id
	 * 
	 * @param oboFoundryId
	 * @return the latest ontology version
	 */
	public NcboOntology findLatestOntologyVersionByOboFoundryId(
			final String oboFoundryId) {
		return (NcboOntology) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.getNamedQuery("VNcboOntologyVersionDAO.GET_LATEST_ONTOLOGY_VERSION_BY_OBO_FOUNDRY_ID");
						query.setString("oboFoundryId", oboFoundryId);

						return query.uniqueResult();
					}
				});

	}

	/**
	 * Finds a given set of ontology versions
	 * 
	 * @return List of ontology objects
	 */
	public List<NcboOntology> findOntologyVersions(List<Integer> versionIds) {
		List<NcboOntology> ontologies = getSession().createCriteria(
				NcboOntology.class).add(Expression.in("id", versionIds)).list();

		return ontologies;
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
						.getNamedQuery("VNcboOntologyVersionDAO.GET_LATEST_ONTOLOGY_VERSIONS_QUERY");
				query.setByte("isCurrent", ApplicationConstants.TRUE);

				return query.list();
			}
		});
	}

	/**
	 * Find all unique latest current versions of ontologies
	 * 
	 * @return list of ontologies
	 */
	public NcboOntology findLatestOntologyVersion(final Integer ontology_id) {
		NcboOntology ontology = (NcboOntology) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.getNamedQuery("VNcboOntologyVersionDAO.GET_LATEST_ONTOLOGY_VERSION_FOR_ONTOLOGY_ID_QUERY");
						query.setInteger("ontologyId", ontology_id);
						query.setByte("isCurrent", ApplicationConstants.TRUE);

						return query.uniqueResult();
					}
				});

		NcboOntologyVersion ontologyVer = findById(ontology.getId());
		Set<NcboOntologyFile> files = ontologyVer.getNcboOntologyFiles();

		for (NcboOntologyFile file : files) {
			ontology.addFilename(file.getFilename());
		}

		return ontology;
	}

	/**
	 * @param transientInstance
	 * @return
	 */
	public NcboOntologyVersion saveOntologyVersion(
			NcboOntologyVersion transientInstance) {
		try {
			Integer newId = (Integer) getHibernateTemplate().save(
					transientInstance);
			getHibernateTemplate().flush();

			return this.findById(newId);
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	/**
	 * Returns the metadata record for a given ontology version
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public NcboOntologyMetadata findOntologyMetadataById(
			final Integer ontologyVersionId) {
		NcboOntologyVersion ncboOntologyVersion = findById(ontologyVersionId);
		Set<NcboOntologyMetadata> metadataSet = ncboOntologyVersion
				.getNcboOntologyMetadatas();

		Object[] metadataArr = metadataSet.toArray();
		NcboOntologyMetadata ncboMetadata = (NcboOntologyMetadata) metadataArr[0];

		return ncboMetadata;
	}
}
