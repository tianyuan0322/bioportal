package org.ncbo.stanford.view.rest.restlet.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(OntologyRestlet.class);
	private OntologyService ontologyService;
	private XMLSerializationService xmlSerializationService;

	@Override
	public void handle(Request request, Response response) {
		
		if(request.getMethod().equals(Method.GET)){
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
		findOntology(request, response);

	}	
	
	
	/**
	 * Handle PUT calls here
	 * 
	 * @param request
	 * @param response
	 */
	private void putRequest(Request request, Response response) {

		// Handle PUT calls here
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println("           PUT call");
		System.out.println("+++++++++++++++++++++++++++++++++++++");

		//updateOntology(request, response);

	}	

	
	
	/**
	 * Returns a specified UserBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void findOntology(Request request, Response response) {
		
		// find the UserBean from request
		//UserBean userBean = findUserBean(request, response);

		OntologyBean ontologyBean = null;
		String ontologyVersionId = (String) request.getAttributes().get("ontology");
		try {
			Integer intId = Integer.parseInt(ontologyVersionId);
			ontologyBean = ontologyService.findOntology(intId);

			if (ontologyBean == null) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Ontology not found");
			}
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
		}
		
		// generate response XML
		getXmlSerializationService().generateXMLResponse (request, response, ontologyBean);
				
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
