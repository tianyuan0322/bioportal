package org.ncbo.stanford.service.metadata;

import java.util.Collection;

import org.ncbo.stanford.bean.RatingBean;
import org.ncbo.stanford.bean.RatingTypeBean;
import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;

/**
 * Thin service layer that forwards all calls to the manager layer.
 * Manager is {@link org.ncbo.stanford.manager.metakb.ReviewMetadataManager}.
 * 
 * @author Tony Loeser
 *
 */
public interface ReviewMetadataService extends BeanCRUDService<ReviewBean> {

	/**
	 * Forward to corresponding manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.ReviewMetadataManager#createRating(ReviewBean)
	 */
	public RatingBean createRating(ReviewBean reviewBean)
		throws MetadataObjectNotFoundException, MetadataException;
	
	/**
	 * Forward to corresponding manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.ReviewMetadataManager#updateRating(RatingBean)
	 */
	public void updateRating(RatingBean ratingBean)
		throws MetadataObjectNotFoundException, MetadataException;
	
	/**
	 * Forward to corresponding manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.ReviewMetadataManager#deleteRating(Integer)
	 */
	public void deleteRating(Integer id)
		throws MetadataObjectNotFoundException;
	
	/**
	 * Forward to corresponding manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.ReviewMetadataManager#getReviewsForOntology(Integer)
	 */
	public Collection<ReviewBean> getReviewsForOntology(Integer id)
		throws MetadataObjectNotFoundException, MetadataException;
	
	/**
	 * Forward to corresponding manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.ReviewMetadataManager#getAllRatingTypes()
	 */
	public Collection<RatingTypeBean> getAllRatingTypes();
	
	/**
	 * Forward to corresponding manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.ReviewMetadataManager#retrieveRatingType(Integer)
	 */
	public RatingTypeBean retrieveRatingType(Integer id)
		throws MetadataObjectNotFoundException;
}
