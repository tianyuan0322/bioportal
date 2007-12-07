package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class OntologyRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(OntologyRestlet.class);
	
	private OntologyService ontologyService;

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
		findOntology(request,response);			
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
	private void findOntology(Request request, Response resp) {
		OntologyBean ontology = null;
		String id = (String) request.getAttributes().get("ontology");

		try {
			Integer intId = Integer.parseInt(id);
			ontology = ontologyService.findOntology(intId, "");

			if (ontology == null) {
				resp.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Ontology not found");
			}
		} catch (NumberFormatException nfe) {
			resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
		}

		if (ontology != null && !resp.getStatus().isError()) {
			XStream xstream = new XStream(new DomDriver());
			xstream.alias("OntologyBean", OntologyBean.class);
			resp.setEntity(xstream.toXML(ontology), MediaType.APPLICATION_XML);
		}
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
}
