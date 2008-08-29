package org.ncbo.stanford.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import com.noelios.restlet.ext.servlet.ServletCall;
import com.noelios.restlet.http.HttpCall;
import com.noelios.restlet.http.HttpRequest;
import com.noelios.restlet.http.HttpResponse;

/**
 * Various utility methods for request and response processing
 * 
 * @author Michael Dorf
 * 
 */
public class RequestUtils {

	/**
	 * Sends a redirect call to a specified URL
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @throws IOException
	 */
	public static void sendRedirect(HttpServletRequest request,
			HttpServletResponse response, String url) throws IOException {
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = request.getContextPath() + url;
		}

		response.sendRedirect(response.encodeRedirectURL(url));
	}

	/**
	 * Parses a query string and puts parameters in the HashMap of <Key, Value>
	 * pairs
	 * 
	 * @param queryString
	 * @return
	 */
	public static HashMap<String, String> parseQueryString(String queryString) {
		HashMap<String, String> parsed = new HashMap<String, String>(1);
		StringTokenizer stAmpersand = new StringTokenizer(queryString, "&");

		while (stAmpersand.hasMoreTokens()) {
			String anItem = stAmpersand.nextToken();

			if (anItem != null) {
				// iterate for the "=" in the Name=Value pair
				StringTokenizer stEqual = new StringTokenizer(anItem, "=");

				if (stEqual.hasMoreTokens()) {
					String aName = stEqual.nextToken();

					if (!StringHelper.isNullOrNullString(aName)
							&& stEqual.hasMoreTokens()) {
						// add the variable to orderElements vector
						String aValue = null;

						if (stEqual.hasMoreTokens()) {
							aValue = stEqual.nextToken();
						}

						parsed.put(aName, aValue);
					}
				}
			}
		}

		return parsed;
	}

	/**
	 * Returns an instance of {@link HttpServletRequest} from a Restlet
	 * {@link Request}
	 * 
	 * @param req
	 * @return
	 */
	public static HttpServletRequest getHttpServletRequest(Request req) {
		if (req instanceof HttpRequest) {
			HttpRequest httpRequest = (HttpRequest) req;
			HttpCall httpCall = httpRequest.getHttpCall();

			if (httpCall instanceof ServletCall) {
				HttpServletRequest httpServletRequest = ((ServletCall) httpCall)
						.getRequest();
				return httpServletRequest;
			}
		}

		return null;
	}

	/**
	 * Returns an instance of {@link HttpServletRequest} from a Restlet
	 * {@link Request}
	 * 
	 * @param req
	 * @return
	 */
	public static HttpServletResponse getHttpServletResponse(Response res) {
		if (res instanceof HttpResponse) {
			HttpResponse httpResponse = (HttpResponse) res;
			HttpCall httpCall = httpResponse.getHttpCall();

			if (httpCall instanceof ServletCall) {
				HttpServletResponse httpServletResponse = ((ServletCall) httpCall)
						.getResponse();
				return httpServletResponse;
			}
		}

		return null;
	}

	/**
	 * Sets the content of an http response
	 * 
	 * @param response
	 * @param statusCode
	 * @param content
	 * @throws IOException
	 */
	public static void setHttpServletResponse(HttpServletResponse response,
			int statusCode, String content) throws IOException {
		response.setStatus(statusCode);
		ServletOutputStream sos = response.getOutputStream();
		sos.print(content);
	}

	/**
	 * Sets the content of an http response usring Restlet response object
	 * 
	 * @param response
	 *            restlet response
	 * @param statusCode
	 * @param content
	 * @throws IOException
	 */
	public static void setHttpServletResponse(Response response,
			Status statusCode, MediaType mediaType, String content) {
		response.setStatus(statusCode);
		response.setEntity(content, mediaType);
	}

	/**
	 * Returns the Session ID from a request
	 * 
	 * @param request
	 * @return
	 */
	public static String getSessionId(Request request) {
		return getHttpServletRequest(request).getParameter(
				RequestParamConstants.PARAM_SESSIONID);
	}

	/**
	 * Returns the user name from a request
	 * 
	 * @param request
	 * @return
	 */
	public static String getUserName(Request request) {
		return getHttpServletRequest(request).getParameter(
				RequestParamConstants.PARAM_USERNAME);
	}

	/**
	 * Returns the applicationId from a request
	 * 
	 * @param request
	 * @return
	 */
	public static String getApplicationId(Request request) {
		return getHttpServletRequest(request).getParameter(
				RequestParamConstants.PARAM_APPLICATIONID);
	}

}