package org.ncbo.stanford.manager.metadata;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * An interface for all API specific ontology metadata managers to conform to.
 * This allows for a seamless Spring injection.
 * 
 * @author Csongor Nyulas
 * @author Michael Dorf
 * 
 */

public interface OntologyMetadataManager {

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
	 * Populates three global maps for quick ID translation:
	 * versionIdToOntologyIdMap viewVersionIdToViewIdMap viewIdToOntologyIdsMap
	 * 
	 * @throws Exception
	 */
	public void populateIdMaps() throws Exception;

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
	 * @throws Exception
	 */
	public List<OntologyBean> findLatestOntologyVersions() throws Exception;

	/**
	 * Returns the list of ontologyBeans for the latest versions of auto-pulled
	 * ontologies.
	 * 
	 * @return the list of ontology beans corresponding to the latest version of
	 *         each virtual ontology
	 * @throws Exception
	 */
	public List<OntologyBean> findLatestAutoPulledOntologyVersions()
			throws Exception;

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
	 * @throws Exception
	 */
	public List<OntologyBean> findLatestActiveOntologyVersions()
			throws Exception;

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
	 * @throws Exception
	 */
	public int getNextAvailableOntologyId() throws Exception;

	/**
	 * Returns the next available (i.e. not already in use) ontology version ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNextAvailableOntologyVersionId() throws Exception;

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
	 * Returns list of ontology beans for all ontology versions that have some
	 * properties that matches the argument
	 * 
	 * @param query
	 *            a String that is searched for in the metadata
	 * @param includeViews
	 *            true if views should be also included in the search, false if
	 *            only ontology metadata should be searched.
	 * @return list of matching ontology beans
	 * @throws Exception
	 */
	public List<OntologyBean> searchOntologyMetadata(String query,
			boolean includeViews) throws Exception;

	// ******************** view specific methods ********************

	/**
	 * Returns the list of ontologyBeans, one for each ontology view's latest
	 * version.
	 * 
	 * @return the list of ontology beans corresponding to the latest version of
	 *         each virtual view
	 * @throws Exception
	 */
	public List<OntologyBean> findLatestOntologyViewVersions() throws Exception;

	/**
	 * Retrieves the ontologyBean representing the latest active version for a
	 * specific virtual view id.
	 * 
	 * @param viewId
	 *            a (virtual) view id
	 * @throws Exception
	 */
	public OntologyBean findLatestActiveOntologyViewVersionById(Integer viewId)
			throws Exception;

	/**
	 * Returns the list of ontologyBeans, one for each ontology view's latest
	 * active version.
	 * 
	 * @return the list of ontology beans corresponding to the latest active
	 *         version of each virtual view
	 * @throws Exception
	 */
	public List<OntologyBean> findLatestActiveOntologyViewVersions()
			throws Exception;

	/**
	 * Returns the next available (i.e. not already in use) virtual view ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNextAvailableVirtualViewId() throws Exception;

	/**
	 * Returns the next available (i.e. not already in use) ontology view
	 * version ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNextAvailableOntologyViewVersionId() throws Exception;

	/**
	 * Returns list of ontology beans for all ontology view versions that have
	 * some properties that matches the argument
	 * 
	 * @param query
	 *            a String that is searched for in the metadata
	 * @return list of matching ontology view beans
	 * @throws Exception
	 */
	public List<OntologyBean> searchOntologyViewMetadata(String query)
			throws Exception;

	/**
	 * Programmatically reloads the metadata ontology stored in the memory
	 */
	public void reloadMetadataOWLModel() throws Exception;

	/**
	 * Interface method for a method in the AbstractOntologyMetadataManager
	 * class.
	 */
	public OWLIndividual getOntologyOrViewInstance(OWLModel metadata, int id);

	/**
	 * Interface method for a method in the AbstractOntologyManagerProtege
	 * class.
	 */
	public OWLModel getMetadataOWLModel() throws Exception;

	/**
	 * Fills an instance of OntologyBean with data gathered from the metadata
	 * OWLIndividual object
	 * 
	 * @param ob
	 * @param ontologyInd
	 * @throws Exception
	 */
	public OntologyBean fillInOntologyBeanFromInstance(OntologyBean ob,
			OWLIndividual ontologyInd) throws Exception;

	/**
	 * Periodically checks whether Protege server is alive
	 */
	public void pingProtegeServer();
}
