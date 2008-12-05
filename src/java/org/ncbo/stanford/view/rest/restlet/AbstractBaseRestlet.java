package org.ncbo.stanford.view.rest.restlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
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

	/**
	 * Handle requests
	 */
	@Override
	public void handle(Request request, Response response) {
		if (request.getMethod().equals(Method.GET)) {
			getRequest(request, response);
		} else if (request.getMethod().equals(Method.POST)) {
			HttpServletRequest httpServletRequest = RequestUtils
					.getHttpServletRequest(request);
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
	protected void getRequest(Request request, Response response) {
		// no GET requests supported
		unsupportedMethod(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	protected void postRequest(Request request, Response response) {
		// no POST requests supported
		unsupportedMethod(request, response);
	}

	/**
	 * Handle PUT calls here
	 * 
	 * @param request
	 * @param response
	 */
	protected void putRequest(Request request, Response response) {
		// no PUT requests supported
		unsupportedMethod(request, response);
	}

	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	protected void deleteRequest(Request request, Response response) {
		// no DELETE requests supported
		unsupportedMethod(request, response);
	}

	private void unsupportedMethod(Request request, Response response) {
		response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
		xmlSerializationService.generateStatusXMLResponse(request, response);
	}

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}
