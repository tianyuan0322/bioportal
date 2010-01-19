package org.ncbo.stanford.view.rest.restlet.review;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.service.annotation.OntologyAnnotationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * Action is determined by type of request.
 * <ul>
 * <li>DELETE: Delete the review.</li>
 * <li>PUT: Update the review with specified values.</li>
 * </ul>
 * 
 * @author tony
 *
 */
public class ReviewRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(ReviewRestlet.class);
	private OntologyAnnotationService ontologyAnnotationService;
	
	// ============================================================
	// POST: Create a fresh review.

	@Override
	public void postRequest(Request request, Response response) {
		createReview(request, response);
	}
	
	private void createReview(Request request, Response response) {
		ReviewBean newReview = new ReviewBean();
		
		try {
			// Verify that the "id" in the URL says "new"
			String idString = extractIdString(request);
			if (!(idString.equalsIgnoreCase("new"))) {
				throw new IllegalArgumentException("Expected id=\"new\" for POST create-review request");
			}
			
			// Fill in non-automatic data from the POST parameters
			populateReviewBeanFromRequest(newReview, request);
			
			// Add the Review bean to the metadata ontology
			// Side affect: newReview will be updated with id, etc.
			ontologyAnnotationService.saveReview(newReview);
			
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, newReview);
		}
	}
	
	// ============================================================
	// PUT: Update the review.
	
	@Override
	public void putRequest(Request request, Response response) {
		updateReview(request, response);
	}
	
	private void updateReview(Request request, Response response) {
		try {
			Integer id = extractId(request);
			ReviewBean rb = ontologyAnnotationService.getReviewForId(id);
			
			// Update the values in the bean
			HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
			String body = httpServletRequest.getParameter("body");
			rb.setBody(body);
			
			// Move the values from the bean to the ontology
			ontologyAnnotationService.saveReview(rb);
			
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
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
	
	// ============================================================
	// DELETE: Delete the review.
	
	@Override
	public void deleteRequest(Request request, Response response) {
		deleteReview(request, response);
	}
	
	private void deleteReview(Request request, Response response) {
		try {
			Integer id = extractId(request);
			
			// Delete the review
			ontologyAnnotationService.deleteReview(id);
			response.setStatus(Status.SUCCESS_OK);

		} catch (InvalidInputException iie) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, iie.getMessage());
			iie.printStackTrace();
			log.error(iie);
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
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
	
	// ============================================================
	// GET: Retrieve and return the review.
	
	@Override
	public void getRequest(Request request, Response response) {
		retrieveReview(request, response);
	}
	
	private void retrieveReview(Request request, Response response) {
		ReviewBean rb = null;
		try {
			Integer id = extractId(request);
			rb = ontologyAnnotationService.getReviewForId(id.intValue());
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			monfe.printStackTrace();
			log.error(monfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, rb);
		}
	}
	
	// ============================================================
	// Helpers

	private void populateReviewBeanFromRequest(ReviewBean reviewBean, Request request) throws Exception {
		HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
		
		String body = httpServletRequest.getParameter("body");
		reviewBean.setBody(body);
	}
	
	private String extractIdString(Request request) {
		// Get the string from the URL that is in the ID position
		String idString = (String)request.getAttributes().get("reviewid");
		return idString;
	}
	
	private Integer extractId(Request request) throws InvalidInputException {
		// Get the Review ID from the URL.
		try {
			String idString = (String)request.getAttributes().get("reviewid");
			return Integer.parseInt(idString);
		} catch (NumberFormatException nfe) {
			throw new InvalidInputException("Malformed reivew ID in REST API call.", nfe);
		}
	}

	/**
	 * Standard setter method.
	 */
	public void setOntologyAnnotationService(OntologyAnnotationService oas) {
		this.ontologyAnnotationService = oas;
	}
}
