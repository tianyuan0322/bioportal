package org.ncbo.stanford.service.concept;

import java.util.ArrayList;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.manager.wrapper.OntologyRetrievalManagerWrapper;
import org.ncbo.stanford.util.constants.ApplicationConstants;

public class ConceptServiceTest extends AbstractBioPortalTest {

	private final static int TEST_ONT_ID = 15817;
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "obo_annot:EnumerationClass";
	
	public void testfindRoot() throws Exception {
		ConceptService service = (ConceptService) applicationContext.getBean(
				"conceptService", ConceptService.class);



		
		
		
		
		ClassBean root = service.findRoot(TEST_ONT_ID);

		
		
		
		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) root
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());
		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel() + " || " + subclass.getId() +" || "+ subclass.getRelations().get(ApplicationConstants.RDF_TYPE));
		}

	

	}
	
	public void FindConcept() throws Exception{
      	
		ConceptService service = (ConceptService) applicationContext.getBean(
				"conceptService", ConceptService.class);
		
		ClassBean conceptBean= service.findConcept(TEST_ONT_ID,TEST_CONCEPT_NAME);
			
      
    	//System.out.println(conceptBean);
    	
    	System.out.println("Subclasses");
    	ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean.getRelations().get(ApplicationConstants.SUB_CLASS);
    	System.out.println("Size:"+subclasses.size());
    	for (ClassBean subclass : subclasses) {
    		System.out.println(subclass.getLabel() + " || " + subclass.getId() +" || "+ subclass.getRelations().get(ApplicationConstants.RDF_TYPE));
    	}
    	
    	String id = subclasses.get(0).getId();
    	
    	service.findConcept(TEST_ONT_ID,id );
    	
    }
	
	

	
}
