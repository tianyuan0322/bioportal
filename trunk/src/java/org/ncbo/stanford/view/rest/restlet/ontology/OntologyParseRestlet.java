package org.ncbo.stanford.view.rest.restlet.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyParseRestlet extends Restlet{

	private static final Log log = LogFactory.getLog(OntologyVersionsRestlet.class);
	private OntologyService ontologyService;
	private OntologyLoadSchedulerService ontologyLoadSchedulerService;
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
		parseOntology(request, response);

	}	
			
	
	
	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void parseOntology(Request request, Response response) {
	
		
		String ontologyVersionId = (String) request.getAttributes().get(MessageUtils.getMessage("entity.ontology"));
		
		// TODO - id validation?
		// find the OntologyBean from request
		//OntologyBean ontologyBean = findOntologyBean(request, response);
		
		// TODO - make both scenario available
		ontologyLoadSchedulerService.parseOntology(ontologyVersionId);

		if (!isParseSuccess()) {
			
			response.setStatus(Status.SERVER_ERROR_INTERNAL, "Error Parsing Ontology " + ontologyVersionId);
		}
		
		getXmlSerializationService().generateStatusXMLResponse(request, response);
			
	}
	
	private boolean isParseSuccess() {
		
		if (ontologyLoadSchedulerService.getErrorIdList().size() > 0) return false;
		else return true;
	}
	
	
	/**
	 * @return the ontologyService
	 */
	public OntologyService getOntologyService() {
		return ontologyService;
	}

	/**
	 * @param ontologyService the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}


	/**
	 * @return the ontologyLoadSchedulerService
	 */
	public OntologyLoadSchedulerService getOntologyLoadSchedulerService() {
		return ontologyLoadSchedulerService;
	}

	/**
	 * @param ontologyLoadSchedulerService the ontologyLoadSchedulerService to set
	 */
	public void setOntologyLoadSchedulerService(
			OntologyLoadSchedulerService ontologyLoadSchedulerService) {
		this.ontologyLoadSchedulerService = ontologyLoadSchedulerService;
	}

	/**
	 * @return the xmlSerializationService
	 */
	public XMLSerializationService getXmlSerializationService() {
		return xmlSerializationService;
	}

	/**
	 * @param xmlSerializationService the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}
