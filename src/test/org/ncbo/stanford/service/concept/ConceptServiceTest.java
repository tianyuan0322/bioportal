package org.ncbo.stanford.service.concept;

import java.util.ArrayList;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class ConceptServiceTest  extends AbstractDependencyInjectionSpringContextTests{

	protected String[] getConfigLocations() {
		return new String[] {"classpath:applicationContext-datasources.xml",
			"classpath:applicationContext-services.xml",
			"classpath:applicationContext-rest.xml",
			"classpath:applicationContext-security.xml"};
	}
	
	 public void testfindRoot(){
			
			ConceptService service = (ConceptService) applicationContext.getBean("conceptService", ConceptService.class);
			ClassBean root = service.findRoot(2854);
		 
			System.out.println("Subclasses");
	    	ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) root.getRelations().get(ApplicationConstants.SUB_CLASS);
	    	System.out.println("Size:"+subclasses.size());
	    	for (ClassBean subclass : subclasses) {
	    		System.out.println(subclass.getLabel()+ " "+subclass.getId());
	    	}
		 
	 }
	
}


