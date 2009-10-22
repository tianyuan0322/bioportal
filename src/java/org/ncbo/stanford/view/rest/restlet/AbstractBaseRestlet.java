package org.ncbo.stanford.view.rest.restlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.logging.UsageLoggingBean;
import org.ncbo.stanford.service.logging.UsageLoggingService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.BeanHelper;
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

		// first, check for http param "logonly=true". If present, ignore
		// normal execution, just log the request
		String logOnly = (String) httpServletRequest
				.getParameter(RequestParamConstants.PARAM_LOGONLY);
		boolean logOnlyBool = RequestUtils.parseBooleanParam(logOnly);
		boolean isValidRequest = false;

		if (request.getMethod().equals(Method.GET)) {
			isValidRequest = handleGetRequest(request, response, logOnlyBool);
		} else if (request.getMethod().equals(Method.POST)) {
			String method = httpServletRequest.getParameter(MessageUtils
					.getMessage("http.param.method"));

			if (method == null) {
				isValidRequest = handlePostRequest(request, response,
						logOnlyBool);
			} else {
				if (method
						.equalsIgnoreCase(MessageUtils.getMessage("http.put"))) {
					isValidRequest = handlePutRequest(request, response,
							logOnlyBool);
				} else if (method.equalsIgnoreCase(MessageUtils
						.getMessage("http.delete"))) {
					isValidRequest = handleDeleteRequest(request, response,
							logOnlyBool);
				}
			}
		} else if (request.getMethod().equals(
				MessageUtils.getMessage("http.put"))) {
			isValidRequest = handlePutRequest(request, response, logOnlyBool);
		} else if (request.getMethod().equals(
				MessageUtils.getMessage("http.delete"))) {
			isValidRequest = handleDeleteRequest(request, response, logOnlyBool);
		}

		// disabling logging temporarily because of performance issues
		// if (logRequests() && isValidRequest
		// && response.getStatus().equals(Status.SUCCESS_OK)) {
		// logRequest(request);
		// }
	}

	private boolean handleDeleteRequest(Request request, Response response,
			boolean logOnlyBool) {
		boolean isValidRequest = isDeleteRequestOverridden();

		if (!isValidRequest || !logOnlyBool) {
			deleteRequest(request, response);
		}

		return isValidRequest;
	}

	private boolean handlePutRequest(Request request, Response response,
			boolean logOnlyBool) {
		boolean isValidRequest = isPutRequestOverridden();

		if (!isValidRequest || !logOnlyBool) {
			putRequest(request, response);
		}

		return isValidRequest;
	}

	private boolean handlePostRequest(Request request, Response response,
			boolean logOnlyBool) {
		boolean isValidRequest = isPostRequestOverridden();

		if (!isValidRequest || !logOnlyBool) {
			postRequest(request, response);
		}

		return isValidRequest;
	}

	private boolean handleGetRequest(Request request, Response response,
			boolean logOnlyBool) {
		boolean isValidRequest = isGetRequestOverridden();

		if (!isValidRequest || !logOnlyBool) {
			getRequest(request, response);
		}

		return isValidRequest;
	}

	private void logRequest(Request request) {
		UsageLoggingBean usageLoggingBean = BeanHelper
				.populateUsageLoggingBeanFromRequestForLogging(request);
		usageLoggingService.logUsage(usageLoggingBean);
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

	protected boolean logRequests() {
		return true;
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
