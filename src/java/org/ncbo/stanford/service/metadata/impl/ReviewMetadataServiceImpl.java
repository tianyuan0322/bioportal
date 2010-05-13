package org.ncbo.stanford.service.metadata.impl;

import java.util.Collection;

import org.ncbo.stanford.bean.RatingBean;
import org.ncbo.stanford.bean.RatingTypeBean;
import org.ncbo.stanford.bean.ReviewBean;
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

	// Implement BeanCRUDServiceImpl
	protected SimpleObjectManager<ReviewBean> getObjectManager() {
		return reviewMetadataManager;
	}
	
	// =========================================================================
	// Rating types
	
	// Implement ReviewMetadataService
	public Collection<RatingTypeBean> getAllRatingTypes() {
		return reviewMetadataManager.getAllRatingTypes();
	}
	
	public RatingTypeBean retrieveRatingType(Integer id)
			throws MetadataObjectNotFoundException {
		return reviewMetadataManager.retrieveRatingType(id);
	}


	// =========================================================================
	// Ratings

	// Implement ReviewMetadataService
	public RatingBean createRating(ReviewBean reviewBean)
			throws MetadataObjectNotFoundException, MetadataException {
		return reviewMetadataManager.createRating(reviewBean);
	}

	// Implement ReviewMetadataService
	public void updateRating(RatingBean ratingBean)
			throws MetadataObjectNotFoundException, MetadataException {
		reviewMetadataManager.updateRating(ratingBean);
	}

	// Implement ReviewMetadataService
	public void deleteRating(Integer id)
			throws MetadataObjectNotFoundException {
		reviewMetadataManager.deleteRating(id);
	}


	// =========================================================================
	// Reviews

	// Implement ReviewMetadataService
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
