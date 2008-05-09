package org.ncbo.stanford.view.rest.restlet.user;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.DatabaseException;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.helper.UserHelper;


public class UsersRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(UsersRestlet.class);

	private UserService userService;
	private XMLSerializationService xmlSerializationService;

	@Override
	public void handle(Request request, Response response) {
		
		if (request.getMethod().equals(Method.GET)) {
			getRequest(request, response);
	
		} else if (request.getMethod().equals(Method.POST)) {
			postRequest(request, response);
		} 		
	}
	

	/**
	 * Handle GET calls here
	 */
	private void getRequest(Request request, Response response) {
		
		listUsers(request, response);
	}

	
	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	private void postRequest(Request request, Response response) {
		
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
			userList = getUserService().findUsers();

		} catch (DatabaseException dbe) {

			response.setStatus(Status.SERVER_ERROR_INTERNAL,
					"DatabaseException occured.");
			dbe.printStackTrace();
			log.error(dbe);

		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
			
		} finally {
									
			// generate response XML
			//getXmlSerializationService().generateUserListXMLResponse (request, response, userList);
			getXmlSerializationService().generateXMLResponse (request, response, userList);
			
		}

	}
	
	
	/**
	 * Return to the response creating user
	 * 
	 * @param request response
	 */
	private void createUser(Request request, Response response) {
				
		UserBean userBean = UserHelper.populateUserBeanFromRequest(request);
		
		// create the user
		try {
			getUserService().createUser(userBean);

		} catch (DatabaseException dbe) {

			response.setStatus(Status.SERVER_ERROR_INTERNAL, "DatabaseException occured.");
			dbe.printStackTrace();
			log.error(dbe);

		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
			
		} finally {
		
			// generate response XML
			getXmlSerializationService().generateXMLResponse (request, response, userBean);
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