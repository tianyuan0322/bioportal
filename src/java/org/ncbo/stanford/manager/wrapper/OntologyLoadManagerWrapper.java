package org.ncbo.stanford.manager.wrapper;

import java.net.URI;

import org.ncbo.stanford.bean.OntologyBean;

public interface OntologyLoadManagerWrapper {
	
	
	public void loadOntology(URI ontologyUri, OntologyBean ontologyBean) throws Exception;


}
