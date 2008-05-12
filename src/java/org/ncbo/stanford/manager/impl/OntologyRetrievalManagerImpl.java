package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.OntologyRetrievalManager;
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

	private static int MAX_RESULTS = 100;

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
	public ClassBean findRootConcept(Integer ontologyId) throws Exception {
		NcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyId);
		String formatHandler = ontologyFormatHandlerMap.get(ontology.getId()
				.getFormat());
		OntologyRetrievalManagerWrapper wrapper = ontologyRetrievalHandlerMap
				.get(formatHandler);

		return wrapper.findRootConcept(ontology);
	}

	public ClassBean findConcept(Integer ontologyId, String conceptId)
			throws Exception {
		NcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyId);
		String formatHandler = ontologyFormatHandlerMap.get(ontology.getId()
				.getFormat());
		OntologyRetrievalManagerWrapper wrapper = ontologyRetrievalHandlerMap
				.get(formatHandler);

		return wrapper.findConcept(ontology, conceptId);
	}

	public ClassBean findPathToRoot(Integer ontologyId, String conceptId)
			throws Exception {
		return new ClassBean();
	}

	public List<ClassBean> findParent(Integer ontologyId, String conceptId)
			throws Exception {
		return new ArrayList();
	}

	public List<ClassBean> findChildren(Integer ontologyId, String conceptId)
			throws Exception {
		return new ArrayList();
	}

	public List<SearchResultBean> findConceptNameExact(
			List<Integer> ontologyIds, String query) {
		return new ArrayList();
	}

	public List<SearchResultBean> findConceptNameStartsWith(
			List<Integer> ontologyIds, String query) {
		return new ArrayList();
	}

	public List<SearchResultBean> findConceptNameContains(
			List<Integer> ontologyIds, String query) {

		List<SearchResultBean> searchResults = new ArrayList<SearchResultBean>();
		HashMap<String, List<NcboOntology>> formatLists = new HashMap<String, List<NcboOntology>>();
		
		for (String key : ontologyFormatHandlerMap.keySet()) {
			formatLists.put(key, new ArrayList<NcboOntology>());
		}

		for (Integer ontologyId : ontologyIds) {
			NcboOntology ontology = ncboOntologyVersionDAO
					.findOntologyVersion(ontologyId);
			String formatHandler = ontologyFormatHandlerMap.get(ontology
					.getId().getFormat());
			((List<NcboOntology>) formatLists.get(formatHandler)).add(ontology);
		}

		for (String formatHandler : formatLists.keySet()) {
			OntologyRetrievalManagerWrapper wrapper = ontologyRetrievalHandlerMap
					.get(formatHandler);
			searchResults.addAll(wrapper.findConceptNameContains(formatLists
					.get(formatHandler), query, true, MAX_RESULTS));
		}
		
		return searchResults;
	}

	public List<SearchResultBean> findConceptPropertyExact(
			List<Integer> ontologyIds, String property, String query) {
		return new ArrayList();
	}

	public List<SearchResultBean> findConceptPropertyStartsWith(
			List<Integer> ontologyIds, String property, String query) {
		return new ArrayList();
	}

	public List<SearchResultBean> findConceptPropertyContains(
			List<Integer> ontologyIds, String property, String query) {
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
	 * @param ontologyRetrievalHandlerMap
	 *            the ontologyRetrievalHandlerMap to set
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
