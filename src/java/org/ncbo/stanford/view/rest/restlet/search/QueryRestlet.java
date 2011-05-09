package org.ncbo.stanford.view.rest.restlet.search;

import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.search.QuerySearchService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
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
	 * Handle PUT calls here
	 */
	@Override
	public void putRequest(Request request, Response response) {
		emptyOrReloadSearchCache(request, response);
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
		String objectTypes = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_OBJECT_TYPES);
		String includeProperties = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_INCLUDEPROPERTIES);
		String includeDefinitions = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_INCLUDE_DEFINITIONS);
		String isExactMatch = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_ISEXACTMATCH);
		String pageSize = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGESIZE);
		String pageNum = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGENUM);
		String maxNumHits = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAXNUMHITS);
		String subtreeRootConceptId = RequestUtils
				.parseStringParam((String) httpRequest
						.getParameter(RequestParamConstants.PARAM_SUBTREEROOTCONCEPTID));
		String query = RequestUtils.getAttributeOrRequestParam(
				RequestParamConstants.PARAM_QUERY, request);

		List<Integer> ontologyIdsInt = RequestUtils
				.parseIntegerListParam(ontologyIds);
		List<String> objectTypesStr = RequestUtils
				.parseStringListParam(objectTypes);
		boolean includePropertiesBool = RequestUtils
				.parseBooleanParam(includeProperties);
		boolean includeDefinitionsBool = RequestUtils
				.parseBooleanParam(includeDefinitions);
		boolean isExactMatchBool = RequestUtils.parseBooleanParam(isExactMatch);
		Integer pageSizeInt = RequestUtils.parseIntegerParam(pageSize);
		Integer pageNumInt = RequestUtils.parseIntegerParam(pageNum);
		Integer maxNumHitsInt = RequestUtils.parseIntegerParam(maxNumHits);
		Page<SearchBean> searchResults = null;

		try {
			// subtreeRootConceptId requires one and ONLY one ontology id passed
			// in as a parameter
			if (!StringHelper.isNullOrNullString(subtreeRootConceptId)
					&& ontologyIdsInt.size() != 1) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.branchsingleontology"));
			} else if (!StringHelper.isNullOrNullString(subtreeRootConceptId)) {
				subtreeRootConceptId = URLDecoder.decode(subtreeRootConceptId,
						MessageUtils.getMessage("default.encoding"));
			}

			searchResults = queryService.executeQuery(query, ontologyIdsInt,
					objectTypesStr, includePropertiesBool, isExactMatchBool,
					pageSizeInt, pageNumInt, maxNumHitsInt,
					subtreeRootConceptId, includeDefinitionsBool);
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
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

	private void emptyOrReloadSearchCache(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String reloadCache = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_RELOADCACHE);
		boolean reloadCacheBool = RequestUtils.parseBooleanParam(reloadCache);

		try {
			if (reloadCacheBool) {
				queryService.reloadSearchCache();
			} else {
				queryService.emptySearchCache();
			}
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
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
