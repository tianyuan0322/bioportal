package org.ncbo.stanford.service.loader.scheduler;

import java.util.List;

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
	public void parseOntology(String ontologyVersionId);
	
	/**
	 * Parses all ontologies in given range
	 * 
	 * @param ontologyVersionId
	 */
	public void parseOntologies(String startId, String endId);
	
	public List<Integer> getErrorIdList();
}
