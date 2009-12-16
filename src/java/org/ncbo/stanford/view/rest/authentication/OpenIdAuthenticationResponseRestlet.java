/**
 * 
 */
package org.ncbo.stanford.view.rest.authentication;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ApplicationBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserRole;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.exception.ApplicationNotFoundException;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.security.ui.ApplicationAuthenticationDetails;
import org.ncbo.stanford.util.security.userdetails.ApplicationDetailsService;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.ParameterList;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.springframework.security.AuthenticationException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.HttpSessionContextIntegrationFilter;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

/**
 * @author s.reddy
 * 
 */
public class OpenIdAuthenticationResponseRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OpenIdAuthenticationResponseRestlet.class);

	private SessionService sessionService;
	private CustomNcboUserDAO ncboUserDAO = null;
	private ApplicationDetailsService applicationDetailsService;

	@Override
	public void handle(Request request, Response response) {
		authenticateOpenId(request, response);
	}

	@SuppressWarnings("unchecked")
	private void authenticateOpenId(Request request, Response response)
			throws AuthenticationException {
		String accessedResource = request.getResourceRef().getPath();
		try {

			// get http servlet request and response objects.
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);

			HttpSession httpSession = httpRequest.getSession(true);
			// retrieve previously stored manager information from session
			ConsumerManager manager = (ConsumerManager) httpSession
					.getAttribute("manager");

			ParameterList openidResp = new ParameterList(httpRequest
					.getParameterMap());

			// retrieve the previously stored discovery information
			DiscoveryInformation discovered = (DiscoveryInformation) httpSession
					.getAttribute("discovered");

			String applicationId = (String) httpSession
					.getAttribute("applicationId");

			// extract receiving URL from the HTTP request
			StringBuffer receivingURL = httpRequest.getRequestURL();
			String queryString = httpRequest.getQueryString();

			if (queryString != null && queryString.length() > 0)
				receivingURL.append("?").append(httpRequest.getQueryString());

			// verify the response
			VerificationResult verification = manager.verify(receivingURL
					.toString(), openidResp, discovered);

			// examine the verification result and extract the verified
			// identifierR
			Identifier verified = verification.getVerifiedId();

			String openId = openidResp.getParameterValue("openid.identity");
			if (verified != null) {
				System.out.println("openId login successful");

				// find user with openId in Bioportal.
				List<NcboUser> userList = ncboUserDAO.findByOpenId(openId);
				if (userList != null && userList.size() != 0) {
					NcboUser user = userList.get(0);
					// get user roles and set as grantedAuthority
					Set<NcboUserRole> userRole = user.getNcboUserRoles();
					GrantedAuthority grantedAuthority[] = new GrantedAuthority[userRole
							.size()];
					int i = 0;
					for (NcboUserRole ncboUserRole : userRole) {
						if (ncboUserRole != null) {
							GrantedAuthorityImpl role = new GrantedAuthorityImpl(
									ncboUserRole.getNcboLRole().getName());
							grantedAuthority[i] = role;
							i++;
						}
					}

					// create authentication object with granted roles.
					final UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(
							user.getUsername(), user.getPassword(),
							grantedAuthority);
					authReq.setDetails(new ApplicationAuthenticationDetails(
							httpRequest, applicationId));

					final RESTfulSession session = sessionService
							.createNewSession();
					session.setApplicationId(applicationId);
					session
							.setAttribute(
									AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY,
									user.getUsername());

					// initialize the security context.
					final SecurityContext secCtx = SecurityContextHolder
							.getContext();
					secCtx.setAuthentication(authReq);
					// set SecurityContext to RESTfulSession obj
					session
							.setAttribute(
									HttpSessionContextIntegrationFilter.SPRING_SECURITY_CONTEXT_KEY,
									secCtx);
					// load application from given applicationId
					@SuppressWarnings("unused")
					ApplicationBean applicationBean = applicationDetailsService
							.loadApplicationByApplicationId(applicationId);
					// add user and session info to the response
					UserBean userBean = new UserBean();
					userBean.populateFromEntity(user);
					RequestUtils.setHttpServletResponse(response,
							Status.SUCCESS_OK, MediaType.TEXT_XML,
							xmlSerializationService
									.getSuccessAsXML(xmlSerializationService
											.getSuccessBean(session.getId(),
													request, userBean)));
				} else {
					// this will execute if openId not exist in bioportal
					// database.
					SecurityContextHolder.getContext().setAuthentication(null);
					RequestUtils.setHttpServletResponse(response,
							Status.SERVER_ERROR_INTERNAL, MediaType.TEXT_XML,
							xmlSerializationService.getErrorAsXML(
									ErrorTypeEnum.INVALID_OPENID,
									accessedResource));
				}

			} else {
				// this will execute if user not verified by openId provider
				// site
				SecurityContextHolder.getContext().setAuthentication(null);
				RequestUtils.setHttpServletResponse(response,
						Status.SERVER_ERROR_INTERNAL, MediaType.TEXT_XML,
						xmlSerializationService.getErrorAsXML(
								ErrorTypeEnum.INVALID_CREDENTIALS,
								accessedResource));
			}
		} catch (ApplicationNotFoundException notFound) {
			// this will execute if applicationId not exist in bioportal
			log.error(notFound.getMessage());
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

	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	public String obtainOpenId(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_OPENId);
	}

	protected String obtainApplicationId(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_APPLICATIONID);
	}

	public CustomNcboUserDAO getNcboUserDAO() {
		return ncboUserDAO;
	}

	public void setNcboUserDAO(CustomNcboUserDAO ncboUserDAO) {
		this.ncboUserDAO = ncboUserDAO;
	}

	public ApplicationDetailsService getApplicationDetailsService() {
		return applicationDetailsService;
	}

	public void setApplicationDetailsService(
			ApplicationDetailsService applicationDetailsService) {
		this.applicationDetailsService = applicationDetailsService;
	}
}
