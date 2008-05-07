package org.ncbo.stanford.service.xml.impl;

import org.apache.commons.validator.GenericValidator;

import org.restlet.data.Status;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.response.ErrorBean;
import org.ncbo.stanford.bean.response.ErrorStatusBean;
import org.ncbo.stanford.bean.response.SuccessBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.constants.ApplicationConstants;

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
		xstream.alias(ApplicationConstants.SUCCESS_XML_TAG_NAME, SuccessBean.class);

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
	public String getSuccessAsXML(String sessionId, String accessedResource, Object data) {
		String xmlResult = ApplicationConstants.XML_DECLARATION + "\n";
		XStream xstream = new XStream();
//		xstream.alias(ApplicationConstants.RESPONSE_XML_TAG_NAME, SuccessBean.class);

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
		xstream.alias("ontology", OntologyBean.class);
		xstream.alias("user", UserBean.class);
		xstream.alias(ApplicationConstants.RESPONSE_XML_TAG_NAME, SuccessBean.class);
		xstream.alias(ApplicationConstants.ERROR_XML_TAG_NAME, ErrorBean.class);			
	}
	
	
	
	
	
	
	
	/**
	 * Generate Error XML from status object.
	 * Note that ErrorStatusBean is used instead of ErrorBean. 
	 * Status object is passed around instead of in-house ErrorTypeEnum object.
	 * 
	 * cyoun
	 * 
	 * @param status
	 * @param accessedResource
	 * @return
	 */
	public String getErrorAsXML (Status status, String accessedResource) {
				
		String xmlResult = ApplicationConstants.XML_DECLARATION + "\n";
		
		XStream xstream = new XStream();
		xstream.alias(ApplicationConstants.ERROR_XML_TAG_NAME, ErrorStatusBean.class);

		ErrorStatusBean errorStatusBean = new ErrorStatusBean(status);
		
		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			errorStatusBean.setAccessedResource(accessedResource);
		}	
		
		return xmlResult + xstream.toXML(errorStatusBean);
		
	}
	
	
	
}
