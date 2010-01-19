package org.ncbo.stanford.manager.metadata.impl;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.AbstractOntologyMetadataManager;
import org.ncbo.stanford.manager.metadata.OntologyAnnotationManager;
import org.ncbo.stanford.util.metadata.OntologyAnnotationUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * XXX Manager for moving Annotations around.
 *
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 */
public class OntologyAnnotationManagerImpl extends AbstractOntologyMetadataManager
		implements OntologyAnnotationManager {
	
	static String REVIEW_CLASS_NAME = "metadata:Review";
	static String REVIEW_INST_STEM = "http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#Review_";

	@Override
	public List<ReviewBean> findReviewBeansForOntology(OntologyBean ob) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReviewBean getReviewBean(Integer id) throws Exception {
		// Get the metadata ontology
		OWLModel metadataOntology = getMetadataOWLModel(); // throws Exception
		// Figure out the instance name and get it
		String name = convertIdToName(id);
		OWLIndividual reviewInd = metadataOntology.getOWLIndividual(name);
		// Make a bean with corresponding ID
		ReviewBean result = new ReviewBean();
		result.setId(convertNameToId(reviewInd.getName()));
		// Fill in the bean from the instance properties
		OntologyAnnotationUtils.fillInReviewBeanFromInstance(result, reviewInd);
		return result;
	}

	@Override
	public void saveNewReviewBean(ReviewBean rb) throws Exception {
		// Get the metadata ontology
		OWLModel metadataOntology = getMetadataOWLModel(); // throws Exception
		// Get the review class
		OWLNamedClass ontClass = metadataOntology.getOWLNamedClass(REVIEW_CLASS_NAME); // returns null on error!!
		// Create a new instance of review
		OWLIndividual reviewInd = ontClass.createOWLIndividual(null);
		// Peel the ID off of the new instance name
		Integer id = convertNameToId(reviewInd.getName());
		// Update the properties of the review instance (save is implied)
		OntologyAnnotationUtils.fillInReviewPropertiesFromBean(reviewInd, rb); // throws MetadataException
		// Update the ID in the review bean
		rb.setId(id);
	}

	@Override
	public void saveReviewBean(ReviewBean rb) throws Exception {
		OWLModel metadataOntology = getMetadataOWLModel(); // throws Exception
		// Figure out the instance name and get it
		String name = convertIdToName(rb.getId());
		OWLIndividual reviewInd = metadataOntology.getOWLIndividual(name);
		// Update the properties of the review instance (save is implied)
		OntologyAnnotationUtils.fillInReviewPropertiesFromBean(reviewInd, rb);
	}

	// Name handling
	protected Integer convertNameToId(String name) {
		int numStart = name.lastIndexOf('_') + 1;
		return new Integer(name.substring(numStart));
	}
	
	protected String convertIdToName(Integer id) {
		return REVIEW_INST_STEM+id;
	}

	@Override
	public void deleteReview(Integer id) throws Exception {
		OWLModel metadataOntology = getMetadataOWLModel();
		String name = convertIdToName(id);
		OWLIndividual reviewInd = metadataOntology.getOWLIndividual(name);
		reviewInd.delete();
	}
	
}
