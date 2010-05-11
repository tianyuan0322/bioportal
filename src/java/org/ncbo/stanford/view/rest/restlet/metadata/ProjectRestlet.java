package org.ncbo.stanford.view.rest.restlet.metadata;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.manager.metakb.ProjectMetadataManager;
import org.ncbo.stanford.manager.metakb.SimpleObjectManager;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.restlet.data.Request;

/**
 * REST API access to Project metadata objects.
 * <p>
 * Action is determined by type of request.
 * <ul>
 * <li>POST: Create a new project.</li>
 * <li>GET: Retrieve a particular project.</li>
 * <li>DELETE: Delete the project.</li>
 * <li>PUT: Update the project with specified values.</li>
 * </ul>
 * 
 * @author <a href="mailto:loeser@cs.stanford.edu">Tony Loeser</a>
 *
 */
public class ProjectRestlet extends AbstractCRUDRestlet<ProjectBean> {
	
	private static final Log log = LogFactory.getLog(ProjectRestlet.class);
	private ProjectMetadataManager projectMetadataManager;
	
	// =========================================================================
	// Implement helpers from abstract class
	
	// Implement AbstractCRUDRestlet
	protected SimpleObjectManager<ProjectBean> getSimpleObjectManager() {
		return projectMetadataManager;
	}

	// Implement AbstractCRUDRestlet
	protected void logError(Exception e) {
		log.error(e);
	}

	// Implement AbstractCRUDRestlet
	protected void populateBeanFromRequest(ProjectBean bean, Request request) {
		HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
		
		String name = httpServletRequest.getParameter("name");
		bean.setName(name);
		
		String description = httpServletRequest.getParameter("description");
		bean.setDescription(description);
		
		String homePage = httpServletRequest.getParameter("homePage");
		bean.setHomePage(homePage);
		
		String people = httpServletRequest.getParameter("people");
		bean.setPeople(people);
		
		String institution = httpServletRequest.getParameter("institution");
		bean.setInstitution(institution);
		
		String userIdString = httpServletRequest.getParameter("userId");
		if (userIdString != null && !userIdString.equals("")) {
			Integer userId = new Integer(userIdString);
			bean.setUserId(userId);
		}
	}
	
	// =========================================================================
	// Standard accessors
	
	// Override AbstractBaseRestlet
	public void setXmlSerializationService(XMLSerializationService xmlSerializationService) {
		xmlSerializationService.alias("projectBean", ProjectBean.class);
		this.xmlSerializationService = xmlSerializationService;
	}
	
	public void setProjectMetadataManager(ProjectMetadataManager projectMetadataManager) {
		this.projectMetadataManager = projectMetadataManager;
	}

}
