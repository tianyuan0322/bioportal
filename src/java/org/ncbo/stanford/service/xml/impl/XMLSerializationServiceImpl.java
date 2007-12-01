package org.ncbo.stanford.service.xml.impl;

import org.apache.commons.validator.GenericValidator;
import org.ncbo.stanford.bean.response.ErrorBean;
import org.ncbo.stanford.bean.response.SuccessBean;
import org.ncbo.stanford.enumeration.ErrorType;
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
	public String getErrorAsXML(ErrorType errorType, String accessedResource) {
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
}
