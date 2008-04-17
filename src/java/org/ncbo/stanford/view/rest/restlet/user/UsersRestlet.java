package org.ncbo.stanford.view.rest.restlet.user;

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
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;

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

	
	//TODO verify if this is obsolete
	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	private void postRequest(Request request, Response response) {
		Form form = request.getEntityAsForm();

		// 1/ Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(10002400);

		RestletFileUpload rfu = new RestletFileUpload(factory);
		rfu.setSizeMax(10000000);
		try {
			List<FileItem> files = rfu.parseRepresentation(request.getEntity());

			
			
			
//			ontologyLoadProcessorService.processOntologyLoad(files.get(0),
//					buildBeanFromForm(form));
			
			
		} catch (Exception e) {
			RequestUtils.setHttpServletResponse(response,
					Status.CLIENT_ERROR_BAD_REQUEST, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(
							ErrorTypeEnum.INVALID_FILE, null));
			e.printStackTrace();
			return;
		}

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