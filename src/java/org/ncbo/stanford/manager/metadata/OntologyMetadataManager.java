package org.ncbo.stanford.manager.metadata;

import java.util.List;

import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.bean.OntologyBean;

/**
 * An interface for all API specific ontology metadata managers to conform to. This allows
 * for a seamless Spring injection.
 * 
 * @author Csongor Nyulas
 * 
 */
/**
 * @author csnyulas
 * 
 */
public interface OntologyMetadataManager {

	/**
	 * Saves the ontology metadata specified by the ontologyBean representing an
	 * ontology/view version.
	 * 
	 * @param ontologyBean
	 * @throws Exception
	 */
	public void saveOntologyOrView(OntologyBean ontologyBean) throws Exception;

	/**
	 * Updates the ontology metadata specified by the ontologyBean representing
	 * an ontology/view version.
	 * 
	 * @param ontologyBean
	 * @throws Exception
	 */
	public void updateOntologyOrView(OntologyBean ontologyBean)
			throws Exception;

	/**
	 * Updates the metadata of an ontology version specified by the ontologyBean
	 * with ontology metrics information specified by metricsBean.
	 * 
	 * @param ontologyBean
	 * @param metricsBean
	 * @throws Exception
	 */
	public void updateOntologyMetrics(OntologyBean ontologyBean,
			OntologyMetricsBean metricsBean) throws Exception;

	/**
	 * Returns the OntologyMetricsBean belonging to the ontology or view
	 * represented by the argument.
	 * 
	 * @param ontologyBean
	 *            and OntologyBean representing an ontology or a view version
	 * @return the metrics bean corresponding to an ontology or view
	 * @throws Exception
	 */
	public OntologyMetricsBean getOntologyMetrics(OntologyBean ontologyBean)
			throws Exception;

	/**
	 * Deletes or "deprecates" the ontology metadata specified by the
	 * ontologyBean representing an ontology or view version.
	 * 
	 * @param ontologyBean
	 * @param removeMetadata
	 * @throws Exception
	 */
	public void deleteOntologyOrView(OntologyBean ontologyBean,
			boolean removeMetadata) throws Exception;

	/**
	 * Retrieves the ontologyBean representing an ontology or a view version for
	 * a specific version id.
	 * 
	 * @param ontologyOrViewVersionId
	 *            an ontology or view version id
	 * @throws Exception
	 */
	public OntologyBean findOntologyOrViewVersionById(
			Integer ontologyOrViewVersionId) throws Exception;

	// /**
	// * Retrieves the ontologyBean representing the latest version for a
	// specific ontology id.
	// *
	// * @param ontologyId a (virtual) ontology id
	// */
	// public OntologyBean findLatestOntologyVersionById(Integer ontologyId);

	/**
	 * Returns the list of ontologyBeans, one for each ontology's latest
	 * version.
	 * 
	 * @return the list of ontology beans corresponding to the latest version of
	 *         each virtual ontology
	 */
	public List<OntologyBean> findLatestOntologyVersions();

	/**
	 * Returns the list of ontologyBeans for the latest versions of auto-pulled
	 * ontologies.
	 * 
	 * @return the list of ontology beans corresponding to the latest version of
	 *         each virtual ontology
	 */
	public List<OntologyBean> findLatestAutoPulledOntologyVersions();

	/**
	 * Retrieves the ontologyBean representing the latest version for a specific
	 * ontology/view id.
	 * 
	 * @param ontologyId
	 *            a (virtual) ontology/view id
	 * @throws Exception
	 */
	public OntologyBean findLatestOntologyOrViewVersionById(
			Integer ontologyOrViewId) throws Exception;

	/**
	 * Retrieves the ontologyBean representing the latest active version for a
	 * specific ontology/view id.
	 * 
	 * @param ontologyId
	 *            a (virtual) ontology/view id
	 * @throws Exception
	 */
	public OntologyBean findLatestActiveOntologyOrViewVersionById(
			Integer ontologyOrViewId) throws Exception;

	/**
	 * Returns the list of ontologyBeans, one for each ontology's latest active
	 * version.
	 * 
	 * @return the list of ontology beans corresponding to the latest active
	 *         version of each virtual ontology
	 */
	public List<OntologyBean> findLatestActiveOntologyVersions();

