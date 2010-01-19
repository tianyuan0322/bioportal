package org.ncbo.stanford.service.annotation;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.ReviewBean;

/**
 * @author tony
 *
 */
public interface OntologyAnnotationService {
	
	// ==================================================
	// Projects
	
	public boolean deleteProject(Integer id);
	
	public void getProjectForId(int id);
	
	
	// ==================================================
	// Reviews
	
	/**
	 * Return <code>true</code> iff there was a corresponding review to delete.
	 * 
	 * @param rb
	 */
	public boolean deleteReview(Integer id) throws Exception;
	
	/**
	 * @param id
	 */
	public ReviewBean getReviewForId(int id) throws Exception;
	
	/**
	 * @param ob
	 * @return
	 */
	public List<ReviewBean> getReviewsForOntology(OntologyBean ob) throws Exception;
	
	/**
	 * If id is Null then save it as a new review; otherwise save in object
	 * indicated by id.
	 * 
	 * @param rb
	 */
	public void saveReview(ReviewBean rb) throws Exception;
}