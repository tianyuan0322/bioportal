package org.ncbo.stanford.view.rest.restlet.search;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * Restlet responsible for executing indexing operations
 * 
 * @author Michael Dorf
 * 
 */
public class IndexRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(IndexRestlet.class);
	private IndexSearchService indexService;

	/**
	 * Handle POST calls here
	 */
	@Override
	protected void postRequest(Request request, Response response) {
		indexAllOntologies(request, response);
	}

	/**
	 * Handle PUT calls here
	 */
	@Override
	protected void putRequest(Request request, Response response) {
		handlePutRequest(request, response);
	}

	/**
	 * Handle DELETE calls here
	 */
	@Override
	protected void deleteRequest(Request request, Response response) {
		removeOntologies(request, response);
	}

	/**
	 * Index all ontologies
	 * 
	 * @param request
	 * @param response
	 */
	private void indexAllOntologies(Request request, Response response) {
		try {
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);
			boolean doBackup = getDoBackup(httpRequest);
			boolean doOptimize = getDoOptimize(httpRequest);

			indexService.indexAllOntologies(doBackup, doOptimize);
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
	 * Index ontologies or backup index or optimize index
	 * 
	 * @param request
	 * @param response
	 */
	private void handlePutRequest(Request request, Response response) {
		try {
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);
			List<Integer> ontologyIds = getOntologyIds(httpRequest);
			boolean doBackup = getDoBackup(httpRequest);
			boolean doOptimize = getDoOptimize(httpRequest);

			if (ontologyIds != null) {
				indexService.indexOntologies(ontologyIds, doBackup, doOptimize);
			} else if (doBackup) {
				indexService.backupIndex();
			} else if (doOptimize) {
				indexService.optimizeIndex();
			} else {
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
						"No valid parameters supplied");
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
	 * Removes ontologies from index
	 * 
	 * @param request
	 * @param response
	 */
	private void removeOntologies(Request request, Response response) {
		try {
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);
			List<Integer> ontologyIds = getOntologyIds(httpRequest);
			boolean doBackup = getDoBackup(httpRequest);
			boolean doOptimize = getDoOptimize(httpRequest);

			indexService.removeOntologies(ontologyIds, doBackup, doOptimize);
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

	private boolean getDoBackup(HttpServletRequest httpRequest) {
		String doBackupStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_DOBACKUP);

		return RequestUtils.parseBooleanParam(doBackupStr);
	}

	private boolean getDoOptimize(HttpServletRequest httpRequest) {
		String doOptimizeStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_DOOPTIMIZE);

		return RequestUtils.parseBooleanParam(doOptimizeStr);
	}

	/**
	 * @param indexService
	 *            the indexService to set
	 */
	public void setIndexService(IndexSearchService indexService) {
		this.indexService = indexService;
	}
}
