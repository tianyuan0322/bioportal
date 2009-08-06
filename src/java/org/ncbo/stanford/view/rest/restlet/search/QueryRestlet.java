package org.ncbo.stanford.view.rest.restlet.search;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.service.search.QuerySearchService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * Restlet responsible for executing search query operations
 * 
 * @author Michael Dorf
 * 
 */
public class QueryRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(QueryRestlet.class);
	private QuerySearchService queryService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		executeSearch(request, response);
	}

	/**
	 * Execute search
	 * 
	 * @param request
	 * @param response
	 */
	private void executeSearch(Request request, Response response) {
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
		String maxNumHits = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAXNUMHITS);

		String query = Reference.decode((String) request.getAttributes().get(
				RequestParamConstants.PARAM_QUERY));
		List<Integer> ontologyIdsInt = RequestUtils
				.parseIntegerListParam(ontologyIds);
		boolean includePropertiesBool = RequestUtils
				.parseBooleanParam(includeProperties);
		boolean isExactMatchBool = RequestUtils.parseBooleanParam(isExactMatch);
		Integer pageSizeInt = RequestUtils.parseIntegerParam(pageSize);
		Integer pageNumInt = RequestUtils.parseIntegerParam(pageNum);
		Integer maxNumHitsInt = RequestUtils.parseIntegerParam(maxNumHits);
		Page<SearchBean> searchResults = null;

		try {
			searchResults = queryService.executeQuery(query, ontologyIdsInt,
					includePropertiesBool, isExactMatchBool, pageSizeInt,
					pageNumInt, maxNumHitsInt);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					searchResults);
		}
	}

	/**
	 * @param queryService
	 *            the queryService to set
	 */
	public void setQueryService(QuerySearchService queryService) {
		this.queryService = queryService;
	}
}
