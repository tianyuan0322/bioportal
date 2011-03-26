package org.ncbo.stanford.view.rest.restlet.metadata;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.metadata.RatingTypeBean;
import org.ncbo.stanford.service.metadata.ReviewMetadataService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 * Supports retrieval of the (statically defined) rating type objects.  A GET request
 * retrieves the list of objects.
 * 
 * @author Tony Loeser
 */
public class RatingTypeRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(RatingTypeRestlet.class);
	private ReviewMetadataService reviewMetadataService;
	
	
	// =========================================================================
	// GET -- Return a list of all rating types
	
	// Override AbstractBaseRestlet
	public void getRequest(Request request, Response response) {
		Collection<RatingTypeBean> result = null;
		try {
			result = reviewMetadataService.getAllRatingTypes();
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
	
	// Override AbstractBaseRestlet
	public void setXmlSerializationService(XMLSerializationService xmlSerializationService) {
		xmlSerializationService.alias("ratingTypeBean", RatingTypeBean.class);
		this.xmlSerializationService = xmlSerializationService;
	}
	
	public void setReviewMetadataService(ReviewMetadataService reviewMetadataService) {
		this.reviewMetadataService = reviewMetadataService;
	}
}