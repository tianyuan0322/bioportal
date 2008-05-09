package org.ncbo.stanford.manager.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;

public interface OntologyRetrievalManagerWrapper {

	
	
	
	
	public ClassBean findConcept(NcboOntology ontologyVersion, String conceptID) throws Exception;
	
	public ClassBean findRootConcept(NcboOntology ontologyVersion) throws Exception;
	
	public List<SearchResultBean> findConceptNameContains(List<NcboOntology> ontologyVersions,
			String query, boolean includeObsolete, int maxToReturn);
	
	public List<SearchResultBean> findConceptNameExact(List<NcboOntology> ontologyVersions,
			String query, boolean includeObsolete, int maxToReturn);
	
	public List<SearchResultBean> findConceptNameStartsWith(List<NcboOntology> ontologyVersions,
			String query, boolean includeObsolete, int maxToReturn);
}
