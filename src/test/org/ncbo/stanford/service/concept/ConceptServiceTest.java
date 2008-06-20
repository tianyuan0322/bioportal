package org.ncbo.stanford.service.concept;

import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;

public class ConceptServiceTest extends AbstractBioPortalTest {

	private final static int TEST_ONT_ID = 34237;
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "obo_annot:EnumerationClass";
	
	public void findRoot() throws Exception {
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
	
	public void testSearchConcept() throws Exception {
		ConceptService service = (ConceptService) applicationContext.getBean(
				"conceptService", ConceptService.class);



		
		String query="pizza";
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(new Integer(TEST_ONT_ID));
		List<SearchResultBean> results = service.findConceptNameContains(ids, query);
		
		
		
		
		
		for (SearchResultBean result : results) {
			System.out.println(result.getNames().get(0).getLabel());
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
