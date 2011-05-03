package org.ncbo.stanford.view.rest.restlet.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.UserNotFoundException;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class UserRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(UserRestlet.class);
	private UserService userService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		// Handle GET calls here
		findUser(request, response);
	}

	/**
	 * Handle PUT calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void putRequest(Request request, Response response) {
		// Handle PUT calls here
		updateUser(request, response);
	}

	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void deleteRequest(Request request, Response response) {
		// Handle DELETE calls here
		deleteUser(request, response);
	}

	/**
	 * Returns a specified UserBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void findUser(Request request, Response response) {
		// find the UserBean from request
		UserBean userBean = findUserBean(request, response);

		// generate response XML
		xmlSerializationService
				.generateXMLResponse(request, response, userBean);
	}

	/**
	 * Update a specified UserBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void updateUser(Request request, Response response) {
		// find the UserBean from request
		UserBean userBean = findUserBean(request, response);
		RESTfulSession session = null;

		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {
			// now update the user
			try {
				// populate UserBean from Request object
				BeanHelper.populateUserBeanFromRequest(request, userBean);
				session = userService.updateUser(userBean);
			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}

		// generate response XML
		xmlSerializationService
				.generateXMLResponse(request, response, session);
	}

	/**
	 * Delete a specified UserBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void deleteUser(Request request, Response response) {
		// find the UserBean by UserID
		UserBean userBean = findUserBean(request, response);

		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {
			// now delete the user
			try {
				userService.deleteUser(userBean);
			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}

			xmlSerializationService.generateXMLResponse(request, response,
					userBean);
		}

		// generate response XML
		// display success XML when successful, otherwise call
		// generateUserXMLResponse
		if (!response.getStatus().isError()) {
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		} else {
			xmlSerializationService.generateXMLResponse(request, response,
					userBean);
		}
	}

	/**
	 * Returns a specified UserBean and set the response status if there is an
	 * error
	 * 
	 * 
	 * @param request
	 * @param response
	 */
	private UserBean findUserBean(Request request, Response response) {
		UserBean userBean = null;
		String userId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.userid"));

		try {
			Integer intId = Integer.parseInt(userId);
			userBean = userService.findUser(intId);

			// if user is not found, set Error in the Status object
			if (userBean == null || userBean.getId() == null) {
				throw new UserNotFoundException(MessageUtils
						.getMessage("msg.error.userNotFound"));
			}
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe
					.getMessage());
			nfe.printStackTrace();
			log.error(nfe);
		} catch (UserNotFoundException unfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, unfe.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		}

		return userBean;
	}

	/**
	 * @param userService
	 * 
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
