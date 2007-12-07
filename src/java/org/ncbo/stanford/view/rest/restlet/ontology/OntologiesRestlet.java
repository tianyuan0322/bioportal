package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.upload.UploadService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

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
		listOntologies(response);	
	}
	
	
	/**
	 * Handle POST calls here
	 * @param request
	 * @param response
	 */
	private void postRequest(Request request,Response response){		
		RestletFileUpload rfu = new RestletFileUpload();
		try{
		List files = rfu.parseRequest(request);
		} catch (FileUploadException fue) {
			RequestUtils.setHttpServletResponse(response,
					Status.CLIENT_ERROR_BAD_REQUEST, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(
							ErrorTypeEnum.INVALID_FILE,null));
			return;
		}
		RequestUtils
		.setHttpServletResponse(response, Status.SUCCESS_OK,
				MediaType.TEXT_XML, xmlSerializationService
						.getSuccessAsXML(RequestUtils.getSessionId(request),
								null));
	}
	
	/**
	 * Return to the response a listing of ontologies
	 * @param response
	 */
	private void listOntologies(Response response) {
		List<OntologyBean> ontList = ontologyService.findLatestOntologyVersions();
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("OntologyBean", OntologyBean.class);
		response.setEntity(xstream.toXML(ontList), MediaType.APPLICATION_XML);
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
}
