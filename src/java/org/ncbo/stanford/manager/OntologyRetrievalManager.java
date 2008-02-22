package org.ncbo.stanford.manager;

import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;

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
public interface OntologyRetrievalManager {

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

	// 
	// Concept methods
	//
	public ClassBean getRootConcept(Integer ontologyId);
	
	public ClassBean findConcept(String id, Integer ontologyId);

	public ArrayList<ClassBean> findPathToRoot(String id, Integer ontologyId);

	public ClassBean findParent(String id, Integer ontologyId);

	public ArrayList<ClassBean> findChildren(String id, Integer ontologyId);

	public ArrayList<ClassBean> findConceptNameExact(String query,
			ArrayList<Integer> ontologyIds);

	public ArrayList<ClassBean> findConceptNameStartsWith(String query,
			ArrayList<Integer> ontologyIds);

	public ArrayList<ClassBean> findConceptNameContains(String query,
			ArrayList<Integer> ontologyIds);

	public ArrayList<ClassBean> findConceptPropertyExact(String property,
			String query, ArrayList<Integer> ontologyIds);

	public ArrayList<ClassBean> findConceptPropertyStartsWith(
			String property, String query, ArrayList<Integer> ontologyIds);

	public ArrayList<ClassBean> findConceptPropertyContains(String property,
			String query, ArrayList<Integer> ontologyIds);
}
