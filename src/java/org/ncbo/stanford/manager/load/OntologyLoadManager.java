package org.ncbo.stanford.manager.load;

import java.net.URI;

import org.ncbo.stanford.bean.OntologyBean;

/**
 * An interface for all API specific load managers to conform to. This allows
 * for a seamless Spring injection.
 * 
 * @author Michael Dorf
 * 
 */
public interface OntologyLoadManager {

	/**
	 * Load an ontology using the vendor API
	 * 
	 * @param ontologyUri
	 * @param ob
	 * @throws Exception
	 */
	public void loadOntology(URI ontologyUri, OntologyBean ob)
			throws Exception;

	/**
	 * Create a Lucene Index for an ontology using the vendor API
	 * 
	 * @param ob
	 */
//	public void indexOntology(OntologyBean ob);
	
	/**
	 * Remove all traces of a given ontology version
	 * 
	 * @param ontologyBean
	 * @throws Exception
	 */
	public void cleanup(OntologyBean ontologyBean) throws Exception;
}
