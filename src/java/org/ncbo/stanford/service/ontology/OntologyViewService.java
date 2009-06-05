package org.ncbo.stanford.service.ontology;

import java.util.List;

import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;

/**
 * Exposes services that provide read and write operations on ontology views
 * 
 * @author Csongor Nyulas
 */
public interface OntologyViewService {
//	/**
//	 * Returns the latest version of an ontology view using its obo foundry id
//	 * 
//	 * @param oboFoundryId
//	 * @return
//	 */
//	public OntologyViewBean findLatestOntologyViewVersionByOboFoundryId(
//			String oboFoundryId);

	/**
	 * Returns a single record for each ontology view in the system. If more than one
	 * version of ontology view exists, return the latest version.
	 * 
	 * @return list of Ontology view beans
	 */
	public List<OntologyViewBean> findLatestOntologyViewVersions();

	/**
	 * Returns a single record for each ontology view which is active in the system.
	 * If more than one version of ontology view exists, return the latest and active
	 * version. "active" meaning parse status is "ready" or "not applicable".
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyViewBean> findLatestActiveOntologyViewVersions();

	/**
	 * Returns all versions for given virtual view. 
	 * 
	 * @return list of Ontology view beans
	 */
	public List<OntologyViewBean> findAllOntologyViewVersionsByVirtualViewId(
			Integer viewId);

	/**
	 * Searches Ontology View Metadata
	 * 
	 * 
	 * @return list of Ontology view beans
	 */
	public List<OntologyViewBean> searchOntologyViewMetadata(String query);

	/**
	 * Returns a single ontology view version record
	 * 
	 * @param ontologyViewVersionId
	 * @return
	 */
	public OntologyViewBean findOntologyView(Integer ontologyViewVersionId);

	/**
	 * Finds the latest version of a given virtual view
	 * 
	 * @param viewId
	 * @return
	 */
	public OntologyViewBean findLatestOntologyViewVersion(Integer viewId);

	/**
	 * Create an ontology view
	 * 
	 * @param ontologyViewBean
	 * @return
	 */
	public void createOntologyView(OntologyViewBean ontologyViewBean,
			FilePathHandler filePathHander) throws Exception;

	/**
	 * Update an ontology view
	 * 
	 * @param ontologyViewBean
	 * @return
	 */
	public void updateOntologyView(OntologyViewBean ontologyViewBean) throws Exception;

	/**
	 * Update an ontology view Category
	 * 
	 * @param ontologyViewBean
	 * @return
	 */
	public void cleanupOntologyViewCategory(OntologyViewBean ontologyViewBean);

	/**
	 * Delete an ontology view
	 * 
	 * @param ontologyViewVersionId
	 * @return
	 */
	public void deleteOntologyView(Integer ontologyViewVersionId) throws Exception;

	/**
	 * Delete several ontologie views
	 * 
	 * @param ontologyViewVersionIds
	 * @return
	 */
	public void deleteOntologyViews(List<Integer> ontologyViewVersionIds)
			throws Exception;

}
