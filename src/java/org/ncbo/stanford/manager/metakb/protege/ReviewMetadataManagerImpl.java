package org.ncbo.stanford.manager.metakb.protege;

import java.util.Collection;
import java.util.Iterator;

import org.ncbo.stanford.bean.RatingBean;
import org.ncbo.stanford.bean.RatingTypeBean;
import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.ReviewMetadataManager;
import org.ncbo.stanford.manager.metakb.protege.DAO.RatingDAO;
import org.ncbo.stanford.manager.metakb.protege.DAO.RatingTypeDAO;
import org.ncbo.stanford.manager.metakb.protege.DAO.ReviewDAO;

/**
 * Implementation of {@link ReviewMetadataManager} for Protege OWL metadata KB.
 * 
 * @author Tony Loeser
 */
public class ReviewMetadataManagerImpl extends SimpleObjectManagerImpl<ReviewBean>
		implements ReviewMetadataManager {
	
	public ReviewMetadataManagerImpl() {
		super(ReviewDAO.class);
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
			getDAO(RatingDAO.class).deleteObject(rbIt.next().getId());
		}
		super.deleteObject(id);
	}

	// Implement interface
	public Collection<ReviewBean> getReviewsForOntology(Integer id)
			throws MetadataObjectNotFoundException, MetadataException {
		String query = "SELECT ?obj " +
					   "WHERE { ?obj <metadata:ontologyId> \""+id+"\"^^xsd:int . " +
					   "        ?obj <rdf:type> <metadata:Review> . }";
		return getDAO(ReviewDAO.class).getInstancesForSPARQLQuery(query);
	}
	
	
	// =========================================================================
	// Rating Types

	// Implement from ReviewMetadataManager
	public Collection<RatingTypeBean> getAllRatingTypes() {
		return getDAO(RatingTypeDAO.class).getAllObjects();
	}
	
	// Implement from ReviewMetadataManager
	public RatingTypeBean retrieveRatingType(Integer id)
			throws MetadataObjectNotFoundException {
		return getDAO(RatingTypeDAO.class).retreiveObject(id);
	}

	// =========================================================================
	// Ratings
	
	// Implement from ReviewMetadataManager
	public RatingBean createRating(ReviewBean reviewBean)
			throws MetadataException, MetadataObjectNotFoundException {
		// Create the new rating instance
		RatingBean ratingBean = getDAO(RatingDAO.class).createObject();
		// Add it to the review (both the bean and in the kb)
		Collection<RatingBean> ratings = reviewBean.getRatings();
		ratings.add(ratingBean);
		getDAO(ReviewDAO.class).updateObject(reviewBean);

		return ratingBean;
	}

	// Implement from ReviewMetadataManager
	public RatingBean retrieveRating(Integer id) throws MetadataObjectNotFoundException {
		return getDAO(RatingDAO.class).retreiveObject(id);
	}

	// Implement from ReviewMetadataManager
	public void deleteRating(Integer ratingId)
			throws MetadataObjectNotFoundException {
		getDAO(RatingDAO.class).deleteObject(ratingId);
	}

	// Implement from ReviewMetadataManager
	public void updateRating(RatingBean ratingBean)
			throws MetadataException, MetadataObjectNotFoundException {
		getDAO(RatingDAO.class).updateObject(ratingBean);
	}
}
