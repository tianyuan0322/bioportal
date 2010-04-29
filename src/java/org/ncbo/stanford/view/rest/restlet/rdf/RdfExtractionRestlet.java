package org.ncbo.stanford.view.rest.restlet.rdf;

import org.apache.log4j.Logger;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.concept.RdfService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class RdfExtractionRestlet extends AbstractBaseRestlet {

	private static transient Logger log = Logger
		.getLogger(RdfExtractionRestlet.class);
	
	private RdfService rdfService;
	private OntologyService ontologyService;
	private String rdfDir;

	@Override
	public void getRequest(Request request, Response response) {
		generateRdf(request, response);
	}
	
	private void generateRdf(Request request, Response response) {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology;
			
			String conceptId = getConceptId(request);
			String ontologyVersionId = (String) request.getAttributes().get(
					MessageUtils.getMessage("entity.ontologyversionid"));

			String rdfOutput = "";

			if (StringHelper.isNullOrNullString(ontologyVersionId)) {
				// process ALL ontologies
				rdfService.generateRdf(manager, ontologyService, rdfDir);
				rdfOutput = "files have been generated in: " + rdfDir;
			} else {
				Integer ontologyVersionIdInt = RequestUtils.parseIntegerParam(ontologyVersionId);

				if (StringHelper.isNullOrNullString(conceptId)) {
					// process ALL concepts
					ontology = rdfService.generateRdf(manager, rdfDir, ontologyVersionIdInt);
					rdfOutput = "file has been generated in: " + rdfDir;
				} else {
					// process SPECIFIC concept
					ontology = rdfService.generateRdf(manager, rdfDir, ontologyVersionIdInt, conceptId);
					StringDocumentTarget outputString = new StringDocumentTarget();
					manager.saveOntology(ontology, outputString);
					rdfOutput = outputString.toString();
				}
			}
			
			// Add the contents to the response
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_PLAIN, rdfOutput);
			
		} catch (InvalidInputException iie) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, iie.getMessage());
			iie.printStackTrace();
			log.error(iie);
			xmlSerializationService.generateStatusXMLResponse(request, response);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
			xmlSerializationService.generateStatusXMLResponse(request, response);	
		}

	}
	

	public RdfService getRdfService() {
		return rdfService;
	}
	
	public void setRdfService(RdfService rdfService) {
		this.rdfService = rdfService;
	}
	
	public String getRdfDir() {
		return rdfDir;
	}

	public void setRdfDir(String rdfDir) {
		this.rdfDir = rdfDir;
	}
	

	public OntologyService getOntologyService() {
		return ontologyService;
	}

	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}
}
