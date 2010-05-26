package org.ncbo.stanford.view.rest.restlet.rdf;

import org.apache.log4j.Logger;
import org.ncbo.stanford.exception.ConceptNotFoundException;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
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
import java.util.List;

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
			boolean isVirtual = false;
			String rdfOutput = "";

			String conceptId = getConceptId(request);
			
			// Creating the List For conceptIds
			List<String> conceptIds = getConceptIds(request);

			// version id
			String ontologyVersionId = (String) request.getAttributes().get(
					MessageUtils.getMessage("entity.ontologyversionid"));

			// virtual id
			if (StringHelper.isNullOrNullString(ontologyVersionId)) {
				ontologyVersionId = (String) request.getAttributes().get(
						MessageUtils.getMessage("entity.ontologyid"));
				isVirtual = true;
			}

			if (StringHelper.isNullOrNullString(ontologyVersionId)) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
				/*
				 * TODO: this code needs to be protected behind ADMIN SERVICE as a POST.
				 * SEE Tracker: #2159
				 * 
				// process ALL ontologies
				rdfService.generateRdf(manager, rdfDir, ontologyService);
				rdfOutput = "files have been generated in: " + rdfDir;
				*/
			} else {
				Integer ontologyVersionIdInt = RequestUtils
						.parseIntegerParam(ontologyVersionId);

				if (StringHelper.isNullOrNullString(conceptId)) {
					throw new InvalidInputException(MessageUtils
							.getMessage("msg.error.conceptidrequired"));
					/*
					 *  TODO: this code needs to be protected behind ADMIN SERVICE as a POST.
					 *  SEE Tracker: #2159
					 *  
					// process ALL concepts
					ontology = rdfService.generateRdf(manager, rdfDir,
							ontologyVersionIdInt, isVirtual);
					rdfOutput = "file has been generated in: " + rdfDir;
					*/
				} else if (!conceptIds.isEmpty()) {
					// process a LIST of concepts
					ontology = rdfService.generateRdf(manager, rdfDir,
							ontologyVersionIdInt, isVirtual, conceptIds);
					StringDocumentTarget outputString = new StringDocumentTarget();
					manager.saveOntology(ontology, outputString);
					rdfOutput = outputString.toString();
				} else {
					// process a SPECIFIC concept
					ontology = rdfService.generateRdf(manager, rdfDir,
							ontologyVersionIdInt, isVirtual, conceptId);
					StringDocumentTarget outputString = new StringDocumentTarget();
					manager.saveOntology(ontology, outputString);
					rdfOutput = outputString.toString();
				}
			}

			// Add the contents to the response
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_PLAIN, rdfOutput);
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
			xmlSerializationService.generateStatusXMLResponse(request, response);
		} catch (OntologyNotFoundException onfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
			xmlSerializationService.generateStatusXMLResponse(request, response);
		} catch (ConceptNotFoundException cnfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, cnfe.getMessage());
			xmlSerializationService.generateStatusXMLResponse(request, response);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			xmlSerializationService.generateStatusXMLResponse(request, response);
			e.printStackTrace();
			log.error(e);
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
