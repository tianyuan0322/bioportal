package org.ncbo.stanford.manager.wrapper;

import org.ncbo.stanford.bean.concept.ClassBean;

public interface OntologyRetrievalManagerWrapper {

	
	
	
	
	public ClassBean findConcept(String conceptID, Integer ontologyId);
	
	public ClassBean findRootConcept(Integer ontologyId);
	
}
