package org.ncbo.stanford.view.rest.restlet.ontology;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.CommonsFileUploadFilePathHandlerImpl;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.URIUploadFilePathHandlerImpl;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class OntologiesRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(OntologiesRestlet.class);
	private OntologyService ontologyService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		listOntologies(request, response);
	}

	/**
	 * Handle POST calls here
	 *
	 * @param request
	 * @param response
	 */
	@Override
	public void postRequest(Request request, Response response) {
		createOntology(request, response);
	}

	/**
	 * Handle PUT calls here
	 */
	@Override
	public void putRequest(Request request, Response response) {
		reloadMetadataOntology(request, response);
	}

	/**
	 * Handle DELETE calls here
	 *
	 * @param request
	 * @param response
	 */
	@Override
	public void deleteRequest(Request request, Response response) {
		deleteOntologiesOrViews(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 *
	 * @param response
	 */
	private void listOntologies(Request request, Response response) {
		List<OntologyBean> ontologyList = null;
		String lastSegment = request.getResourceRef().getLastSegment();

		try {
			if (lastSegment.equals(RequestParamConstants.PARAM_ACTIVE)) {
				ontologyList = ontologyService
						.findLatestActiveOntologyVersions();
			} else if (lastSegment.equals(RequestParamConstants.PARAM_PULLED)) {
				ontologyList = ontologyService
						.findLatestAutoPulledOntologyVersions();
			} else {
				ontologyList = ontologyService.findLatestOntologyVersions();
			}
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML with XSL
			// String xslFile = MessageUtils.getMessage("xsl.ontology.findall");
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyList);
		}
	}

	/**
	 * Return to the response creating Ontology
	 *
	 * @param request
	 *            response
	 */
	private void createOntology(Request request, Response response) {
		OntologyBean ontologyBean = BeanHelper
				.populateOntologyBeanFromRequest(request);

		// create the ontology
		try {
			// no file handler for remote case since there is no file to upload.
			FilePathHandler filePathHandler = null;
			if (ontologyBean.isMetadataOnly()) {
				filePathHandler = null;
			} else if (StringUtils.isNotBlank(ontologyBean
					.getDownloadLocation())) {
				filePathHandler = new URIUploadFilePathHandlerImpl(
						CompressedFileHandlerFactory
								.createFileHandler(ontologyBean.getFormat()),
						new URI(ontologyBean.getDownloadLocation()));
			} else {
				filePathHandler = new CommonsFileUploadFilePathHandlerImpl(
						CompressedFileHandlerFactory
								.createFileHandler(ontologyBean.getFormat()),
						ontologyBean.getFileItem());
			}
			ontologyService.createOntologyOrView(ontologyBean, filePathHandler);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.toString());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyBean);
		}
	}

	private void reloadMetadataOntology(Request request, Response response) {
		try {
			ontologyService.reloadMetadataOntology();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		}
	}

	/**
	 * Delete several ontologies4
	 *
	 * @param request
	 * @param response
	 */
	private void deleteOntologiesOrViews(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String removeMetadata = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_REMOVE_METADATA);
		String removeOntologyFiles = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_REMOVE_ONTOLOGY_FILES);

		try {
			List<Integer> ontologyVersionIds = getOntologyVersionIds(httpRequest);
			boolean removeMetadataBool = RequestUtils
					.parseBooleanParam(removeMetadata);
			boolean removeOntologyFilesBool = RequestUtils
					.parseBooleanParam(removeOntologyFiles);

			if (ontologyVersionIds != null) {
				ontologyService.deleteOntologiesOrViews(ontologyVersionIds,
						removeMetadataBool, removeOntologyFilesBool);
				List<String> errorOntologies = ontologyService
						.getErrorOntologies();

				if (!errorOntologies.isEmpty()) {
					throw new Exception(
							"Error Deprecating/Deleting Ontologies: "
									+ errorOntologies);
				}
			} else {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidsinvalid"));
			}
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		}
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}
}