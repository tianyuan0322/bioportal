package org.ncbo.stanford.view.rest.authentication;

import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.context.HttpSessionContextIntegrationFilter;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.security.ui.ApplicationAuthenticationDetails;
import org.ncbo.stanford.view.session.RESTfulSession;
import org.ncbo.stanford.view.session.expiration.system.impl.SessionExpirationSystem;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * Handles Acegi form based authentication. Simulates the the Acegi's
 * authenticationProcessingFilter in RESTful environment.
 * 
 * @author Michael Dorf
 * 
 */
public final class AuthenticationRestlet extends Restlet {

	private static final Log log = LogFactory
			.getLog(AuthenticationRestlet.class);

	private AuthenticationManager authenticationManager;
	private SessionExpirationSystem sessionExpirationSystem;

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
			final Authentication auth = getAuthenticationManager()
					.authenticate(authReq);

			final RESTfulSession session = sessionExpirationSystem
					.createNewSession();
			session.setApplicationId(applicationId);
			session
					.setAttribute(
							AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY,
							username);
			/*
			 * initialize the security context.
			 */
			final SecurityContext secCtx = SecurityContextHolder.getContext();
			secCtx.setAuthentication(auth);
			session
					.setAttribute(
							HttpSessionContextIntegrationFilter.ACEGI_SECURITY_CONTEXT_KEY,
							secCtx);

			response.setStatus(Status.SUCCESS_OK);
			response.setEntity("Success: " + session.getId(),
					MediaType.TEXT_PLAIN);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			SecurityContextHolder.getContext().setAuthentication(null);

			response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			response.setEntity("Failure", MediaType.TEXT_PLAIN);
		}
	}

	// public void logout(ActionEvent e) {
	// final HttpServletRequest request = RequestUtils.getServletRequest();
	// request.getSession(false).removeAttribute(
	// HttpSessionContextIntegrationFilter.ACEGI_SECURITY_CONTEXT_KEY);
	//
	// /* simulate the SecurityContextLogoutHandler
	// */
	// SecurityContextHolder.clearContext();
	// request.getSession(false).invalidate();
	// loggedInUserBean.logout();
	// }
	//	
	// public String obtainFullRequestUrl() {
	// return AbstractProcessingFilter.obtainFullRequestUrl(
	// RequestUtils.getServletRequest());
	// }
	//

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
	 * @return the authenticationManager
	 */
	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
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
	 * @return the sessionExpirationSystem
	 */
	public SessionExpirationSystem getSessionExpirationSystem() {
		return sessionExpirationSystem;
	}

	/**
	 * @param sessionExpirationSystem
	 *            the sessionExpirationSystem to set
	 */
	public void setSessionExpirationSystem(
			SessionExpirationSystem sessionExpirationSystem) {
		this.sessionExpirationSystem = sessionExpirationSystem;
	}
}
