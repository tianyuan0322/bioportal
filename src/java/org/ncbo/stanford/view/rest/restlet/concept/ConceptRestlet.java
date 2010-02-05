package org.ncbo.stanford.view.rest.restlet.concept;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.exception.ConceptNotFoundException;
import org.ncbo.stanford.exception.InvalidInputException;
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
		String noRelations = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NORELATIONS);

		String conceptId = getConceptId(request);
		Integer maxNumChildrenInt = RequestUtils
				.parseIntegerParam(maxNumChildren);
		Integer offsetInt = RequestUtils.parseIntegerParam(offset);
		Integer limitInt = RequestUtils.parseIntegerParam(limit);
		Boolean lightBool = RequestUtils.parseBooleanParam(light);
		Boolean noRelationsBool = RequestUtils.parseBooleanParam(noRelations);
		Integer ontologyVersionIdInt = RequestUtils
				.parseIntegerParam(ontologyVersionId);

		try {
			if (ontologyVersionIdInt == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
			}

			if (StringHelper.isNullOrNullString(conceptId)) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.conceptidrequired"));
			} else if (conceptId
					.equals(RequestParamConstants.PARAM_ROOT_CONCEPT)) {
				// root concept
				concept = conceptService.findRootConcept(ontologyVersionIdInt,
						maxNumChildrenInt, lightBool);
			} else if (conceptId
					.equalsIgnoreCase(RequestParamConstants.PARAM_ALL_CONCEPTS)) {
				// all concepts
				concept = conceptService.findAllConcepts(
						new OntologyVersionIdBean(ontologyVersionIdInt),
						offsetInt, limitInt);
			} else {
				// specific concept
				concept = conceptService.findConcept(ontologyVersionIdInt,
						conceptId, maxNumChildrenInt, lightBool,
						noRelationsBool);
			}

			if (concept == null) {
				throw new ConceptNotFoundException(MessageUtils
						.getMessage("msg.error.conceptNotFound"));
			}
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
		} catch (ConceptNotFoundException cnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, cnfe.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
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
