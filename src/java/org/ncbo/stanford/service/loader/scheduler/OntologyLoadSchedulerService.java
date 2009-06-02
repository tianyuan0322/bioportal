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
	 * Parses given ontologies
	 * 
	 * @param ontologyVersionIdList
	 * @param formatHandler
	 */
	public void parseOntologies(List<Integer> ontologyVersionIdList,
			String formatHandler);

	/**
	 * Returns info about ontologies that did not process successfully
	 * 
	 * @return
	 */
	public List<String> getErrorOntologies();
}
