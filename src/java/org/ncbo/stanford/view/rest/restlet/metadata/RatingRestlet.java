package org.ncbo.stanford.view.rest.restlet.metadata;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.metadata.RatingBean;
import org.ncbo.stanford.bean.metadata.RatingTypeBean;
import org.ncbo.stanford.bean.metadata.ReviewBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.service.metadata.ReviewMetadataService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 * REST API access to Rating metadata objects.
 * <p>
 * There are two actions:
 * <ul>
 *   <li>POST: Add a rating for a particular review.</li>
 *   <li>DELETE: Delete a rating, determined by uid parameter.</li>
 * </ul>
 * 
 * @author Tony Loeser
 */
public class RatingRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(RatingRestlet.class);
	private ReviewMetadataService reviewMetadataService;

	// =========================================================================
	// POST -- add a rating for a particular review

	// Override AbstractBaseRestlet
	public void postRequest(Request request, Response response) {
		RatingBean ratingBean = new RatingBean();
		try {
			// Check that the "id" in the URL path says "new"
			String idString = (String)request.getAttributes().get("id");
			if (!(idString.equalsIgnoreCase("new"))) {
				throw new InvalidInputException("Expected id=\"new\" for POST create-rating request");
			}

			// Get the review so we can add the rating to it
			HttpServletRequest httpRequest = RequestUtils.getHttpServletRequest(request);
			String reviewIdString = (String)httpRequest.getParameter(RequestParamConstants.PARAM_META_REV_ID);
			if (reviewIdString == null || reviewIdString == "") {
				throw new InvalidInputException("Missing reviewId parameter");
			}
			Integer reviewId = new Integer(reviewIdString);
			ReviewBean reviewBean = reviewMetadataService.retrieveObject(reviewId);
			
			// Populate the rating bean
			String ratingTypeIdString = (String)httpRequest.getParameter(RequestParamConstants.PARAM_META_RATE_TYPE_ID);
			if (ratingTypeIdString == null || reviewIdString == "") {
				throw new InvalidInputException("Missing ratingTypeId parameter");
			}
			Integer ratingTypeId = new Integer(ratingTypeIdString);
			RatingTypeBean ratingTypeBean = reviewMetadataService.retrieveRatingType(ratingTypeId);
			ratingBean.setType(ratingTypeBean);
			String valueString = (String)httpRequest.getParameter(RequestParamConstants.PARAM_META_VALUE);
			if (valueString == null || valueString == "") {
				throw new InvalidInputException("Missing value parameter");
			}
			Integer value = new Integer(valueString);
			ratingBean.setValue(value);
			
			reviewMetadataService.addRating(reviewBean, ratingBean);
		} catch (InvalidInputException iie) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
							   "Bad parameter input format: "+iie.getMessage());
			iie.printStackTrace();
			log.error(iie);
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Review not found"+monfe.getMessage());
			monfe.printStackTrace();
			log.error(monfe);
		} catch (MetadataException me) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, "Problem creating rating"+me.getMessage());
			me.printStackTrace();
			log.error(me);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, ratingBean);
		}
	}
	
	
	// =========================================================================
	// DELETE -- remove a rating
	
	// Override AbstractBaseRestlet
	public void deleteRequest(Request request, Response response) {
		try {
			// Get the id from the URL path
			String idString = (String)request.getAttributes().get(RequestParamConstants.PARAM_META_ID);
			Integer ratingId = new Integer(idString);
			
			reviewMetadataService.deleteRating(ratingId);
		} catch (NullPointerException npe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Missing id value ");
			npe.printStackTrace();
			log.error(npe);
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad integer format in id: "+nfe.getMessage());
			nfe.printStackTrace();
			log.error(nfe);
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, monfe.getMessage());
			monfe.printStackTrace();
			log.error(monfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateStatusXMLResponse(request, response);
		}
	}
	
	
	// =========================================================================
	// Accessors
	
	// Override AbstractBaseRestlet
	public void setXmlSerializationService(XMLSerializationService xmlSerializationService) {
		xmlSerializationService.alias("ratingBean", RatingBean.class);
		xmlSerializationService.alias("ratingTypeBean", RatingTypeBean.class);
		this.xmlSerializationService = xmlSerializationService;
	}
	
	public void setReviewMetadataService(ReviewMetadataService reviewMetadataService) {
		this.reviewMetadataService = reviewMetadataService;
	}
}