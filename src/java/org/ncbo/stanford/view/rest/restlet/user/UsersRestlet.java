package org.ncbo.stanford.view.rest.restlet.user;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.DatabaseException;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.UserHelper;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


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
			
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML, xmlSerializationService.getSuccessAsXML(
							RequestUtils.getSessionId(request), request
									.getResourceRef().getPath(), userList));

		} catch (DatabaseException dbe) {

			response.setStatus(Status.SERVER_ERROR_INTERNAL,
					"DatabaseException occured.");
			dbe.printStackTrace();
			log.error(dbe);

		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		}
	

		// generate response XML
		generateUserListXMLResponse (request, response, userList);

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

			response.setStatus(Status.SERVER_ERROR_INTERNAL,
			"DatabaseException occured.");
			dbe.printStackTrace();
			log.error(dbe);

		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		}
		
		// generate response XML
		generateUserXMLResponse (request, response, userBean);

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


	/*
	 * REFACTORING!
	 * 
	 */
	
	
	/**
	 * Generates Generic XML response which contains status info 
	 * whether success or fail.  session id and access resource info is included.
	 * 
	 * @param request response
	 *            the userService to set
	 */
	private void generateStatusXMLResponse (Request request, Response response) {
		
		String accessedResource = request.getResourceRef().getPath();
		
		if (!response.getStatus().isError()) {
		
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML, xmlSerializationService.getSuccessAsXML(
						RequestUtils.getSessionId(request), accessedResource));
			
		
		} else {

			RequestUtils.setHttpServletResponse(response, response.getStatus(),
					MediaType.TEXT_XML, xmlSerializationService.getErrorAsXML(
							response.getStatus(), accessedResource));

		}
			
		
	}
		
	
	/**
	 * Generates User Specific XML response which contains status info 
	 * If success, user info is included.
	 * 
	 * @param request response
	 *            the userService to set
	 */
	private void generateUserXMLResponse(Request request, Response response,
			UserBean userBean) {

		//if (userBean != null && response.getStatus().isSuccess()) {
		if (!response.getStatus().isError()) {
			
			XStream xstream = new XStream(new DomDriver());
			xstream.alias("UserBean", UserBean.class);
			response.setEntity(xstream.toXML(userBean),
					MediaType.APPLICATION_XML);

		} else {
			
			generateStatusXMLResponse (request, response);

			
		}
	}
	
	/**
	 * Generates User Specific XML response which contains status info 
	 * If success, user info is included.
	 * 
	 * @param request response
	 *            the userService to set
	 */
	private void generateUserListXMLResponse(Request request, Response response,
			List<UserBean> userList) {
		
		if (userList.size() > 0) {

			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML, xmlSerializationService
							.getSuccessAsXML(
									RequestUtils.getSessionId(request), request
											.getResourceRef().getPath(),
									userList));
		} else {

			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "User Not found");
			generateStatusXMLResponse(request, response);
		}
			
	
	}
	

	
}