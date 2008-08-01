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
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.manager.OntologyRetrievalManager;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Nick Griffith
 * 
 * 
 */
@Transactional(readOnly = true)
public class ConceptServiceImpl implements ConceptService {
	
	private static final Log log = LogFactory.getLog(ConceptServiceImpl.class);
	
	private static int MAX_RESULTS = 100;

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>();
	private Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap = new HashMap<String, OntologyRetrievalManager>();
	
	//private OntologyRetrievalManager ontologyRetrievalManager;
	
	// TODO - validate this
	// This is identical to findRootConcept
	// REMOVE THIS FROM INTERFACE?????
	public ClassBean findRoot(Integer ontologyId) throws Exception { 
		return findRootConcept(ontologyId); 
	}
	
	/**
	 * Get the root concept for the specified ontology.
	 */
	public ClassBean findRootConcept(Integer ontologyId) throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyId);
		String formatHandler = ontologyFormatHandlerMap.get(ontology
				.getFormat());		
		OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
				.get(formatHandler);

		return manager.findRootConcept(ontology);
	}

	
	public ClassBean findConcept(Integer ontologyId, String conceptId)
			throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyId);
		String formatHandler = ontologyFormatHandlerMap.get(ontology
				.getFormat());
		OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
				.get(formatHandler);

		return manager.findConcept(ontology, conceptId);
	}

	public ClassBean findPathFromRoot(Integer ontologyId, String conceptId,boolean light)
			throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
		.findOntologyVersion(ontologyId);
		String formatHandler = ontologyFormatHandlerMap.get(ontology
			.getFormat());
		OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
			.get(formatHandler);

		return manager.findPathFromRoot(ontology, conceptId,light);
	}

	/*
	public List<ClassBean> findParent(Integer ontologyId, String conceptId)
			throws Exception {
		return new ArrayList();
	}

	public List<ClassBean> findChildren(Integer ontologyId, String conceptId)
			throws Exception {
		return new ArrayList();
	}
	*/

	public List<SearchResultBean> findConceptNameExact(
			List<Integer> ontologyIds, String query) {
		List<SearchResultBean> searchResults = new ArrayList<SearchResultBean>();
		HashMap<String, List<VNcboOntology>> formatLists = new HashMap<String, List<VNcboOntology>>();
		
		for (String key : ontologyFormatHandlerMap.keySet()) {
			formatLists.put(key, new ArrayList<VNcboOntology>());
		}
		
		List<VNcboOntology> ontologies = new ArrayList<VNcboOntology>();
		
		if(ontologyIds.isEmpty()){
			ontologies = ncboOntologyVersionDAO.findLatestOntologyVersions();
		}else{
			ontologies = ncboOntologyVersionDAO
		.findOntologyVersions(ontologyIds);
		}
		
		for (VNcboOntology ontology : ontologies) {
			if(ontology.getStatusId().equals(StatusEnum.STATUS_READY.getStatus())){
				String formatHandler = ontologyFormatHandlerMap.get(ontology
						.getFormat());
				
				System.out.println("Looking For "+formatHandler);
				((List<VNcboOntology>) formatLists.get(formatHandler)).add(ontology);
			}
		}

		for (String formatHandler : formatLists.keySet()) {
			OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
					.get(formatHandler);
			searchResults.addAll(manager.findConceptNameExact(formatLists
					.get(formatHandler), query, true, MAX_RESULTS));
		}
		
		return searchResults;
	}

	public List<SearchResultBean> findConceptNameStartsWith(
			List<Integer> ontologyIds, String query) {
		List<SearchResultBean> searchResults = new ArrayList<SearchResultBean>();
		HashMap<String, List<VNcboOntology>> formatLists = new HashMap<String, List<VNcboOntology>>();
		
		for (String key : ontologyFormatHandlerMap.keySet()) {
			formatLists.put(key, new ArrayList<VNcboOntology>());
		}
		
		List<VNcboOntology> ontologies = new ArrayList<VNcboOntology>();
		
		if(ontologyIds.isEmpty()){
			ontologies = ncboOntologyVersionDAO.findLatestOntologyVersions();
		}else{
			ontologies = ncboOntologyVersionDAO
		.findOntologyVersions(ontologyIds);
		}
		for (VNcboOntology ontology : ontologies) {
			if(ontology.getStatusId().equals(StatusEnum.STATUS_READY.getStatus())){
				String formatHandler = ontologyFormatHandlerMap.get(ontology
						.getFormat());
				
				System.out.println("Looking For "+formatHandler);
				((List<VNcboOntology>) formatLists.get(formatHandler)).add(ontology);
			}
		}

		for (String formatHandler : formatLists.keySet()) {
			OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
					.get(formatHandler);
			searchResults.addAll(manager.findConceptNameStartsWith(formatLists
					.get(formatHandler), query, true, MAX_RESULTS));
		}
		
		return searchResults;
	}

	public List<SearchResultBean> findConceptNameContains(
			List<Integer> ontologyIds, String query) {

		List<SearchResultBean> searchResults = new ArrayList<SearchResultBean>();
		HashMap<String, List<VNcboOntology>> formatLists = new HashMap<String, List<VNcboOntology>>();
		
		for (String key : ontologyFormatHandlerMap.values()) {
			System.out.println("Key is "+key);
			formatLists.put(key, new ArrayList<VNcboOntology>());
		}

		List<VNcboOntology> ontologies = new ArrayList<VNcboOntology>();
		
		if(ontologyIds.isEmpty()){
			ontologies = ncboOntologyVersionDAO.findLatestOntologyVersions();
		}else{
			ontologies = ncboOntologyVersionDAO
		.findOntologyVersions(ontologyIds);
		}
		
		for (VNcboOntology ontology : ontologies) {
			if(ontology.getStatusId().equals(StatusEnum.STATUS_READY.getStatus())){
			String formatHandler = ontologyFormatHandlerMap.get(ontology
					.getFormat());
			
			System.out.println("Looking For "+formatHandler);
			((List<VNcboOntology>) formatLists.get(formatHandler)).add(ontology);
			}
		}

		for (String formatHandler : formatLists.keySet()) {
			OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
					.get(formatHandler);
			searchResults.addAll(manager.findConceptNameContains(formatLists
					.get(formatHandler), query, true, MAX_RESULTS));
		}
		
		return searchResults;
	}

	public List<SearchResultBean> findConceptPropertyExact(
			List<Integer> ontologyIds, String property, String query) {
		throw new UnsupportedOperationException();
	}

	public List<SearchResultBean> findConceptPropertyStartsWith(
			List<Integer> ontologyIds, String property, String query) {
		throw new UnsupportedOperationException();
	}

	public List<SearchResultBean> findConceptPropertyContains(
			List<Integer> ontologyIds,  String query) {
		List<SearchResultBean> searchResults = new ArrayList<SearchResultBean>();
		HashMap<String, List<VNcboOntology>> formatLists = new HashMap<String, List<VNcboOntology>>();
		
		for (String key : ontologyFormatHandlerMap.values()) {
			System.out.println("Key is "+key);
			formatLists.put(key, new ArrayList<VNcboOntology>());
		}

		List<VNcboOntology> ontologies = new ArrayList<VNcboOntology>();
		
		if(ontologyIds.isEmpty()){
			ontologies = ncboOntologyVersionDAO.findLatestOntologyVersions();
		}else{
			ontologies = ncboOntologyVersionDAO
		.findOntologyVersions(ontologyIds);
		}
		
		for (VNcboOntology ontology : ontologies) {
			if(ontology.getStatusId().equals(StatusEnum.STATUS_READY.getStatus())){
			String formatHandler = ontologyFormatHandlerMap.get(ontology
					.getFormat());
			
			System.out.println("Looking For "+formatHandler);
			((List<VNcboOntology>) formatLists.get(formatHandler)).add(ontology);
			}
		}

		for (String formatHandler : formatLists.keySet()) {
			OntologyRetrievalManager manager = ontologyRetrievalHandlerMap
					.get(formatHandler);
			searchResults.addAll(manager.findConceptPropertyContains(formatLists
					.get(formatHandler), query, true, MAX_RESULTS));
		}
		
		return searchResults;
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
	public Map<String, OntologyRetrievalManager> getOntologyRetrievalHandlerMap() {
		return ontologyRetrievalHandlerMap;
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
		

	/*
	public List<ClassBean> findParent(int ontologyId, String id) throws Exception {
		return ontologyRetrievalManager.findParent(ontologyId, id);
	}

	public List<ClassBean> findChildren(int ontologyId, String id ) throws Exception {
		return ontologyRetrievalManager.findChildren(ontologyId, id);
	}*/

}
