package org.ncbo.stanford.view.rest.restlet.user;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.ext.fileupload.RestletFileUpload;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.DateHelper;


/**
 * 
 * @author cyoun
 *
 */

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
			
		
		try {
			HttpServletRequest httpServletRequest = RequestUtils
					.getHttpServletRequest(request);
			
			// TODO code clean up later. Use constants
			String username = httpServletRequest.getParameter("username");
			String password = httpServletRequest.getParameter("password");
			String firstname = httpServletRequest.getParameter("firstname");
			String lastname = httpServletRequest.getParameter("lastname");
			String email = httpServletRequest.getParameter("email");
			Date dateCreated = DateHelper.getDateFrom(httpServletRequest.getParameter("dateCreated"));
		
			NcboUser ncboUser = new NcboUser(username, password, email,
					firstname, lastname, dateCreated);
			
			/*
			System.out.println("**************************");
			System.out.println("NCBO username = " + ncboUser.getUsername());
			System.out.println(ncboUser.getPassword());
			System.out.println(ncboUser.getFirstname());
			System.out.println(ncboUser.getLastname());
			System.out.println(ncboUser.getEmail());
			System.out.println(ncboUser.getDateCreated());
			System.out.println("**************************");
			*/
			
			// create UserBean from NcboUser and save it
			getUserService().createUser(getUserService().populateUser(ncboUser));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}

		/*		
		    // OBSOLETE CODE
			// BUG in Restlet API
			// Make sure Entity is consumed first time here, otherwise it gets emptied out after first usage
			Form form = request.getEntityAsForm();
	
			String username = form.getFirstValue("username");
			String password = form.getFirstValue("password");
			String firstname = form.getFirstValue("firstname");
			String lastname = form.getFirstValue("lastname");
			String email = form.getFirstValue("email");
			Date dateCreated = DateHelper.getDateFrom(form.getFirstValue("dateCreated"));

			System.out.println("1. username = " + username);
			System.out.println("2. firstname = " + firstname);
		*/
			

		 
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