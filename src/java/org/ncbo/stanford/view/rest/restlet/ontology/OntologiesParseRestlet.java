package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologiesParseRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(OntologiesParseRestlet.class);
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
		parseOntologies(request, response);
	}

	/**
	 * Return to the response the list of ontologies that didn't parse
	 * successfully
	 * 
	 * @param response
	 */
	private void parseOntologies(Request request, Response response) {
		try {
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);
			List<Integer> ontologyVersionIds = getOntologyVersionIds(httpRequest);

			ontologyLoadSchedulerService.parseOntologies(ontologyVersionIds);
			List<String> errorOntologies = ontologyLoadSchedulerService
					.getErrorOntologies();

			if (!errorOntologies.isEmpty()) {
				throw new Exception("Error Parsing Ontologies: "
						+ errorOntologies);
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
	 * @param ontologyLoadSchedulerService
	 *            the ontologyLoadSchedulerService to set
	 */
	public void setOntologyLoadSchedulerService(
			OntologyLoadSchedulerService ontologyLoadSchedulerService) {
		this.ontologyLoadSchedulerService = ontologyLoadSchedulerService;
	}
}