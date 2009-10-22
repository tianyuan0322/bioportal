package org.ncbo.stanford.manager.retrieval;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;

/**
 * An interface designed to provide an abstraction layer to ontology and concept
 * operations. The service layer will consume this interface instead of directly
 * calling a specific implementation (i.e. LexGrid, Protege etc.). This
 * interface should be used by internal services and should not be exposed to
 * upper layers.
 * 
 * @author Michael Dorf
 */
public interface OntologyRetrievalManager {
	public ClassBean findConcept(OntologyBean ob, String conceptId,
			boolean light) throws Exception;

	public ClassBean findRootConcept(OntologyBean ob, boolean light)
			throws Exception;

	public ClassBean findPathFromRoot(OntologyBean ob, String conceptId,
			boolean light) throws Exception;

	public boolean hasParent(OntologyBean ob, String childConceptId,
			String parentConceptId) throws Exception;
}
