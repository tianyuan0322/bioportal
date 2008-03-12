package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.manager.wrapper.OntologyRetrievalManagerWrapper;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * Tests loading ontologies into Protege using the OntologyLoaderProtegeImpl
 * 
 * @author Benjamin Dai
 */
public class OntologyRetrievalManagerProtegeTest extends AbstractBioPortalTest {
	private final static int TEST_ONT_ID = 10000;
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#Pizza";

	public void testGetRootNode() throws Exception {
		OntologyRetrievalManagerWrapper ocMgr = (OntologyRetrievalManagerWrapper) applicationContext
				.getBean("ontologyRetrievalManagerWrapperProtege",
						OntologyRetrievalManagerWrapper.class);

		ClassBean conceptBean = ocMgr.findRootConcept(TEST_ONT_ID).get(0);

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

	public void PizzaConcept() throws Exception {
		System.out.println("Starting testGetConcept");
		OntologyRetrievalManagerWrapper ocMgr = (OntologyRetrievalManagerWrapper) applicationContext
				.getBean("ontologyRetrievalManagerWrapperProtege",
						OntologyRetrievalManagerWrapper.class);
		ClassBean conceptBean = ocMgr.findConcept(TEST_ONT_ID, TEST_CONCEPT_NAME);

		outputConcept(conceptBean);

		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());
		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel() + " " + subclass.getId());
		}

	}

	public void CheeseyVegetablePizzaConcept() throws Exception {
		System.out.println("Starting cheesyvegetablepizza concept");
		OntologyRetrievalManagerWrapper ocMgr = (OntologyRetrievalManagerWrapper) applicationContext
				.getBean("ontologyRetrievalManagerWrapperProtege",
						OntologyRetrievalManagerWrapper.class);
		ClassBean classBean = ocMgr.findConcept(TEST_ONT_ID, "CheeseyVegetableTopping"
				);

		outputConcept(classBean);

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
