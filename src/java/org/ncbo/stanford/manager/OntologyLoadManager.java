package org.ncbo.stanford.manager;

import org.ncbo.stanford.bean.OntologyBean;

public interface OntologyLoadManager {
	/**
	 * Loads the specified ontology into the BioPortal repository.
	 */
	public void loadOntology(OntologyBean ontologyBean) throws Exception;

}
