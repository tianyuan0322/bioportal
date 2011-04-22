package org.ncbo.stanford.view.rest.restlet.user;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class UsersRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(UsersRestlet.class);
	private UserService userService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		listUsers(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void postRequest(Request request, Response response) {
		createUser(request, response);
	}

	/**
	 * Return to the response a listing of users
	 * 
	 * @param response
	 */
	private void listUsers(Request request, Response response) {
		List<UserBean> userList = null;

		try {
			userList = userService.findUsers();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					userList);
		}
	}

	/**
	 * Return to the response creating user
	 * 
	 * @param request
	 *            response
	 */
	private void createUser(Request request, Response response) {
		UserBean userBean = null;

		try {
			userBean = BeanHelper.populateUserBeanFromRequest(request);
			String username = userBean.getUsername();
			String password = userBean.getPassword();
			String email = userBean.getEmail();
			
			if (StringHelper.isNullOrNullString(username)
					|| StringHelper.isNullOrNullString(password)
					|| StringHelper.isNullOrNullString(email)) {
				throw new InvalidInputException();
			}

			userService.createUser(userBean);
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
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