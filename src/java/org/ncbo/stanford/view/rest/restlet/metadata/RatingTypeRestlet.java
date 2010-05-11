package org.ncbo.stanford.view.rest.restlet.metadata;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.RatingTypeBean;
import org.ncbo.stanford.manager.metakb.ReviewMetadataManager;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class RatingTypeRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(RatingTypeRestlet.class);
	private ReviewMetadataManager reviewMetadataManager;
	
	
	// =========================================================================
	// GET -- Return a list of all rating types
	
	// Override AbstractBaseRestlet
	public void getRequest(Request request, Response response) {
		Collection<RatingTypeBean> result = null;
		try {
			result = reviewMetadataManager.getAllRatingTypes();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, result);
		}
	}

	
	// =========================================================================
	// Accessors
	
	public void setReviewMetadataManager(ReviewMetadataManager reviewMetadataManager) {
		this.reviewMetadataManager = reviewMetadataManager;
	}
}