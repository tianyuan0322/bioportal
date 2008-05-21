package org.ncbo.stanford.service.loader.scheduler;

/**
 * Interface to process (parse) an already loaded ontology using vendor API
 * (LexGrid, Protege etc.)
 * 
 * @author Michael Dorf
 * 
 */
public interface OntologyLoadSchedulerService {

	/**
	 * Retrieve all ontologies that need to be parsed and send them through the
	 * vendor API
	 */
	public void parseOntologies();
	
	/**
	 * Parses a single ontology
	 * 
	 * @param ontologyVersionId
	 */
	public void parseOntology(Integer ontologyVersionId);
}
