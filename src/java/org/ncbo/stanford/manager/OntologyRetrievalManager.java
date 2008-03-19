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
	public ClassBean findRootConcept(Integer ontologyId) throws Exception;
	
	public ClassBean findConcept(Integer ontologyId, String id) throws Exception;

	public ArrayList<ClassBean> findPathToRoot(Integer ontologyId, String conceptId) throws Exception;

	public ArrayList<ClassBean> findParent(Integer ontologyId, String conceptId) throws Exception;

	public ArrayList<ClassBean> findChildren(Integer ontologyId, String conceptId) throws Exception;

	public ArrayList<ClassBean> findConceptNameExact(ArrayList<Integer> ontologyIds, String query
			) ;

	public ArrayList<ClassBean> findConceptNameStartsWith(ArrayList<Integer> ontologyIds, String query) ;

	public ArrayList<ClassBean> findConceptNameContains(ArrayList<Integer> ontologyIds, String query);

	public ArrayList<ClassBean> findConceptPropertyExact(ArrayList<Integer> ontologyIds, String property,
			String query) ;

	public ArrayList<ClassBean> findConceptPropertyStartsWith(ArrayList<Integer> ontologyIds,
			String property, String query) ;

	public ArrayList<ClassBean> findConceptPropertyContains(ArrayList<Integer> ontologyIds, String property,
			String query);
}
