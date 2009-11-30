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
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#Pizza";

	@Autowired
	private OntologyRetrievalManagerProtegeImpl ocMgr;

	@Autowired
	private OntologyMetadataManager ontologyMetadataManager;

	@Test
	public void testPathToRoot() throws Exception {
		OntologyBean version = ontologyMetadataManager
				.findOntologyOrViewVersionById(TEST_ONT_VERSION_ID);
		ClassBean conceptBean = ocMgr.findPathFromRoot(version, "SpicyPizza",
				false);

		System.out.println("Path");

		// outputConcept(conceptBean);

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
		OntologyBean version = ontologyMetadataManager
				.findOntologyOrViewVersionById(TEST_ONT_VERSION_ID);
		ClassBean conceptBean = ocMgr.findRootConcept(version, false);

		System.out.println("ROOT");

		// outputConcept(conceptBean);

		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());

		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel() + " " + subclass.getId());
		}
	}

	@Test
	public void testPizzaConcept() throws Exception {
		System.out.println("Starting testGetConcept");
		ClassBean conceptBean = null; // ocMgr.findConcept(TEST_ONT_ID,
		// TEST_CONCEPT_NAME);

		outputConcept(conceptBean);

		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());
		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel() + " " + subclass.getId());
		}
	}

	@Test
	public void testCheeseyVegetablePizzaConcept() throws Exception {
		System.out.println("Starting cheesyvegetablepizza concept");
		ClassBean classBean = null; // ocMgr.findConcept(TEST_ONT_ID,
		// "CheeseyVegetableTopping");

		outputConcept(classBean);
	}
	
	@Test
	public void testFindAllClasses() throws Exception {
		final int ONT_VERSION_ID = 10007;
		OntologyBean oBean = ontologyMetadataManager.findOntologyOrViewVersionById(ONT_VERSION_ID);
		Iterator<ClassBean> clsIt = ocMgr.listAllClasses(oBean);
		
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
}
