package org.ncbo.stanford.service.ontology;

import java.io.File;
import java.util.List;

import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.bean.GroupBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;

/**
 * Exposes services that provide read and write operations on ontologies
 * 
 * @author Michael Dorf
 * @author Nick Griffith
 */
public interface OntologyService {

	/**
	 * Return the list of all categories
	 * 
	 * @return
	 */
	public List<CategoryBean> findAllCategories();

	/**
	 * Return the list of category Ids that correspond to the given obo foundry
	 * names
	 * 
	 * @param oboFoundryNames
	 * @return
	 */
	public List<Integer> findCategoryIdsByOBOFoundryNames(
			String[] oboFoundryNames);

	/**
	 * Return the list of all groups
	 * 
	 * @return
	 */
	public List<GroupBean> findAllGroups();

	/**
	 * Returns the latest version of an ontology using its obo foundry id
	 * 
	 * @param oboFoundryId
	 * @return
	 */
	public OntologyBean findLatestOntologyVersionByOboFoundryId(
			String oboFoundryId);

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findLatestOntologyVersions();

	/**
	 * Returns a single record for each ontology which is active in the system.
	 * If more than one version of ontology exists, return the latest and active
	 * version. "active" meaning parse status is "ready".
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findLatestActiveOntologyVersions();

	/**
	 * Returns all versions for given ontology. Two steps : 1. Get list of
	 * OntologyVersions to get the list of ontologyVersionId. 2. Get list of
	 * NcboOntology entity from the list of ontVersionId.
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findAllOntologyOrViewVersionsByVirtualId(
			Integer ontologyOrViewId);

	/**
	 * Searches common fields from ontology or view metadata for an arbitrary
	 * query string
	 * 
	 * @param query
	 *            search a string
	 * @param includeViews
	 *            if set to true search will include also the metadata of views,
	 *            otherwise it searches only in metadata of ontologies (no
	 *            views)
	 * @return list of ontology beans that have metadata matching the query
	 *         string
	 */
	public List<OntologyBean> searchOntologyMetadata(String query,
			boolean includeViews);

	/**
	 * Returns a single ontology version record
	 * 
	 * @param ontologyVersionId
	 * @return
	 * @throws Exception
	 */
	public OntologyBean findOntologyOrView(Integer ontologyVersionId)
			throws Exception;

	/**
	 * Finds the latest version of a given ontology
	 * 
	 * @param ontologyId
	 * @return
	 * @throws Exception
	 */
	public OntologyBean findLatestOntologyOrViewVersion(Integer ontologyId)
			throws Exception;

	/**
	 * Finds the latest "active" version of a given ontology
	 * 
	 * @param ontologyId
	 * @return
	 * @throws Exception
	 */
	public OntologyBean findLatestActiveOntologyOrViewVersion(Integer ontologyId)
			throws Exception;

	/**
	 * Create an ontology
	 * 
	 * @param ontologyBean
	 * @return
	 */
	public void createOntologyOrView(OntologyBean ontologyBean,
			FilePathHandler filePathHander) throws Exception;

	/**
	 * Update an ontology
	 * 
	 * @param ontologyBean
	 * @return
	 */
	public void updateOntologyOrView(OntologyBean ontologyBean)
			throws Exception;

	/**
	 * Update an ontology Category
	 * 
	 * @param ontologyBean
	 * @return
	 */
	public void cleanupOntologyCategory(OntologyBean ontologyBean);

	/**
	 * Delete an ontology
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public void deleteOntologyOrView(Integer ontologyVersionId)
			throws Exception;

	/**
	 * Delete several ontologies
	 * 
	 * @param ontologyVersionIds
	 * @return
	 */
	public void deleteOntologiesOrViews(List<Integer> ontologyVersionIds)
			throws Exception;

	/**
	 * Get File object for a specific ontology
	 * 
	 * @param ontologyBean
	 * @return
	 */
	public File getOntologyFile(OntologyBean ontologyBean) throws Exception;

	/**
	 * Get the ontology metrics bean belonging to an ontology or view
	 * 
	 * @param ontologyBean
	 * @return
	 */
	public OntologyMetricsBean getOntologyMetrics(OntologyBean ontologyBean)
			throws Exception;

	// ******************** view specific methods ********************

	/**
	 * Returns a single record for each ontology view in the system. If more
	 * than one version of ontology view exists, return the latest version.
	 * 
	 * @return list of ontology beans
	 */
	public List<OntologyBean> findLatestOntologyViewVersions();

	/**
	 * Returns a single record for each ontology view which is active in the
	 * system. If more than one version of ontology view exists, return the
	 * latest and active version. "active" meaning parse status is "ready" or
	 * "not applicable".
	 * 
	 * @return list of ontology beans
	 */
	public List<OntologyBean> findLatestActiveOntologyViewVersions();

	/**
	 * Programmatically reloads the metadata ontology stored in the memory
	 */
	public void reloadMetadataOntology();
}
