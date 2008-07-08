package org.ncbo.stanford.service.xml.impl;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.bean.concept.PropertyBean;
import org.ncbo.stanford.bean.response.ErrorBean;
import org.ncbo.stanford.bean.response.ErrorStatusBean;
import org.ncbo.stanford.bean.response.SuccessBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.ontology.impl.OntologyServiceImpl;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.TraxSource;

public class XMLSerializationServiceImpl implements XMLSerializationService {

	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);

	private static HashMap<String, Transformer> transformers = new HashMap<String, Transformer>();
	private static XStream xstream = null;

	/**
	 * Generate an XML representation of a specific error This is going to
	 * retire when ErrorTypeEnum is replaced with Restlet.Status object - cyoun
	 * 
	 * @param errorType
	 * @return
	 */
	public String getErrorAsXML(ErrorTypeEnum errorType, String accessedResource) {
		String xmlHeader = ApplicationConstants.XML_DECLARATION + "\n";

		getXStreamInstance().omitField(ErrorBean.class, "errorType");
		ErrorBean errorBean = new ErrorBean(errorType);

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			errorBean.setAccessedResource(accessedResource);
		}

		return xmlHeader + getXStreamInstance().toXML(errorBean);
	}

	/**
	 * Generate an XML representation of a specific error.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public String getErrorAsXML(Request request, Response response) {

		String xmlHeader = ApplicationConstants.XML_DECLARATION + "\n";

		String accessedResource = request.getResourceRef().getPath();

		ErrorStatusBean errorStatusBean = new ErrorStatusBean(response
				.getStatus());

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			errorStatusBean.setAccessedResource(accessedResource);
		}

		return xmlHeader + getXStreamInstance().toXML(errorStatusBean);

	}

	/**
	 * Generate an XML representation of a successfully processed request. This
	 * should only be used when no other XML response is expected (i.e.
	 * authentication).
	 * 
	 * @param successBean
	 * @return String
	 */
	public String getErrorAsXML(ErrorStatusBean errorStatusBean) {

		String xmlHeader = ApplicationConstants.XML_DECLARATION + "\n";

		return xmlHeader + getXStreamInstance().toXML(errorStatusBean);

	}

	/**
	 * Generate an XML representation of a successfully processed request. This
	 * should only be used when no other XML response is expected (i.e.
	 * authentication).
	 * 
	 * @param successBean
	 * @return String
	 */
	public String getSuccessAsXML(SuccessBean successBean) {

		String xmlHeader = ApplicationConstants.XML_DECLARATION + "\n";

		return xmlHeader + getXStreamInstance().toXML(successBean);

	}

	/**
	 * returns SuccessBean with sessionId, accessedResource populated
	 */
	public ErrorStatusBean getErrorBean(Request request, Response response) {

		String accessedResource = request.getResourceRef().getPath();
		ErrorStatusBean errorStatusBean = new ErrorStatusBean(response
				.getStatus());

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			errorStatusBean.setAccessedResource(accessedResource);
		}

		return errorStatusBean;
	}

	/**
	 * returns SuccessBean with sessionId, accessedResource populated
	 */
	public SuccessBean getSuccessBean(Request request) {
		String sessionId = RequestUtils.getSessionId(request);
		String accessedResource = request.getResourceRef().getPath();

		SuccessBean successBean = new SuccessBean();
		successBean.setSessionId(sessionId);

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			successBean.setAccessedResource(accessedResource);
		}

		return successBean;
	}

	/**
	 * returns SuccessBean with sessionId, accessedResource and data populated
	 */
	public SuccessBean getSuccessBean(Request request, Object data) {
		SuccessBean successBean = getSuccessBean(request);

		if (data != null) {
			successBean.getData().add(data);
		}

		return successBean;
	}

	/**
	 * returns SuccessBean with sessionId, accessedResource and data populated
	 */
	public SuccessBean getSuccessBean(String sessionId, Request request,
			Object data) {
		SuccessBean successBean = getSuccessBean(request);
		successBean.setSessionId(sessionId);

		if (data != null) {
			successBean.getData().add(data);
		}

		return successBean;
	}

	/**
	 * Generate an XML representation of a successfully processed request with
	 * XSL Transformation. This should only be used when no other XML response
	 * is expected (i.e. authentication).
	 * 
	 * @param data
	 * @param xsltFile
	 * @return String
	 * @throws TransformerException
	 */
	public String applyXSL(Object data, String xsltFile)
			throws TransformerException {

		// SuccessBean is not being used in findAll type of restlet requests....
		// - cyoun

		// create source
		TraxSource traxSource = new TraxSource(data, getXStreamInstance());
		traxSource.setSourceAsList(Arrays.asList(data));

		// create buffer for XML output
		Writer buffer = new StringWriter();

		getTransformerInstance(xsltFile).transform(traxSource,
				new StreamResult(buffer));

		return buffer.toString();
	}

	/**
	 * Generates Generic XML response which contains status info whether success
	 * or fail. session id and access resource info is included.
	 * 
	 * @param request
	 *            response the userService to set
	 */
	public void generateStatusXMLResponse(Request request, Response response) {

		if (!response.getStatus().isError()) {

			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML,
					getSuccessAsXML(getSuccessBean(request)));

		} else {

			RequestUtils.setHttpServletResponse(response, response.getStatus(),
					MediaType.TEXT_XML, getErrorAsXML(getErrorBean(request,
							response)));

		}

	}

	/**
	 * Generates XML response. If SUCCESS - Entity info is displayed. else -
	 * Error info is displayed.
	 * 
	 * @param request
	 *            response data
	 */

	public void generateXMLResponse(Request request, Response response,
			Object data) {

		// SUCCESS, include the bean info
		if (!response.getStatus().isError()) {

			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML, getSuccessAsXML(getSuccessBean(request,
							data)));

			// if ERROR, just status, no bean info
		} else {
			generateStatusXMLResponse(request, response);
		}
	}

	/**
	 * Generates XML response then apply XSL transformation. This is useful to
	 * filter huge XML response such as findAll() Ontologies.
	 * 
	 * If SUCCESS - Entity info is displayed. else - Error info is displayed.
	 * 
	 * @param request
	 *            response data
	 */

	public void generateXMLResponse(Request request, Response response,
			Object data, String xsltFile) {

		// if SUCCESS, include the bean info
		if (!response.getStatus().isError()) {

			try {
				RequestUtils.setHttpServletResponse(response,
						Status.SUCCESS_OK, MediaType.TEXT_XML, applyXSL(data,
								xsltFile));
			} catch (TransformerException e) {
				// XML parse ERROR
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				generateStatusXMLResponse(request, response);
				e.printStackTrace();
				log.error(e);
			}

			// if ERROR, just status, no bean info
		} else {
			generateStatusXMLResponse(request, response);
		}
	}

	/**
	 * returns a singleton transformer instance for a XSL file specified. do not
	 * use synchronized since it is expensive.
	 */
	public static Transformer getTransformerInstance(String xslFile)
			throws TransformerException {

		Transformer transformer = (Transformer) transformers.get(xslFile);
		if (transformer == null) {
			File ontologyXSLT = new File(xslFile);
			transformer = TransformerFactory.newInstance().newTransformer(
					new StreamSource(ontologyXSLT));
			transformers.put(xslFile, transformer);
		}
		return transformer;
	}

	/**
	 * returns a singleton XStream instance. do not use synchronized since it is
	 * expensive.
	 */
	public static XStream getXStreamInstance() {

		if (xstream == null) {
			xstream = new XStream();

			setXStreamAliases(xstream);
		}
		return xstream;
	}

	/**
	 * set aliases for XStream
	 */
	private static void setXStreamAliases(XStream xstream) {

		xstream.alias(MessageUtils.getMessage("entity.ontology"),
				OntologyBean.class);

		xstream.alias(MessageUtils.getMessage("entity.user"), UserBean.class);
		xstream.omitField(UserBean.class, "password");

		xstream.alias(MessageUtils.getMessage("entity.classbean"),
				ClassBean.class);
		xstream.alias(MessageUtils.getMessage("entity.propertybean"),
				PropertyBean.class);
		xstream.alias(MessageUtils.getMessage("entity.instancebean"),
				InstanceBean.class);
		xstream.alias(MessageUtils.getMessage("entity.searchbean"),
				SearchResultBean.class);
		xstream.alias(ApplicationConstants.RESPONSE_XML_TAG_NAME,
				SuccessBean.class);
		xstream.alias(ApplicationConstants.ERROR_XML_TAG_NAME, ErrorBean.class);
		xstream.alias(ApplicationConstants.SUCCESS_XML_TAG_NAME,
				SuccessBean.class);

	}

}
