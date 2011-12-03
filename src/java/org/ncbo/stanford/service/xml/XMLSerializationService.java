package org.ncbo.stanford.service.xml;

import java.util.HashMap;

import org.ncbo.stanford.bean.response.AbstractResponseBean;
import org.restlet.Request;
import org.restlet.Response;

import com.thoughtworks.xstream.XStream;

/**
 * An interface that allows predefined structure of XML responses
 * 
 * @author Michael Dorf
 * 
 */
public interface XMLSerializationService {

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

	public XStream getXmlSerializer();
}
