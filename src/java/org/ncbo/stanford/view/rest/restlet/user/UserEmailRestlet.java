package org.ncbo.stanford.view.rest.restlet.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class UserEmailRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(UsersRestlet.class);

	private UserService userService;
	private XMLSerializationService xmlSerializationService;

	@Override
	public void handle(Request request, Response response) {

		if (request.getMethod().equals(Method.GET)) {
			getRequest(request, response);

		}
	}

	/**
	 * Handle GET calls here
	 */
	private void getRequest(Request request, Response response) {

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
			userBean = getUserService().findUserByEmail(email);

			response.setStatus(Status.SUCCESS_OK);

			// if user is not found, set Error in the Status object
			if (userBean == null || userBean.getId() == null) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, MessageUtils
						.getMessage("msg.error.userNotFound"));
			}

		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);

		} finally {

			// generate response XML
			getXmlSerializationService().generateXMLResponse(request, response,
					userBean);

		}

	}

	/**
	 * @return the userService
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * @param userService
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
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
