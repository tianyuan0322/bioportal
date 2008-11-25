/**
 * 
 */
package org.ncbo.stanford.service.concept.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.service.concept.ConceptService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Nick Griffith
 * @author Michael Dorf
 * 
 */
@Transactional(readOnly = true)
public class ConceptServiceImpl implements ConceptService {

	private static final Log log = LogFactory.getLog(ConceptServiceImpl.class);
	private static int MAX_RESULTS_PER_ONTOLOGY = 100;

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	private Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap = new HashMap<String, OntologyRetrievalManager>(
			0);

	/**
	 * Get the root concept for the specified ontology.
	 */
	public ClassBean findRootConcept(Integer ontologyVersionId)
			throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);

		return getRetrievalManager(ontology).findRootConcept(ontology);
	}

	public ClassBean findConcept(Integer ontologyVersionId, String conceptId)
			throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);

		return getRetrievalManager(ontology).findConcept(ontology, conceptId);
	}

	public ClassBean findPathFromRoot(Integer ontologyVersionId,
			String conceptId, boolean light) throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);

		return getRetrievalManager(ontology).findPathFromRoot(ontology,
				conceptId, light);
	}

	/*
	 * public List<SearchResultBean> findConceptNameExact( List<Integer>
	 * ontologyVersionIds, String query) { List<SearchResultBean> searchResults =
	 * new ArrayList<SearchResultBean>(); List<VNcboOntology> ontologies =
	 * null;
	 * 
	 * if (ontologyVersionIds.isEmpty()) { ontologies =
	 * ncboOntologyVersionDAO.findLatestOntologyVersions(); } else { ontologies =
	 * ncboOntologyVersionDAO .findOntologyVersions(ontologyVersionIds); }
	 * 
	 * HashMap<String, List<VNcboOntology>> formatLists =
	 * getFormatLists(ontologies);
	 * 
	 * for (String formatHandler : formatLists.keySet()) {
	 * OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
	 * .get(formatHandler); searchResults
	 * .addAll(manager.findConceptNameExact(formatLists .get(formatHandler),
	 * query, true, MAX_RESULTS_PER_ONTOLOGY)); }
	 * 
	 * return searchResults; }
	 * 
	 * public List<SearchResultBean> findConceptNameStartsWith( List<Integer>
	 * ontologyVersionIds, String query) { List<SearchResultBean> searchResults =
	 * new ArrayList<SearchResultBean>(); List<VNcboOntology> ontologies =
	 * null;
	 * 
	 * if (ontologyVersionIds.isEmpty()) { ontologies =
	 * ncboOntologyVersionDAO.findLatestOntologyVersions(); } else { ontologies =
	 * ncboOntologyVersionDAO .findOntologyVersions(ontologyVersionIds); }
	 * 
	 * HashMap<String, List<VNcboOntology>> formatLists =
	 * getFormatLists(ontologies);
	 * 
	 * for (String formatHandler : formatLists.keySet()) {
	 * OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
	 * .get(formatHandler); searchResults
	 * .addAll(manager.findConceptNameStartsWith(formatLists
	 * .get(formatHandler), query, true, MAX_RESULTS_PER_ONTOLOGY)); }
	 * 
	 * return searchResults; }
	 * 
	 * public List<SearchResultBean> findConceptNameContains( List<Integer>
	 * ontologyVersionIds, String query) { List<SearchResultBean> searchResults =
	 * new ArrayList<SearchResultBean>(); List<VNcboOntology> ontologies =
	 * null;
	 * 
	 * if (ontologyVersionIds.isEmpty()) { ontologies =
	 * ncboOntologyVersionDAO.findLatestOntologyVersions(); } else { ontologies =
	 * ncboOntologyVersionDAO .findOntologyVersions(ontologyVersionIds); }
	 * 
	 * HashMap<String, List<VNcboOntology>> formatLists =
	 * getFormatLists(ontologies);
	 * 
	 * for (String formatHandler : formatLists.keySet()) {
	 * OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
	 * .get(formatHandler);
	 * searchResults.addAll(manager.findConceptNameContains(formatLists
	 * .get(formatHandler), query, true, MAX_RESULTS_PER_ONTOLOGY)
	 *  ); }
	 * 
	 * return searchResults; }
	 * 
	 * public List<SearchResultBean> findConceptPropertyExact( List<Integer>
	 * ontologyVersionIds, String property, String query) { throw new
	 * UnsupportedOperationException(); }
	 * 
	 * public List<SearchResultBean> findConceptPropertyStartsWith( List<Integer>
	 * ontologyIds, String property, String query) { throw new
	 * UnsupportedOperationException(); }
	 * 
	 * public List<SearchResultBean> findConceptPropertyContains( List<Integer>
	 * ontologyVersionIds, String query) { List<SearchResultBean> searchResults =
	 * new ArrayList<SearchResultBean>(); HashMap<String, List<VNcboOntology>>
	 * formatLists = new HashMap<String, List<VNcboOntology>>();
	 * 
	 * for (String key : ontologyFormatHandlerMap.values()) {
	 * formatLists.put(key, new ArrayList<VNcboOntology>()); }
	 * 
	 * List<VNcboOntology> ontologies = new ArrayList<VNcboOntology>();
	 * 
	 * if (ontologyVersionIds.isEmpty()) { ontologies =
	 * ncboOntologyVersionDAO.findLatestOntologyVersions(); } else { ontologies =
	 * ncboOntologyVersionDAO .findOntologyVersions(ontologyVersionIds); }
	 * 
	 * for (VNcboOntology ontology : ontologies) { if
	 * (ontology.getStatusId().equals( StatusEnum.STATUS_READY.getStatus())) {
	 * String formatHandler = ontologyFormatHandlerMap.get(ontology
	 * .getFormat()); ((List<VNcboOntology>) formatLists.get(formatHandler))
	 * .add(ontology); } }
	 * 
	 * for (String formatHandler : formatLists.keySet()) {
	 * OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
	 * .get(formatHandler);
	 * searchResults.addAll(manager.findConceptPropertyContains(
	 * formatLists.get(formatHandler), query, true, MAX_RESULTS_PER_ONTOLOGY)); }
	 * 
	 * return searchResults; }
	 */
	private HashMap<String, List<VNcboOntology>> getFormatLists(
			List<VNcboOntology> ontologies) {
		HashMap<String, List<VNcboOntology>> formatLists = new HashMap<String, List<VNcboOntology>>();

		for (String key : ontologyRetrievalHandlerMap.keySet()) {
			formatLists.put(key, new ArrayList<VNcboOntology>());
		}

		for (VNcboOntology ontology : ontologies) {
			if (ontology.getStatusId().equals(
					StatusEnum.STATUS_READY.getStatus())) {
				String handler = ontologyFormatHandlerMap.get(ontology
						.getFormat());
				formatLists.get(handler).add(ontology);
			}
		}

		return formatLists;
	}

	private OntologyRetrievalManager getRetrievalManager(VNcboOntology ontology) {
		String formatHandler = ontologyFormatHandlerMap.get(ontology
				.getFormat());
		return ontologyRetrievalHandlerMap.get(formatHandler);
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
	 * @param ontologyRetrievalHandlerMap
	 *            the ontologyRetrievalHandlerMap to set
	 */
	public void setOntologyRetrievalHandlerMap(
			Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap) {
		this.ontologyRetrievalHandlerMap = ontologyRetrievalHandlerMap;
	}

	/**
	 * @param ontologyFormatHandlerMap
	 *            the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}
}
