package org.ncbo.stanford.manager.metakb.protege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.RatingBean;
import org.ncbo.stanford.bean.RatingTypeBean;
import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Basic test for storing ReviewBean objects in the metadata kb.
 *
 * @author Tony Loeser
 */
public class ReviewMetadataManagerImplTest extends AbstractBioPortalTest {

	@Autowired
	ReviewMetadataManagerImpl reviewMan;
	
	@Test
	public void testCrudOperations() throws Exception {
		
		// == CREATE ==
		
		ReviewBean rBean = reviewMan.createObject();
		rBean.setText("Body_1");
		rBean.setUserId(new Integer(12));
		rBean.setOntologyId(new Integer(1000));
		rBean.setProjectId(new Integer(2));
		
		List<RatingBean> ratings = new ArrayList<RatingBean>();
		rBean.setRatings(ratings);
		
		reviewMan.updateObject(rBean);
		Integer id = rBean.getId();
		
		// == UPDATE == 
		
		rBean.setText("Body_2");
		reviewMan.updateObject(rBean);
		
		// == RETRIEVE ==
		
		ReviewBean rBean_2 = reviewMan.retrieveObject(id);
		Assert.assertEquals("Retrieved properties didn't match", rBean_2.getText(), rBean.getText());
		Assert.assertEquals("Retrieved properties didn't match", rBean_2.getUserId(), rBean.getUserId());
		Assert.assertEquals("IDs don't match", rBean_2.getId(), rBean.getId());
		
		// == DELETE ==
		
		reviewMan.deleteObject(id);
		try {
			reviewMan.retrieveObject(id);
			Assert.fail("Attempt to retrieve deleted review should throw exception");
		} catch (MetadataObjectNotFoundException e) {
			// OK
		} catch (Exception e) {
			Assert.fail("Incorrect exception on get of non-existent review: "+e);
		}
	}
	
//	@Test
//	public void cleanUpMetadataKb() throws Exception {
//		// Delete all objects hanging around in the Kb. CAREFUL
//		for (Iterator<ReviewBean> rbIt = reviewMan.getReviewDAO().getAllObjects().iterator(); rbIt.hasNext(); ) {
//			ReviewBean rBean = rbIt.next();
//			reviewMan.getReviewDAO().deleteObject(rBean.getId());
//			System.out.println("Deleted review: "+rBean.getId());
//		}
//		for (Iterator<RatingBean> ratIt = reviewMan.getRatingDAO().getAllObjects().iterator(); ratIt.hasNext(); ) {
//			RatingBean ratBean = ratIt.next();
//			reviewMan.getRatingDAO().deleteObject(ratBean.getId());
//			System.out.println("Deleted rating: "+ratBean.getId());
//		}
//	}
	
	@Test
	public void testGetRatingTypes() throws Exception {
		System.out.println("This should print before the kb loads.");
		Collection<RatingTypeBean> ratingTypes = reviewMan.getAllRatingTypes();
		Assert.assertEquals("Wrong number of rating types", 6, ratingTypes.size());
	}
	
	@Test
	public void testRatings() throws Exception {
		
		ReviewBean reviewBean = reviewMan.createObject();
		Integer reviewId = reviewBean.getId();
		reviewBean.setText("Text");
		reviewBean.setProjectId(new Integer(12));
		reviewMan.updateObject(reviewBean);
		
		RatingTypeBean ratingTypeBean = reviewMan.retrieveRatingType(new Integer(1));
		
		RatingBean ratingBean = reviewMan.createRating(reviewBean);
		Integer ratingId = ratingBean.getId();
		ratingBean.setValue(new Integer(3));
		ratingBean.setType(ratingTypeBean);
		reviewMan.updateRating(ratingBean);
		
		// Retrieve just the rating
		RatingBean ratingBean_2 = reviewMan.retrieveRating(ratingId);
		Assert.assertEquals("Problem with rating type", new Integer(1), ratingBean_2.getType().getId());
		Assert.assertEquals("Problem with rating value", new Integer(3), ratingBean_2.getValue());
		
		// Retrieve the whole review
		ReviewBean reviewBean_3 = reviewMan.retrieveObject(reviewId);
		Collection<RatingBean> ratings_3 = reviewBean_3.getRatings();
		Assert.assertEquals("Problem retrieving ratings in review", 1, ratings_3.size());
		RatingBean ratingBean_3 = ratings_3.iterator().next();
		Assert.assertEquals("Problem with rating type", new Integer(1), ratingBean_3.getType().getId());
		Assert.assertEquals("Problem with rating value", new Integer(3), ratingBean_3.getValue());
		
		// Add another rating
		RatingBean ratingBean_4 = reviewMan.createRating(reviewBean_3);
		Integer ratingId_4 = ratingBean_4.getId();
		RatingTypeBean ratingTypeBean_4 = reviewMan.retrieveRatingType(new Integer(2));
		ratingBean_4.setValue(new Integer(1));
		ratingBean_4.setType(ratingTypeBean_4);
		reviewMan.updateRating(ratingBean_4);
		Assert.assertEquals("Trouble adding rating", 2, reviewBean_3.getRatings().size());
		
		// Retrieve both the ratings
		ReviewBean reviewBean_5 = reviewMan.retrieveObject(reviewId);
		Assert.assertEquals("Wrong number of ratings in retrieved review", 2, reviewBean_5.getRatings().size());
		
		// Delete a rating
		reviewMan.deleteRating(ratingBean.getId());
		ReviewBean reviewBean_6 = reviewMan.retrieveObject(reviewId);
		Assert.assertEquals("Didn't manage to delete a rating?", 1, reviewBean_6.getRatings().size());
		RatingBean ratingBean_6 = reviewBean_6.getRatings().iterator().next();
		Assert.assertEquals("Wrong rating seemed to get deleted", new Integer(1), ratingBean_6.getValue());
		
		// Delete the whole thing
		reviewMan.deleteObject(reviewId);
		try {
			reviewMan.retrieveRating(ratingId_4);
			Assert.fail("Deleting the review should have deleted the ratings");
		} catch (MetadataObjectNotFoundException e) {
			// OK
		} catch (Exception e) {
			Assert.fail("Incorrect exception on get of non-existent rating: "+e);
		}
	}	
}
