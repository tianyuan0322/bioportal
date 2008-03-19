package org.ncbo.stanford.manager.wrapper;

import java.util.ArrayList;

import org.ncbo.stanford.bean.concept.ClassBean;

public interface OntologyRetrievalManagerWrapper {

	
	
	
	
	public ClassBean findConcept(Integer ontologyVersionId, String conceptID) throws Exception;
	
	public ClassBean findRootConcept(Integer ontologyVersionId) throws Exception;
	
}
