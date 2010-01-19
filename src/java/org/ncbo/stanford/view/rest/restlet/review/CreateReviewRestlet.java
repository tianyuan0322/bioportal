package org.ncbo.stanford.view.rest.restlet.review;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.service.annotation.OntologyAnnotationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * This is a POST request.
 * 
 * @author tony
 *
 */
public class CreateReviewRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(CreateReviewRestlet.class);
	private OntologyAnnotationService ontologyAnnotationService;

	
	@Override
	public void postRequest(Request request, Response response) {
		createReview(request, response);
	}
	
	private void createReview(Request request, Response response) {
		
		ReviewBean newReview = new ReviewBean();
		
		try {
			// Fill in non-automatic data from the POST parameters
			// Following example in BeanHelper#populateOntologyBeanFromRequest
			HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest(request);
			String body = httpServletRequest.getParameter("body");
			newReview.setBody(body);
			
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

	/**
	 * Standard setter method.
	 */
	public void setOntologyAnnotationService(OntologyAnnotationService oas) {
		this.ontologyAnnotationService = oas;
	}
}
