package org.ncbo.stanford.view.rest.restlet.metadata;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.manager.metakb.ReviewMetadataManager;
import org.ncbo.stanford.manager.metakb.SimpleObjectManager;
import org.ncbo.stanford.util.RequestUtils;
import org.restlet.data.Request;

public class ReviewRestlet extends AbstractCRUDRestlet<ReviewBean> {

	private static final Log log = LogFactory.getLog(ReviewRestlet.class);
	private ReviewMetadataManager reviewMetadataManager;
	
	// =========================================================================
	// Implement helpers from abstract class

	// Implement AbstractCRUDRestlet
	protected SimpleObjectManager<ReviewBean> getSimpleObjectManager() {
		return reviewMetadataManager;
	}

	// Implement AbstractCRUDRestlet
	protected void logError(Exception e) {
		log.error(e);
	}

	// Implement AbstractCRUDRestlet
	protected void populateBeanFromRequest(ReviewBean bean, Request request) {
		HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
		
		String text = httpServletRequest.getParameter("text");
		bean.setText(text);
		
		String userIdString = httpServletRequest.getParameter("userId");
		if (userIdString != null && !userIdString.equals("")) {
			Integer userId = new Integer(userIdString);
			bean.setUserId(userId);
		}

		String projectIdString = httpServletRequest.getParameter("projectId");
		if (projectIdString != null && !projectIdString.equals("")) {
			Integer projectId = new Integer(projectIdString);
			bean.setProjectId(projectId);
		}

		String ontologyIdString = httpServletRequest.getParameter("ontologyId");
		if (ontologyIdString != null && !ontologyIdString.equals("")) {
			Integer ontologyId = new Integer(ontologyIdString);
			bean.setOntologyId(ontologyId);
		}		
	}

	
	// =========================================================================
	// Accessors

	public void setReviewMetadataManager(ReviewMetadataManager reviewMetadataManager) {
		this.reviewMetadataManager = reviewMetadataManager;
	}
}
