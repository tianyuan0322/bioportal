package org.ncbo.stanford.manager.impl;

import org.ncbo.stanford.manager.OntologyLoader;
import junit.framework.TestCase;
import edu.stanford.smi.protegex.owl.database.CreateOWLDatabaseFromFileProjectPlugin;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protege.storage.database.*;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.net.*;
import java.util.ArrayList;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.model.ProtegeCls;

import org.ncbo.stanford.bean.ConceptBean;

/**
 * Tests loading ontologies into Protege using the OntologyLoaderProtegeImpl
 * 
 * @author Benjamin Dai
 */
public class OntologyAndConceptManagerProtegeTest extends TestCase {
	private final static int TEST_ONT_ID = 10000;
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#Pizza";

	public void testGetRootNode() {
    	OntologyAndConceptManagerProtege ocMgr = new OntologyAndConceptManagerProtege();
    	ConceptBean conceptBean = ocMgr.getRootConcept(TEST_ONT_ID);
  
      	System.out.println("ROOT");
        
    	outputConcept(conceptBean);

    	int i = 0;
    	System.out.println("CHILDREN");
    	for (Iterator it = conceptBean.getChildren().iterator(); it.hasNext(); ) {
    		ConceptBean child = (ConceptBean)it.next();
     		System.out.println("Child "+ (++i));
     		outputConcept(child);
    	}
    }
    
	public void testGetConcept() {
      	System.out.println("Starting testGetConcept");
		OntologyAndConceptManagerProtege ocMgr = new OntologyAndConceptManagerProtege();
    	ConceptBean conceptBean = ocMgr.findConcept(TEST_CONCEPT_NAME, TEST_ONT_ID);
  
        
    	outputConcept(conceptBean);

    	int i = 0;
    	for (Iterator it = conceptBean.getChildren().iterator(); it.hasNext(); ) {
    		ConceptBean child = (ConceptBean)it.next();
    		System.out.println("Child "+ (++i));
    		outputConcept(child);
    	}
    	
    	System.out.println("Parent of " + TEST_CONCEPT_NAME);
    	ConceptBean parent = conceptBean.getParent();
    	outputConcept(parent);

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
