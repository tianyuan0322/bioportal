package org.ncbo.stanford.util.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletInputStream;
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
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class AuthenticationFilter extends AbstractAuthFilter implements
		InitializingBean {
	private static final Log log = LogFactory
			.getLog(AuthenticationFilter.class);

	private AuthenticationService authenticationService;
	private SessionService sessionService;

	@SuppressWarnings("unchecked")
	public AuthenticationFilter(Context context,
			WebApplicationContext springAppContext) {
		super(context, springAppContext);
		authenticationService = (AuthenticationService) springAppContext
				.getBean("authenticationService", AuthenticationService.class);
		sessionService = (SessionService) springAppContext.getBean(
				"sessionService", SessionService.class);
		exceptionPaths = (ArrayList<String>) springAppContext.getBean(
				"authenticationExceptionPaths", ArrayList.class);
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		return authenticateUser(request, response);
	}

	private int authenticateUser(Request request, Response response) {
		int retVal = CONTINUE;
		Reference ref = request.getResourceRef();

		if (isException(ref)) {
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

			if (!StringHelper.isNullOrNullString(userApiKey)) {
				appApiKey = apiKey;
				apiKey = userApiKey;
			}
		}
		UserBean user = null;

		if (StringHelper.isNullOrNullString(apiKey)) {
			error = ErrorTypeEnum.APIKEY_REQUIRED;
		} else if (!StringHelper.isValidUUID(apiKey)) {
			error = ErrorTypeEnum.INVALID_APIKEY;
		} else {
			session = sessionService.get(apiKey);

			if (session == null) {
				try {
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
			String path = ref.getPath();
			retVal = STOP;
			RequestUtils.setHttpServletResponse(response,
					Status.CLIENT_ERROR_FORBIDDEN, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(error, path));
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
