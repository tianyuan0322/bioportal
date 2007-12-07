package org.ncbo.stanford.view.rest.restlet.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.service.user.UserService;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class UserRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(UserRestlet.class);

	private UserService userService;

	@Override
	public void handle(Request request, Response response) {
				
		if(request.getMethod().equals(Method.GET)){
			getRequest(request, response);
		}else if(request.getMethod().equals(Method.PUT)){		
		}else if(request.getMethod().equals(Method.DELETE)){			
		}
		
		
	}
	
	
	/**
	 * Handle GET calls here
	 */
	private void getRequest(Request request,Response response){
		getUser(request, response);
	}
	
	/**
	 * Returns a specified UserBean to the response
	 * @param request
	 * @param resp
	 */
	private void getUser(Request request, Response resp) {
		UserBean usr = null;
		String id = (String) request.getAttributes().get("user");

		try {
			Integer intId = Integer.parseInt(id);
			usr = userService.getUser(intId);

			if (usr == null) {
				resp.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "User not found");
			}
		} catch (NumberFormatException nfe) {
			resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
		}

		if (usr != null && !resp.getStatus().isError()) {
			XStream xstream = new XStream(new DomDriver());
			xstream.alias("UserBean", UserBean.class);
			resp.setEntity(xstream.toXML(usr), MediaType.APPLICATION_XML);
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
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
