package org.ncbo.stanford.manager.metadata;

import java.util.List;

import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyViewBean;

/**
 * An interface for all API specific ontology view metadata managers to conform to. This allows
 * for a seamless Spring injection.
 * 
 * @author Csongor Nyulas
 * 
 */
/**
 * @author csnyulas
 *
 */
public interface OntologyViewMetadataManager {

	/**
	 * Saves the ontology view metadata specified by the ontologyViewBean representing an ontology view version.
	 * 
	 * @param ontologyViewBean
	 * @throws Exception
	 */
	public void saveOntologyView(OntologyViewBean ontologyViewBean) throws Exception;


	/**
	 * Updates the ontology view metadata specified by the ontologyViewBean representing an ontology view version.
	 * 
	 * @param ontologyViewBean
	 * @throws Exception
	 */
	public void updateOntologyView(OntologyViewBean ontologyViewBean) throws Exception;

	/**
	 * Updates the metadata of an ontology view version specified by the ontologyViewBean 
	 * with ontology metrics information specified by metricsBean.
	 * 
	 * @param ontologyViewBean
	 * @param metricsBean
	 * @throws Exception
	 */
	public void updateOntologyViewMetrics(OntologyViewBean ontologyViewBean, OntologyMetricsBean metricsBean) throws Exception;
	
	/**
	 * Deletes the ontology view metadata specified by the ontologyViewBean representing an ontology view version.
	 * 
	 * @param ontologyViewBean
	 * @throws Exception
	 */
	public void deleteOntologyView(OntologyViewBean ontologyViewBean) throws Exception;
	
	/**
	 * Retrieves the ontologyViewBean representing an ontology view version for a specific ontology view version id.
	 * 
	 * @param ontologyViewVersionId an ontology view version id
	 */
	public OntologyViewBean findOntologyViewById(Integer ontologyViewVersionId);

	/**
	 * Retrieves the ontologyViewBean representing the latest version for a specific virtual view id.
	 * 
	 * @param viewId a (virtual) view id
	 */
	public OntologyViewBean findLatestOntologyViewVersionById(Integer viewId);
	
	/**
	 * Returns the list of ontologyViewBeans, one for each ontology view's latest version.
	 *  
	 * @return the list of ontology view beans corresponding to the latest version of each virtual view
	 */
	public List<OntologyViewBean> findLatestOntologyViewVersions();

	/**
	 * Retrieves the ontologyViewBean representing the latest active version for a specific virtual view id.
	 * 
	 * @param viewId a (virtual) view id
	 */
	public OntologyViewBean findLatestActiveOntologyViewVersionById(Integer viewId);
	
	/**
	 * Returns the list of ontologyViewBeans, one for each ontology view's latest active version.
	 *  
	 * @return the list of ontology view beans corresponding to the latest active version of each virtual view
	 */
	public List<OntologyViewBean> findLatestActiveOntologyViewVersions();
	
	/**
	 * Returns the list of ontologyViewBeans, one for each version of a specific virtual view.
	 *  
	 * @param viewId a virtual view id
	 * @return the list of ontology view beans corresponding to the versions of the virtual view with ID <code>viewId</code>
	 * @throws Exception 
	 */
	public List<OntologyViewBean> findAllOntologyViewVersionsById(Integer viewId) throws Exception;

	/**
	 * Returns the next available (i.e. not already in use) virtual view ID
	 * 
	 * @return
	 */
	public int getNextAvailableVirtualViewId();
	
	/**
	 * Returns the next available (i.e. not already in use) ontology view version ID
	 * 
	 * @return
	 */
	public int getNextAvailableOntologyViewVersionId();

	/**
	 * Returns list of ontology view beans for all ontology view versions that have some 
	 * properties that matches the argument
	 * 
	 * @param query a String that is searched for in the metadata
	 * @return list of matching ontology view beans
	 */
	public List<OntologyViewBean> searchOntologyViewMetadata(String query);
}
