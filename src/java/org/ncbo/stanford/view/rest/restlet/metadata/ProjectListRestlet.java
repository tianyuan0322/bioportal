package org.ncbo.stanford.view.rest.restlet.metadata;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.manager.metakb.ProjectMetadataManager;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class ProjectListRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(ProjectListRestlet.class);
	private ProjectMetadataManager projectMetadataManager;

	// Override AbstractBaseRestlet
	public void getRequest(Request request, Response response) {
		// Perform various queries depending on the parameters
		// If no parameters are provided, return all projects
		
		// Look for the user id in here. If you get one, do a user query.
		HttpServletRequest httpRequest = RequestUtils.getHttpServletRequest(request);
		String userIdString = (String)httpRequest.getParameter(RequestParamConstants.PARAM_META_USER_ID);		
		Collection<ProjectBean> result = null;
		try {
			if (userIdString == null) {
				// No parameters; return all projects
				result = projectMetadataManager.retrieveAllObjects();
			} else {
				// There is a user id parameter.
				Integer userId = new Integer(userIdString);
				result = projectMetadataManager.getProjectsForUser(userId);
			}
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad user id: "+nfe.getMessage());
			nfe.printStackTrace();
			log.error(nfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, result);
		}	
	}

	// ============================================================
	// Accessors
	
	public void setProjectMetadataManager(
			ProjectMetadataManager projectMetadataManager) {
		this.projectMetadataManager = projectMetadataManager;
	}
}
