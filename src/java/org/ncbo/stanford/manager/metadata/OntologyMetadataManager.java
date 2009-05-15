package org.ncbo.stanford.manager.metadata;

import java.util.List;

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
	 * Saves the ontology metadata specified by the ontologyBean representing an ontology version.
	 * 
	 * @param ontologyBean
	 * @throws Exception
	 */
	public void saveOntology(OntologyBean ontologyBean) throws Exception;

//	/**
//	 * Saves the ontology metadata specified by the ontologyBean representing a virtual ontology.
//	 * 
//	 * @param ontologyBean
//	 * @throws Exception
//	 */
//	public void saveVirtualOntology(OntologyBean ontologyBean) throws Exception;
	
	/**
	 * Retrieves the ontologyBean representing an ontology version for a specific ontology version id.
	 * 
	 * @param ontologyVersionId an ontology version id
	 */
	public OntologyBean findOntologyById(Integer ontologyVersionId);

	/**
	 * Retrieves the ontologyBean representing the latest version for a specific ontology id.
	 * 
	 * @param ontologyId a (virtual) ontology id
	 */
	public OntologyBean findLatestOntologyVersionById(Integer ontologyId);
	
	/**
	 * Returns the list of ontologyBeans, one for each ontology's latest version.
	 *  
	 * @return the list of ontology beans corresponding to the latest version of each virtual ontology
	 */
	public List<OntologyBean> findLatestOntologyVersions();

	/**
	 * Retrieves the ontologyBean representing the latest active version for a specific ontology id.
	 * 
	 * @param ontologyId a (virtual) ontology id
	 */
	public OntologyBean findLatestActiveOntologyVersionById(Integer ontologyId);
	
	/**
	 * Returns the list of ontologyBeans, one for each ontology's latest active version.
	 *  
	 * @return the list of ontology beans corresponding to the latest active version of each virtual ontology
	 */
	public List<OntologyBean> findLatestActiveOntologyVersions();
	
	/**
	 * Returns the list of ontologyBeans, one for each version of a specific virtual ontology.
	 *  
	 * @param ontologyId a (virtual) ontology id
	 * @return the list of ontology beans corresponding to the versions of the virtual ontology with ID <code>ontologyId</code>
	 * @throws Exception 
	 */
	public List<OntologyBean> findAllOntologyVersionsById(Integer ontologyId) throws Exception;

	
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
}
