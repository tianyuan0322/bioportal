/**
 * 
 */
package org.ncbo.stanford.service.annotation;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.ReviewBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tony
 *
 */
public class OntologyAnnotationServiceTest extends AbstractBioPortalTest{
	
	@Autowired
	private OntologyAnnotationService ontologyAnnotationService;
	
	@Test
	public void testBasicInteraction() throws Exception {
		// Create a new Review
		String bodyText = "Here is a test annotation body.";
		ReviewBean rb = new ReviewBean();
		rb.setBody(bodyText);
		ontologyAnnotationService.saveReview(rb);
		Integer id = rb.getId();
		System.out.println("Review saved, with id: "+id);
		
		// Retrieve that Review
		ReviewBean rb2 = ontologyAnnotationService.getReviewForId(id);
		System.out.println("Retrieved review with body: "+rb.getBody());
		
		// Delete the Review
		boolean deleteSuccess = ontologyAnnotationService.deleteReview(id);
		System.out.println("Deleted review? "+deleteSuccess);
	}
}
