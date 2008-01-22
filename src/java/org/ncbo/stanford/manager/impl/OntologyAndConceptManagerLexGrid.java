package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ConceptBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.OntologyAndConceptManager;
import org.ncbo.stanford.service.ontology.impl.OntologyServiceImpl;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * A default implementation to OntologyAndConceptManager interface designed to
 * provide an abstraction layer to ontology and concept operations. The service
 * layer will consume this interface instead of directly calling a specific
 * implementation (i.e. LexGrid, Protege etc.). Do not use this class directly
 * in upper layers.
 * 
 * NOTE(bdai): This class with all the ontology and concept methods seems to be
 * a bit bloated. Will refactor if it becomes a problem.

 * 
 * @author Benjamin Dai
 * 
 */
public class OntologyAndConceptManagerLexGrid implements OntologyAndConceptManager {

	private static final Log log = LogFactory
			.getLog(OntologyAndConceptManagerLexGrid.class);

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;

	// private OntologyLoaderLexGridImpl lexGridLoader = null;

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return
	 */
	public List<OntologyBean> findLatestOntologyVersions() {
		return new ArrayList();
	}

	/**
	 * Returns the specified ontology TODO: Implement this first - bdai.
	 * 
	 * @param id
	 * @return
	 */

	public OntologyBean findOntology(Integer id) {
		return null;
	}

	public OntologyBean findOntology(Integer id, String version) {
		return null;
	}

	public List<OntologyBean> findOntologyVersions(Integer id) {
		return new ArrayList();
	}

	public List<String> findProperties(Integer id) {
		return new ArrayList();
	}

	/**
	 * Loads the specified ontology into the BioPortal repository.
	 */
	public void loadOntology(OntologyBean ontology) {
	}

	//
	// Concept methods
	//

	/**
	 * Get the root concept for the specified ontology.
	 */
	public ConceptBean getRootConcept(Integer ontologyId) {
		return new ConceptBean();
	}

	

	public ConceptBean findConcept(String conceptID, Integer ontologyId) {
		return new ConceptBean();
	}

	public ArrayList<ConceptBean> findPathToRoot(String id, Integer ontologyId) {
		return new ArrayList();
	}

	public ConceptBean findParent(String id, Integer ontologyId) {
		return new ConceptBean();
	}

	public ArrayList<ConceptBean> findChildren(String id, Integer ontologyId) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptNameExact(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptNameStartsWith(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptNameContains(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptPropertyExact(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptPropertyStartsWith(
			String property, String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptPropertyContains(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	//
	// Non interface methods
	//
	/**
	 * @return the ncboOntologyVersionDAO
	 */
	public CustomNcboOntologyVersionDAO getNcboOntologyVersionDAO() {
		return ncboOntologyVersionDAO;
	}

	/**
	 * @param ncboOntologyVersionDAO
	 *            the ncboOntologyVersionDAO to set
	 */
	public void setNcboOntologyVersionDAO(
			CustomNcboOntologyVersionDAO ncboOntologyVersionDAO) {
		this.ncboOntologyVersionDAO = ncboOntologyVersionDAO;
	}

}
