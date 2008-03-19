package org.ncbo.stanford.manager.wrapper;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;

public interface OntologyRetrievalManagerWrapper {

	
	
	
	
	public ClassBean findConcept(NcboOntology ontologyVersion, String conceptID) throws Exception;
	
	public ClassBean findRootConcept(NcboOntology ontologyVersion) throws Exception;
	
}
