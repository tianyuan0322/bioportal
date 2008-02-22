package org.ncbo.stanford.manager.impl;

import junit.framework.TestCase;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.manager.wrapper.impl.OntologyRetrievalManagerWrapperProtegeImpl;

/**
 * Tests loading ontologies into Protege using the OntologyLoaderProtegeImpl
 * 
 * @author Benjamin Dai
 */
public class OntologyRetrievalManagerProtegeTest extends TestCase {
	private final static int TEST_ONT_ID = 10000;
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#Pizza";

	public void testGetRootNode() {
    	OntologyRetrievalManagerWrapperProtegeImpl ocMgr = new OntologyRetrievalManagerWrapperProtegeImpl();
    	ClassBean conceptBean = ocMgr.getRootConcept(TEST_ONT_ID);
  
      	System.out.println("ROOT");
        
    	outputConcept(conceptBean);
    }
    
	public void testPizzaConcept() {
      	System.out.println("Starting testGetConcept");
    	OntologyRetrievalManagerWrapperProtegeImpl ocMgr = new OntologyRetrievalManagerWrapperProtegeImpl();
    	ClassBean conceptBean = ocMgr.findConcept(TEST_CONCEPT_NAME, TEST_ONT_ID);
      
    	outputConcept(conceptBean);
    }

	public void testCheeseyVegetablePizzaConcept() {
      	System.out.println("Starting cheesyvegetablepizza concept");
    	OntologyRetrievalManagerWrapperProtegeImpl ocMgr = new OntologyRetrievalManagerWrapperProtegeImpl();
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
