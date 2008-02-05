package org.ncbo.stanford.manager.impl;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.ncbo.stanford.bean.ConceptBean;
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
    	ConceptBean conceptBean = ocMgr.getRootConcept(TEST_ONT_ID);
  
      	System.out.println("ROOT");
        
    	outputConcept(conceptBean);

    	int i = 0;
    	
    	List children = conceptBean.getChildren();

    	this.assertEquals(children.size(), 2);
    	
    	System.out.println("CHILDREN");
    	for (Iterator it = children.iterator(); it.hasNext(); ) {
    		ConceptBean child = (ConceptBean)it.next();
     		System.out.println("Child "+ (++i));
     		outputConcept(child);
    	}
    	
    }
    
	public void testPizzaConcept() {
      	System.out.println("Starting testGetConcept");
    	OntologyRetrievalManagerWrapperProtegeImpl ocMgr = new OntologyRetrievalManagerWrapperProtegeImpl();
    	ConceptBean conceptBean = ocMgr.findConcept(TEST_CONCEPT_NAME, TEST_ONT_ID);
  
        
    	outputConcept(conceptBean);

    	List children = conceptBean.getChildren();
    	this.assertEquals(children.size(), 11);

    	int i = 0;
    	for (Iterator it = children.iterator(); it.hasNext(); ) {
    		ConceptBean child = (ConceptBean)it.next();
    		System.out.println("Child "+ (++i));
    		outputConcept(child);
    	}
    	
    	System.out.println("Parents of " + TEST_CONCEPT_NAME);

    	i = 0;
    	for (Iterator it = conceptBean.getParents().iterator(); it.hasNext(); ) {
    		ConceptBean parent = (ConceptBean)it.next();
    		System.out.println("Parent "+ (++i));
    		outputConcept(parent);
    	}
    }

	public void testCheeseyVegetablePizzaConcept() {
      	System.out.println("Starting cheesyvegetablepizza concept");
    	OntologyRetrievalManagerWrapperProtegeImpl ocMgr = new OntologyRetrievalManagerWrapperProtegeImpl();
    	ConceptBean conceptBean = ocMgr.findConcept("CheeseyVegetableTopping", TEST_ONT_ID);
  
        
    	outputConcept(conceptBean);

    	List children = conceptBean.getChildren();
    	this.assertEquals(children.size(), 0);

    	int i = 0;
    	for (Iterator it = children.iterator(); it.hasNext(); ) {
    		ConceptBean child = (ConceptBean)it.next();
    		System.out.println("Child "+ (++i));
    		outputConcept(child);
    	}

    	
    	System.out.println("Parents of " + TEST_CONCEPT_NAME);

    	i = 0;
    	List parents = conceptBean.getParents();
    	this.assertEquals(parents.size(), 2);
    	for (Iterator it = parents.iterator(); it.hasNext(); ) {
    		ConceptBean parent = (ConceptBean)it.next();
    		System.out.println("Parent "+ (++i));
    		outputConcept(parent);
    	}
    }

    
    //
    // Private methods
    //
    /**
     * Output concept content for debugging.  Potential helper method to be added to ConceptBean itself or in the 
     * helper package.
     */
    private void outputConcept(ConceptBean conceptBean) {
    	System.out.println("conceptBean: " + conceptBean);
    	System.out.println("name: " + conceptBean.getDisplayLabel());
    	System.out.println("concept id: " + conceptBean.getId());
    	System.out.println("ontology id: " + conceptBean.getOntologyId());
    	System.out.println("display label: " + conceptBean.getDisplayLabel());
    }
    
}
