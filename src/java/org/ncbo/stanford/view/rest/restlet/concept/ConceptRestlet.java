package org.ncbo.stanford.view.rest.restlet.concept;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class ConceptRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ConceptRestlet.class);
	private ConceptService conceptService;

	/**
	 * Handle GET calls here
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		findConcept(request, response);
	}

	/**
	 * Return to the response an individual ontology
	 * 
	 * @param request
	 * @param response
	 */
	private void findConcept(Request request, Response response) {
		ClassBean concept = null;
		String conceptId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.conceptid"));
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));

		if (log.isDebugEnabled()) {
			for (String key : request.getAttributes().keySet()) {

				log.debug("Attribute: " + key + " Val: "
						+ request.getAttributes().get(key));
			}
		}

		// See if concept ID is being passed through param for full URL ID
		// concepts
		if (conceptId == null || conceptId.equalsIgnoreCase("")) {
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);
			conceptId = (String) httpRequest
					.getParameter(RequestParamConstants.PARAM_CONCEPT_ID);
		}

		if (log.isDebugEnabled()) {
			log.debug("finding concept - ovid: " + ontologyVersionId
					+ ", cid: " + conceptId);
		}

		try {
			Integer ontVersionId = Integer.parseInt(ontologyVersionId);

			if (conceptId
					.equalsIgnoreCase(RequestParamConstants.PARAM_ROOT_CONCEPT)) {
				concept = conceptService.findRootConcept(ontVersionId);
			} else {
				// URL Decode the concept Id
				conceptId = URLDecoder.decode(conceptId, MessageUtils
						.getMessage("default.encoding"));
				concept = conceptService.findConcept(ontVersionId, conceptId);
			}

			if (concept == null) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND,
						"Concept not found");
			}
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe
					.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}

		xmlSerializationService.generateXMLResponse(request, response, concept);
	}

	/**
	 * @param conceptService
	 *            the conceptService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
}
