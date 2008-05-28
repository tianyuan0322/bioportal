package org.ncbo.stanford.view.rest.restlet.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
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

public class MetaDataSearchRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(MetaDataSearchRestlet.class);
	
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
	 */
	private void getRequest(Request request,Response response){		
		searchConcept(request,response);			
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
	private void searchConcept(Request request, Response response) {
		List<OntologyBean> ontologies = null;
		String query = (String) request.getAttributes().get("query");		
		
		try {
			
			
						
			ontologies = ontologyService.searchOntologyMetadata(query);
			
			if (ontologies.isEmpty()) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Query not found");
			}
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
		} catch(Exception e){
			e.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}

		getXmlSerializationService().generateXMLResponse (request, response, ontologies);
	}


	public OntologyService getOntologyService() {
		return ontologyService;
	}


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
