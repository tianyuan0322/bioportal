package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.CommonsFileUploadFilePathHandlerImpl;


public class OntologiesRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(OntologiesRestlet.class);
	private OntologyService ontologyService;
	private XMLSerializationService xmlSerializationService;

	@Override
	public void handle(Request request, Response response) {

		if (request.getMethod().equals(Method.GET)) {
			getRequest(request, response);
	
		} else if (request.getMethod().equals(Method.POST)) {
			postRequest(request, response);
		} 
	}

	/**
	 * Handle GET calls here
	 */
	private void getRequest(Request request, Response response) {
		listOntologies(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	private void postRequest(Request request, Response response) {
		
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
			ontologyList = getOntologyService().findLatestOntologyVersions();

		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
			
		} finally {
					
			// generate response XML with XSL
			String xslFile = MessageUtils.getMessage("xsl.ontology.findall");
			getXmlSerializationService().generateXMLResponse (request, response, ontologyList, xslFile);
		}

	}


	/**
	 * Return to the response creating Ontology
	 * 
	 * @param request response
	 */
	private void createOntology(Request request, Response response) {
		
		OntologyBean ontologyBean = BeanHelper.populateOntologyBeanFromRequest(request);
		
		// set userId from request
		HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
		String userId = httpServletRequest.getParameter( MessageUtils.getMessage("http.param.userId"));
		ontologyBean.setUserId(new Integer(userId));
		
		// create the ontology
		try {
			
			FilePathHandler filePathHandler = new CommonsFileUploadFilePathHandlerImpl(
					CompressedFileHandlerFactory.createFileHandler(ontologyBean
							.getFormat()), ontologyBean.getFileItem());

			getOntologyService().createOntology(ontologyBean, filePathHandler);

		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
			
		} finally {
		
			// generate response XML
			getXmlSerializationService().generateXMLResponse (request, response, ontologyBean);
		}

	}
	
	/**
	 * @return the ontologyService
	 */
	public OntologyService getOntologyService() {
		return ontologyService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @return the xmlSerializationService
	 */
	public XMLSerializationService getXmlSerializationService() {
		return xmlSerializationService;
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