package org.ncbo.stanford.view.rest.restlet.diff;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.diff.DiffService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * @author Natasha Noy
 */
public class DiffRestlet extends AbstractBaseRestlet {

	private DiffService diffService;
	private static final Log log = LogFactory.getLog(DiffRestlet.class);

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		getDiffsForOntology(request, response);
	}

	/**
	 * Handle PUT calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void postRequest(Request request, Response response) {
		// Handle PUT calls here
		parseDiff(request, response);
	}

	private void parseDiff(Request request, Response response) {
		try {
			HttpServletRequest httpRequest = RequestUtils.getHttpServletRequest(request);
			String path = request.getResourceRef().getPath();
            System.out.println("parseDiff path="+path);
			if (path.contains(RequestParamConstants.PARAM_ALL)) {				
				List<Integer> ontologyIds = getOntologyIds(httpRequest);
				diffService.createDiffForAllActiveVersionsOfOntology(ontologyIds);
			} else if (path.contains(RequestParamConstants.PARAM_LATEST)) {
				List<Integer> ontologyIds = getOntologyIds(httpRequest);				
				diffService.createDiffForLatestActiveOntologyVersionPair(ontologyIds);
			} else {
				Integer ontologyVersionNew = getOntologyVersionId(httpRequest, RequestParamConstants.PARAM_ONTOLOGY_VERSION_NEW);
				Integer ontologyVersionOld = getOntologyVersionId(httpRequest, RequestParamConstants.PARAM_ONTOLOGY_VERSION_OLD);					
				diffService.createDiff(ontologyVersionNew, ontologyVersionOld);
			}

		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
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
	 * Returns a list of diffs that exist, as a list of pairs of ontology
	 * version ids: v1-v2, v2-v3, ...
	 * 
	 * @param request
	 * @param response
	 */
	private void getDiffsForOntology(Request request, Response response) {
		List<ArrayList<String>> versionList = getListOfAllDiffs(request,
				response);

		// generate response XML
		xmlSerializationService.generateXMLResponse(request, response,
				versionList);
	}

	private List<ArrayList<String>> getListOfAllDiffs(Request request,
			Response response) {
		List<ArrayList<String>> versionList = null;
		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		try {
			Integer intId = Integer.parseInt(ontologyId);
			versionList = diffService.getAllDiffsForOntology(intId);
			response.setStatus(Status.SUCCESS_OK);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		}

		return versionList;
	}

	public void setDiffService(DiffService diffService) {
		this.diffService = diffService;
	}
	
	private Integer getOntologyVersionId(HttpServletRequest httpRequest, String parameter) {
		String ontologyIdStr = (String) httpRequest.getParameter(parameter);
		return RequestUtils.parseIntegerParam(ontologyIdStr);
	}	
	
}
