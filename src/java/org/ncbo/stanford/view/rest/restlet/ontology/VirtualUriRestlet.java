package org.ncbo.stanford.view.rest.restlet.ontology;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
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
		Integer maxNumChildrenInt = RequestUtils
				.parseIntegerParam(maxNumChildren);

		try {
			Integer ontologyIdInt = Integer.parseInt(ontologyId);

			if (conceptId == null) {
				returnObject = ontologyService
						.findLatestOntologyOrViewVersion(ontologyIdInt);

				if (returnObject == null) {
					//TODO if we could test whether the virtual id is for an ontology or for a view
					//     we could return more appropriate message (i.e. "msg.error.ontologyViewNotFound")
					response.setStatus(Status.CLIENT_ERROR_NOT_FOUND,
							MessageUtils
									.getMessage("msg.error.ontologyNotFound"));
				}
			} else {
				OntologyBean ontBean = ontologyService
						.findLatestActiveOntologyOrViewVersion(ontologyIdInt);

				if (ontBean == null) {
					//TODO if we could test whether the virtual id is for an ontology or for a view
					//     we could return more appropriate message (i.e. "msg.error.ontologyViewNotFound")
					response.setStatus(Status.CLIENT_ERROR_NOT_FOUND,
							MessageUtils
									.getMessage("msg.error.ontologyNotFound"));
				} else {
					if (conceptId
							.equalsIgnoreCase(RequestParamConstants.PARAM_ROOT_CONCEPT)) {
						returnObject = conceptService.findRootConcept(ontBean
								.getId(), maxNumChildrenInt);
					} else {
						// URL Decode the concept Id
						conceptId = URLDecoder.decode(conceptId, MessageUtils
								.getMessage("default.encoding"));
						returnObject = conceptService.findConcept(ontBean
								.getId(), conceptId, maxNumChildrenInt);
					}

					if (returnObject == null) {
						response.setStatus(Status.CLIENT_ERROR_NOT_FOUND,
								"Concept not found");
					}
				}
			}
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe
					.getMessage());
		} catch (OntologyNotFoundException onfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, onfe
					.getMessage());
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
