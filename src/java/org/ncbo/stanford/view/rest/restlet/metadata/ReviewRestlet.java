
package org.ncbo.stanford.view.rest.restlet.metadata;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.metadata.RatingBean;
import org.ncbo.stanford.bean.metadata.RatingTypeBean;
import org.ncbo.stanford.bean.metadata.ReviewBean;
import org.ncbo.stanford.service.metadata.BeanCRUDService;
import org.ncbo.stanford.service.metadata.ReviewMetadataService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;

/**
 * REST API access to Review metadata objects.
 * <p>
 * Action is determined by type of request.  For list of actions, see
 * {@link AbstractCRUDRestlet}.
 * 
 * @author Tony Loeser
 */
public class ReviewRestlet extends AbstractCRUDRestlet<ReviewBean> {

	private static final Log log = LogFactory.getLog(ReviewRestlet.class);
	private ReviewMetadataService reviewMetadataService;
	
	// =========================================================================
	// Implement helpers from abstract class

	// Implement AbstractCRUDRestlet
	protected BeanCRUDService<ReviewBean> getBeanCRUDService() {
		return reviewMetadataService;
	}

	// Implement AbstractCRUDRestlet
	protected void logError(Exception e) {
		log.error(e);
	}

	// Implement AbstractCRUDRestlet
	protected void populateBeanFromRequest(ReviewBean bean, Request request) {
		HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
		
		String text = httpServletRequest.getParameter(RequestParamConstants.PARAM_META_TEXT);
		bean.setText(text);
		
		String userIdString = httpServletRequest.getParameter(RequestParamConstants.PARAM_META_USER_ID);
		if (userIdString != null && !userIdString.equals("")) {
			Integer userId = new Integer(userIdString);
			bean.setUserId(userId);
		}

		String projectIdString = httpServletRequest.getParameter(RequestParamConstants.PARAM_META_PROJ_ID);
		if (projectIdString != null && !projectIdString.equals("")) {
			Integer projectId = new Integer(projectIdString);
			bean.setProjectId(projectId);
		}

		String ontologyIdString = httpServletRequest.getParameter(RequestParamConstants.PARAM_META_ONT_ID);
		if (ontologyIdString != null && !ontologyIdString.equals("")) {
			Integer ontologyId = new Integer(ontologyIdString);
			bean.setOntologyId(ontologyId);
		}		
	}

	
	// =========================================================================
	// Accessors

	// Override AbstractBaseRestlet
	public void setXmlSerializationService(XMLSerializationService xmlSerializationService) {
		xmlSerializationService.alias("reviewBean", ReviewBean.class);
		xmlSerializationService.alias("ratingBean", RatingBean.class);
		xmlSerializationService.alias("ratingTypeBean", RatingTypeBean.class);
		this.xmlSerializationService = xmlSerializationService;
	}
	
	public void setReviewMetadataService(ReviewMetadataService reviewMetadataService) {
		this.reviewMetadataService = reviewMetadataService;
	}
}
