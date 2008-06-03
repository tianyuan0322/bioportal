package org.ncbo.stanford.view.rest.restlet.user;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;


public class UserRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(UserRestlet.class);

	private UserService userService;
	private XMLSerializationService xmlSerializationService;

	
	@Override
	public void handle(Request request, Response response) {
									
		if(request.getMethod().equals(Method.GET)){
				getRequest(request, response);	
						
		} else if(request.getMethod().equals(Method.POST)){
			
			HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
			//String method = httpServletRequest.getParameter(RequestParamConstants.PARAM_METHOD);
			String method = httpServletRequest.getParameter( MessageUtils.getMessage("http.param.method"));

			if (method != null) {

				if (method.equalsIgnoreCase(MessageUtils.getMessage("http.put"))) {

					putRequest(request, response);

				} else if (method.equalsIgnoreCase(MessageUtils.getMessage("http.delete"))) {

					deleteRequest(request, response);
				}
			}
			
		}  else if(request.getMethod().equals(MessageUtils.getMessage("http.put"))){
			
			putRequest(request, response);
			
		}  else if(request.getMethod().equals(MessageUtils.getMessage("http.delete"))){
			
			deleteRequest(request, response);

		}  
		
	}


	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	private void getRequest(Request request, Response response) {

		// Handle GET calls here
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println("           GET call");
		System.out.println("+++++++++++++++++++++++++++++++++++++");

		findUser(request, response);

	}	
	
	
	/**
	 * Handle PUT calls here
	 * 
	 * @param request
	 * @param response
	 */
	private void putRequest(Request request, Response response) {

		// Handle PUT calls here
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println("           PUT call");
		System.out.println("+++++++++++++++++++++++++++++++++++++");

		updateUser(request, response);

	}
	
	
	
	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	private void deleteRequest(Request request, Response response) {

		// Handle DELETE calls here
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println("           DELETE call");
		System.out.println("+++++++++++++++++++++++++++++++++++++");

		//deleteRequest(request, response);
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
		getXmlSerializationService().generateXMLResponse (request, response, userBean);
		
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
			userBean = BeanHelper.populateUserBeanFromRequest(request);
			
			// set the id
			userBean.setId(id);
		
			// now update the user
			try {
				getUserService().updateUser(userBean);

			} catch (Exception e) {
				response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}

		}

		// generate response XML
		getXmlSerializationService().generateXMLResponse (request, response, userBean);
		
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

			} catch (Exception e) {
				response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
			
			getXmlSerializationService().generateXMLResponse (request, response, userBean);
		}

		// generate response XML
		// display success XML when successful, otherwise call generateUserXMLResponse
		if (!response.getStatus().isError()) {
			getXmlSerializationService().generateStatusXMLResponse (request, response);
		} else {
			getXmlSerializationService().generateXMLResponse (request, response, userBean);
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
		String id = (String) request.getAttributes().get(MessageUtils.getMessage("entity.user"));

		try {
			Integer intId = Integer.parseInt(id);
			userBean = userService.findUser(intId);

			response.setStatus(Status.SUCCESS_OK);
			
			// if user is not found, set Error in the Status object
			if (userBean == null || userBean.getId() == null) {
	
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, MessageUtils.getMessage("msg.error.userNotFound"));
			}
		
		} catch (NumberFormatException nfe) {
			
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
			nfe.printStackTrace();
			log.error(nfe);	

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
	 *           
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
