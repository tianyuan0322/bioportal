package org.ncbo.stanford.view.rest.authentication;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.AuthenticationException;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.security.SecurityContextHolder;
import org.ncbo.stanford.util.security.authentication.AuthenticationService;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * Handles form based authentication with username and password.
 * 
 * @author Michael Dorf
 * 
 */
public final class AuthenticationRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(AuthenticationRestlet.class);

	private SessionService sessionService;
	private AuthenticationService authenticationService;

	@Override
	public void handle(Request request, Response response) {
		authenticate(request, response);
	}

	/**
	 * Authenticates the user and creates a new user session
	 * 
	 * @param request
	 * @param response
	 */
	private void authenticate(Request request, Response response) {
		RESTfulSession userSession = null;
		String apiKey = null;

		try {
			HttpServletRequest httpServletRequest = RequestUtils
					.getHttpServletRequest(request);

			final String username = obtainUsername(httpServletRequest);
			final String password = obtainPassword(httpServletRequest);
			final String appApiKey = obtainApiKey(httpServletRequest);

			if (StringHelper.isNullOrNullString(appApiKey)
					|| sessionService.get(appApiKey) == null) {
				throw new AuthenticationException(
						"This application did not pass proper credentials to authenticate users.");
			}

			UserBean user = authenticationService.authenticate(username,
					password);
			apiKey = user.getApiKey();
			sessionService.invalidate(apiKey);
			userSession = sessionService.createNewSession(apiKey);
			userSession.setAttribute(ApplicationConstants.SECURITY_CONTEXT_KEY,
					new SecurityContextHolder(apiKey, appApiKey, user));
		} catch (BPRuntimeException re) {
			if (apiKey != null) {
				sessionService.invalidate(apiKey);
			}
			re.printStackTrace();
			log.error(re);
			response.setStatus(Status.SERVER_ERROR_INTERNAL, re.getMessage());
		} catch (AuthenticationException ae) {
			response.setStatus(Status.CLIENT_ERROR_FORBIDDEN, ae.getMessage());
		} catch (Exception e) {
			if (apiKey != null) {
				sessionService.invalidate(apiKey);
			}
			e.printStackTrace();
			log.error(e);
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					userSession);
		}
	}

	protected String obtainPassword(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_PASSWORD);
	}

	protected String obtainUsername(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_USERNAME);
	}

	protected String obtainApiKey(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_APIKEY);
	}

	/**
	 * @param sessionService
	 *            the sessionService to set
	 */
	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	/**
	 * @param authenticationService
	 *            the authenticationService to set
	 */
	public void setAuthenticationService(
			AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
}
