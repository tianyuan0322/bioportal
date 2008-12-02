package org.ncbo.stanford.view.rest.restlet.ontology;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.CompressionUtils;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.FileRepresentation;

public class OntologyDownloadRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OntologyDownloadRestlet.class);
	private OntologyService ontologyService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		// Handle GET calls here
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
				File file = ontologyService.getOntologyFile(ontologyBean);
				String versionNumber = ontologyBean.getVersionNumber();
				versionNumber = (StringHelper.isNullOrNullString(versionNumber) ? ""
						: versionNumber);
				String[] splitFilename = splitFilename(getFilename(ontologyBean
						.getFilenames()));

				if (splitFilename == null) {
					response.setStatus(Status.SERVER_ERROR_INTERNAL,
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
			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * Returns a specified OntologyBean and set the response status if there is
	 * an error. This is used for find, findAll, update, delete.
	 * 
	 * @param request
	 * @param response
	 */
	private OntologyBean findOntologyBean(Request request, Response response) {
		OntologyBean ontologyBean = null;
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));

		try {
			Integer intId = Integer.parseInt(ontologyVersionId);
			ontologyBean = ontologyService.findOntology(intId);

			response.setStatus(Status.SUCCESS_OK);

			// if ontologyBean is not found, set Error in the Status object
			if (ontologyBean == null || ontologyBean.getId() == null) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, MessageUtils
						.getMessage("msg.error.ontologyNotFound"));
			}
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe
					.getMessage());
			nfe.printStackTrace();
			log.error(nfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		}

		return ontologyBean;
	}

	private String prepareVersion(String version) {
		if (!StringHelper.isNullOrNullString(version)) {
			return "_v" + version.trim().replaceAll("[\\s\\t]+", "_");
		}

		return "";
	}

	private String[] splitFilename(String filename) {
		String[] splitFilename = null;

		if (!StringHelper.isNullOrNullString(filename)) {
			splitFilename = new String[2];
			splitFilename[0] = filename;
			splitFilename[1] = "";

			int lastDot = filename.lastIndexOf('.');
			String name = filename.substring(0, lastDot);
			String ext = filename.substring(lastDot + 1);

			if (!StringHelper.isNullOrNullString(name)
					&& !StringHelper.isNullOrNullString(ext)) {
				splitFilename[0] = name;
				splitFilename[1] = ext;
			}
		}

		return splitFilename;
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
