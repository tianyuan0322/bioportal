package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class VirtualUriRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(VirtualUriRestlet.class);
	private OntologyService ontologyService;
	private ConceptService conceptService;
	private XMLSerializationService xmlSerializationService;

	@Override
	public void handle(Request request, Response response) {

		if (request.getMethod().equals(Method.GET)) {
			getRequest(request, response);

		}
	}

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	private void getRequest(Request request, Response response) {

		// Handle GET calls here
		getVirtualEntity(request, response);

	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void getVirtualEntity(Request request, Response response) {

		String id = (String) request.getAttributes().get("concept");
		String ontology = (String) request.getAttributes().get("ontology");
		Object returnObject = null;

		try {
			Integer ontId = Integer.parseInt(ontology);
			if (id == null) {
				returnObject = ontologyService.findLatestOntologyVersion(ontId);

				if (returnObject == null) {
					response.setStatus(Status.CLIENT_ERROR_NOT_FOUND,
							"Ontology not found");
				}

			} else {
				OntologyBean ontBean = ontologyService
						.findLatestOntologyVersion(ontId);
				returnObject = conceptService.findConcept(ontBean.getId(), id);
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
			getXmlSerializationService().generateXMLResponse(request, response,
					returnObject);
		}

	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	/**
	 * @return the ontologyService
	 */
	public OntologyService getOntologyService() {
		return ontologyService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	public XMLSerializationService getXmlSerializationService() {
		return xmlSerializationService;
	}

	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}
