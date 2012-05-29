package org.ncbo.stanford.view.rest.restlet.ontology;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.exception.NotDownloadableOntologyException;
import org.ncbo.stanford.util.CompressionUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;
import org.ncbo.stanford.util.security.OntologyDownloadAccessControl;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;

public class OntologyDownloadRestlet extends AbstractOntologyBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OntologyDownloadRestlet.class);

	protected OntologyDownloadAccessControl ontologyDownloadAccessControl;

	public void setOntologyDownloadAccessControl(
			OntologyDownloadAccessControl ontologyDownloadAccessControl) {
		this.ontologyDownloadAccessControl = ontologyDownloadAccessControl;
	}
	
	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		downloadOntology(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void downloadOntology(Request request, Response response) {
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {
			try {
				
				if (! ontologyDownloadAccessControl.isDownloadable(ontologyBean)) {
					throw new NotDownloadableOntologyException();
				}
				
				File file = ontologyService.getOntologyFile(ontologyBean);
				String versionNumber = ontologyBean.getVersionNumber();
				versionNumber = (StringHelper.isNullOrNullString(versionNumber) ? ""
						: versionNumber);
				String[] splitFilename = AbstractFilePathHandler
						.splitFilename(getFilename(ontologyBean.getFilenames()));

				if (splitFilename == null) {
					response.setStatus(Status.CLIENT_ERROR_NOT_FOUND,
							"No file found for ontology "
									+ ontologyBean.getDisplayLabel() + " (Id: "
									+ ontologyBean.getId() + ")");
				} else {
					try {
						FileRepresentation fileRepresentation = new FileRepresentation(
								file, MediaType.APPLICATION_ALL, 60);
						response.setEntity(fileRepresentation);
						String filename = splitFilename[0]
								+ prepareVersion(versionNumber);
						filename += (StringHelper
								.isNullOrNullString(splitFilename[1])) ? ""
								: "." + splitFilename[1];

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
			} catch (NotDownloadableOntologyException e) {
				response.setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
				xmlSerializationService
						.generateStatusXMLResponse(request, response);
			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}
	}

	private String prepareVersion(String version) {
		if (!StringHelper.isNullOrNullString(version)) {
			return "_v" + version.trim().replaceAll("[\\s\\t]+", "_");
		}

		return "";
	}

	private String getFilename(List<String> filenames) {
		String filename = null;

		for (String f : filenames) {
			filename = f;

			if (CompressionUtils.isCompressed(f)) {
				break;
			}
		}

		return filename;
	}
}
