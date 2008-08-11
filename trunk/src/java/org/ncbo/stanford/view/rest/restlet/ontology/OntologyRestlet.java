package org.ncbo.stanford.view.rest.restlet.ontology;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.BeanHelper;
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
					
	} else if(request.getMethod().equals(Method.POST)){
		
		HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
		String method = httpServletRequest.getParameter( MessageUtils.getMessage("http.param.method"));

		if (method != null) {

			if (method.equalsIgnoreCase(MessageUtils.getMessage("http.put"))) {

				putRequest(request, response);

			} else if (method.equalsIgnoreCase(MessageUtils.getMessage("http.delete"))) {

				deleteRequest(request, response);
			}
		}
		
	}  else if(request.getMethod().equals(MessageUtils.getMessage("http.put"))){
		
		putRequest(request, response);
		
	}  else if(request.getMethod().equals(MessageUtils.getMessage("http.delete"))){
		
		deleteRequest(request, response);

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
		updateOntology(request, response);

	}	
	
	
	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	private void deleteRequest(Request request, Response response) {
		
		// Handle DELETE calls here
		deleteOntology(request, response);

	}
	
	
	
	/**
	 * Returns a specified OntologyBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void findOntology(Request request, Response response) {
		
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		// generate response XML
		getXmlSerializationService().generateXMLResponse (request, response, ontologyBean);
		
	}
	
	/**
	 * Update a specified OntologyBean to the response
	 * @param request
	 * @param resp
	 */
	private void updateOntology(Request request, Response response) {

				
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {	
		
			// 1. save implicit values such as id 
			Integer id = ontologyBean.getId();
			Integer ontologyId = ontologyBean.getOntologyId();
			Integer internalVersionNumber = ontologyBean.getInternalVersionNumber();
			
			// 2. populate OntologyBean from Request object
			ontologyBean = BeanHelper.populateOntologyBeanFromRequest(request);
			
			// 3. populate implicit values
			ontologyBean.setId(id);
			ontologyBean.setOntologyId(ontologyId);
			ontologyBean.setInternalVersionNumber(internalVersionNumber);
		
			
			// 4. now update the ontology
			try {
				
				getOntologyService().cleanupOntologyCategory(ontologyBean);
				getOntologyService().updateOntology(ontologyBean);
				

			} catch (Exception e) {
				response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}

		}

		// generate response XML
		getXmlSerializationService().generateXMLResponse (request, response, ontologyBean);
		
	}
	
	
	/**
	 * Delete a specified UserBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void deleteOntology(Request request, Response response) {
		
		// find the UserBean by UserID
		OntologyBean ontologyBean = findOntologyBean(request, response);

		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {	
		
			// now delete the user
			try {

				getOntologyService().deleteOntology(ontologyBean);

			} catch (Exception e) {
				response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
			
			//getXmlSerializationService().generateXMLResponse (request, response, ontologyBean);
		}

		// generate response XML
		// display success XML when successful, otherwise display bean XML
		if (!response.getStatus().isError()) {
			getXmlSerializationService().generateStatusXMLResponse (request, response);
		} else {
			getXmlSerializationService().generateXMLResponse (request, response, ontologyBean);
		}

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
