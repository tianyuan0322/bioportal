package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.service.ontology.OntologyViewService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.CommonsFileUploadFilePathHandlerImpl;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyViewsRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(OntologyViewsRestlet.class);
	private OntologyViewService ontologyViewService;

	/**
	 * Handle GET calls here
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		listOntologies(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	protected void postRequest(Request request, Response response) {
		createOntology(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void listOntologies(Request request, Response response) {
		List<OntologyViewBean> ontologyList = null;

		try {
			ontologyList = ontologyViewService.findLatestOntologyViewVersions();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML with XSL
			String xslFile = MessageUtils.getMessage("xsl.ontology.view.findall");
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyList, xslFile);
		}
	}

	/**
	 * Return to the response creating Ontology
	 * 
	 * @param request
	 *            response
	 */
	private void createOntology(Request request, Response response) {
		OntologyViewBean ontologyViewBean = BeanHelper
				.populateOntologyViewBeanFromRequest(request);

		// create the ontology
		try {
			// no file handler for remote case since there is no file to upload.
			if (ontologyViewBean.isRemote()) {
				ontologyViewService.createOntologyView(ontologyViewBean, null);
			} else {
				FilePathHandler filePathHandler = new CommonsFileUploadFilePathHandlerImpl(
						CompressedFileHandlerFactory
								.createFileHandler(ontologyViewBean.getFormat()),
						ontologyViewBean.getFileItem());

				ontologyViewService.createOntologyView(ontologyViewBean, filePathHandler);
			}
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyViewBean);
		}
	}

	/**
	 * @param ontologyViewService
	 *            the ontologyViewService to set
	 */
	public void setOntologyViewService(OntologyViewService ontologyViewService) {
		this.ontologyViewService = ontologyViewService;
	}
}