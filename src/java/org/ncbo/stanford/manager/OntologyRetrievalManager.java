package org.ncbo.stanford.manager;

import java.util.List;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;

/**
 * An interface designed to provide an abstraction layer to ontology and concept
 * operations. The service layer will consume this interface instead of directly
 * calling a specific implementation (i.e. LexGrid, Protege etc.). This
 * interface should be used by internal services and should not be exposed to
 * upper layers.
 * 
 * NOTE(bdai): It appears that all ontology and concept methods should be
 * included int this class. However, it may be worth it to refactor into two
 * classes if it becomes too big.
 * 
 * @author Michael Dorf
 * 
 */
public interface OntologyRetrievalManager {
	public ClassBean findConcept(NcboOntology ontologyVersion, String conceptId)
			throws Exception;

	public ClassBean findRootConcept(NcboOntology ontologyVersion)
			throws Exception;

	public List<SearchResultBean> findConceptNameContains(
			List<NcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn);

	public List<SearchResultBean> findConceptNameExact(
			List<NcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn);

	public List<SearchResultBean> findConceptNameStartsWith(
			List<NcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn);
	
	public ClassBean findPathToRoot(NcboOntology ontologyVersion, String conceptId) throws Exception;
}
