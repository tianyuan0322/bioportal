package org.ncbo.stanford.service.xml.impl;

import java.util.List;

import org.apache.commons.validator.GenericValidator;

import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.response.ErrorBean;
import org.ncbo.stanford.bean.response.ErrorStatusBean;
import org.ncbo.stanford.bean.response.SuccessBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;

import com.thoughtworks.xstream.XStream;

public class XMLSerializationServiceImpl implements XMLSerializationService {

	/**
	 * Generate an XML representation of a specific error
	 * 
	 * @param errorType
	 * @return
	 */
	public String getErrorAsXML(ErrorTypeEnum errorType, String accessedResource) {
		String xmlResult = ApplicationConstants.XML_DECLARATION + "\n";

		XStream xstream = new XStream();
		xstream.alias(ApplicationConstants.ERROR_XML_TAG_NAME, ErrorBean.class);
		xstream.omitField(ErrorBean.class, "errorType");
		ErrorBean errorBean = new ErrorBean(errorType);

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			errorBean.setAccessedResource(accessedResource);
		}

		return xmlResult + xstream.toXML(errorBean);
	}

	/**
	 * Generate an XML representation of a successfully processed request. This
	 * should only be used when no other XML response is expected (i.e.
	 * authentication).
	 * 
	 * @param errorType
	 * @return
	 */
	public String getSuccessAsXML(String sessionId, String accessedResource) {
		String xmlResult = ApplicationConstants.XML_DECLARATION + "\n";

		XStream xstream = new XStream();
		xstream.alias(ApplicationConstants.SUCCESS_XML_TAG_NAME,
				SuccessBean.class);

		SuccessBean successBean = new SuccessBean();
		successBean.setSessionId(sessionId);

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			successBean.setAccessedResource(accessedResource);
		}

		return xmlResult + xstream.toXML(successBean);
	}

	/**
	 * Generate an XML representation of a successfully processed request. This
	 * should only be used when no other XML response is expected (i.e.
	 * authentication).
	 * 
	 * @param errorType
	 * @return
	 */
	public String getSuccessAsXML(String sessionId, String accessedResource,
			Object data) {
		String xmlResult = ApplicationConstants.XML_DECLARATION + "\n";
		XStream xstream = new XStream();
		// xstream.alias(ApplicationConstants.RESPONSE_XML_TAG_NAME,
		// SuccessBean.class);

		setXStreamAliases(xstream);

		SuccessBean successBean = new SuccessBean();
		successBean.setSessionId(sessionId);
		successBean.setData(data);

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			successBean.setAccessedResource(accessedResource);
		}

		return xmlResult + xstream.toXML(successBean);
	}

	private void setXStreamAliases(XStream xstream) {
		
		//xstream.alias(RequestParamConstants.ENTITY_ONTOLOGY, OntologyBean.class);
		//xstream.alias(RequestParamConstants.ENTITY_USER, UserBean.class);
		xstream.alias(MessageUtils.getMessage("entity.ontology"), OntologyBean.class);
		xstream.alias(MessageUtils.getMessage("entity.user"), UserBean.class);
		xstream.alias(ApplicationConstants.RESPONSE_XML_TAG_NAME,
				SuccessBean.class);
		xstream.alias(ApplicationConstants.ERROR_XML_TAG_NAME, ErrorBean.class);
	}

	
	/**
	 * Generate Error XML from status object. Note that ErrorStatusBean is used
	 * instead of ErrorBean. Status object is passed around instead of in-house
	 * ErrorTypeEnum object.
	 * 
	 * @author cyoun
	 * 
	 * @param status
	 * @param accessedResource
	 * @return
	 */
	public String getErrorAsXML(Status status, String accessedResource) {

		String xmlResult = ApplicationConstants.XML_DECLARATION + "\n";

		XStream xstream = new XStream();
		xstream.alias(ApplicationConstants.ERROR_XML_TAG_NAME,
				ErrorStatusBean.class);

		ErrorStatusBean errorStatusBean = new ErrorStatusBean(status);

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			errorStatusBean.setAccessedResource(accessedResource);
		}

		return xmlResult + xstream.toXML(errorStatusBean);

	}

	
	/**
	 * Generates Generic XML response which contains status info whether success
	 * or fail. session id and access resource info is included.
	 * 
	 * @param request
	 *            response the userService to set
	 */
	public void generateStatusXMLResponse(Request request, Response response) {

		String accessedResource = request.getResourceRef().getPath();

		if (!response.getStatus().isError()) {

			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML, getSuccessAsXML(RequestUtils
							.getSessionId(request), accessedResource));

		} else {

			RequestUtils.setHttpServletResponse(response, response.getStatus(),
					MediaType.TEXT_XML, getErrorAsXML(response.getStatus(),
							accessedResource));

		}

	}

	/**
	 * Generates XML response.
	 * If SUCCESS - Entity info is displayed.
	 * else - Error info is displayed.
	 * 
	 * @param request response data 
	 */
	
	public void generateXMLResponse(Request request, Response response,
			Object data) {
		
		String sessionId = RequestUtils.getSessionId(request);
		String accessedResource = request.getResourceRef().getPath();
		
		// if SUCCESS, include the bean info
		if (!response.getStatus().isError()) {
		
			/*
			// prepare SuccessBean which contains sessionId and Data
			getSuccessBean(request, response, data);
			
			// prepare XML from SuccessBean.
			// note that SuccessBean and DataBean has be to registered with XStream
			String xmlResult = ApplicationConstants.XML_DECLARATION + "\n";
			String xmlResponse = xmlResult + getXStream().toXML(data);
			
			response.setEntity(xmlResponse, MediaType.APPLICATION_XML);
			*/
			
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML, getSuccessAsXML(sessionId, accessedResource, data));
		
		// if ERROR, just status, no bean info
		} else {

			generateStatusXMLResponse(request, response);

		}

		
	}
	

	/**
	 * Generates User Specific XML response which contains status info If
	 * success, user info is included.
	 * 
	 * @param request
	 *            response the userService to set
	 */
	// TODO - make this generic
	
	public void generateUserListXMLResponse(Request request, Response response,
			List<UserBean> userList) {

		String sessionId = RequestUtils.getSessionId(request);
		String accessedResource = request.getResourceRef().getPath();
		
		if (userList.size() > 0) {
						
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML, getSuccessAsXML(sessionId, accessedResource, userList));
					
			
			
		} else {

			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "User Not found");
			generateStatusXMLResponse(request, response);
		}		
	}
	
	
	/*
	private SuccessBean getSuccessBean(Request request, Response response) {
		
		String sessionId = RequestUtils.getSessionId(request);
		String accessedResource = request.getResourceRef().getPath();

		SuccessBean successBean = new SuccessBean();
		successBean.setSessionId(sessionId);

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			successBean.setAccessedResource(accessedResource);
		}
		
		return successBean;
	}
	
	
	private SuccessBean getSuccessBean(Request request, Response response, Object data) {
		
		String sessionId = RequestUtils.getSessionId(request);
		String accessedResource = request.getResourceRef().getPath();

		SuccessBean successBean = new SuccessBean();
		successBean.setSessionId(sessionId);
		successBean.setData(data);

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			successBean.setAccessedResource(accessedResource);
		}
		
		return successBean;
	}
	
	//TODO - use Constants, not String
	private XStream getXStream() {
		
		XStream xstream = new XStream();
		
		// register entities here
		xstream.alias(RequestParamConstants.ENTITY_ONTOLOGY, OntologyBean.class);
		xstream.alias(RequestParamConstants.ENTITY_USER, UserBean.class);
		xstream.alias(ApplicationConstants.RESPONSE_XML_TAG_NAME,
				SuccessBean.class);
		xstream.alias(ApplicationConstants.ERROR_XML_TAG_NAME, ErrorStatusBean.class);
		
		return xstream;
	}
	*/

}
