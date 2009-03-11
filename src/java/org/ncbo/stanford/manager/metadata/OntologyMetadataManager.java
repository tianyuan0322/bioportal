package org.ncbo.stanford.manager.metadata;

import org.ncbo.stanford.bean.OntologyBean;

/**
 * An interface for all API specific ontology metadata managers to conform to. This allows
 * for a seamless Spring injection.
 * 
 * @author Csongor Nyulas
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
	 * @param ontologyVersionid
	 */
	public OntologyBean findOntologyById(Integer ontologyVersionid);

}
