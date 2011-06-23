package org.ncbo.stanford.util.security.filter;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.exception.AuthenticationException;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.security.authentication.AuthenticationService;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class AuthenticationFilter extends AbstractAuthFilter {
	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(AuthenticationFilter.class);

	private AuthenticationService authenticationService;
	private SessionService sessionService;

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

		String apiKey = RequestUtils.getRequestParameter(httpRequest,
				RequestParamConstants.PARAM_APIKEY);

		if (StringHelper.isNullOrNullString(apiKey)) {
			apiKey = RequestUtils.getRequestParameter(httpRequest,
					RequestParamConstants.PARAM_APPLICATIONID);
		}

		if (!StringHelper.isNullOrNullString(apiKey)) {
			String userApiKey = RequestUtils.getRequestParameter(httpRequest,
					RequestParamConstants.PARAM_USER_APIKEY);

			// Both app and user api keys supplied. Verify that they are
			// different and store
			if (!StringHelper.isNullOrNullString(userApiKey)
					&& !userApiKey.equals(apiKey)) {
				appApiKey = apiKey;
				apiKey = userApiKey;
			}
		}

		String apiKeyMsg = "Please visit " + MessageUtils.getMessage("ui.url")
				+ "/account to get your API key.";

		if (StringHelper.isNullOrNullString(apiKey)) {
			error = ErrorTypeEnum.APIKEY_REQUIRED;
			error
					.setErrorMessage("A valid API key is required to call REST services. "
							+ apiKeyMsg);
		} else if (!StringHelper.isValidUUID(apiKey)) {
			error = ErrorTypeEnum.INVALID_APIKEY;
			error.setErrorMessage("The API key you supplied is invalid. "
					+ apiKeyMsg);
		} else if (appApiKey != null && !StringHelper.isValidUUID(appApiKey)) {
			error = ErrorTypeEnum.INVALID_APPAPIKEY;
			error
					.setErrorMessage("The Application you are using to access BioPortal services supplied an invalid API key. "
							+ apiKeyMsg);
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
					}

					session = authenticationService.authenticate(apiKey);
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
					.generateStatusXMLResponse(request, response);
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
