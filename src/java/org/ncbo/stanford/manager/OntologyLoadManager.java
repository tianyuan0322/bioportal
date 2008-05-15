package org.ncbo.stanford.manager;

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
	 * @param ontologyBean
	 * @throws Exception
	 */
	public void loadOntology(URI ontologyUri, OntologyBean ontologyBean)
			throws Exception;
}
