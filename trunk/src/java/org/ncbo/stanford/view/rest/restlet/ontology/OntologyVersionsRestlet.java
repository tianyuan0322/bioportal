package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyVersionsRestlet extends Restlet {

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
		listOntologies(request, response);

	}	
			
	
	
	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void listOntologies(Request request, Response response) {
	
		
		String ontologyVersionId = (String) request.getAttributes().get(MessageUtils.getMessage("entity.ontology"));
		
		List<OntologyBean> ontologyList = null;	
		
		try {
			ontologyList = getOntologyService().findAllOntologyVersionsByOntologyId(Integer.parseInt(ontologyVersionId));
			
			response.setStatus(Status.SUCCESS_OK);
			
			// if no data is not found, set Error in the Status object
			if (ontologyList == null || ontologyList.size() == 0) {
	
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, MessageUtils.getMessage("msg.error.ontologyNotFound"));
			}
		
		} catch (NumberFormatException nfe) {
		
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
			nfe.printStackTrace();
			log.error(nfe);	

		}		
		catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
			
		} finally {
					
			// generate response XML with XSL
			getXmlSerializationService().generateXMLResponse (request, response, ontologyList);
		}

	}
	
	
	/**
	 * Returns a specified OntologyBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void findOntologyVersions(Request request, Response response) {
		
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		// generate response XML
		getXmlSerializationService().generateXMLResponse (request, response, ontologyBean);
		
	}
	
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
