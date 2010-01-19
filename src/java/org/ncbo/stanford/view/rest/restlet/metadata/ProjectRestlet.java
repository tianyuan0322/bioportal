package org.ncbo.stanford.view.rest.restlet.metadata;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.service.metadata.ProjectMetadataService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.rest.restlet.review.ReviewRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

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
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 *
 */
public class ProjectRestlet extends AbstractBaseRestlet {
	
	private static final Log log = LogFactory.getLog(ReviewRestlet.class);
	private ProjectMetadataService projectMetadataService;
	
	// ============================================================
	// POST: Create a new project

	@Override
	public void postRequest(Request request, Response response) {
		createProject(request, response);
	}
	
	private void createProject(Request request, Response response) {
		ProjectBean newProject = new ProjectBean();
		try {
			// Verify that the "id" in the URL says "new"
			String idString = extractIdString(request);
			if (!(idString.equalsIgnoreCase("new"))) {
				throw new IllegalArgumentException("Expected id=\"new\" for POST create-project request");
			}
			
			// Fill in non-automatic data from the POST parameters
			populateProjectBeanFromRequest(newProject, request);
			
			// Add the Project to the metadata ontology
			// Side affect: newProject will be updated with id, etc.
			projectMetadataService.saveProject(newProject);
		} catch (IllegalArgumentException iae) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			iae.printStackTrace();
			log.error(iae);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, newProject);
		}
	}
	
	// ============================================================
	// GET: Retrieve and return the project.
	
	@Override
	public void getRequest(Request request, Response response) {
		retrieveProject(request, response);
	}
	
	private void retrieveProject(Request request, Response response) {
		ProjectBean projectBean = null;
		try {
			Integer id = extractId(request);
			projectBean = projectMetadataService.getProjectForId(id);
		} catch (InvalidInputException iie) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			iie.printStackTrace();
			log.error(iie);
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			monfe.printStackTrace();
			log.error(monfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, projectBean);
		}
	}
	
	// ============================================================
	// DELETE: Delete the project.
	
	@Override
	public void deleteRequest(Request request, Response response) {
		deleteProject(request, response);
	}
	
	private void deleteProject(Request request, Response response) {
		try {
			Integer id = extractId(request);
			projectMetadataService.deleteProject(id);
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			monfe.printStackTrace();
			log.error(monfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateStatusXMLResponse(request, response);
		}
	}

	// ============================================================
	// PUT: Update the project.
	
	@Override
	public void putRequest(Request request, Response response) {
		updateProject(request, response);
	}

	private void updateProject(Request request, Response response) {
		ProjectBean projectBean = null;
		try {
			Integer id = extractId(request);
			projectBean = projectMetadataService.getProjectForId(id);
			populateProjectBeanFromRequest(projectBean, request);
			projectMetadataService.saveProject(projectBean);
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			monfe.printStackTrace();
			log.error(monfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, projectBean);
		}
	}

	// ============================================================
	// Helpers
	
	private void populateProjectBeanFromRequest(ProjectBean projectBean, Request request) throws Exception {
		HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
		
		String name = httpServletRequest.getParameter("name");
		projectBean.setName(name);
		
		String description = httpServletRequest.getParameter("description");
		projectBean.setDescription(description);
		
		String homePage = httpServletRequest.getParameter("homePage");
		projectBean.setHomePage(homePage);
		
		String people = httpServletRequest.getParameter("people");
		projectBean.setPeople(people);
		
		String institution = httpServletRequest.getParameter("institution");
		projectBean.setInstitution(institution);
		
		String userIdString = httpServletRequest.getParameter("userId");
		if (userIdString != null && !userIdString.equals("")) {
			Integer userId = new Integer(userIdString);
			projectBean.setUserId(userId);
		}
	}
	
	private String extractIdString(Request request) {
		// Get the string from the URL that is in the ID position
		String idString = (String)request.getAttributes().get("projectid");
		return idString;
	}
	
	private Integer extractId(Request request) throws InvalidInputException {
		// Get the Project ID from the URL.
		try {
			String idString = (String)request.getAttributes().get("projectid");
			return Integer.parseInt(idString);
		} catch (NumberFormatException nfe) {
			throw new InvalidInputException("Malformed project ID in REST API call.", nfe);
		}
	}
	
	/**
	 * Standard setter method.
	 */
	public void setProjectMetadataService(
			ProjectMetadataService projectMetadataService) {
		this.projectMetadataService = projectMetadataService;
	}

}
