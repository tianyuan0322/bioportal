package org.ncbo.stanford.view.rest.restlet.concept;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
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
	public void getRequest(Request request, Response response) {
		findConcept(request, response);
	}

	/**
	 * Return to the response an individual ontology
	 * 
	 * @param request
	 * @param response
	 */
	private void findConcept(Request request, Response response) {
		Object concept = null;
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String maxNumChildren = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAXNUMCHILDREN);
		String offset = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_OFFSET);
		String limit = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_LIMIT);
		String light = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_LIGHT);

		String conceptId = getConceptId(request);
		Integer maxNumChildrenInt = RequestUtils
				.parseIntegerParam(maxNumChildren);
		Integer offsetInt = RequestUtils.parseIntegerParam(offset);
		Integer limitInt = RequestUtils.parseIntegerParam(limit);
		Boolean lightBool = RequestUtils.parseBooleanParam(light);

		try {
			Integer ontologyVersionIdInt = Integer.parseInt(ontologyVersionId);

			if (StringHelper.isNullOrNullString(conceptId)) {
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
						MessageUtils.getMessage("msg.error.conceptidrequired"));
			} else if (conceptId
					.equalsIgnoreCase(RequestParamConstants.PARAM_ROOT_CONCEPT)) {
				// root concept
				concept = conceptService.findRootConcept(ontologyVersionIdInt,
						maxNumChildrenInt, lightBool);
			} else if (conceptId
					.equalsIgnoreCase(RequestParamConstants.PARAM_ALL_CONCEPTS)) {
				// all concepts
				concept = conceptService.findAllConcepts(
						new OntologyVersionIdBean(ontologyVersionId),
						offsetInt, limitInt);
			} else {
				// specific concept
				concept = conceptService.findConcept(ontologyVersionIdInt,
						conceptId, maxNumChildrenInt, lightBool);
			}

			if (concept == null) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, MessageUtils
						.getMessage("msg.error.conceptNotFound"));
			}
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe
					.getMessage());
		} catch (OntologyNotFoundException onfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, onfe
					.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					concept);
		}
	}

	/**
	 * @param conceptService
	 *            the conceptService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
}
