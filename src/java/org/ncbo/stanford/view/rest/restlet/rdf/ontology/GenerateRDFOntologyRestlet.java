/**
 * 
 */
package org.ncbo.stanford.view.rest.restlet.rdf.ontology;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.concept.RdfService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author g.prakash
 * 
 */
public class GenerateRDFOntologyRestlet extends AbstractBaseRestlet {

	private RdfService rdfService;
	private OntologyService ontologyService;
	private String rdfFilePath;

	/**
	 * This method handles the calls for POST request
	 */
	public void postRequest(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Check for "all"
		String processAllOntologiesStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_ONTOLOGY_VERSION_IDS);
		boolean processAllOntologies = (processAllOntologiesStr != null) ? processAllOntologiesStr
				.equalsIgnoreCase(RequestParamConstants.PARAM_ALL_CONCEPTS)
				: false;

		List<Integer> ontologyVersionIds = null;
		List<Integer> ontologyIds = null;
		try {
			ontologyVersionIds = RequestUtils
					.parseIntegerListParam(RequestUtils
							.getAttributeOrRequestParam(
									RequestParamConstants.PARAM_ONTOLOGY_VERSION_IDS,
									request));

			ontologyIds = RequestUtils.parseIntegerListParam(RequestUtils
					.getAttributeOrRequestParam(
							RequestParamConstants.PARAM_ONTOLOGY_IDS, request));

			if (ontologyVersionIds != null && !ontologyVersionIds.isEmpty()) {
				generateRdfForOntology(request, response);
			} else if (ontologyIds != null && !ontologyIds.isEmpty()) {
				generateRdfForOntology(request, response);
			} else if (processAllOntologies == true) {
				generateRdfForAllOntologies(request, response);
			} else {
				response.setStatus(Status.SERVER_ERROR_INTERNAL,
						"Invalid input");
				xmlSerializationService.generateStatusXMLResponse(request,
						response);

				throw new InvalidInputException();
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	/**
	 * After POST calls this method handle the generating RDFFile's and
	 * generating the response with the RDFFile's
	 * 
	 * @param request
	 * @param response
	 */
	public void generateRdfForAllOntologies(Request request, Response response) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		try {
			rdfService.generateRdf(manager, rdfFilePath, ontologyService);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
			e.printStackTrace();
		} finally {
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		}
	}

	/**
	 * After POST calls this method handle the generating RDFFile's and
	 * generating the response with the RDFFile's according to their
	 * ontologyVersionId's and OntologyId's
	 * 
	 * @param request
	 * @param response
	 */

	public void generateRdfForOntology(Request request, Response response) {
		// Creating the OWLOtnologyManager
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		String rdfOutput = "";

		try {
			// Creating the OWLOntology
			OWLOntology ontology;
			boolean isVirtual = false;
			Integer ontId = null;
			OntologyBean ont = null;
			Integer ontologyVersionIdInt = null;
			// Creating the instance of HttpServletRequest
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);
			// Creating the List For conceptIds (this will also get just one id)
			List<Integer> ontologyVersionIds = getOntologyVersionIds(httpRequest);

			// Check to make sure a concept was passed
			if (ontologyVersionIds != null) {
				// Iterating the list of ontologyVersionId
				for (Integer ontologyVersionId : ontologyVersionIds) {

					// Try to get the OntologyBean
					ont = ontologyService.findOntologyOrView(ontologyVersionId);
					ontologyVersionIdInt = ont.getId();
					// Check for valid ontology

				}
			}

			// For Virtual
			List<Integer> ontologyIds = getOntologyIds(httpRequest);
			if (ontologyIds != null) {
				for (Integer ontologyid : ontologyIds) {

					ont = ontologyService
							.findLatestActiveOntologyOrViewVersion(ontologyid);
					ontId = ont.getOntologyId();
					isVirtual = true;

				}

				if (isVirtual) {
					ont = ontologyService
							.findLatestActiveOntologyOrViewVersion(ontId);
					ontologyVersionIdInt = ont.getId();
				} else {
					ont = ontologyService
							.findOntologyOrView(ontologyVersionIdInt);

				}
			}
			ontology = rdfService.generateRdf(manager, rdfFilePath, ont);

			StringDocumentTarget outputString = new StringDocumentTarget();
			manager.saveOntology(ontology, outputString);
			rdfOutput = outputString.toString();
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
			e.printStackTrace();
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					rdfOutput);
		}
	}

	public void setRdfService(RdfService rdfService) {
		this.rdfService = rdfService;
	}

	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	public void setRdfFilePath(String rdfFilePath) {
		this.rdfFilePath = rdfFilePath;
	}

}
