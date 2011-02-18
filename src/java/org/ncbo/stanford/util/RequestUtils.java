package org.ncbo.stanford.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ncbo.stanford.bean.http.HttpInputStreamWrapper;
import org.ncbo.stanford.util.helper.DateHelper;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;

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

	private static final String BIOPORTAL_USER_AGENT = "BioPortal-Core";
	public static final String PARAM_SEPARATOR = "&";
	private static final String RESTLET_RESERVED_ATTRIBUTE_PREFIX = "org.restlet";
	private static final String COMMA_DELIMITER = ",";

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
	 * Determines whether a given parameter exists in the request
	 * 
	 * @param request
	 * @param paramName
	 * @return boolean
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static boolean parameterExists(HttpServletRequest request,
			String paramName) {
		boolean exists = false;
		Enumeration paramNames = request.getParameterNames();

		while (paramNames.hasMoreElements()) {
			if (((String) paramNames.nextElement()).equals(paramName)) {
				exists = true;
				break;
			}
		}

		return exists;
	}

	/**
	 * Determines whether the request contains at least one parameter
	 * 
	 * @param request
	 * @return boolean
	 * @throws
	 */
	public static boolean isEmptyRequest(HttpServletRequest request) {
		return request.getParameterNames().hasMoreElements();
	}

	/**
	 * Parses a query string and puts parameters in the HashMap of <Key, Value>
	 * pairs
	 * 
	 * @param queryString
	 * @return
	 */
	public static Map<String, String> parseQueryString(String queryString) {
		HashMap<String, String> parsed = new HashMap<String, String>(1);
		StringTokenizer stAmpersand = new StringTokenizer(queryString,
				PARAM_SEPARATOR);

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

		Representation r = new StringRepresentation(content, mediaType);
		HttpServletResponse servletResponse = getHttpServletResponse(response);
		String charEncoding = servletResponse.getCharacterEncoding();

		if (charEncoding != null) {
			r.setCharacterSet(new CharacterSet(charEncoding));
		}

		response.setEntity(r);
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

	/**
	 * Returns the API Key from a request
	 * 
	 * @param request
	 * @return
	 */
	public static String getApiKey(Request request) {
		return getHttpServletRequest(request).getParameter(
				RequestParamConstants.PARAM_APIKEY);
	}
	
	public static boolean parseBooleanParam(String booleanVal) {
		boolean isPresent = false;

		if (booleanVal != null
				&& RequestParamConstants.PARAM_VALUE_TRUE.contains(StringHelper
						.removeSpaces(booleanVal).toLowerCase())) {
			isPresent = true;
		}

		return isPresent;
	}

	public static Integer parseIntegerParam(String integerVal) {
		Integer val = null;

		try {
			val = Integer.parseInt(StringHelper.removeSpaces(integerVal));
		} catch (NumberFormatException e) {
		}

		return val;
	}

	public static Long parseLongParam(String longVal) {
		Long val = null;

		try {
			val = Long.parseLong(StringHelper.removeSpaces(longVal));
		} catch (NumberFormatException e) {
		}

		return val;
	}

	public static String parseStringParam(String value) {
		if (value != null) {
			value = value.trim();
		}

		return value;
	}

	public static Date parseDateParam(String dateVal) {
		return DateHelper.getDateFrom(StringHelper.removeSpaces(dateVal));
	}

	public static List<Integer> parseIntegerListParam(String integerListParam) {
		List<Integer> integerList = new ArrayList<Integer>(0);

		if (!StringHelper.isNullOrNullString(integerListParam)) {
			integerListParam = StringHelper.removeSpaces(integerListParam);

			for (String integerParam : integerListParam.split(",")) {
				try {
					Integer integerParamInt = Integer.parseInt(integerParam);
					integerList.add(integerParamInt);
				} catch (NumberFormatException e) {
				}
			}
		}

		return integerList;
	}

	public static List<String> parseStringListParam(String stringListParam) {
		List<String> stringList = new ArrayList<String>(0);

		if (!StringHelper.isNullOrNullString(stringListParam)) {
			stringListParam = StringHelper.trimSpaces(stringListParam);

			for (String stringParam : stringListParam.split(",")) {
				stringList.add(StringHelper.trimSpaces(stringParam));
			}
		}

		return stringList;
	}
	
	public static List<URI> parseURIListParam(String URIListParam) {
		List<String> URIs = parseStringListParam(URIListParam);
		
		ArrayList<URI> parsedURIs = new ArrayList<URI>();
		for (String URI : URIs) {
			try {
				parsedURIs.add(new URIImpl(URI));
			} catch (Exception e) {
				// Quash errors
			}
		}
		
		return parsedURIs;
	}

	public static List<Integer> parseIntegerListParam(String[] integerListParam) {
		List<Integer> integerList = new ArrayList<Integer>(0);

		for (String integerParam : integerListParam) {
			if (!StringHelper.isNullOrNullString(integerParam)) {
				integerParam = StringHelper.removeSpaces(integerParam);
				try {
					// simplified solution: just parses the String input to an
					// Integer
					// Integer integerParamInt = Integer.parseInt(integerParam);
					// integerList.add(integerParamInt);

					// combining the two solutions (i.e. also parsing each
					// string input for multiple Integer values)
					List<Integer> integerParamInts = parseIntegerListParam(integerParam);
					integerList.addAll(integerParamInts);
				} catch (NumberFormatException e) {
				}
			}
		}

		return integerList;
	}

	public static String getAttributeOrRequestParam(String name, Request request) {
		String param = (String) request.getAttributes().get(name);

		// See if the param being passed via query string
		if (StringHelper.isNullOrNullString(param)) {
			HttpServletRequest httpRequest = getHttpServletRequest(request);
			param = (String) httpRequest.getParameter(name);
		}

		if (param != null) {
			try {
				param = URLDecoder.decode(param, MessageUtils
						.getMessage("default.encoding"));
			} catch (UnsupportedEncodingException e) {
				// this shouldn't happen
				e.printStackTrace();
			}
		}

		return param;
	}

	/**
	 * Inside this we collect the value of Attributes and after Separating the
	 * Id they Stored inside the List
	 * 
	 * @param name
	 * @param request
	 * @return
	 */
	public static List<String> getAttributeOrRequestParams(String name,
			Request request) {
		// Creating the List with the name of params of String type
		List<String> paramList = new ArrayList<String>();

		String paramData = (String) request.getAttributes().get(name);

		if (StringHelper.isNullOrNullString(paramData)) {
			HttpServletRequest httpRequest = getHttpServletRequest(request);
			paramData = (String) httpRequest.getParameter(name);
		}
		
		if (paramData != null) {
			try {
				paramData = URLDecoder.decode(paramData, MessageUtils
						.getMessage("default.encoding"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		// Creating the Array of Type String with the name of Param
		String[] paramFormat = StringHelper.split(paramData, COMMA_DELIMITER);

		// Iterate the All Value of Params
		for (int i = 0; i < paramFormat.length; i++) {
			// Adding the All Value Inside The ArrayList
			paramList.add(paramFormat[i]);
		}

		return paramList;
	}

	/**
	 * Executes an HTTP post
	 * 
	 * @param postUrl
	 * @param postParams
	 * @return
	 * @throws IOException
	 */
	public static HttpInputStreamWrapper doHttpPost(String postUrl,
			HashMap<String, String> postParams) throws Exception {
		InputStream is = null;
		String postData = new String("");
		String encoding = MessageUtils.getMessage("default.encoding");

		// Send data
		URL url = new URL(postUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// Construct post parameters
		if (postParams != null) {
			for (String key : postParams.keySet()) {
				postData += URLEncoder.encode(key, encoding) + "="
						+ URLEncoder.encode(postParams.get(key), encoding)
						+ PARAM_SEPARATOR;
			}

			postData = (postData.length() > 0) ? postData.substring(0, postData
					.length() - 1) : postData;

			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn
					.getOutputStream());
			wr.write(postData);
			wr.flush();
			wr.close();
		}

		try {
			is = conn.getInputStream();
		} catch (Exception e) {
			is = conn.getErrorStream();
		}

		// BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// String line;
		//		
		// while ((line = rd.readLine()) != null) {
		// System.out.println(line);
		// }

		int statusCode = conn.getResponseCode();

		return new HttpInputStreamWrapper(statusCode, is);
	}

	/**
	 * Executes an HTTP post
	 * 
	 * @param baseUrl
	 * @param getParams
	 * @return
	 * @throws IOException
	 */
	public static HttpInputStreamWrapper doHttpGet(String baseUrl,
			HashMap<String, String> getParams) throws Exception {
		InputStream is = null;
		String getData = new String("");
		String encoding = MessageUtils.getMessage("default.encoding");

		// Construct get parameters
		if (getParams != null) {
			for (String key : getParams.keySet()) {
				getData += URLEncoder.encode(key, encoding) + "="
						+ URLEncoder.encode(getParams.get(key), encoding)
						+ PARAM_SEPARATOR;
			}

			getData = (getData.length() > 0) ? getData.substring(0, getData
					.length() - 1) : getData;
		}

		// Send data
		URL url = new URL(baseUrl + ((getData.length() > 0) ? "?" : "")
				+ getData);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", BIOPORTAL_USER_AGENT);

		try {
			is = conn.getInputStream();
		} catch (Exception e) {
			is = conn.getErrorStream();
		}

		// BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// String line;
		//				
		// while ((line = rd.readLine()) != null) {
		// System.out.println(line);
		// }

		int statusCode = conn.getResponseCode();

		return new HttpInputStreamWrapper(statusCode, is);
	}

	public static String getResourceAttributesAsString(Request request) {
		Map<String, Object> attr = request.getAttributes();
		StringBuffer sb = null;
		String attrStr = null;

		if (!attr.isEmpty()) {
			sb = new StringBuffer();

			for (String key : attr.keySet()) {
				if (!key.contains(RESTLET_RESERVED_ATTRIBUTE_PREFIX)) {
					sb.append(key);
					sb.append("=");
					sb.append(attr.get(key));
					sb.append(PARAM_SEPARATOR);
				}
			}

			if (sb.length() > 0) {
				attrStr = sb.toString();
				attrStr = attrStr.substring(0, attrStr.length() - 1);
			}
		}

		return attrStr;
	}

	@SuppressWarnings("unchecked")
	public static String getRequestParametersAsString(
			HttpServletRequest httpServletRequest) {
		Map params = httpServletRequest.getParameterMap();
		StringBuffer sb = null;
		String attrStr = null;

		if (!params.isEmpty()) {
			sb = new StringBuffer();

			for (Object key : params.keySet()) {
				sb.append(key);
				sb.append("=");
				sb.append(httpServletRequest.getParameter((String) key));
				sb.append(PARAM_SEPARATOR);
			}

			if (sb.length() > 0) {
				attrStr = sb.toString();
				attrStr = attrStr.substring(0, attrStr.length() - 1);
			}
		}

		return attrStr;
	}
}