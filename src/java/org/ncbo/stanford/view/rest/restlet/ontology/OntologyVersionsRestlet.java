package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class OntologyVersionsRestlet extends AbstractOntologyBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OntologyVersionsRestlet.class);

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		// Handle GET calls here
		listOntologies(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void listOntologies(Request request, Response response) {
		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		List<OntologyBean> ontologyList = null;
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String excludeDeprecated = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_EXCLUDE_DEPRECATED);
		boolean excludeDeprecatedBool = RequestUtils
				.parseBooleanParam(excludeDeprecated);
		Integer ontologyIdInt = RequestUtils.parseIntegerParam(ontologyId);
		
		try {
			if (ontologyIdInt == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyidinvalid"));
			}

			ontologyList = ontologyService
					.findAllOntologyOrViewVersionsByVirtualId(ontologyIdInt,
							excludeDeprecatedBool);

			// if no data is not found, set Error in the Status object
			if (ontologyList == null || ontologyList.isEmpty()) {
				// TODO if we could test whether the virtual id is for an
				// ontology or for a view
				// we could return more appropriate message (i.e.
				// "msg.error.ontologyViewNotFound")
				throw new OntologyNotFoundException(MessageUtils
						.getMessage("msg.error.ontologyNotFound"));
			}
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (OntologyNotFoundException e) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML with XSL
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyList);
		}
	}
}
