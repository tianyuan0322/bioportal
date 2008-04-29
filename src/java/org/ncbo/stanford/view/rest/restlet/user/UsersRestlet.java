package org.ncbo.stanford.view.rest.restlet.user;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.DateHelper;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;

// cyoun
import org.ncbo.stanford.domain.generated.NcboUser;


/**
 * Question : the naming convention - when to use "find" or "get"?
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
		
		
		
/*
		Form form = request.getResourceRef().getQueryAsForm();

		System.out.println(" Form as entity from = " + form);

		System.out.println(" Form as query String = " + form.getQueryString());
		

		System.out.println("Trying to getParameters...");
		//StringBuffer sb = new StringBuffer("foo");
		for (Parameter p : form) {
			// System.out.println(p);
			System.out.println("field name = " + p.getName() + "  value = "
					+ p.getValue());

		}
*/
		
		Form form = request.getEntityAsForm();
		

		 if (form != null) {
		  

			 String username = form.getFirstValue("username");
			 String password = form.getFirstValue("password");
			String firstname = form.getFirstValue("firstname");

			System.out.println("username = " + username);
			System.out.println("firstname = " + firstname);
		 }
		 
		 /*
		 *String lastname =
		 * form.getFirstValue("lastname"); String email =
		 * form.getFirstValue("email"); Date dateCreated =
		 * DateHelper.getDateFrom(form .getFirstValue("dateCreated"));
		 * 
		 * 
		 * 
		 * 
		 * NcboUser ncboUser = new NcboUser(username, password, email,
		 * firstname, lastname, dateCreated);
		 
		 *System.out.println("+++++++++++++++++++++++++"); 
		 *System.out.println("createUser() : NCBO USER created "); System.out.println(username);
		 * System.out.println(password); System.out.println(email);
		 * System.out.println("----");
		 * System.out.println(ncboUser.getUsername());
		 * System.out.println(ncboUser.getPassword());
		 * System.out.println(ncboUser.getEmail());
		 * System.out.println("+++++++++++++++++++++++++");
		 * 
		 *  // getUserService().saveUser(ncboUser);
		 *  }
		 * 
		 */
		
		/*
		List<UserBean> userList = getUserService().findUsers();

		RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
				MediaType.TEXT_XML, xmlSerializationService.getSuccessAsXML(
						RequestUtils.getSessionId(request), request
								.getResourceRef().getPath(), userList));
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