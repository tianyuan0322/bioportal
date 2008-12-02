package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.CommonsFileUploadFilePathHandlerImpl;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologiesRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(OntologiesRestlet.class);
	private OntologyService ontologyService;
	private XMLSerializationService xmlSerializationService;

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
		List<OntologyBean> ontologyList = null;

		try {
			ontologyList = ontologyService.findLatestOntologyVersions();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML with XSL
			String xslFile = MessageUtils.getMessage("xsl.ontology.findall");
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
		OntologyBean ontologyBean = BeanHelper
				.populateOntologyBeanFromRequest(request);

		// create the ontology
		try {
			// no file handler for remote case since there is no file to upload.
			if (ontologyBean.isRemote()) {
				ontologyService.createOntology(ontologyBean, null);
			} else {
				FilePathHandler filePathHandler = new CommonsFileUploadFilePathHandlerImpl(
						CompressedFileHandlerFactory
								.createFileHandler(ontologyBean.getFormat()),
						ontologyBean.getFileItem());

				ontologyService.createOntology(ontologyBean, filePathHandler);
			}
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyBean);
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
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}