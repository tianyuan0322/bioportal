package org.ncbo.stanford.view.rest.restlet.search;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.search.IndexService;
import org.ncbo.stanford.service.search.QueryService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class SearchRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(SearchRestlet.class);

	private QueryService queryService;
	private IndexService indexService;
	private XMLSerializationService xmlSerializationService;

	@Override
	public void handle(Request request, Response response) {
		if (request.getMethod().equals(Method.GET)) {
			getRequest(request, response);
		}
	}

	/**
	 * Handle GET calls here
	 */
	private void getRequest(Request request, Response response) {
		searchConcept(request, response);
	}

	/**
	 * Return to the response an individual ontology
	 * 
	 * @param ref
	 * @param resp
	 */
	private void searchConcept(Request request, Response resp) {
		// List<SearchResultBean> concepts = null;
		String query = (String) request.getAttributes().get("query");
		query = Reference.decode(query);
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String ontologies = (String) httpRequest.getParameter("ontologies");

		try {

			List<Integer> ontologyIds = new ArrayList<Integer>();
			if (!ontologies
					.equalsIgnoreCase(RequestParamConstants.PARAM_ALL_ONTOLOGIES)) {
				String[] ontologyStrings = ontologies.split(",");
				for (int x = 0; x < ontologyStrings.length; x++) {
					ontologyIds.add(Integer.parseInt(ontologyStrings[x]));
				}

			}

			/*
			 * concepts = conceptService.findConceptNameContains(ontologyIds,
			 * query);
			 * 
			 * if (concepts.isEmpty()) {
			 * resp.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Concept not
			 * found"); }
			 */} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}

		// getXmlSerializationService().generateXMLResponse(request, resp,
		// concepts);
	}

	public XMLSerializationService getXmlSerializationService() {
		return xmlSerializationService;
	}

	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}

	/**
	 * @param queryService the queryService to set
	 */
	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

	/**
	 * @param indexService the indexService to set
	 */
	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}
}
