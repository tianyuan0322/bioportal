package org.ncbo.stanford.view.rest.authentication;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.exception.AuthenticationException;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.security.authentication.AuthenticationService;
import org.ncbo.stanford.util.security.filter.AuthenticationFilter;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 * Handles form based authentication with username and password.
 * 
 * <pre>
 * Important Note: 
 * 		This class should NEVER be used without having 
 * 		the AuthenticationFilter in place. It relies on 
 * 		certain key parameters having been set in the filter.
 * </pre>
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
	public void anyMethodRequest(Request request, Response response) {
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
		String userApiKey = null;

		try {
			HttpServletRequest httpServletRequest = RequestUtils
					.getHttpServletRequest(request);

			String appApiKey = obtainApiKey(httpServletRequest);

			// TODO: @@@@@ mdorf: This block is temporary to allow users
			// to adjust to the new security structure. It forces users with API
			// keys to go through the new authentication/authorization process,
			// while users without API keys to be allowed access. Once this
			// block is removed, no API-less access will be allowed.
			if (StringHelper.isNullOrNullString(appApiKey)) {
				appApiKey = AuthenticationFilter.TEMP_ADMIN_APIKEY;
				userApiKey = appApiKey;
			} else {
				userApiKey = obtainUserApiKey(httpServletRequest);
			}

			// This has already gone through the AuthenticationFilter
			// The code assumes that a valid session must exist for this API Key
			if (StringHelper.isNullOrNullString(appApiKey)
					|| sessionService.get(appApiKey) == null) {
				throw new AuthenticationException(
						"This application did not pass proper credentials to authenticate users.");
			}

			// TODO: @@@@@ mdorf: uncomment this call once the above temporary
			// block is removed
			// userApiKey = obtainUserApiKey(httpServletRequest);

			// When a user API key is supplied, the assumption is that if the
			// request has made it this far, it has already been successfully
			// validated and authenticated by the AuthenticationFilter.
			final String username = obtainUsername(httpServletRequest);
			final String password = obtainPassword(httpServletRequest);

			if (StringHelper.isNullOrNullString(userApiKey)
					|| !StringHelper.isNullOrNullString(username)) {
				userSession = authenticationService.authenticate(username,
						password);
			} else {
				userSession = sessionService.get(userApiKey);

				// This should never happen, but just in case, handle it.
				// NULL session means that somehow the user succeeded passing
				// through AuthenticationFilter without having a valid session
				// in place. If this happens, it's a code bug.
				if (userSession == null) {
					log.error("Invalid session logged. User API key: "
							+ userApiKey + ", App API key: " + appApiKey);
					throw new AuthenticationException(
							"Your session appears to be invalid due to an application error. "
									+ "Please contact BioPortal support and report this message so we "
									+ "can investiage it promptly.");
				}

				// this is required in order to invalidate the old session
				userSession = authenticationService.authenticate(userApiKey);
			}
		} catch (BPRuntimeException re) {
			if (userApiKey != null) {
				sessionService.invalidate(userApiKey);
			}
			re.printStackTrace();
			log.error(re);
			response.setStatus(Status.SERVER_ERROR_INTERNAL, re.getMessage());
		} catch (AuthenticationException ae) {
			response.setStatus(Status.CLIENT_ERROR_FORBIDDEN, ae.getMessage());
		} catch (Exception e) {
			if (userApiKey != null) {
				sessionService.invalidate(userApiKey);
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

	protected String obtainUserApiKey(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_USER_APIKEY);
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
