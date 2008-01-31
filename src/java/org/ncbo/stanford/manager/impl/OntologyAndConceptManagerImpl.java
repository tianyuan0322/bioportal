package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ConceptBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.OntologyAndConceptManager;
import org.ncbo.stanford.manager.wrapper.OntologyAndConceptManagerWrapper;

/**
 * A default implementation to OntologyAndConceptManager interface designed to
 * provide an abstraction layer to ontology and concept operations. The service
 * layer will consume this interface instead of directly calling a specific
 * implementation (i.e. LexGrid, Protege etc.). Do not use this class directly
 * in upper layers.
 * 
 * @author Michael Dorf
 * 
 */
public class OntologyAndConceptManagerImpl implements OntologyAndConceptManager {

	private static final Log log = LogFactory
			.getLog(OntologyAndConceptManagerImpl.class);

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private OntologyAndConceptManagerWrapper ontologyAndConceptManagerWrapperLexGrid;
	private OntologyAndConceptManagerWrapper ontologyAndConceptManagerWrapperProtege;	

	
	/**
	 * Default Constructor
	 */
	public OntologyAndConceptManagerImpl() {
	}

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return
	 */
	public List<OntologyBean> findLatestOntologyVersions() {
		ArrayList<OntologyBean> ontBeanList = new ArrayList<OntologyBean>(1);
		List<NcboOntology> ontEntityList = ncboOntologyVersionDAO
				.findLatestOntologyVersions();

		for (NcboOntology ncboOntology : ontEntityList) {
			OntologyBean ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
			ontBeanList.add(ontologyBean);
		}

		return ontBeanList;
	}

	
	
	
	
	
	
	/**
	 * Returns the specified ontology.
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
		return null;
	}

	public ConceptBean findConcept(String conceptID, Integer ontologyId) {
		return null;
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

	/**
	 * @return the ontologyAndConceptManagerWrapperLexGrid
	 */
	public OntologyAndConceptManagerWrapper getOntologyAndConceptManagerWrapperLexGrid() {
		return ontologyAndConceptManagerWrapperLexGrid;
	}

	/**
	 * @param ontologyAndConceptManagerWrapperLexGrid the ontologyAndConceptManagerWrapperLexGrid to set
	 */
	public void setOntologyAndConceptManagerWrapperLexGrid(
			OntologyAndConceptManagerWrapper ontologyAndConceptManagerWrapperLexGrid) {
		this.ontologyAndConceptManagerWrapperLexGrid = ontologyAndConceptManagerWrapperLexGrid;
	}

	/**
	 * @return the ontologyAndConceptManagerWrapperProtege
	 */
	public OntologyAndConceptManagerWrapper getOntologyAndConceptManagerWrapperProtege() {
		return ontologyAndConceptManagerWrapperProtege;
	}

	/**
	 * @param ontologyAndConceptManagerWrapperProtege the ontologyAndConceptManagerWrapperProtege to set
	 */
	public void setOntologyAndConceptManagerWrapperProtege(
			OntologyAndConceptManagerWrapper ontologyAndConceptManagerWrapperProtege) {
		this.ontologyAndConceptManagerWrapperProtege = ontologyAndConceptManagerWrapperProtege;
	}

	//
	// Private methods
	//

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
