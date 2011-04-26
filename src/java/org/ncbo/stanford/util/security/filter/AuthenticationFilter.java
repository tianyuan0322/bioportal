package org.ncbo.stanford.util.security.filter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.exception.AuthenticationException;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.security.SecurityContextHolder;
import org.ncbo.stanford.util.security.authentication.AuthenticationService;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class AuthenticationFilter extends AbstractAuthFilter {
	private static final Log log = LogFactory
			.getLog(AuthenticationFilter.class);

	private AuthenticationService authenticationService;
	private SessionService sessionService;

	// TODO: to be removed later (see a note below in authenticateUser())
	public static String TEMP_ADMIN_APIKEY = "fcc74490-5a25-11e0-9a6e-005056aa215e";

	public AuthenticationFilter(Context context,
			WebApplicationContext springAppContext) {
		super(context, springAppContext);
		authenticationService = (AuthenticationService) springAppContext
				.getBean("authenticationService", AuthenticationService.class);
		sessionService = (SessionService) springAppContext.getBean(
				"sessionService", SessionService.class);
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		return authenticateUser(request, response);
	}

	private int authenticateUser(Request request, Response response) {
		int retVal = CONTINUE;

		if (isException(request)) {
			return retVal;
		}

		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		ErrorTypeEnum error = null;
		String appApiKey = null;
		RESTfulSession session = null;

		String apiKey = httpRequest
				.getParameter(RequestParamConstants.PARAM_APIKEY);

		if (StringHelper.isNullOrNullString(apiKey)) {
			apiKey = httpRequest
					.getParameter(RequestParamConstants.PARAM_APPLICATIONID);
		}

		if (!StringHelper.isNullOrNullString(apiKey)) {
			String userApiKey = httpRequest
					.getParameter(RequestParamConstants.PARAM_USER_APIKEY);

			// Both app and user api keys supplied. Verify that they are
			// different and store
			if (!StringHelper.isNullOrNullString(userApiKey)
					&& !userApiKey.equals(apiKey)) {
				appApiKey = apiKey;
				apiKey = userApiKey;
			}
		} else {
			// TODO: @@@@@ mdorf: This "else" block is temporary to allow users
			// to adjust to the new security structure. It forces users with API
			// keys to go through the new authentication/authorization process,
			// while users without API keys to be allowed access. Once this
			// block is removed, no API-less access will be allowed.
			String ip = null;
			String hostname = null;

			try {
				InetAddress addr = InetAddress.getLocalHost();
				ip = addr.getHostAddress();
				hostname = addr.getCanonicalHostName();
			} catch (UnknownHostException e) {
				ip = httpRequest.getRemoteAddr();
				hostname = httpRequest.getRemoteHost();
			}

			Reference referrer = request.getReferrerRef();

			log.info("A user identified by IP: " + ip + ", Hostname: "
					+ hostname
					+ ((referrer != null) ? ", and Referrer: " + referrer : "")
					+ ", Accessed Resource: \"" + request.getResourceRef()
					+ "\" attempted access with an empty API Key.");

			apiKey = TEMP_ADMIN_APIKEY;
		}

		UserBean user = null;

		if (StringHelper.isNullOrNullString(apiKey)) {
			error = ErrorTypeEnum.APIKEY_REQUIRED;
		} else if (!StringHelper.isValidUUID(apiKey)) {
			error = ErrorTypeEnum.INVALID_APIKEY;
		} else if (appApiKey != null && !StringHelper.isValidUUID(appApiKey)) {
			error = ErrorTypeEnum.INVALID_APPAPIKEY;
		} else {
			session = sessionService.get(apiKey);

			if (session == null) {
				try {
					// Application API key supplied. Make sure it's valid!
					// Authenticate it and create a its own session container.
					// No need to store it anywhere at this point.
					if (appApiKey != null
							&& sessionService.get(appApiKey) == null) {
						authenticationService.authenticate(appApiKey);
						sessionService.createNewSession(appApiKey);
					}

					user = authenticationService.authenticate(apiKey);
					session = sessionService.createNewSession(apiKey);
					session.setAttribute(
							ApplicationConstants.SECURITY_CONTEXT_KEY,
							new SecurityContextHolder(apiKey, appApiKey, user));
				} catch (AuthenticationException e) {
					error = ErrorTypeEnum.INVALID_CREDENTIALS;
					error.setErrorMessage(e.getMessage());
				}
			}
		}

		if (error != null) {
			retVal = STOP;
			response.setStatus(Status.CLIENT_ERROR_FORBIDDEN, error
					.getErrorMessage());
			xmlSerializationService
					.generateXMLResponse(request, response, null);
		} else {
			request.getAttributes().put(ApplicationConstants.USER_SESSION_NAME,
					session);
		}
		return retVal;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(authenticationService,
				"authenticationService must be specified");
		Assert.notNull(sessionService, "sessionService must be specified");
	}
}
