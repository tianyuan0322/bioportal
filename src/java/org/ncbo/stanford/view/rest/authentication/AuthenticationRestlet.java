package org.ncbo.stanford.view.rest.authentication;

import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.view.session.expiration.system.impl.SessionExpirationSystem;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Handles Acegi form based authentication.
 * Simulates the the Acegi's authenticationProcessingFilter
 * in RESTful environment.
 * 
 * @author Michael Dorf
 *
 */
public final class AuthenticationRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(
			AuthenticationRestlet.class);

	private AuthenticationManager authenticationManager;
	private SessionExpirationSystem sessionExpirationSystem;

	@Override
	public void handle(Request request, Response response) {
		authenticate(request, response);
	}
	
	private String authenticate(Request request, Response response) {
		String outcome = null;
		
		try {
			Form form = request.getEntityAsForm();
			
			final String username = obtainUsername(form);
			final String password = obtainPassword(form);
			final String applicationId = obtainApplicationId(form);
			
			response.setEntity("Username: " + username + "<br/>Password: " + password, MediaType.TEXT_HTML);
			
			
			
			final UsernamePasswordAuthenticationToken authReq = 
				new UsernamePasswordAuthenticationToken(
						username, password);

//			final HttpServletRequest request = RequestUtils.getServletRequest();
//			final HttpServletResponse response = RequestUtils.getServletResponse();
//			
//			authReq.setDetails(new WebAuthenticationDetails(request));
//
//			final HttpSession session = request.getSession();
//			session.setAttribute(
//							AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY,
//							username);
//			
//			/* perform authentication
//			 */
//			final Authentication auth = getAuthenticationManager()
//					.authenticate(authReq);
//
//			/* initialize the security context.
//			 */
//			final SecurityContext secCtx = SecurityContextHolder.getContext();
//			secCtx.setAuthentication(auth);
//			session.setAttribute(
//							HttpSessionContextIntegrationFilter.ACEGI_SECURITY_CONTEXT_KEY,
//							secCtx);
//			loggedInUserBean.setLoggedInUser(username);
//
//			if (isRedirect) {
//				String targetUrl = obtainFullRequestUrl(request);
//				RequestUtils.sendRedirect(request, response, targetUrl);
//			}			
		} catch (Exception e) {
//			log.error(e);
//			SecurityContextHolder.getContext().setAuthentication(null);
//			FacesUtils.addErrorMessage(
//					ViewConstants.LOGIN_FORM,
//					ApplicationConstants.INCORRECT_USERNAME_KEY);
//			loggedInUserBean.logout();
//
//			if (isRedirect) {
//				outcome = ViewConstants.FAILURE;
//			}			
		}
		
		return outcome;
	}

//	public void logout(ActionEvent e) {
//		final HttpServletRequest request = RequestUtils.getServletRequest();
//		request.getSession(false).removeAttribute(
//				HttpSessionContextIntegrationFilter.ACEGI_SECURITY_CONTEXT_KEY);
//
//		/* simulate the SecurityContextLogoutHandler
//		 */
//		SecurityContextHolder.clearContext();
//		request.getSession(false).invalidate();
//		loggedInUserBean.logout();
//	}
//	
//    public String obtainFullRequestUrl() {
//        return AbstractProcessingFilter.obtainFullRequestUrl(
//        		RequestUtils.getServletRequest());
//    }
//
//	private static String obtainFullRequestUrl(HttpServletRequest request) {
//        String targetUrl = 
//        	AbstractProcessingFilter.obtainFullRequestUrl(request);
//
//        if (targetUrl == null) {
//        	targetUrl = FacesUtils.getCompleteUrlForNavigationResult(
//        			ViewConstants.TSN_DEFAULTURL);
//        }
//        
//        return targetUrl;
//    }

	
	
	
	
	
	
	
	
	
	
	
	
	

    protected String obtainPassword(Form form) {
    	return form.getFirstValue(RequestParamConstants.PARAM_PASSWORD);
    }

    protected String obtainUsername(Form form) {
    	return form.getFirstValue(RequestParamConstants.PARAM_USERNAME);
    }


    protected String obtainApplicationId(Form form) {
        return form.getFirstValue(RequestParamConstants.PARAM_APPLICATIONID);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	/**
	 * @return the authenticationManager
	 */
	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	/**
	 * @param authenticationManager the authenticationManager to set
	 */
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * @return the sessionExpirationSystem
	 */
	public SessionExpirationSystem getSessionExpirationSystem() {
		return sessionExpirationSystem;
	}

	/**
	 * @param sessionExpirationSystem the sessionExpirationSystem to set
	 */
	public void setSessionExpirationSystem(
			SessionExpirationSystem sessionExpirationSystem) {
		this.sessionExpirationSystem = sessionExpirationSystem;
	}
}
