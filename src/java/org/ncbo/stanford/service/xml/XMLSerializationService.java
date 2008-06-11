package org.ncbo.stanford.service.xml;

import java.util.List;

import javax.xml.transform.TransformerException;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.response.SuccessBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * An interface that allows predefined structure of XML responses
 * 
 * @author Michael Dorf
 * 
 */
public interface XMLSerializationService {

	/**
	 * Generate an XML representation of a specific error
	 * 
	 * @param errorType
	 * @return
	 */
	public String getErrorAsXML(ErrorTypeEnum errorType, String accessedResource);

	/**
	 * Generate an XML representation of a specific error.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public String getErrorAsXML(Request request, Response response);
	
	public String getSuccessAsXML(SuccessBean successBean);
	
	public SuccessBean getSuccessBean(Request request);
	
	public SuccessBean getSuccessBean(Request request, Object data);
	
	
	/**
	 * Generate an XML representation of a successfully processed request with
	 * XSL Transformation. This should only be used when no other XML response
	 * is expected (i.e. authentication).
	 * 
	 * @param sessionId
	 * @param accessedResource
	 * @param data
	 * @param xsltFile
	 * @return String
	 * @throws TransformerException
	 */
	public String applyXSL(Object data, String xsltFile) throws TransformerException;
	
	public void generateXMLResponse(Request request, Response response,
			Object data);
	
	public void generateXMLResponse(Request request, Response response,
			Object data, String xsltFile);	

	public void generateStatusXMLResponse (Request request, Response response) ;
	

}
