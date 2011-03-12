package org.ncbo.stanford.service.loader.fixer;

public interface OntologyIntegrityFixerService {
	/**
	 * Goes through all latest versions of parsed ontologies and fixes dependent
	 * artifacts such as search index or metrics that may be out of sync with
	 * the latest ontology version
	 */
	public void fixOntologies();
}
