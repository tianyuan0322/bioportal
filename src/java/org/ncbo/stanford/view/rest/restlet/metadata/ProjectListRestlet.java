package org.ncbo.stanford.view.rest.restlet.metadata;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.metadata.ProjectBean;
import org.ncbo.stanford.service.metadata.ProjectMetadataService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 * Respond to POST requests by returning lists of projects.
 * <ul>
 *   <li>With no parameters: Return all projects.</li>
 *   <li>With user id: Return all projects for that user.</li>
 * </ul>
 * 
 * @author Tony Loeser
 */
public class ProjectListRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(ProjectListRestlet.class);
	private ProjectMetadataService projectMetadataService;

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
				result = projectMetadataService.retrieveAllObjects();
			} else {
				// There is a user id parameter.
				Integer userId = new Integer(userIdString);
				result = projectMetadataService.getProjectsForUser(userId);
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
	
	// Override AbstractBaseRestlet
	public void setXmlSerializationService(XMLSerializationService xmlSerializationService) {
		xmlSerializationService.alias("projectBean", ProjectBean.class);
		this.xmlSerializationService = xmlSerializationService;
	}
	
	public void setProjectMetadataService(ProjectMetadataService projectMetadataService) {
		this.projectMetadataService = projectMetadataService;
	}
}
