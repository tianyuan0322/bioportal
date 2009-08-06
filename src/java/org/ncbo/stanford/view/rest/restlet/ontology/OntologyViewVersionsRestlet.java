package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.service.ontology.OntologyViewService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyViewVersionsRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OntologyViewVersionsRestlet.class);
	private OntologyViewService ontologyViewService;

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
		List<OntologyViewBean> ontologyList = null;

		try {
			ontologyList = ontologyViewService
					.findAllOntologyViewVersionsByVirtualViewId(Integer
							.parseInt(ontologyId));

			response.setStatus(Status.SUCCESS_OK);

			// if no data is not found, set Error in the Status object
			if (ontologyList == null || ontologyList.isEmpty()) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, MessageUtils
						.getMessage("msg.error.ontologyViewNotFound"));
			}
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe
					.getMessage());
			nfe.printStackTrace();
			log.error(nfe);
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

	/**
	 * @param ontologyViewService
	 *            the ontologyViewService to set
	 */
	public void setOntologyViewService(OntologyViewService ontologyViewService) {
		this.ontologyViewService = ontologyViewService;
	}
}
