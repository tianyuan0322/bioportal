package org.ncbo.stanford.manager.retrieval;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;

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
	public ClassBean findConcept(VNcboOntology ontologyVersion, String conceptId)
			throws Exception;

	public ClassBean findRootConcept(VNcboOntology ontology) throws Exception;

	public ClassBean findPathFromRoot(VNcboOntology ontology, String conceptId,
			boolean light) throws Exception;
}
