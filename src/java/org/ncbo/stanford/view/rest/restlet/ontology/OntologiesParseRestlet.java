package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologiesParseRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OntologiesParseRestlet.class);
	private OntologyLoadSchedulerService ontologyLoadSchedulerService;

	/**
	 * Handle PUT calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void putRequest(Request request, Response response) {
		// Handle PUT calls here
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
			String formatHandler = (String) httpRequest
					.getParameter(RequestParamConstants.PARAM_ONTOLOGYPARSER);

			if (ontologyVersionIds != null) {
				ontologyLoadSchedulerService.parseOntologies(
						ontologyVersionIds, formatHandler);
				List<String> errorOntologies = ontologyLoadSchedulerService
						.getErrorOntologies();

				if (!errorOntologies.isEmpty()) {
					throw new Exception("Error Parsing Ontologies: "
							+ errorOntologies);
				}
			} else {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidsinvalid"));
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
	 * @param ontologyLoadSchedulerService
	 *            the ontologyLoadSchedulerService to set
	 */
	public void setOntologyLoadSchedulerService(
			OntologyLoadSchedulerService ontologyLoadSchedulerService) {
		this.ontologyLoadSchedulerService = ontologyLoadSchedulerService;
	}
}
