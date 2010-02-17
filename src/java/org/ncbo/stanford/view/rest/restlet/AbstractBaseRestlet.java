package org.ncbo.stanford.view.rest.restlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.logging.UsageLoggingService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * An abstract restlet that contains common functionality as well as default
 * handling for GET, POST, PUT, and DELETE methods
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractBaseRestlet extends Restlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(AbstractBaseRestlet.class);

	protected XMLSerializationService xmlSerializationService;
	protected UsageLoggingService usageLoggingService;

	/**
	 * Handle requests
	 */
	@Override
	public void handle(Request request, Response response) {
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);

		if (request.getMethod().equals(Method.GET)) {
			getRequest(request, response);
		} else if (request.getMethod().equals(Method.POST)) {
			String method = httpServletRequest.getParameter(MessageUtils
					.getMessage("http.param.method"));

			if (method == null) {
				postRequest(request, response);
			} else {
				if (method
						.equalsIgnoreCase(MessageUtils.getMessage("http.put"))) {
					putRequest(request, response);
				} else if (method.equalsIgnoreCase(MessageUtils
						.getMessage("http.delete"))) {
					deleteRequest(request, response);
				}
			}
		} else if (request.getMethod().equals(
				MessageUtils.getMessage("http.put"))) {
			putRequest(request, response);
		} else if (request.getMethod().equals(
				MessageUtils.getMessage("http.delete"))) {
			deleteRequest(request, response);
		}
	}

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	public void getRequest(Request request, Response response) {
		// no GET requests supported
		unsupportedMethod(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	public void postRequest(Request request, Response response) {
		// no POST requests supported
		unsupportedMethod(request, response);
	}

	/**
	 * Handle PUT calls here
	 * 
	 * @param request
	 * @param response
	 */
	public void putRequest(Request request, Response response) {
		// no PUT requests supported
		unsupportedMethod(request, response);
	}

	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	public void deleteRequest(Request request, Response response) {
		// no DELETE requests supported
		unsupportedMethod(request, response);
	}

	private void unsupportedMethod(Request request, Response response) {
		response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
		xmlSerializationService.generateStatusXMLResponse(request, response);
	}

	protected List<Integer> getOntologyVersionIds(HttpServletRequest httpRequest)
			throws Exception {
		return getIntegerList(httpRequest,
				RequestParamConstants.PARAM_ONTOLOGY_VERSION_IDS);
	}

	protected List<Integer> getOntologyIds(HttpServletRequest httpRequest)
			throws Exception {
		return getIntegerList(httpRequest,
				RequestParamConstants.PARAM_ONTOLOGY_IDS);
	}

	protected String getConceptId(Request request) {
		return RequestUtils.getAttributeOrRequestParam(MessageUtils
				.getMessage("entity.conceptid"), request);
	}

	private List<Integer> getIntegerList(HttpServletRequest httpRequest,
			String paramName) throws Exception {
		List<Integer> integers = null;

		if (RequestUtils.parameterExists(httpRequest, paramName)) {
			String integerListStr = (String) httpRequest
					.getParameter(paramName);
			integers = RequestUtils.parseIntegerListParam(integerListStr);

			if (integers.isEmpty()) {
				throw new Exception("You must supply at least one valid "
						+ paramName);
			}
		}

		return integers;
	}

	private boolean isGetRequestOverridden() {
		return isMethodOverridden("getRequest");
	}

	private boolean isPostRequestOverridden() {
		return isMethodOverridden("postRequest");
	}

	private boolean isPutRequestOverridden() {
		return isMethodOverridden("putRequest");
	}

	private boolean isDeleteRequestOverridden() {
		return isMethodOverridden("deleteRequest");
	}

	private boolean isMethodOverridden(String methodName) {
		Class<?>[] params = new Class<?>[] { Request.class, Response.class };
		java.lang.reflect.Method handler = null;

		try {
			handler = this.getClass().getMethod(methodName, params);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}

		return this.getClass().equals(handler.getDeclaringClass());
	}

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}

	/**
	 * @param usageLoggingService
	 *            the usageLoggingService to set
	 */
	public void setUsageLoggingService(UsageLoggingService usageLoggingService) {
		this.usageLoggingService = usageLoggingService;
	}
}
