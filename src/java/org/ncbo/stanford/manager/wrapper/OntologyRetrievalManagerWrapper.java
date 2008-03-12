package org.ncbo.stanford.manager.wrapper;

import java.util.ArrayList;

import org.ncbo.stanford.bean.concept.ClassBean;

public interface OntologyRetrievalManagerWrapper {

	
	
	
	
	public ClassBean findConcept(Integer ontologyId, String conceptID) throws Exception;
	
	public ArrayList<ClassBean> findRootConcept(Integer ontologyId) throws Exception;
	
}
