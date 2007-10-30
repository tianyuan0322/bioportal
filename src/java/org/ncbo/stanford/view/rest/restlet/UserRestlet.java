package org.ncbo.stanford.view.rest.restlet;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.service.user.UserService;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.springframework.transaction.annotation.Transactional;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class UserRestlet extends Restlet {

	private UserService userService;

	@Override
	@Transactional
	public void handle(Request request, Response response) {
		UserBean user = getUser(request.getResourceRef(), response);

		if (user != null && !response.getStatus().isError()) {
			XStream xstream = new XStream(new DomDriver());
			xstream.alias("UserBean", UserBean.class);
			response.setEntity(xstream.toXML(user), MediaType.APPLICATION_XML);
		}
	}

	private UserBean getUser(Reference ref, Response resp) {
		UserBean usr = null;
		String id = ref.getLastSegment();

		try {
			Integer intId = Integer.parseInt(id);
			usr = userService.getUser(intId);

			if (usr == null) {
				resp.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "User not found");
			}
		} catch (NumberFormatException nfe) {
			resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
		}

		return usr;
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
