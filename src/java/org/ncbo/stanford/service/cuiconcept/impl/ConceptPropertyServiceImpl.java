/**
 * 
 */
package org.ncbo.stanford.service.cuiconcept.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.enumeration.PropertyTypeEnum;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.cuiconcept.ConceptPropertyService;
import org.ncbo.stanford.service.search.QuerySearchService;
import org.ncbo.stanford.util.paginator.impl.Page;

/**
 * @author s.reddy
 * 
 */
public class ConceptPropertyServiceImpl implements ConceptPropertyService {

	private static final Log log = LogFactory
			.getLog(ConceptPropertyServiceImpl.class);
	private OntologyMetadataManager ontologyMetadataManager;
	private Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap = new HashMap<String, OntologyRetrievalManager>(
			0);
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);

	private QuerySearchService queryService;
	private ConceptService conceptService;

	public List<ClassBean> findConceptsByProperty(PropertyTypeEnum type,
			String value, Collection<Integer> ontologyIds) {

		List<ClassBean> resultClassbeans = new ArrayList<ClassBean>();
		try {

			/*
			 * for (Integer ontologyId : ontologyIds) { ClassBean concept =
			 * conceptService.findConcept(ontologyId, value, null, true, true);
			 * if (concept != null) { resultClassbeans.add(concept); } else {
			 */
			Page<SearchBean> searchQueryResults = queryService.executeQuery(
					value, ontologyIds, null, true, true, null, null);
			if (searchQueryResults != null) {
				Iterator<SearchBean> searchResults = searchQueryResults
						.getContents().iterator();
				SearchBean searchResult;
				ClassBean cuiConcept;

				while (searchResults.hasNext()) {
					searchResult = searchResults.next();

					cuiConcept = conceptService.findConcept(searchResult
							.getOntologyVersionId(), searchResult
							.getConceptId(), null, false, false, false);
					cuiConcept.setOntologyVersionId(searchResult
							.getOntologyVersionId().toString());
					resultClassbeans.add(cuiConcept);
				}
			}
		} catch (Exception ex) {
			log.error("error while getting class beans", ex.getCause());
		}
		return resultClassbeans;
	}

	protected OntologyRetrievalManager getRetrievalManager(OntologyBean ontology) {
		String formatHandler = ontologyFormatHandlerMap.get(ontology
				.getFormat().toUpperCase());
		return ontologyRetrievalHandlerMap.get(formatHandler);
	}

	/**
	 * @param ontologyRetrievalHandlerMap
	 *            the ontologyRetrievalHandlerMap to set
	 */
	public void setOntologyRetrievalHandlerMap(
			Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap) {
		this.ontologyRetrievalHandlerMap = ontologyRetrievalHandlerMap;
	}

	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}

	public OntologyMetadataManager getOntologyMetadataManager() {
		return ontologyMetadataManager;
	}

	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}

	public QuerySearchService getQueryService() {
		return queryService;
	}

	public void setQueryService(QuerySearchService queryService) {
		this.queryService = queryService;
	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	public Map<String, OntologyRetrievalManager> getOntologyRetrievalHandlerMap() {
		return ontologyRetrievalHandlerMap;
	}

	public Map<String, String> getOntologyFormatHandlerMap() {
		return ontologyFormatHandlerMap;
	}

}
