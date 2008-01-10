package org.ncbo.stanford.manager;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;

/**
 * An interface designed to provide an abstraction layer to ontology and concept
 * operations. The service layer will consume this interface instead of directly
 * calling a specific implementation (i.e. LexGrid, Protege etc.). This
 * interface should be used by internal services and should not be exposed to
 * upper layers.
 * 
 * @author Michael Dorf
 * 
 */
public interface OntologyAndConceptManager {

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return
	 */
	public List<OntologyBean> findLatestOntologyVersions();
	
	public OntologyBean findOntology(Integer id);

	public void loadOntology(OntologyBean ontology);
}
