package org.ncbo.stanford.manager.metakb;

import java.util.Collection;

import org.ncbo.stanford.bean.RatingBean;
import org.ncbo.stanford.bean.RatingTypeBean;
import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;

/**
 * Manager for reviews handles basic review storage, and association with
 * ratings and rating types.
 * 
 * @author Tony Loeser
 */
public interface ReviewMetadataManager extends SimpleObjectManager<ReviewBean> {
	
	// ========== Reviews ==========
	
	/**
	 * Return all reviews associated with a particular ontology.
	 * 
	 * @param id of the ontology that is being reviewed.  At this time, that is
	 *     just an integer -- virtual ontologies and views are coming.
	 * @return all of the review beans
	 * @throws MetadataException when there is a problem creating the java beans
	 *     for the reviews
	 */
	public Collection<ReviewBean> getReviewsForOntology(Integer id)
		throws MetadataException, MetadataObjectNotFoundException;
	
	// ========== Rating Types ==========
	
	/**
	 * Return a java bean for each rating type that can be associated with a rating.
	 */
	public Collection<RatingTypeBean> getAllRatingTypes();
	
	/**
	 * Retrieve the rating type bean for the supplied id.
     * 
	 * @throws MetadataObjectNotFoundException when there is no rating type with the
	 *     supplied id in the persistent store.
	 */
	public RatingTypeBean retrieveRatingType(Integer id)
		throws MetadataObjectNotFoundException;
	
	// ========== Ratings ==========

	/**
	 * Create a new rating, associated with the given review.
	 * 
	 * @param reviewBean the review with which this rating will be (permanently) associated
	 * @return the new, blank rating bean (to be filled in and updated)
	 * @throws MetadataException when there is a problem creating the rating bean
	 * @throws MetadataObjectNotFoundException when the corresponding review is not present
	 *     in the persistent store.
	 */
	public RatingBean createRating(ReviewBean reviewBean)
		throws MetadataException, MetadataObjectNotFoundException;
	
	/**
	 * Retrieve the rating corresponding to a unique id.
	 * 
	 * @throws MetadataObjectNotFoundException when there is no rating with matching id in
	 *     the persistent store.
	 */
	public RatingBean retrieveRating(Integer id)
		throws MetadataObjectNotFoundException;
	
	/**
	 * Delete the rating corresponding to the unique id.
	 * 
	 * @throws MetadataObjectNotFoundException when there is no such rating in the persistent
	 *      store
	 */
	public void deleteRating(Integer ratingId)
		throws MetadataObjectNotFoundException;
	
	/**
	 * Update the values for a rating in the persistent store.  The object in the persistent
	 * store will be retrieved using the id property on the rating bean.  Then the values are
	 * copied from the bean to the stored object.
	 * 
	 * @param ratingBean the rating to update
	 * @throws MetadataException
	 * @throws MetadataObjectNotFoundException when there is no corresponding rating in the store
	 */
	public void updateRating(RatingBean ratingBean)
		throws MetadataException, MetadataObjectNotFoundException;
}
