package org.ncbo.stanford.view.rest.restlet.metadata;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.service.metadata.ProjectMetadataService;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.rest.restlet.review.ReviewRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class ProjectListRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(ReviewRestlet.class);
	private ProjectMetadataService projectMetadataService;

	@Override
	public void getRequest(Request request, Response response) {
		// Perform various queries depending on the parameters
		// If no parameters are provided, return all projects
		List<ProjectBean> result = null;
		try {
			result = projectMetadataService.getAllProjects();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, result);
		}	
	}

	// ============================================================
	// Getters / setters
	
	public void setProjectMetadataService(
			ProjectMetadataService projectMetadataService) {
		this.projectMetadataService = projectMetadataService;
	}
}
