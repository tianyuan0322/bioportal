package org.ncbo.stanford.service.ontology;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;

/**
 * Exposes services that provide read and write operations on ontologies
 * 
 * @author Michael Dorf
 * @author Nick Griffith
 */
public interface OntologyService {

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findLatestOntologyVersions();
	
	/**
	 * Searches Ontology Metadata
	 * 
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> searchOntologyMetadata(String query);	

	/**
	 * Returns a single ontology version record
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public OntologyBean findOntology(Integer ontologyVersionId);

	/**
	 * Finds the latest version of a given ontology
	 * 
	 * @param ontologyId
	 * @return
	 */
	public OntologyBean findLatestOntologyVersion(Integer ontologyId);
	
	/**
	 * Find all versions of the given ontology in the system
	 * 
	 * @param ontologyId
	 * @return
	 */
	public List<OntologyBean> findAllOntologyVersions(Integer ontologyId);

	/**
	 * Find ontology properties, such as "definition", "synonyms", "cui"...
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public List<String> findProperties(Integer ontologyVersionId);
	
	/**	
	 * Create an ontology 
	 * 	  
	 * @param ontologyBean
	 * @return
	 */
	public void createOntology(OntologyBean ontologyBean);
	
	/**	
	 * Update an ontology 
	 * 	  
	 * @param ontologyBean
	 * @return
	 */
	public void updateOntology(OntologyBean ontologyBean);
	
	/**	
	 * Update an ontology Category
	 * 	  
	 * @param ontologyBean
	 * @return
	 */
	public void cleanupOntologyCategory(OntologyBean ontologyBean, List<Integer> oldCategoryIds);
	
	/**	
	 * Delete an ontology 
	 * 	  
	 * @param ontologyBean
	 * @return
	 */
	public void deleteOntology(OntologyBean ontologyBean);
}
