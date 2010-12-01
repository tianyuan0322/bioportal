package org.ncbo.stanford.view.rest.restlet.metadata;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.metadata.RatingBean;
import org.ncbo.stanford.bean.metadata.RatingTypeBean;
import org.ncbo.stanford.bean.metadata.ReviewBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.metadata.ReviewMetadataService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * Provides REST API for retrieval of Request metadata objects.  A GET request
 * retrieves the list, depending on parameters.  (So far, just one parameter.)
 * <ul>
 *   <li>For ontology id parameter, retrieve all ratings for that ontology.</li>
 * </ul>
 * 
 * @author Tony Loeser
 */
public class ReviewListRestlet extends AbstractBaseRestlet {
	
	private static final Log log = LogFactory.getLog(ReviewListRestlet.class);
	private ReviewMetadataService reviewMetadataService;
	
	// =========================================================================
	// Handle GET
	
	// Override AbstractBaseRestlet
	public void getRequest(Request request, Response response) {
		// Perform various queries depending on the parameters
		// So far, all we can do is get all reviews for a particular ontology ID.
		
		// Look for the ontology id
		HttpServletRequest httpRequest = RequestUtils.getHttpServletRequest(request);
		String ontologyIdString = (String)httpRequest.getParameter(RequestParamConstants.PARAM_META_ONT_ID);
		Collection<ReviewBean> result = null;
		try {
			if (ontologyIdString == null) {
				// No parameters.  For now, that is an error.
				throw new InvalidInputException("No ontology ID found in Review list request");
			} else {
				// Got an ontology id.
				Integer ontologyId = new Integer(ontologyIdString);
				result = reviewMetadataService.getReviewsForOntology(ontologyId);
			}
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad ontology id: "+nfe.getMessage());
			nfe.printStackTrace();
			log.error(nfe);
		} catch (InvalidInputException iie) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, iie.getMessage());
			iie.printStackTrace();
			log.error(iie);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
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
		xmlSerializationService.alias("reviewBean", ReviewBean.class);
		xmlSerializationService.alias("ratingBean", RatingBean.class);
		xmlSerializationService.alias("ratingTypeBean", RatingTypeBean.class);
		this.xmlSerializationService = xmlSerializationService;
	}
	
	public void setReviewMetadataService(ReviewMetadataService reviewMetadataService) {
		this.reviewMetadataService = reviewMetadataService;
	}
}
