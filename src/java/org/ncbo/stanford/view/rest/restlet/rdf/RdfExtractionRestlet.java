package org.ncbo.stanford.view.rest.restlet.rdf;

import java.util.List;

import org.apache.log4j.Logger;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.exception.ConceptNotFoundException;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.concept.ConceptService;
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
	private ConceptService conceptService;
	private String rdfFilePath;

	@Override
	public void getRequest(Request request, Response response) {
		generateRdf(request, response);
	}

	private void generateRdfForAllOntologies(Request request, Response response) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		/*
		 * TODO: this code needs to be protected behind ADMIN SERVICE as a POST.
		 * SEE Tracker: #2159
		 */

		// Process ALL ontologies
		try {
			String rdfOutput = "";

			rdfService.generateRdf(manager, rdfFilePath, ontologyService);
			rdfOutput = "files have been generated in: " + rdfFilePath;

			// Add the contents to the response
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_PLAIN, rdfOutput);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
			e.printStackTrace();
			log.error(e);
		}
	}

	private void generateRdfForOntology(Request request, Response response) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		/*
		 * TODO: this code needs to be protected behind ADMIN SERVICE as a POST.
		 * SEE Tracker: #2159
		 */

		// process ALL concepts

		try {
			boolean isVirtual = false;
			String rdfOutput = "";
			String ontologyId = null;
			OntologyBean ont = null;
			Integer ontologyVersionIdInt = null;

			// Version id
			String ontologyVersionId = (String) request.getAttributes().get(
					MessageUtils.getMessage("entity.ontologyversionid"));

			// Virtual id
			if (StringHelper.isNullOrNullString(ontologyVersionId)) {
				ontologyId = (String) request.getAttributes().get(
						MessageUtils.getMessage("entity.ontologyid"));
				isVirtual = true;
			}

			// Try to get ontology
			if (isVirtual) {
				Integer ontologyIdInt = RequestUtils
						.parseIntegerParam(ontologyId);
				ont = ontologyService
						.findLatestActiveOntologyOrViewVersion(ontologyIdInt);
				ontologyVersionIdInt = ont.getId();
			} else {
				ontologyVersionIdInt = RequestUtils
						.parseIntegerParam(ontologyVersionId);
				ont = ontologyService.findOntologyOrView(ontologyVersionIdInt);
			}

			rdfService.generateRdf(manager, rdfFilePath, ont);
			rdfOutput = "file has been generated in: " + rdfFilePath;

			// Add the contents to the response
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_PLAIN, rdfOutput);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
			e.printStackTrace();
			log.error(e);
		}
	}

	private void generateRdf(Request request, Response response) {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology;
			boolean isVirtual = false;
			String rdfOutput = "";
			String ontologyId = null;
			Integer ontologyVersionIdInt = null;
			OntologyBean ont = null;

			// Creating the List For conceptIds (this will also get just one id)
			List<String> conceptIds = getConceptIds(request);

			// Check to make sure a concept was passed
			if (conceptIds.isEmpty())
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.conceptidrequired"));

			// Version id
			String ontologyVersionId = (String) request.getAttributes().get(
					MessageUtils.getMessage("entity.ontologyversionid"));

			// Virtual id
			if (StringHelper.isNullOrNullString(ontologyVersionId)) {
				ontologyId = (String) request.getAttributes().get(
						MessageUtils.getMessage("entity.ontologyid"));
				isVirtual = true;
			}

			// Try to get ontology
			if (isVirtual) {
				Integer ontologyIdInt = RequestUtils
						.parseIntegerParam(ontologyId);
				ont = ontologyService
						.findLatestActiveOntologyOrViewVersion(ontologyIdInt);
				ontologyVersionIdInt = ont.getId();
			} else {
				ontologyVersionIdInt = RequestUtils
						.parseIntegerParam(ontologyVersionId);
				ont = ontologyService.findOntologyOrView(ontologyVersionIdInt);
			}

			// Check for valid ontology
			if (ont == null)
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));

			// Check that passed conceptIds are valid
			for (String conceptId : conceptIds) {
				ClassBean concept = conceptService.findConcept(ont.getId(),
						conceptId, null, false, false);
				if (concept == null) {
					throw new ConceptNotFoundException(MessageUtils
							.getMessage("msg.error.conceptNotFound"));
				}
			}

			// Process concepts
			ontology = rdfService.generateRdf(manager, rdfFilePath, ont,
					conceptIds);
			StringDocumentTarget outputString = new StringDocumentTarget();
			manager.saveOntology(ontology, outputString);
			rdfOutput = outputString.toString();

			// Add the contents to the response
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_PLAIN, rdfOutput);
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		} catch (ConceptNotFoundException cnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, cnfe.getMessage());
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
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

	public void setRdfFilePath(String rdfFilePath) {
		this.rdfFilePath = rdfFilePath;
	}

	public OntologyService getOntologyService() {
		return ontologyService;
	}

	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @param conceptService
	 *            the conceptService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
}
