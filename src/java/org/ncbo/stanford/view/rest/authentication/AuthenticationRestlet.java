package org.ncbo.stanford.view.rest.authentication;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.exception.AuthenticationException;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.UserNotFoundException;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Handles Acegi form based authentication. Simulates the the Acegi's
 * authenticationProcessingFilter in RESTful environment.
 * 
 * @author Michael Dorf
 * 
 */
public final class AuthenticationRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(AuthenticationRestlet.class);

	private SessionService sessionService;
	private UserService userService;

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
		String accessedResource = request.getResourceRef().getPath();
		RESTfulSession appSession = null;

		try {
			HttpServletRequest httpServletRequest = RequestUtils
					.getHttpServletRequest(request);

			final String username = obtainUsername(httpServletRequest);
			final String password = obtainPassword(httpServletRequest);
			final String appApiKey = obtainApiKey(httpServletRequest);

			if (StringHelper.isNullOrNullString(appApiKey)
					|| (appSession = sessionService.get(appApiKey)) == null) {
				throw new AuthenticationException(
						"This application did not pass proper credentials to authenticate users.");
			}

			UserBean userBean = userService.findUser(username);

			if (userBean == null) {
				log
						.error("There is no user in the system corresponding to username: "
								+ username);
				throw new AuthenticationException(
						"Invalid credentials supplied");
			}

		} catch (BPRuntimeException re) {
///			sessionService.invalidate()
			re.printStackTrace();
			log.error(re);
			response.setStatus(Status.SERVER_ERROR_INTERNAL, re.getMessage());
		} catch (AuthenticationException ae) {
			response.setStatus(Status.CLIENT_ERROR_FORBIDDEN, ae.getMessage());
		} catch (Exception e) {
///			sessionService.invalidate()
			e.printStackTrace();
			log.error(e);
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
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
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
