package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.OntologyRetrievalManager;
import org.ncbo.stanford.manager.wrapper.AbstractOntologyManagerWrapperLexGrid;
import org.ncbo.stanford.manager.wrapper.AbstractOntologyManagerWrapperProtege;

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
public class OntologyRetrievalManagerImpl implements OntologyRetrievalManager {

	private static final Log log = LogFactory
			.getLog(OntologyRetrievalManagerImpl.class);

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private AbstractOntologyManagerWrapperLexGrid ontologyRetrievalManagerWrapperLexGrid;
	private AbstractOntologyManagerWrapperProtege ontologyRetrievalManagerWrapperProtege;	

	
	/**
	 * Default Constructor
	 */
	public OntologyRetrievalManagerImpl() {
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


	//
	// Concept methods
	//

	/**
	 * Get the root concept for the specified ontology.
	 */
	public ClassBean getRootConcept(Integer ontologyId) {
		return null;
	}

	public ClassBean findConcept(String conceptID, Integer ontologyId) {
		return null;
	}

	public ArrayList<ClassBean> findPathToRoot(String id, Integer ontologyId) {
		return new ArrayList();
	}

	public ClassBean findParent(String id, Integer ontologyId) {
		return new ClassBean();
	}

	public ArrayList<ClassBean> findChildren(String id, Integer ontologyId) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptNameExact(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptNameStartsWith(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptNameContains(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptPropertyExact(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptPropertyStartsWith(
			String property, String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptPropertyContains(String property,
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
	 * @return the ontologyRetrievalManagerWrapperLexGrid
	 */
	public AbstractOntologyManagerWrapperLexGrid getOntologyRetrievalManagerWrapperLexGrid() {
		return ontologyRetrievalManagerWrapperLexGrid;
	}

	/**
	 * @param ontologyRetrievalManagerWrapperLexGrid the ontologyRetrievalManagerWrapperLexGrid to set
	 */
	public void setOntologyRetrievalManagerWrapperLexGrid(
			AbstractOntologyManagerWrapperLexGrid ontologyRetrievalManagerWrapperLexGrid) {
		this.ontologyRetrievalManagerWrapperLexGrid = ontologyRetrievalManagerWrapperLexGrid;
	}

	/**
	 * @return the ontologyRetrievalManagerWrapperProtege
	 */
	public AbstractOntologyManagerWrapperProtege getOntologyRetrievalManagerWrapperProtege() {
		return ontologyRetrievalManagerWrapperProtege;
	}

	/**
	 * @param ontologyRetrievalManagerWrapperProtege the ontologyRetrievalManagerWrapperProtege to set
	 */
	public void setOntologyRetrievalManagerWrapperProtege(
			AbstractOntologyManagerWrapperProtege ontologyRetrievalManagerWrapperProtege) {
		this.ontologyRetrievalManagerWrapperProtege = ontologyRetrievalManagerWrapperProtege;
	}

	//
	// Private methods
	//
}