	/**
	 * Returns the list of ontology(View)Beans, one for each ontology's/view's
	 * latest active version.
	 * 
	 * @return the list of ontology/view beans corresponding to the latest
	 *         active version of each virtual ontology/view
	 * @throws Exception
	 */
	public List<OntologyBean> findLatestActiveOntologyOrOntologyViewVersions()
			throws Exception;

	/**
	 * Returns the list of ontology(View)Beans, one for each ontology's/view's
	 * latest active version.
	 * 
	 * @param ontologyOrViewIds
	 *            a list of (virtual) ontology/view ids
	 * @return the list of ontology (view) beans corresponding to the latest
	 *         active version of each virtual ontology/view
	 * @throws Exception
	 */
	public List<OntologyBean> findLatestActiveOntologyOrOntologyViewVersions(
			List<Integer> ontologyOrViewIds) throws Exception;

	/**
	 * Returns the list of ontology(View)Beans, one for each version of a
	 * specific virtual ontology/view.
	 * 
	 * @param ontologyOrViewId
	 *            a (virtual) ontology/view id
	 * @param excludeDeprecated
	 * @return the list of ontology/view beans corresponding to the versions of
	 *         the virtual ontology/view with ID <code>ontologyOrViewId</code>
	 * @throws Exception
	 */
	public List<OntologyBean> findAllOntologyOrViewVersionsById(
			Integer ontologyOrViewId, boolean excludeDeprecated)
			throws Exception;

	/**
	 * Returns the next available (i.e. not already in use) virtual ontology ID
	 * 
	 * @return
	 */
	public int getNextAvailableOntologyId();

	/**
	 * Returns the next available (i.e. not already in use) ontology version ID
	 * 
	 * @return
	 */
	public int getNextAvailableOntologyVersionId();

	/**
	 * Returns the latest version of an ontology using its obo foundry id
	 * 
	 * @param oboFoundryId
	 * @return
	 */
	public OntologyBean findLatestOntologyVersionByOboFoundryId(
			String oboFoundryId);

	/**
	 * Returns list of ontology beans for all ontology versions that have some
	 * properties that matches the argument
	 * 
	 * @param query
	 *            a String that is searched for in the metadata
	 * @param includeViews
	 *            true if views should be also included in the search, false if
	 *            only ontology metadata should be searched.
	 * @return list of matching ontology beans
	 */
	public List<OntologyBean> searchOntologyMetadata(String query,
			boolean includeViews);

	// ******************** view specific methods ********************

	/**
	 * Returns the list of ontologyBeans, one for each ontology view's latest
	 * version.
	 * 
	 * @return the list of ontology beans corresponding to the latest version of
	 *         each virtual view
	 */
	public List<OntologyBean> findLatestOntologyViewVersions();

	/**
	 * Retrieves the ontologyBean representing the latest active version for a
	 * specific virtual view id.
	 * 
	 * @param viewId
	 *            a (virtual) view id
	 */
	public OntologyBean findLatestActiveOntologyViewVersionById(Integer viewId);

	/**
	 * Returns the list of ontologyBeans, one for each ontology view's latest
	 * active version.
	 * 
	 * @return the list of ontology beans corresponding to the latest active
	 *         version of each virtual view
	 */
	public List<OntologyBean> findLatestActiveOntologyViewVersions();

	/**
	 * Returns the next available (i.e. not already in use) virtual view ID
	 * 
	 * @return
	 */
	public int getNextAvailableVirtualViewId();

	/**
	 * Returns the next available (i.e. not already in use) ontology view
	 * version ID
	 * 
	 * @return
	 */
	public int getNextAvailableOntologyViewVersionId();

	/**
	 * Returns list of ontology beans for all ontology view versions that have
	 * some properties that matches the argument
	 * 
	 * @param query
	 *            a String that is searched for in the metadata
	 * @return list of matching ontology view beans
	 */
	public List<OntologyBean> searchOntologyViewMetadata(String query);

	/**
	 * Programmatically reloads the metadata ontology stored in the memory
	 */
	public void reloadMetadataOWLModel();
}
