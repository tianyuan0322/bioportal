package org.ncbo.stanford.view.rest.restlet.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.ontology.OntologyViewService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class VirtualViewUriRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(VirtualViewUriRestlet.class);
	private OntologyViewService ontologyViewService;
	private ConceptService conceptService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		// Handle GET calls here
		getVirtualEntity(request, response);
	}

	/**
	 * Return to the response a listing of ontology views
	 * 
	 * @param response
	 */
	private void getVirtualEntity(Request request, Response response) {
		String conceptId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.conceptid"));
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));
		Object returnObject = null;

		try {
			Integer ontId = Integer.parseInt(ontologyVersionId);

			if (conceptId == null) {
				returnObject = ontologyViewService.findLatestOntologyViewVersion(ontId);

				if (returnObject == null) {
					response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, MessageUtils
							.getMessage("msg.error.ontologyViewNotFound"));
				}
			} else {
				OntologyViewBean ontBean = ontologyViewService
						.findLatestOntologyViewVersion(ontId);
				returnObject = conceptService.findConcept(ontBean.getId(),
						conceptId);

				if (returnObject == null) {
					response.setStatus(Status.CLIENT_ERROR_NOT_FOUND,
							"Concept not found");
				}
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
					returnObject);
		}
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	/**
	 * @param ontologyViewService
	 *            the ontologyViewService to set
	 */
	public void setOntologyViewService(OntologyViewService ontologyViewService) {
		this.ontologyViewService = ontologyViewService;
	}
}
