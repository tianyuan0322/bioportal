package org.ncbo.stanford.view.rest.restlet.search;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class IndexRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(IndexRestlet.class);

	private IndexSearchService indexService;
	private XMLSerializationService xmlSerializationService;

	/**
	 * Handle GET calls here
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		// no GET requests should be made to this restlet
		response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		xmlSerializationService.generateStatusXMLResponse(request, response);
	}

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
		indexOntology(request, response);
	}

	/**
	 * Handle DELETE calls here
	 */
	@Override
	protected void deleteRequest(Request request, Response response) {
		removeOntology(request, response);
	}

	/**
	 * Index all ontologies
	 * 
	 * @param request
	 * @param response
	 */
	private void indexAllOntologies(Request request, Response response) {
		try {
			indexService.indexAllOntologies();
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
	 * Index ontology
	 * 
	 * @param request
	 * @param response
	 */
	private void indexOntology(Request request, Response response) {
		try {
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);

			Integer ontologyId = getOntologyId(request);
			boolean doBackup = getDoBackup(httpRequest);
			boolean doOptimize = getDoOptimize(httpRequest);

			indexService.indexOntology(ontologyId, doBackup, doOptimize);
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
	 * Removes ontology from index
	 * 
	 * @param request
	 * @param response
	 */
	private void removeOntology(Request request, Response response) {
		try {
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);

			Integer ontologyId = getOntologyId(request);
			boolean doBackup = getDoBackup(httpRequest);
			boolean doOptimize = getDoOptimize(httpRequest);

			indexService.removeOntology(ontologyId, doBackup, doOptimize);
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

	private Integer getOntologyId(Request request) throws Exception {
		Integer ontologyId = null;
		String ontologyIdStr = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));

		if (StringHelper.isNullOrNullString(ontologyIdStr)) {
			throw new Exception("You must supply a valid ontology id");
		}

		try {
			ontologyId = Integer.parseInt(ontologyIdStr);
		} catch (NumberFormatException e) {
			throw new Exception("Invalid ontology id supplied");
		}

		return ontologyId;
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

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}
