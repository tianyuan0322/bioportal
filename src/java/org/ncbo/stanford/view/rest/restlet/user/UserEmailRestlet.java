package org.ncbo.stanford.view.rest.restlet.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.UserNotFoundException;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class UserEmailRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(UsersRestlet.class);
	private UserService userService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		getUser(request, response);
	}

	/**
	 * Return to the response a listing of users
	 * 
	 * @param response
	 */
	private void getUser(Request request, Response response) {
		UserBean userBean = null;
		String email = (String) request.getAttributes().get("email");

		try {
			userBean = userService.findUserByEmail(email);
			response.setStatus(Status.SUCCESS_OK);

			// if user is not found, set Error in the Status object
			if (userBean == null || userBean.getId() == null) {
				throw new UserNotFoundException(MessageUtils
						.getMessage("msg.error.userNotFound"));
			}
		} catch (UserNotFoundException unfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, unfe.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					userBean);
		}
	}

	/**
	 * @param userService
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
