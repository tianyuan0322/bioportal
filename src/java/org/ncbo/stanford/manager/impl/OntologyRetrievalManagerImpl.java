package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.OntologyRetrievalManager;
import org.ncbo.stanford.manager.wrapper.OntologyLoadManagerWrapper;
import org.ncbo.stanford.manager.wrapper.OntologyRetrievalManagerWrapper;

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
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>();
	private Map<String, OntologyRetrievalManagerWrapper> ontologyRetrievalHandlerMap = new HashMap<String, OntologyRetrievalManagerWrapper>();

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
	public ClassBean findRootConcept(Integer ontologyId) {
		
		NcboOntology ontology = ncboOntologyVersionDAO.findOntologyVersion(ontologyId);
		String formatHandler = ontologyFormatHandlerMap.get(ontology.getFormat());		
		OntologyRetrievalManagerWrapper wrapper = ontologyRetrievalHandlerMap.get(formatHandler);
		return wrapper.findRootConcept(ontologyId);		
	}

	public ClassBean findConcept(String conceptID, Integer ontologyId) {
		NcboOntology ontology = ncboOntologyVersionDAO.findOntologyVersion(ontologyId);
		OntologyRetrievalManagerWrapper wrapper = ontologyRetrievalHandlerMap.get(ontology.getFormat());
		return wrapper.findConcept(conceptID,ontologyId);	
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

	public ArrayList<ClassBean> findConceptPropertyStartsWith(String property,
			String query, ArrayList<Integer> ontologyIds) {
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
	 * @return the ontologyRetrievalHandlerMap
	 */
	public Map<String, OntologyRetrievalManagerWrapper> getOntologyRetrievalHandlerMap() {
		return ontologyRetrievalHandlerMap;
	}

	/**
	 * @param ontologyRetrievalHandlerMap the ontologyRetrievalHandlerMap to set
	 */
	public void setOntologyRetrievalHandlerMap(
			Map<String, OntologyRetrievalManagerWrapper> ontologyRetrievalHandlerMap) {
		this.ontologyRetrievalHandlerMap = ontologyRetrievalHandlerMap;
	}
	
	/**
	 * @return the ontologyFormatHandlerMap
	 */
	public Map<String, String> getOntologyFormatHandlerMap() {
		return ontologyFormatHandlerMap;
	}

	/**
	 * @param ontologyFormatHandlerMap
	 *            the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}

	//
	// Private methods
	//
}
