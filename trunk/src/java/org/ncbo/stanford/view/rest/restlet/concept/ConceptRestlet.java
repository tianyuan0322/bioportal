package org.ncbo.stanford.view.rest.restlet.concept;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ConceptRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(ConceptRestlet.class);
	
	private ConceptService conceptService;
	private XMLSerializationService xmlSerializationService;

	@Override
	public void handle(Request request, Response response) {
		
		if(request.getMethod().equals(Method.GET)){
			getRequest(request, response);
		}
		
	}
	
	
	/**
	 * Handle GET calls here
	 */
	private void getRequest(Request request,Response response){		
		findConcept(request,response);			
	}
	
	
	/**
	 * Handle POST calls here
	 * @param request
	 * @param response
	 */
	private void postRequest(Request request,Response response){
		
	}
	
	/**
	 * Return to the response an individual ontology
	 * @param ref
	 * @param resp
	 */
	private void findConcept(Request request, Response resp) {
		ClassBean concept = null;
		String id = (String) request.getAttributes().get("concept");
		String ontology = (String) request.getAttributes().get("ontology");
		
		try {
			Integer ontId = Integer.parseInt(ontology);
			if(id.equalsIgnoreCase(RequestParamConstants.PARAM_ROOT_CONCEPT)){
				concept = conceptService.findRoot(ontId);
			}else{
				concept = conceptService.findConcept(ontId, id);
			}
			
			if (concept == null) {
				resp.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Concept not found");
			}
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
		} catch(Exception e){
			e.printStackTrace();
			resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}

		getXmlSerializationService().generateXMLResponse (request, resp, concept);
		
	}

	/**
	 * @return the conceptService
	 */
	public ConceptService getConceptService() {
		return conceptService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}


	public XMLSerializationService getXmlSerializationService() {
		return xmlSerializationService;
	}


	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
	
}
