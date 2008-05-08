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
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.helper.UserHelper;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.exception.DatabaseException;

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
		
		// find the UserBean from request
		UserBean userBean = findUserBean(request, response);

		// generate response XML
		generateUserXMLResponse (request, response, userBean);
		
	}
	

	/**
	 * Update a specified UserBean to the response
	 * @param request
	 * @param resp
	 */
	private void updateUser(Request request, Response response) {

				
		// find the UserBean from request
		UserBean userBean = findUserBean(request, response);

		// if "find" was successful, proceed to update
		//if ( userBean != null && userBean.getId() != null) {
		if (!response.getStatus().isError()) {	
		
			// save the id for later
			Integer id = userBean.getId();
			
			// populate UserBean from Request object
			// currently id is not provided in the request object
			userBean = UserHelper.populateUserBeanFromRequest(request);
			
			// set the id
			userBean.setId(id);
		
			// now update the user
			try {
				getUserService().updateUser(userBean);

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

		}

		// generate response XML
		generateUserXMLResponse (request, response, userBean);
		
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

				getUserService().deleteUser(userBean);
			
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
			
			generateUserXMLResponse (request, response, userBean);
		}

		// generate response XML
		// display success XML when successful, otherwise call generateUserXMLResponse
		if (!response.getStatus().isError()) {
			generateStatusXMLResponse (request, response);
		} else {
			generateUserXMLResponse (request, response, userBean);
		}

	}
	
	
	
	/**
	 * Returns a specified UserBean and set the response status if there is an error
	 * 
	 * 
	 * @param request
	 * @param response
	 */
	private UserBean findUserBean(Request request, Response response) {
		
		UserBean userBean = null;
		String id = (String) request.getAttributes().get("user");

		try {
			Integer intId = Integer.parseInt(id);
			userBean = userService.findUser(intId);

			response.setStatus(Status.SUCCESS_OK);
			
			// if user is not found, set Error in the Status object
			if (userBean == null || userBean.getId() == null) {
	
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "User Not found");
			}
		
		} catch (NumberFormatException nfe) {
			
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
			nfe.printStackTrace();
			log.error(nfe);	
			
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
			
		
}
