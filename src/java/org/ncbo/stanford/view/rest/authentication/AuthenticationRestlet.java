package org.ncbo.stanford.view.rest.authentication;

import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.context.HttpSessionContextIntegrationFilter;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.security.ui.ApplicationAuthenticationDetails;
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
	private SessionService sessionService;
	private XMLSerializationService xmlSerializationService;

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
			final Authentication auth = getAuthenticationManager()
					.authenticate(authReq);

			final RESTfulSession session = sessionService.createNewSession();
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
			RequestUtils
					.setHttpServletResponse(response, Status.SUCCESS_OK,
							MediaType.TEXT_XML, xmlSerializationService
									.getSuccessAsXML(session.getId(),
											accessedResource));
		} catch (AuthenticationException ex) {
			SecurityContextHolder.getContext().setAuthentication(null);

			RequestUtils.setHttpServletResponse(response,
					Status.CLIENT_ERROR_FORBIDDEN, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(
							ErrorTypeEnum.INVALID_CREDENTIALS, accessedResource));
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
	 * @return the sessionService
	 */
	public SessionService getSessionService() {
		return sessionService;
	}

	/**
	 * @param sessionService
	 *            the sessionService to set
	 */
	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	/**
	 * @return the xmlSerializationService
	 */
	public XMLSerializationService getXmlSerializationService() {
		return xmlSerializationService;
	}

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}
