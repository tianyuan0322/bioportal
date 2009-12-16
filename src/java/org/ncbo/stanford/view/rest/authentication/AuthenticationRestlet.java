package org.ncbo.stanford.view.rest.authentication;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.security.ui.ApplicationAuthenticationDetails;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.context.HttpSessionContextIntegrationFilter;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

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

	private AuthenticationManager authenticationManager;
	private SessionService sessionService;
	private UserService userService;

	@Override
	public void handle(Request request, Response response) {
		authenticate(request, response);
	}

	/**
	 * Authenticates the user and stores the security context inside the session
	 * 
	 * @param request
	 * @param response
	 */
	private void authenticate(Request request, Response response) {
		String accessedResource = request.getResourceRef().getPath();

		try {
			HttpServletRequest httpServletRequest = RequestUtils
					.getHttpServletRequest(request);

			final String username = obtainUsername(httpServletRequest);
			final String password = obtainPassword(httpServletRequest);
			final String applicationId = obtainApplicationId(httpServletRequest);

			final UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(
					username, password);
			authReq.setDetails(new ApplicationAuthenticationDetails(
					httpServletRequest, applicationId));

			/*
			 * perform authentication
			 */
			final Authentication auth = authenticationManager
					.authenticate(authReq);

			final RESTfulSession session = sessionService.createNewSession();
			session.setApplicationId(applicationId);
			session
					.setAttribute(
							AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY,
							username);
			/*
			 * initialize the security context.
			 */
			final SecurityContext secCtx = SecurityContextHolder.getContext();
			secCtx.setAuthentication(auth);
			session
					.setAttribute(
							HttpSessionContextIntegrationFilter.SPRING_SECURITY_CONTEXT_KEY,
							secCtx);
			// add user and session info to the response
			UserBean userBean = userService.findUser(username);
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML, xmlSerializationService
							.getSuccessAsXML(xmlSerializationService
									.getSuccessBean(session.getId(), request,
											userBean)));
		} catch (AuthenticationServiceException e) {
			e.printStackTrace();
			log.error(e);
			SecurityContextHolder.getContext().setAuthentication(null);

			RequestUtils.setHttpServletResponse(response,
					Status.SERVER_ERROR_INTERNAL, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(
							ErrorTypeEnum.RUNTIME_ERROR, accessedResource));
		} catch (AuthenticationException ex) {
			SecurityContextHolder.getContext().setAuthentication(null);

			RequestUtils.setHttpServletResponse(response,
					Status.CLIENT_ERROR_FORBIDDEN, MediaType.TEXT_XML,
					xmlSerializationService
							.getErrorAsXML(ErrorTypeEnum.INVALID_CREDENTIALS,
									accessedResource));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			SecurityContextHolder.getContext().setAuthentication(null);

			RequestUtils.setHttpServletResponse(response,
					Status.SERVER_ERROR_INTERNAL, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(
							ErrorTypeEnum.RUNTIME_ERROR, accessedResource));
		}
	}

	protected String obtainPassword(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_PASSWORD);
	}

	protected String obtainUsername(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_USERNAME);
	}

	protected String obtainApplicationId(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_APPLICATIONID);
	}

	/**
	 * @param authenticationManager
	 *            the authenticationManager to set
	 */
	public void setAuthenticationManager(
			AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
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
