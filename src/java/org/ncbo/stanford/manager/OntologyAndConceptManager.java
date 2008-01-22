package org.ncbo.stanford.manager;

import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.bean.ConceptBean;
import org.ncbo.stanford.bean.OntologyBean;

/**
 * An interface designed to provide an abstraction layer to ontology and concept
 * operations. The service layer will consume this interface instead of directly
 * calling a specific implementation (i.e. LexGrid, Protege etc.). This
 * interface should be used by internal services and should not be exposed to
 * upper layers.
 * 
 * NOTE(bdai):  It appears that all ontology and concept methods should be included int this
 * class. However, it may be worth it to refactor into two classes if it becomes
 * too big. 
 * 
 * @author Michael Dorf
 * 
 */
public interface OntologyAndConceptManager {

	//
	// Ontology methods
	//
	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return
	 */
	public List<OntologyBean> findLatestOntologyVersions();

	public OntologyBean findOntology(Integer id);

	public List<OntologyBean> findOntologyVersions(Integer id);

	public OntologyBean findOntology(Integer id, String version);

	public List<String> findProperties(Integer id);

	/**
	 * Loads the specified ontology into the BioPortal repository.
	 */
	public void loadOntology(OntologyBean ontology);

	// 
	// Concept methods
	//
	public ConceptBean getRootConcept(Integer ontologyId);
	
	public ConceptBean findConcept(String id, Integer ontologyId);

	public ArrayList<ConceptBean> findPathToRoot(String id, Integer ontologyId);

	public ConceptBean findParent(String id, Integer ontologyId);

	public ArrayList<ConceptBean> findChildren(String id, Integer ontologyId);

	public ArrayList<ConceptBean> findConceptNameExact(String query,
			ArrayList<Integer> ontologyIds);

	public ArrayList<ConceptBean> findConceptNameStartsWith(String query,
			ArrayList<Integer> ontologyIds);

	public ArrayList<ConceptBean> findConceptNameContains(String query,
			ArrayList<Integer> ontologyIds);

	public ArrayList<ConceptBean> findConceptPropertyExact(String property,
			String query, ArrayList<Integer> ontologyIds);

	public ArrayList<ConceptBean> findConceptPropertyStartsWith(
			String property, String query, ArrayList<Integer> ontologyIds);

	public ArrayList<ConceptBean> findConceptPropertyContains(String property,
			String query, ArrayList<Integer> ontologyIds);
}
