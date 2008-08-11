package org.ncbo.stanford.view.rest.restlet.ontology;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.FileRepresentation;

public class OntologyDownloadRestlet extends Restlet{

	private static final Log log = LogFactory.getLog(OntologyVersionsRestlet.class);
	private OntologyService ontologyService;
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
		downloadOntology(request, response);

	}	
			
	
	
	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void downloadOntology(Request request, Response response) {
			
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);
		
		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {

			try {
							
				File file = ontologyService.getOntologyFile(ontologyBean);
				String filename = (String)ontologyBean.getFilenames().toArray()[0];
				
 	            try {
 	            	
 	            	FileRepresentation fileRepresentation = new FileRepresentation(file, MediaType.APPLICATION_ALL, 60);
 	 	           	response.setEntity(fileRepresentation);
 	 	           	RequestUtils.getHttpServletResponse(response).setHeader(
							"Content-Disposition",
							"attachment; filename=\"" + filename + "\";");
 	            	
 	            } catch (Exception e) {
 	                e.printStackTrace();
 	                return;
 	            }
								
			} catch (Exception e) {

				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}
		
		//getXmlSerializationService().generateStatusXMLResponse(request, response);
			
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









	//TODO - refactor this later...
	/**
	 * Returns a specified OntologyBean and set the response status if there is an error.
	 * This is used for find, findAll, update, delete.
	 * 
	 * @param request
	 * @param response
	 */
	private OntologyBean findOntologyBean(Request request, Response response) {
		
		OntologyBean ontologyBean = null;
		String ontologyVersionId = (String) request.getAttributes().get(MessageUtils.getMessage("entity.ontology"));

		try {
			Integer intId = Integer.parseInt(ontologyVersionId);
			ontologyBean = getOntologyService().findOntology(intId);

			response.setStatus(Status.SUCCESS_OK);
			
			// if ontologyBean is not found, set Error in the Status object
			if (ontologyBean == null || ontologyBean.getId() == null) {
	
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, MessageUtils.getMessage("msg.error.ontologyNotFound"));
			}
		
		} catch (NumberFormatException nfe) {
			
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
			nfe.printStackTrace();
			log.error(nfe);	

		} catch (Exception e) {
			
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		}
				
		return ontologyBean;
	}



}

