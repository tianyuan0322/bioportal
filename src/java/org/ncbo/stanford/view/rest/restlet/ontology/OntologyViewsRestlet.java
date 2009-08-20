package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
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
	private OntologyService ontologyService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		listOntologyViews(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void postRequest(Request request, Response response) {
		createOntologyView(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void listOntologyViews(Request request, Response response) {
		List<OntologyBean> ontologyList = null;

		try {
			ontologyList = ontologyService.findLatestOntologyViewVersions();
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
	private void createOntologyView(Request request, Response response) {
		OntologyBean ontologyViewBean = BeanHelper
				.populateOntologyBeanFromRequest(request);

		//if this RESTlet got called we set the isView flag to true explicitly 
		ontologyViewBean.setView(true);
		
		// create the ontology
		try {
			// no file handler for remote case since there is no file to upload.
			if (ontologyViewBean.isRemote()) {
				ontologyService.createOntologyOrView(ontologyViewBean, null);
			} else {
				FilePathHandler filePathHandler = new CommonsFileUploadFilePathHandlerImpl(
						CompressedFileHandlerFactory
								.createFileHandler(ontologyViewBean.getFormat()),
						ontologyViewBean.getFileItem());

				ontologyService.createOntologyOrView(ontologyViewBean, filePathHandler);
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
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}
}