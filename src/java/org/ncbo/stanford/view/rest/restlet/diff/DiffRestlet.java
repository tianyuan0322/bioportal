package org.ncbo.stanford.view.rest.restlet.diff;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.diff.DiffService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
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
