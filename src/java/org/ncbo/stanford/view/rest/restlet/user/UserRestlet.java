package org.ncbo.stanford.view.rest.restlet.user;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.helper.UserHelper;
import org.ncbo.stanford.util.RequestUtils;


public class UserRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(UserRestlet.class);

	private UserService userService;
	private XMLSerializationService xmlSerializationService;

	@Override
	public void handle(Request request, Response response) {
				
		if(request.getMethod().equals(Method.GET)){
			
			// Handle GET calls here
			findUser(request, response);
		
		} else if(request.getMethod().equals(Method.POST)){
			
			
			HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
			
			// TODO move string values to Constants
			String method = httpServletRequest.getParameter("method");

			if (method != null) {
				
				if (method.equalsIgnoreCase("PUT")) {

					// Handle PUT calls here
					System.out.println("+++++++++++++++++++++++++++++++++++++");
					System.out.println("           PUT call");
					System.out.println("+++++++++++++++++++++++++++++++++++++");

					updateUser(request, response);

				} else if (method.equalsIgnoreCase("DELETE")) {


					// Handle DELETE calls here
					System.out.println("+++++++++++++++++++++++++++++++++++++");
					System.out.println("           DELETE call");
					System.out.println("+++++++++++++++++++++++++++++++++++++");

					deleteUser(request, response);
				}
			}
		}
		
		
	}
	
	
	/**
	 * Returns a specified UserBean to the response
	 * @param request
	 * @param resp
	 */
	private void findUser(Request request, Response response) {
		
		// find the UserBean by UserID
		UserBean userBean = findUserBean(request, response);

		// prepare the response
		if (userBean != null && !response.getStatus().isError()) {
			XStream xstream = new XStream(new DomDriver());
			xstream.alias("UserBean", UserBean.class);
			response.setEntity(xstream.toXML(userBean), MediaType.APPLICATION_XML);
		}
	}
	

	/**
	 * Update a specified UserBean to the response
	 * @param request
	 * @param resp
	 */
	private void updateUser(Request request, Response response) {

		
		String accessedResource = request.getResourceRef().getPath();
		
		// find the UserBean from request
		UserBean userBean = findUserBean(request, response);

		
		if ( userBean != null) {
			
			// save the id for later
			Integer id = userBean.getId();
			
			// populate UserBean from Request object
			// currently id is not provided in the request object
			userBean = UserHelper.populateUserBeanFromRequest(request);
			
			// set the id
			userBean.setId(id);
			
			// now update the user
			getUserService().updateUser(userBean);

			// prepare the response
			if (!response.getStatus().isError()) {

				RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
						MediaType.TEXT_XML, xmlSerializationService.getSuccessAsXML(
								RequestUtils.getSessionId(request), null));
				
				/*

				RequestUtils.setHttpServletResponse(response,
						Status.SUCCESS_OK, MediaType.TEXT_XML,
						xmlSerializationService.getSuccessAsXML(RequestUtils
								.getSessionId(request), accessedResource));
				*/

			} else {

				// TODO
				// 1. determine where to set the response. where the error
				// occurred or at the end.
				// 2. sync response status and runtime error ?
				RequestUtils.setHttpServletResponse(response, response
						.getStatus(), MediaType.TEXT_XML,
						xmlSerializationService.getErrorAsXML(
								ErrorTypeEnum.RUNTIME_ERROR, accessedResource));

				/*
				 * RequestUtils.setHttpServletResponse(response,
				 * Status.SERVER_ERROR_INTERNAL, MediaType.TEXT_XML,
				 * xmlSerializationService.getErrorAsXML(
				 * ErrorTypeEnum.RUNTIME_ERROR, accessedResource));
				 */

			}
		}
		
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

		// now delete the user
		getUserService().deleteUser(userBean);
		
		if (userBean != null && !response.getStatus().isError()) {
			XStream xstream = new XStream(new DomDriver());
			xstream.alias("UserBean", UserBean.class);
			response.setEntity(xstream.toXML(userBean), MediaType.APPLICATION_XML);
		}
	}
	
	
	
	/**
	 * Returns a specified UserBean and set the response status if there is an error
	 * 
	 * 
	 * @param request
	 * @param resp
	 */
	private UserBean findUserBean(Request request, Response response) {
		
		UserBean userBean = null;
		String id = (String) request.getAttributes().get("user");

		try {
			Integer intId = Integer.parseInt(id);
			userBean = userService.findUser(intId);

			if (userBean == null) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "User not found");
			}
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
		}

		return userBean;
	}
	

	/**
	 * @return the userService
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}


	public XMLSerializationService getXmlSerializationService() {
		return xmlSerializationService;
	}


	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
	
	
}
