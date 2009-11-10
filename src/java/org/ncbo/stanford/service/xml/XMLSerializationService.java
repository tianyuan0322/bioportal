package org.ncbo.stanford.service.xml;

import java.util.HashMap;

import javax.xml.transform.TransformerException;

import org.ncbo.stanford.bean.response.AbstractResponseBean;
import org.ncbo.stanford.bean.response.SuccessBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.restlet.data.Request;
import org.restlet.data.Response;

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
	 * @param accessedResource
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
	 * returns SuccessBean with sessionId, accessedResource and data populated
	 */
	public SuccessBean getSuccessBean(String sessionId, Request request,
			Object data);

	/**
	 * Generate an XML representation of a successfully processed request with
	 * XSL Transformation.
	 * 
	 * @param request
	 * @param data
	 * @param xsltFile
	 * @throws TransformerException
	 */
	public String applyXSL(Request request, Object data, String xsltFile)
			throws TransformerException;

	public void generateXMLResponse(Request request, Response response,
			Object data);

	public void generateXMLResponse(Request request, Response response,
			Object data, String xsltFile);

	public void generateStatusXMLResponse(Request request, Response response);

	public AbstractResponseBean processGet(String baseUrl,
			HashMap<String, String> getParams) throws Exception;

	@SuppressWarnings("unchecked")
	public void addImplicitCollection(Class ownerType, String fieldName);

	@SuppressWarnings("unchecked")
	public void omitField(Class definedIn, String fieldName);

	@SuppressWarnings("unchecked")
	public void alias(String name, Class type);

	@SuppressWarnings("unchecked")
	public void aliasField(String alias, Class definedIn, String fieldName);

	public Object fromXML(String xml);
}
