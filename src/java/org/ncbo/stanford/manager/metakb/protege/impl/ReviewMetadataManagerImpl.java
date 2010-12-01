package org.ncbo.stanford.manager.metakb.protege.impl;

import java.util.Collection;
import java.util.Iterator;

import org.ncbo.stanford.bean.metadata.RatingBean;
import org.ncbo.stanford.bean.metadata.RatingTypeBean;
import org.ncbo.stanford.bean.metadata.ReviewBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.ReviewMetadataManager;

/**
 * Implementation of {@link ReviewMetadataManager} for Protege OWL metadata KB.
 * 
 * @author Tony Loeser
 */
public class ReviewMetadataManagerImpl extends SimpleObjectManagerImpl<ReviewBean>
		implements ReviewMetadataManager {
	
	public ReviewMetadataManagerImpl() {
		super(ReviewBean.class);
	}
	
	// =========================================================================
	// Reviews
	
	// Override from SimpleObjectManagerImpl
	// Rating objects are completely subordinate to Review objects.  So, if we
	// are deleting the review we need to delete the ratings.  Otherwise they are
	// just clutter in the kb with no references to them.
	public void deleteObject(Integer id) throws MetadataObjectNotFoundException {
		ReviewBean rBean = retrieveObject(id);
		for (Iterator<RatingBean> rbIt = rBean.getRatings().iterator(); rbIt.hasNext(); ) {
			getDALayer().deleteObject(RatingBean.class, rbIt.next().getId());
		}
		super.deleteObject(id);
	}

	// Implement interface
	public Collection<ReviewBean> getReviewsForOntology(Integer id)
			throws MetadataObjectNotFoundException, MetadataException {
		String query = "SELECT ?obj " +
					   "WHERE { ?obj <metadata:ontologyId> \""+id+"\"^^xsd:int . " +
					   "        ?obj <rdf:type> <metadata:Review> . }";
		return getDALayer().getDAOForBeanType(ReviewBean.class).getInstancesForSPARQLQuery(query);
	}
	
	
	// =========================================================================
	// Rating Types

	// Implement from ReviewMetadataManager
	public Collection<RatingTypeBean> getAllRatingTypes() {
		return getDALayer().getAllObjects(RatingTypeBean.class);
	}
	
	// Implement from ReviewMetadataManager
	public RatingTypeBean retrieveRatingType(Integer id)
			throws MetadataObjectNotFoundException {
		return getDALayer().retrieveObject(RatingTypeBean.class, id);
	}

	// =========================================================================
	// Ratings
	
	@Override
	public void addRating(ReviewBean reviewBean, RatingBean ratingBean)
			throws MetadataObjectNotFoundException, MetadataException {
		getDALayer().saveObject(ratingBean);
		Collection<RatingBean> ratings = reviewBean.getRatings();
		ratings.add(ratingBean);
		getDALayer().saveObject(reviewBean);
	}

	@Override
	public RatingBean retrieveRating(Integer id)
			throws MetadataObjectNotFoundException {
		return getDALayer().retrieveObject(RatingBean.class, id);
	}

	@Override
	public void deleteRating(Integer ratingId)
			throws MetadataObjectNotFoundException {
		getDALayer().deleteObject(RatingBean.class, ratingId);
	}

	@Override
	public void updateRating(RatingBean ratingBean)
			throws MetadataException, MetadataObjectNotFoundException {
		getDALayer().saveObject(ratingBean);
	}
}
