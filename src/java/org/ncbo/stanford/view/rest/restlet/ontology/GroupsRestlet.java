package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.GroupBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class GroupsRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(GroupsRestlet.class);
	private OntologyService ontologyService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		listGroups(request, response);
	}

	/**
	 * Return to the response a listing of groups
	 * 
	 * @param request
	 * @param response
	 */
	private void listGroups(Request request, Response response) {
		List<GroupBean> groupsList = null;

		try {
			groupsList = ontologyService.findAllGroups();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML with XSL
			xmlSerializationService.generateXMLResponse(request, response,
					groupsList);
		}
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}
}