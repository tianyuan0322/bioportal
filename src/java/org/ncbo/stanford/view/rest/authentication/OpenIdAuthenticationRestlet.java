/**
 * 
 */
package org.ncbo.stanford.view.rest.authentication;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;

/**
 * @author s.reddy
 * 
 */
public class OpenIdAuthenticationRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OpenIdAuthenticationRestlet.class);

	// for return path
	private String returnUrl;

	@Override
	public void anyMethodRequest(Request request, Response response) {
		authenticate(request, response);
	}

	@SuppressWarnings("unchecked")
	private void authenticate(Request request, Response response) {
/*		
		String accessedResource = request.getResourceRef().getPath();

		try {
			HttpServletRequest httpServletRequest = RequestUtils
					.getHttpServletRequest(request);
			final String openId = obtainOpenId(httpServletRequest);
			final String applicationId = obtainApplicationId(httpServletRequest);

			ConsumerManager manager = new ConsumerManager();
			// discover the OpenID authentication server's endpoint URL
			List discoveries = manager.discover(openId);

			// attempt to associate with the OpenID provider
			// and retrieve one service endpoint for authentication
			DiscoveryInformation discovered = manager.associate(discoveries);

			// store the discovery, manager information in the user's session
			// for later use
			HttpSession httpSession = RequestUtils.getHttpServletRequest(
					request).getSession(true);
			httpSession.setAttribute("discovered", discovered);
			httpSession.setAttribute("manager", manager);
			httpSession.setAttribute("applicationId", applicationId);

			// generate an AuthRequest message to be sent to the OpenID provider
			AuthRequest authReq = manager.authenticate(discovered, returnUrl);

			// redirect the user to their provider for authentication
			RequestUtils.getHttpServletResponse(response).sendRedirect(
					authReq.getDestinationUrl(true));

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			SecurityContextHolder.getContext().setAuthentication(null);

			RequestUtils.setHttpServletResponse(response,
					Status.SERVER_ERROR_INTERNAL, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(
							ErrorTypeEnum.RUNTIME_ERROR, accessedResource));
		}
*/
	}

	public String obtainOpenId(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_OPENID);
	}

	protected String obtainApplicationId(HttpServletRequest req) {
		return req.getParameter(RequestParamConstants.PARAM_APPLICATIONID);
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

}
