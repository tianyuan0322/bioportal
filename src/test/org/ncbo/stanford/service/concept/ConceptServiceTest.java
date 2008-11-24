package org.ncbo.stanford.service.concept;

import java.util.ArrayList;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;

public class ConceptServiceTest extends AbstractBioPortalTest {

	private final static int TEST_ONT_ID = 3905;
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "obo_annot:EnumerationClass";

	public void FindRoot() throws Exception {
		ConceptService service = (ConceptService) applicationContext.getBean(
				"conceptService", ConceptService.class);
		ClassBean root = service.findRootConcept(TEST_ONT_ID);
		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) root
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());
		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel()
					+ " || "
					+ subclass.getId()
					+ " || "
					+ subclass.getRelations()
							.get(ApplicationConstants.RDF_TYPE));
		}
	}

/*	public void SearchConcept() throws Exception {
		ConceptService service = (ConceptService) applicationContext.getBean(
				"conceptService", ConceptService.class);

		String query = "Atom";
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(new Integer(TEST_ONT_ID));
		List<SearchResultBean> results = service.findConceptNameContains(ids,
				query);

		for (SearchResultBean result : results) {
			for (ClassBean name : result.getNames()) {
				System.out.println(name.toString("	"));
				System.out.println();
			}
		}
	}
*/
/*	public void testSearchConcept1() throws Exception {
		ConceptService service = (ConceptService) applicationContext.getBean(
				"conceptService", ConceptService.class);

		String query = "Atom";
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(new Integer(TEST_ONT_ID));
		List<SearchResultBean> results = service.findConceptNameStartsWith(ids,
				query);

		for (SearchResultBean result : results) {
			for (ClassBean name : result.getNames()) {
				System.out.println(name.toString("	"));
				System.out.println();
			}

		}
	}
*/
	public void FindConcept() throws Exception {
		ConceptService service = (ConceptService) applicationContext.getBean(
				"conceptService", ConceptService.class);

		ClassBean conceptBean = service.findConcept(TEST_ONT_ID,
				TEST_CONCEPT_NAME);

		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());
		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel()
					+ " || "
					+ subclass.getId()
					+ " || "
					+ subclass.getRelations()
							.get(ApplicationConstants.RDF_TYPE));
		}

		String id = subclasses.get(0).getId();

		service.findConcept(TEST_ONT_ID, id);
	}
}
