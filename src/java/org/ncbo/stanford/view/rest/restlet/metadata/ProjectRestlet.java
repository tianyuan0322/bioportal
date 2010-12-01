package org.ncbo.stanford.view.rest.restlet.metadata;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.metadata.ProjectBean;
import org.ncbo.stanford.service.metadata.BeanCRUDService;
import org.ncbo.stanford.service.metadata.ProjectMetadataService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;

/**
 * REST API access to Project metadata objects.
 * <p>
 * Action is determined by type of request.  For list of actions, see
 * {@link AbstractCRUDRestlet}.
 * 
 * @author Tony Loeser
 *
 */
public class ProjectRestlet extends AbstractCRUDRestlet<ProjectBean> {
	
	private static final Log log = LogFactory.getLog(ProjectRestlet.class);
	private ProjectMetadataService projectMetadataService;
	
	// =========================================================================
	// Implement helpers from abstract class
	
	// Implement AbstractCRUDRestlet
	protected BeanCRUDService<ProjectBean> getBeanCRUDService() {
		return projectMetadataService;
	}

	// Implement AbstractCRUDRestlet
	protected void logError(Exception e) {
		log.error(e);
	}

	// Implement AbstractCRUDRestlet
	protected void populateBeanFromRequest(ProjectBean bean, Request request) {
		HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
		
		String name = httpServletRequest.getParameter(RequestParamConstants.PARAM_META_NAME);
		bean.setName(name);
		
		String description = httpServletRequest.getParameter(RequestParamConstants.PARAM_META_DESC);
		bean.setDescription(description);
		
		String homePage = httpServletRequest.getParameter(RequestParamConstants.PARAM_META_HOME_PAGE);
		bean.setHomePage(homePage);
		
		String people = httpServletRequest.getParameter(RequestParamConstants.PARAM_META_PEOPLE);
		bean.setPeople(people);
		
		String institution = httpServletRequest.getParameter(RequestParamConstants.PARAM_META_INST);
		bean.setInstitution(institution);
		
		String userIdString = httpServletRequest.getParameter(RequestParamConstants.PARAM_META_USER_ID);
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
	
	public void setProjectMetadataService(ProjectMetadataService projectMetadataService) {
		this.projectMetadataService = projectMetadataService;
	}

}
