package org.ncbo.stanford.view.rest.restlet.diff;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.diff.DiffService;
import org.ncbo.stanford.util.MessageUtils;
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
	public void putRequest(Request request, Response response) {
		// Handle PUT calls here
		parseDiff(request, response);
	}

	private void parseDiff(Request request, Response response) {
		try {
			String path = request.getResourceRef().getPath();

			if (path.contains(RequestParamConstants.PARAM_ALL)) {
				String ontologyId = (String) request.getAttributes().get(
						MessageUtils.getMessage("entity.ontologyid"));

				Integer intId = Integer.parseInt(ontologyId);
				diffService.createDiffForAllActiveVersionsOfOntology(intId);
			} else if (path.contains(RequestParamConstants.PARAM_LATEST)) {
				String ontologyId = (String) request.getAttributes().get(
						MessageUtils.getMessage("entity.ontologyid"));

				Integer intId = Integer.parseInt(ontologyId);
				diffService.createDiffForLatestActiveOntologyVersionPair(intId);
			} else {
				String ontologyVersionId1 = (String) request
						.getAttributes()
						.get(MessageUtils.getMessage("entity.ontologyversionid1"));
				String ontologyVersionId2 = (String) request
						.getAttributes()
						.get(MessageUtils.getMessage("entity.ontologyversionid2"));

				Integer intId1 = Integer.parseInt(ontologyVersionId1);
				Integer intId2 = Integer.parseInt(ontologyVersionId2);
				diffService.createDiff(intId1, intId2);
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
}
