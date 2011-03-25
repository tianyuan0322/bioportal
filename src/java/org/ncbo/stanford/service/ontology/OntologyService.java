package org.ncbo.stanford.service.ontology;

import java.io.File;
import java.util.List;

import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.bean.GroupBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;

/**
 * Exposes services that provide read and write operations on ontologies
 * 
 * @author Michael Dorf
 * @author Nick Griffith
 */
public interface OntologyService {

	/**
	 * Returns the virtual ontology id corresponding to the given ontology
	 * version id
	 * 
	 * @param versionId
	 * @return
	 */
	public Integer getOntologyIdByVersionId(Integer versionId);

	/**
	 * Returns the virtual view id corresponding to the given view version id
	 * 
	 * @param viewVersionId
	 * @return
	 */
	public Integer getViewIdByViewVersionId(Integer viewVersionId);

	/**
	 * Returns all virtual ontology ids corresponding to the given virtual view
	 * id
	 * 
	 * @param viewId
	 * @return
	 */
	public List<Integer> getOntologyIdsByViewId(Integer viewId);
	
	/**
	 * This method is expected to be run by a scheduler process. Populates three
	 * global maps for quick ID translation: 
	 * 		versionIdToOntologyIdMap
	 * 		viewVersionIdToViewIdMap 
	 * 		viewIdToOntologyIdsMap
	 */
	public void populateIdMaps();
	
	/**
	 * Checks whether access is restricted to a given ontology by its virtual
	 * ontology id
	 * 
	 * @param ontologyId
	 * @return
	 */
	public boolean isInAcl(Integer ontologyId);

	/**
	 * This method is expected to be run by a scheduler process. Populates a
	 * global list of ontology ids that have access restrictions
	 */
	public void populateOntologyAcl();
	
	/**
	 * Return the list of all categories
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<CategoryBean> findAllCategories() throws Exception;

	/**
	 * Return the list of category Ids that correspond to the given obo foundry
	 * names
	 * 
	 * @param oboFoundryNames
	 * @return
	 * @throws Exception 
	 */
	public List<Integer> findCategoryIdsByOBOFoundryNames(
			String[] oboFoundryNames) throws Exception;

	/**
	 * Return the list of all groups
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<GroupBean> findAllGroups() throws Exception;

	/**
	 * Returns the latest version of an ontology using its obo foundry id
	 * 
	 * @param oboFoundryId
	 * @return
	 * @throws Exception 
	 */
	public OntologyBean findLatestOntologyVersionByOboFoundryId(
			String oboFoundryId) throws Exception;

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of Ontology beans
	 * @throws Exception 
	 */
	public List<OntologyBean> findLatestOntologyVersions() throws Exception;

	/**
	 * Returns the list of the latest versions of auto-pulled ontologies.
	 * 
	 * @return the list of ontology beans corresponding to the latest version of
	 *         each ontology
	 * @throws Exception 
	 */
	public List<OntologyBean> findLatestAutoPulledOntologyVersions() throws Exception;

	/**
	 * Returns a single record for each ontology which is active in the system.
	 * If more than one version of ontology exists, return the latest and active
	 * version. "active" meaning parse status is "ready".
	 * 
	 * @return list of Ontology beans
	 * @throws Exception 
	 */
	public List<OntologyBean> findLatestActiveOntologyVersions() throws Exception;

	/**
	 * Returns all versions for given ontology. Two steps : 1. Get list of
	 * OntologyVersions to get the list of ontologyVersionId. 2. Get list of
	 * NcboOntology entity from the list of ontVersionId.
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findAllOntologyOrViewVersionsByVirtualId(
			Integer ontologyOrViewId, boolean excludeDeprecated)
			throws Exception;

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
	 * @throws Exception 
	 */
	public List<OntologyBean> searchOntologyMetadata(String query,
			boolean includeViews) throws Exception;

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
	 * Delete/Deprecate an ontology
	 * 
	 * @param ontologyVersionId
	 * @param removeMetadata
	 * @param removeOntologyFiles
	 * @return
	 */
	public void deleteOntologyOrView(Integer ontologyVersionId,
			boolean removeMetadata, boolean removeOntologyFiles)
			throws Exception;

	/**
	 * Delete/Deprecate an ontology
	 * 
	 * @param ontologyVersionIds
	 * @param removeMetadata
	 * @param removeOntologyFiles
	 * @return
	 */
	public void deleteOntologiesOrViews(List<Integer> ontologyVersionIds,
			boolean removeMetadata, boolean removeOntologyFiles);

	/**
	 * Get File object for a specific ontology
	 * 
	 * @param ontologyBean
	 * @return
	 */
	public File getOntologyFile(OntologyBean ontologyBean) throws Exception;

	// ******************** view specific methods ********************

	/**
	 * Returns a single record for each ontology view in the system. If more
	 * than one version of ontology view exists, return the latest version.
	 * 
	 * @return list of ontology beans
	 * @throws Exception 
	 */
	public List<OntologyBean> findLatestOntologyViewVersions() throws Exception;

	/**
	 * Returns a single record for each ontology view which is active in the
	 * system. If more than one version of ontology view exists, return the
	 * latest and active version. "active" meaning parse status is "ready" or
	 * "not applicable".
	 * 
	 * @return list of ontology beans
	 * @throws Exception 
	 */
	public List<OntologyBean> findLatestActiveOntologyViewVersions() throws Exception;

	/**
	 * Programmatically reloads the metadata ontology stored in the memory
	 * 
	 * @throws Exception
	 */
	public void reloadMetadataOntology() throws Exception;

	/**
	 * Returns info about ontologies that did not process successfully
	 * 
	 * @return
	 */
	public List<String> getErrorOntologies();
	
	/**
	 * Get RdfFile object for a specific ontology
	 * 
	 * @param ontologyBean
	 * @return
	 */
	
	public File findRdfFileForOntology(OntologyBean ontologyBean) throws Exception;
}
