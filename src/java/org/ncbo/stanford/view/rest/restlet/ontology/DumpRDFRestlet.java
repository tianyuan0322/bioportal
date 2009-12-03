package org.ncbo.stanford.view.rest.restlet.ontology;

import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.concept.DumpRDFService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * This restlet is meant to dump an ontology's classes and subclass relationships
 * to RDF N-triples format.  Cross references, definitions, and synonyms added as well.
 * 
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 */
public class DumpRDFRestlet extends AbstractBaseRestlet {
	
	private DumpRDFService dumpRDFService;
	
	@Override
	public void getRequest(Request request, Response response) {
		generateRDF(request, response);
	}

	private void generateRDF(Request request, Response response) {		
		// Pick the ontology version off of the URL
		String ontologyVersionId = 
			(String)request.getAttributes().get(MessageUtils.getMessage("entity.ontologyversionid"));
		
		try {
			Integer ontologyVersionIdInt = Integer.parseInt(ontologyVersionId);
			
			// Generate the file contents
			String myResponse = dumpRDFService.generateRDFDump(ontologyVersionIdInt);

			// Add the contents to the response
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK, MediaType.TEXT_RDF_N3, myResponse);
			
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
			xmlSerializationService.generateStatusXMLResponse(request, response);
		} catch (OntologyNotFoundException onfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
			xmlSerializationService.generateStatusXMLResponse(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
			xmlSerializationService.generateStatusXMLResponse(request, response);
		}
	}	
	
	/**
	 * Setter for Spring IoC
	 */
	public void setDumpRDFService(DumpRDFService dumpRDFService) {
		this.dumpRDFService = dumpRDFService;
	}
}
