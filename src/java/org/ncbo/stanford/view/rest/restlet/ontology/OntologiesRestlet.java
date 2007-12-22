package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.upload.UploadService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.DateHelper;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;

public class OntologiesRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(OntologiesRestlet.class);
	
	private UploadService uploadService;
	private OntologyService ontologyService;
	private XMLSerializationService xmlSerializationService;

	@Override
	public void handle(Request request, Response response) {
		
		if(request.getMethod().equals(Method.GET)){
			getRequest(request, response);
		}else if(request.getMethod().equals(Method.POST)){
			postRequest(request, response);
		}
		
	}
	
	
	/**
	 * Handle GET calls here
	 */
	private void getRequest(Request request,Response response){
		listOntologies(request,response);	
	}
	
	
	/**
	 * Handle POST calls here
	 * @param request
	 * @param response
	 */
	private void postRequest(Request request,Response response){
		Form form = request.getEntityAsForm();

		// 1/ Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(10002400);
		
		
		RestletFileUpload rfu = new RestletFileUpload(factory);
		rfu.setSizeMax(10000000);
		try{		
		
		List<FileItem> files = 	rfu.parseRepresentation(request.getEntity());
		uploadService.uploadOntology(files.get(0).getInputStream(), buildBeanFromForm(form));
		
		} catch (Exception e) {
			RequestUtils.setHttpServletResponse(response,
					Status.CLIENT_ERROR_BAD_REQUEST, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(
							ErrorTypeEnum.INVALID_FILE,null));
			e.printStackTrace();
			return;
		}
		
		
		
		
		RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
				MediaType.TEXT_XML, xmlSerializationService
						.getSuccessAsXML(RequestUtils.getSessionId(request),
								null));
	}
	
	/**
	 * Return to the response a listing of ontologies
	 * @param response
	 */
	private void listOntologies(Request request, Response response) {
		List<OntologyBean> ontList = ontologyService
				.findLatestOntologyVersions();
		RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
				MediaType.TEXT_XML, xmlSerializationService.getSuccessAsXML(
						RequestUtils.getSessionId(request), request
								.getResourceRef().getPath(), ontList));
	}
	
	
	private OntologyBean buildBeanFromForm(Form form){
		OntologyBean bean = new OntologyBean();
		Set<String> names = form.getNames();
		bean.setContactEmail(form.getValues("contactEmail"));
		bean.setContactName(form.getValues("contactName"));
		bean.setDateCreated(DateHelper.getDateFrom(form.getValues("dateCreated")));
		bean.setDateReleased(DateHelper.getDateFrom(form.getValues("dateReleased")));
		bean.setDisplayLabel(form.getValues("displayLabel"));
		bean.setDocumentation(form.getValues("documentation"));
		bean.setFormat(form.getValues("format"));
		bean.setHomepage(form.getValues("homepage"));
		bean.setIsFoundry(Byte.parseByte(form.getValues("isFoundry")));
		bean.setPublication(form.getValues("publication"));
		bean.setUrn(form.getValues("urn"));
		bean.setVersionNumber(form.getValues("versionNumber"));
		bean.setVersionStatus(form.getValues("versionStatus"));
		return bean;
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
	 * @return the uploadService
	 */
	public UploadService getUploadService() {
		return uploadService;
	}

	/**
	 * @param uploadService
	 *            the uploadService to set
	 */
	public void setUploadService(UploadService uploadService) {
		this.uploadService = uploadService;
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
