package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.manager.retrieval.impl.OntologyRetrievalManagerProtegeImpl;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests loading ontologies into Protege using the OntologyLoaderProtegeImpl
 * 
 * @author Benjamin Dai
 */
public class OntologyRetrievalManagerProtegeTest extends AbstractBioPortalTest {
	private final static int TEST_ONT_VERSION_ID = 13578;
	private final static String TEST_PIZZA_DISPLAY_NAME = "Pizza";
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#Pizza";

	@Autowired
	private OntologyRetrievalManagerProtegeImpl ocMgr;

	@Autowired
	private OntologyMetadataManager ontologyMetadataManager;

	@Test
	public void testPathToRoot() throws Exception {
		OntologyBean ontologyBean = getLatestOntologyBean(TEST_PIZZA_DISPLAY_NAME);
		ClassBean conceptBean = ocMgr.findPathFromRoot(ontologyBean, "SpicyPizza",
				false);
		System.out.println("\n\ntestPathToRoot()");
		System.out.println("Path");

		outputConcept(conceptBean);

		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		int count = 0;

		while (subclasses != null) {
			boolean found = false;
			String padding = "";

			for (int x = 0; x <= count; x++) {
				padding = padding + "-";
			}

			for (ClassBean subclass : subclasses) {
				System.out.println(padding + subclass.getLabel() + " : "
						+ subclass.getId());
				if (subclass.getRelations().get(ApplicationConstants.SUB_CLASS) != null) {
					subclasses = (ArrayList<ClassBean>) subclass.getRelations()
							.get(ApplicationConstants.SUB_CLASS);
					found = true;
				}
			}

			if (!found) {
				subclasses = null;
			}

			count += 1;
		}
	}

	@Test
	public void testGetRootNode() throws Exception {
		System.out.println("\n\ntestGetRootNode()");
		OntologyBean ontologyBean = getLatestOntologyBean(TEST_PIZZA_DISPLAY_NAME);
		ClassBean conceptBean = ocMgr.findRootConcept(ontologyBean, false);

		System.out.println("ROOT");

		outputConcept(conceptBean);


	}

	@Test
	public void testPizzaConcept() throws Exception {
		System.out.println("\n\ntestPizzaConcept()");
		System.out.println("Starting testGetConcept");
		ClassBean conceptBean = null; 
		OntologyBean ontologyBean = getLatestOntologyBean(TEST_PIZZA_DISPLAY_NAME);
		conceptBean= ocMgr.findConcept(ontologyBean, TEST_CONCEPT_NAME, false, false, false);

		outputConcept(conceptBean);


	}

	@Test
	public void testCheeseyVegetablePizzaConcept() throws Exception {
		System.out.println("\n\ntestCheeseyVegetablePizzaConcept()");
		System.out.println("Starting cheesyvegetablepizza concept");
		ClassBean classBean = null; 
		OntologyBean ontologyBean = getLatestOntologyBean(TEST_PIZZA_DISPLAY_NAME);
		classBean= ocMgr.findConcept(ontologyBean, "CheeseyVegetableTopping", false, false, false);
		outputConcept(classBean);
	}
	
	@Test
	public void testFindAllClasses() throws Exception {
		System.out.println("\n\ntestFindAllClasses()");
		OntologyBean ontologyBean = getLatestOntologyBean(TEST_PIZZA_DISPLAY_NAME);
		Iterator<ClassBean> clsIt = ocMgr.listAllClasses(ontologyBean);
		
		int numClasses = 0;
		for (; clsIt.hasNext(); ) {
			ClassBean cb = clsIt.next();
			numClasses++;
		}
		System.out.println("Number of classes: "+numClasses);
	}

	//
	// Private methods
	//
	/**
	 * Output concept content for debugging. Potential helper method to be added
	 * to ConceptBean itself or in the helper package.
	 */
	private void outputConcept(ClassBean classBean) {
		System.out.println(classBean);
	}
	
	/**
	 * return the latest NcboOntology that has the display_label provided
	 * 
	 * @param displayLabel
	 * @return
	 * @throws Exception
	 */
	public OntologyBean getLatestOntologyBean(String displayLabel)
			throws Exception {
		List<OntologyBean> list = ontologyMetadataManager
				.findLatestOntologyVersions();
		OntologyBean ob= null;
		for (OntologyBean ncboOntology : list) {
			if (ncboOntology.getDisplayLabel().equalsIgnoreCase(displayLabel)) {
				if (ob== null || ncboOntology.getId() > ob.getId()) {
					ob= ncboOntology;
				}
			}
		}

		return ob;
	}	
	
}
