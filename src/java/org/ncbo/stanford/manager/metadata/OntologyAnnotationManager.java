package org.ncbo.stanford.manager.metadata;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.ReviewBean;

/**
 * Organizes access to ontology annotation entities stored in the metadata ontology.
 * <p>
 * <strong>NOTE: These APIs are experimental, and will change in the near future.</strong>
 *
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 */
public interface OntologyAnnotationManager {

	/**
	 * Saves the new review into the metadata ontology, while assigning it
	 * an ID.  The input review bean is modified to add the new ID.
	 * 
	 * @param rb the bean containing the properties of the new review.  This object
	 *           will be modified, to add the ID that is assigned by the metadata ontology.
	 */
	public void saveNewReviewBean(ReviewBean rb) throws Exception;
	
	public void saveReviewBean(ReviewBean rb) throws Exception;
	
	public ReviewBean getReviewBean(Integer id) throws Exception;
	
	public List<ReviewBean> findReviewBeansForOntology(OntologyBean ob);
	
	public void deleteReview(Integer id) throws Exception;
}
