/**
 * 
 */
package org.ncbo.stanford.view.rest.restlet.rdf;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.FileRepresentation;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.exception.InvalidInputException;

import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.rest.restlet.ontology.AbstractOntologyBaseRestlet;

/**
 * An RdfDownloadRestlet contains functionality handling for GET methods
 * 
 * @author g.prakash
 * 
 */
public class RdfDownloadRestlet extends AbstractOntologyBaseRestlet {

	private static final Log log = LogFactory.getLog(RdfDownloadRestlet.class);

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param reaponse
	 */
	@Override
	public void getRequest(Request request, Response response) {
		downloadRdfFile(request, response);

	}

	/**
	 * This Method provides the functionality for download the rdf file
	 * according to ontologyVersionId and virtualId and if rdf file is not
	 * available on the given path then they set the error related to rdf inside
	 * the response. Here Sourcepath is the Destpath of rdf where they
	 * generating
	 * 
	 * @param request
	 * @param response
	 */
	public void downloadRdfFile(Request request, Response response) {

		try {
			boolean isVirtual = false;

			String ontologyId = null;
			OntologyBean ont = null;
			Integer ontologyVersionIdInt = null;

			// Try to get OntologyVersionId
			String ontologyVersionId = (String) request.getAttributes().get(
					MessageUtils.getMessage("entity.ontologyversionid"));

			// Condition for VirtualId
			if (StringHelper.isNullOrNullString(ontologyVersionId)) {
				ontologyId = (String) request.getAttributes().get(
						MessageUtils.getMessage("entity.ontologyid"));
				isVirtual = true;
			}
			
			if (isVirtual) {
				Integer ontologyVirtualIdInt = RequestUtils
						.parseIntegerParam(ontologyId);
				ont = ontologyService
						.findLatestActiveOntologyOrViewVersion(ontologyVirtualIdInt);

				if (ont == null) {
					throw new InvalidInputException(MessageUtils
							.getMessage("msg.error.ontologyversionidinvalid"));
				}
			} else {
				ontologyVersionIdInt = RequestUtils
						.parseIntegerParam(ontologyVersionId);
				ont = ontologyService.findOntologyOrView(ontologyVersionIdInt);

				if (ont == null) {
					throw new InvalidInputException(MessageUtils
							.getMessage("msg.error.ontologyversionidinvalid"));

				}
			}

			// Finding the Rdf File
			File file = ontologyService.findRdfFileForOntology(ont);

			FileRepresentation fileRepresentation = new FileRepresentation(
					file, MediaType.APPLICATION_RDF_XML, 60);
			response.setEntity(fileRepresentation);

			String filename = ont.getOntologyId() + ".rdf";
			RequestUtils.getHttpServletResponse(response).setHeader(
					"Content-Disposition",
					"attachment; filename=\"" + filename + "\";");

		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
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

}
