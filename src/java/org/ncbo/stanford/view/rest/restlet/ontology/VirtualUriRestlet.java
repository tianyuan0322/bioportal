package org.ncbo.stanford.view.rest.restlet.ontology;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.exception.ConceptNotFoundException;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class VirtualUriRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(VirtualUriRestlet.class);
	private OntologyService ontologyService;
	private ConceptService conceptService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		// Handle GET calls here
		getVirtualEntity(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void getVirtualEntity(Request request, Response response) {
		Object returnObject = null;
		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		String conceptId = getConceptId(request);
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String maxNumChildren = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAXNUMCHILDREN);
		String pageSize = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGESIZE);
		String pageNum = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGENUM);
		String light = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_LIGHT);
		String noRelations = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NORELATIONS);

		Integer maxNumChildrenInt = RequestUtils
				.parseIntegerParam(maxNumChildren);
		Integer pageSizeInt = RequestUtils.parseIntegerParam(pageSize);
		Integer pageNumInt = RequestUtils.parseIntegerParam(pageNum);
		Boolean lightBool = RequestUtils.parseBooleanParam(light);
		Boolean noRelationsBool = RequestUtils.parseBooleanParam(noRelations);
		Integer ontologyIdInt = RequestUtils.parseIntegerParam(ontologyId);

		try {
			if (ontologyIdInt == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyidinvalid"));
			}

			if (conceptId == null) {
				returnObject = ontologyService
						.findLatestOntologyOrViewVersion(ontologyIdInt);

				if (returnObject == null) {
					// TODO if we could test whether the virtual id is for an
					// ontology or for a view
					// we could return more appropriate message (i.e.
					// "msg.error.ontologyViewNotFound")
					throw new OntologyNotFoundException(MessageUtils
							.getMessage("msg.error.ontologyNotFound"));
				}
			} else {
				OntologyBean ontBean = ontologyService
						.findLatestActiveOntologyOrViewVersion(ontologyIdInt);

				if (ontBean == null) {
					// TODO if we could test whether the virtual id is for an
					// ontology or for a view
					// we could return more appropriate message (i.e.
					// "msg.error.ontologyViewNotFound")
					throw new OntologyNotFoundException(MessageUtils
							.getMessage("msg.error.ontologyNotFound"));
				} else {
					if (conceptId
							.equalsIgnoreCase(RequestParamConstants.PARAM_ROOT_CONCEPT)) {
						returnObject = conceptService.findRootConcept(ontBean
								.getId(), maxNumChildrenInt, false);
					} else if (conceptId
							.equalsIgnoreCase(RequestParamConstants.PARAM_ALL_CONCEPTS)) {
						returnObject = conceptService.findAllConcepts(ontBean
								.getId(), pageSizeInt, pageNumInt);
					} else {
						returnObject = conceptService.findConcept(ontBean
								.getId(), conceptId, maxNumChildrenInt,
								lightBool, noRelationsBool, true);
					}

					if (returnObject == null) {
						throw new ConceptNotFoundException(MessageUtils
								.getMessage("msg.error.conceptNotFound"));
					}
				}
			}
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
		} catch (ConceptNotFoundException e) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML with XSL
			xmlSerializationService.generateXMLResponse(request, response,
					returnObject);
		}
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}
}
