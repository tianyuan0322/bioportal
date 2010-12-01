package org.ncbo.stanford.service.metadata.impl;

import java.util.Collection;

import org.ncbo.stanford.bean.metadata.RatingBean;
import org.ncbo.stanford.bean.metadata.RatingTypeBean;
import org.ncbo.stanford.bean.metadata.ReviewBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.ReviewMetadataManager;
import org.ncbo.stanford.manager.metakb.SimpleObjectManager;
import org.ncbo.stanford.service.metadata.ReviewMetadataService;

/**
 * Implement {@link ReviewMetadataService}.
 * 
 * @author Tony Loeser
 */
public class ReviewMetadataServiceImpl extends BeanCRUDServiceImpl<ReviewBean>
		implements ReviewMetadataService {
	
	ReviewMetadataManager reviewMetadataManager;

	@Override
	protected SimpleObjectManager<ReviewBean> getObjectManager() {
		return reviewMetadataManager;
	}
	
	@Override
	public ReviewBean newBean() {
		return new ReviewBean();
	}
	
	// =========================================================================
	// Rating types
	
	@Override
	public Collection<RatingTypeBean> getAllRatingTypes() {
		return reviewMetadataManager.getAllRatingTypes();
	}
	
	public RatingTypeBean retrieveRatingType(Integer id)
			throws MetadataObjectNotFoundException {
		return reviewMetadataManager.retrieveRatingType(id);
	}


	// =========================================================================
	// Ratings

	@Override
	public void addRating(ReviewBean reviewBean, RatingBean ratingBean)
			throws MetadataObjectNotFoundException, MetadataException {
		reviewMetadataManager.addRating(reviewBean, ratingBean);
	}
	
	

	@Override
	public void updateRating(RatingBean ratingBean)
			throws MetadataObjectNotFoundException, MetadataException {
		reviewMetadataManager.updateRating(ratingBean);
	}

	@Override
	public void deleteRating(Integer id)
			throws MetadataObjectNotFoundException {
		reviewMetadataManager.deleteRating(id);
	}


	// =========================================================================
	// Reviews

	@Override
	public Collection<ReviewBean> getReviewsForOntology(Integer id) 
			throws MetadataObjectNotFoundException, MetadataException {
		return reviewMetadataManager.getReviewsForOntology(id);
	}

	// =========================================================================
	// Standard getters / setters
	
	public void setReviewMetadataManager(ReviewMetadataManager reviewMetadataManager) {
		this.reviewMetadataManager = reviewMetadataManager;
	}
}
