package org.ncbo.stanford.service.annotation.impl;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.manager.metadata.OntologyAnnotationManager;
import org.ncbo.stanford.service.annotation.OntologyAnnotationService;

public class OntologyAnnotationServiceImpl implements OntologyAnnotationService {

	
	OntologyAnnotationManager ontologyAnnotationManager;
	
	// ======================================================================
	// Reviews	

	@Override
	public boolean deleteReview(Integer id)  throws Exception {
		ontologyAnnotationManager.deleteReview(id);
		return true;
	}

	@Override
	public ReviewBean getReviewForId(int id)  throws Exception {
		return ontologyAnnotationManager.getReviewBean(id);
	}

	@Override
	public List<ReviewBean> getReviewsForOntology(OntologyBean ob)  throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveReview(ReviewBean rb)  throws Exception {
		if (rb.getId() == null) {
			// This is a new review -- create a fresh OWLIndividual
			ontologyAnnotationManager.saveNewReviewBean(rb);
		} else {
			// This is an existing review -- update an existing OWLIndividual
			ontologyAnnotationManager.saveReviewBean(rb);
		}

	}

	// ======================================================================
	// Projects

	@Override
	public boolean deleteProject(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getProjectForId(int id) {
		// TODO Auto-generated method stub

	}

	// ======================================================================
	// Getters / setters

	public OntologyAnnotationManager getOntologyAnnotationManager() {
		return ontologyAnnotationManager;
	}

	public void setOntologyAnnotationManager(
			OntologyAnnotationManager ontologyAnnotationManager) {
		this.ontologyAnnotationManager = ontologyAnnotationManager;
	}

}
