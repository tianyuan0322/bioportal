/**
 * 
 */
package org.ncbo.stanford.view.rest.restlet.rdf.ontology;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.concept.RdfService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.semanticweb.owlapi.apibinding.OWLManager;
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
		// Check for "all"
		String processAllOntologiesStr = (String) RequestUtils
				.getAttributeOrRequestParam(
						RequestParamConstants.PARAM_ONTOLOGY_VERSION_IDS,
						request);
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
				generateRdfForOntology(request, response, ontologyVersionIds,
						false);
			} else if (ontologyIds != null && !ontologyIds.isEmpty()) {
				generateRdfForOntology(request, response, ontologyIds, true);
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
			e.printStackTrace();
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

	public void generateRdfForOntology(Request request, Response response,
			List<Integer> ontologyIds, boolean isVirtual) {
		// Creating the OWLOtnologyManager
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		try {
			OntologyBean ont = null;
			if (ontologyIds != null) {
				for (Integer ontologyId : ontologyIds) {
					// Try to get the OntologyBean
					if (isVirtual) {
						ont = ontologyService
								.findLatestActiveOntologyOrViewVersion(ontologyId);
					} else {
						ont = ontologyService.findOntologyOrView(ontologyId);
					}

					if (ont == null) {
						throw new InvalidInputException(
								MessageUtils
										.getMessage("msg.error.ontologyversionidinvalid"));
					}

					rdfService.generateRdf(manager, rdfFilePath, ont);
				}
			}
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
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
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
