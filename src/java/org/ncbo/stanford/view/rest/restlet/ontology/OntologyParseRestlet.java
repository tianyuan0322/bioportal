package org.ncbo.stanford.view.rest.restlet.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyParseRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(OntologyParseRestlet.class);
	private OntologyService ontologyService;
	private OntologyLoadSchedulerService ontologyLoadSchedulerService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		// Handle GET calls here
		parseOntology(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void parseOntology(Request request, Response response) {
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));

		// TODO - id validation?
		// find the OntologyBean from request
		// OntologyBean ontologyBean = findOntologyBean(request, response);

		// TODO - make both scenario available
		ontologyLoadSchedulerService.parseOntology(ontologyVersionId);

		if (!isParseSuccess()) {

			response.setStatus(Status.SERVER_ERROR_INTERNAL,
					"Error Parsing Ontology " + ontologyVersionId);
		}

		xmlSerializationService.generateStatusXMLResponse(request, response);
	}

	private boolean isParseSuccess() {
		if (ontologyLoadSchedulerService.getErrorIdList().size() > 0) {
			return false;
		}

		return true;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @param ontologyLoadSchedulerService
	 *            the ontologyLoadSchedulerService to set
	 */
	public void setOntologyLoadSchedulerService(
			OntologyLoadSchedulerService ontologyLoadSchedulerService) {
		this.ontologyLoadSchedulerService = ontologyLoadSchedulerService;
	}
}
