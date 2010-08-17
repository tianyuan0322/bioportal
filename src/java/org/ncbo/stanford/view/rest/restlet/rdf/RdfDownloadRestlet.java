/**
 * 
 */
package org.ncbo.stanford.view.rest.restlet.rdf;

/**
 * An RdfDownloadRestlet contains functionality 
 * handling for GET methods
 * @author g.prakash
 *
 */

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.FileRepresentation;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.CompressionUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.rest.restlet.ontology.AbstractOntologyBaseRestlet;

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
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		if (!response.getStatus().isError()) {
			try {
				// Finding the Rdf File
				File file = ontologyService
						.findRdfFileForOntology(ontologyBean);

				// If File Name is null then they set the Error Message as
				// "do NOT try to generate it "

				if (file == null) {
					response.setStatus(Status.CLIENT_ERROR_NOT_FOUND,
							"do NOT try to generate it ");
				} else {
					try {
						FileRepresentation fileRepresentation = new FileRepresentation(
								file, MediaType.APPLICATION_ALL, 60);
						response.setEntity(fileRepresentation);

						String filename = ontologyBean.getOntologyId() + ".rdf";

						RequestUtils.getHttpServletResponse(response)
								.setHeader(
										"Content-Disposition",
										"attachment; filename=\"" + filename
												+ "\";");
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}
	}
}
