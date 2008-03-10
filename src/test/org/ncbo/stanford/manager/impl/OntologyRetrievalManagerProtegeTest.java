package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.hsqldb.lib.Collection;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.manager.wrapper.OntologyRetrievalManagerWrapper;
import org.ncbo.stanford.manager.wrapper.impl.OntologyRetrievalManagerWrapperProtegeImpl;
import org.ncbo.stanford.service.loader.processor.OntologyLoadProcessorService;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import edu.stanford.smi.protege.model.Cls;

/**
 * Tests loading ontologies into Protege using the OntologyLoaderProtegeImpl
 * 
 * @author Benjamin Dai
 */
public class OntologyRetrievalManagerProtegeTest extends AbstractDependencyInjectionSpringContextTests {
	private final static int TEST_ONT_ID = 10000;
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#Pizza";
	
	

		protected String[] getConfigLocations() {
			return new String[] { "classpath:applicationContext-datasources.xml",
					"classpath:applicationContext-services.xml",
					"classpath:applicationContext-rest.xml",
					"classpath:applicationContext-security.xml" };
		}
	
	

	public void testGetRootNode() {
		OntologyRetrievalManagerWrapper ocMgr = (OntologyRetrievalManagerWrapper) applicationContext.getBean("ontologyRetrievalManagerWrapperProtege",
				OntologyRetrievalManagerWrapper.class);
    	
    	ClassBean conceptBean = ocMgr.findRootConcept(TEST_ONT_ID);
  
      	System.out.println("ROOT");
        
    	//outputConcept(conceptBean);
    	
    	System.out.println("Subclasses");
    	ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean.getRelations().get(ApplicationConstants.SUB_CLASS);
    	System.out.println("Size:"+subclasses.size());
    	for (ClassBean subclass : subclasses) {
    		System.out.println(subclass.getLabel()+ " "+subclass.getId());
    	}
    	
    	
    	
    }
    
	public void PizzaConcept() {
      	System.out.println("Starting testGetConcept");
    	OntologyRetrievalManagerWrapper ocMgr = (OntologyRetrievalManagerWrapper) applicationContext.getBean("ontologyRetrievalManagerWrapperProtege",
				OntologyRetrievalManagerWrapper.class);
    	ClassBean conceptBean = ocMgr.findConcept(TEST_CONCEPT_NAME, TEST_ONT_ID);
      
    	outputConcept(conceptBean);
    	
    	System.out.println("Subclasses");
    	ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean.getRelations().get(ApplicationConstants.SUB_CLASS);
    	System.out.println("Size:"+subclasses.size());
    	for (ClassBean subclass : subclasses) {
    		System.out.println(subclass.getLabel()+ " "+subclass.getId());
    	}
    	
    	
    }

	public void CheeseyVegetablePizzaConcept() {
      	System.out.println("Starting cheesyvegetablepizza concept");
    	OntologyRetrievalManagerWrapper ocMgr = (OntologyRetrievalManagerWrapper) applicationContext.getBean("ontologyRetrievalManagerWrapperProtege",
				OntologyRetrievalManagerWrapper.class);
    	ClassBean classBean = ocMgr.findConcept("CheeseyVegetableTopping", TEST_ONT_ID);
        
    	outputConcept(classBean);
    	
    	
    	
    	
    }

    
    //
    // Private methods
    //
    /**
     * Output concept content for debugging.  Potential helper method to be added to ConceptBean itself or in the 
     * helper package.
     */
    private void outputConcept(ClassBean classBean) {
    	System.out.println(classBean);
    }
    
}
