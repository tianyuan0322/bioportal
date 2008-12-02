package org.ncbo.stanford.view.rest.restlet.search;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.service.search.IndexService;
import org.ncbo.stanford.service.search.QueryService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.paginator.impl.Page;
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
	private void searchConcept(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String ontologyIds = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_ONTOLOGY_IDS);
		String includeProperties = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_INCLUDEPROPERTIES);
		String isExactMatch = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_ISEXACTMATCH);
		String pageSize = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGESIZE);
		String pageNum = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGENUM);

		String query = Reference.decode((String) request.getAttributes().get(
				"query"));
		List<Integer> ontologyIdsInt = RequestUtils.parseIntegerListParam(ontologyIds);
		boolean includePropertiesBool = RequestUtils.parseBooleanParam(includeProperties);
		boolean isExactMatchBool = RequestUtils.parseBooleanParam(isExactMatch);
		Integer pageSizeInt = RequestUtils.parseIntegerParam(pageSize);
		Integer pageNumInt = RequestUtils.parseIntegerParam(pageNum);
		Page<SearchBean> searchResults = null;

		try {
			searchResults = queryService.executeQuery(query, ontologyIdsInt,
					includePropertiesBool, isExactMatchBool, pageSizeInt,
					pageNumInt);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
//			xmlSerializationService.addImplicitCollection(Page.class, "contents");
			xmlSerializationService.generateXMLResponse(request, response,
					searchResults);
		}
	}

	/**
	 * @param queryService
	 *            the queryService to set
	 */
	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

	/**
	 * @param indexService
	 *            the indexService to set
	 */
	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}
