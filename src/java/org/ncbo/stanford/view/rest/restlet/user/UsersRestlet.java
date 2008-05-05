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
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
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

		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println("           POST call");
		System.out.println("+++++++++++++++++++++++++++++++++++++");

		
		createUser(request, response);		
		
		RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
				MediaType.TEXT_XML, xmlSerializationService.getSuccessAsXML(
						RequestUtils.getSessionId(request), null));
	
	}

	/**
	 * Return to the response a listing of users
	 * 
	 * @param response
	 */
	private void listUsers(Request request, Response response) {
		
		List<UserBean> userList = getUserService().findUsers();

		RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
				MediaType.TEXT_XML, xmlSerializationService.getSuccessAsXML(
						RequestUtils.getSessionId(request), request
								.getResourceRef().getPath(), userList));
	}
	
	
	/**
	 * Return to the response creating user
	 * 
	 * @param request response
	 */
	private void createUser(Request request, Response response) {
				
		UserBean userBean = UserHelper.populateUserBeanFromRequest(request);
		
		try {
			getUserService().createUser(userBean);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
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