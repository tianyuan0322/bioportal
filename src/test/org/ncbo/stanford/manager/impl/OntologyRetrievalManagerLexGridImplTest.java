package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.wrapper.impl.OntologyRetrievalManagerWrapperLexGridImpl;
import org.ncbo.stanford.service.loader.processor.OntologyLoadProcessorService;

/**
 * Ensure that the OntologyLoaderLexGridImplTest testcase which is used to load
 * the ontologies has been run before this testcase can be used.
 * 
 * @author Pradip Kanjamala
 */
public class OntologyRetrievalManagerLexGridImplTest extends AbstractBioPortalTest {

	// Test ontology URIs
	private final static String TEST_OWL_PATHNAME = "test/sample_data/pizza.owl";
	private final static String TEST_OWL_URN_VERSION = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#|version 1.3";
	private final static String TEST_OBO_PATHNAME = "test/sample_data/cell.obo";
	private final static String TEST_OBO_URN_VERSION = "urn:lsid:bioontology.org:cell|UNASSIGNED";
	private final static String TEST_LEXGRID_XML_PATHNAME = "test/sample_data/Automobiles.xml";
	private final static String TEST_LEXGRID_XML_URN_VERSION = "urn:oid:11.11.0.1|1.0";
	private final static String TEST_UMLS_PATHNAME = "test/sample_data/sampleUMLS-AIR";
	private final static String TEST_UMLS_URN_VERSION = "urn:oid:2.16.840.1.113883.6.110|1993.bvt";

	OntologyRetrievalManagerWrapperLexGridImpl retrievalManagerLexGrid;
	OntologyLoadProcessorService service;

	// public OntologyLoaderLexGridImplTest() {
	// super();
	//
	//		 
	// }

	public void testInitialize() {
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
		.getBean("ontologyRetrievalManagerWrapperLexGrid",
				OntologyRetrievalManagerWrapperLexGridImpl.class);

		System.out.println("Initializing retrievalManagerLexGrid");

	}
	
	public void testFindOBORootConcept() throws Exception {
		System.out.println("testFindOBORootConcept()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);

		NcboOntology ncboOntology = retrievalManagerLexGrid.getLatestNcboOntology(3000);
		ClassBean classBean = retrievalManagerLexGrid.findRootConcept(ncboOntology);
		System.out.println("Root concept is " + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	public void testFindGenericOwlRootConcept() throws Exception {
		System.out.println("testFindGenericOwlRootConcept()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);
		NcboOntology ncboOntology = retrievalManagerLexGrid.getLatestNcboOntology(3001);
		ClassBean classBean = retrievalManagerLexGrid.findRootConcept(ncboOntology);
		System.out.println("Root concept is " + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);

	}

	public void testFindLexGridXMLRootConcept() throws Exception {
		System.out.println("testFindLexGridXMLRootConcept()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);
		NcboOntology ncboOntology = retrievalManagerLexGrid.getLatestNcboOntology(3002);
		ClassBean classBean = retrievalManagerLexGrid.findRootConcept(ncboOntology);
		System.out.println("Root concept is " + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	public void testFindUMLSRootConcept() throws Exception {
		System.out.println("testFindUMLSRootConcept()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);
		NcboOntology ncboOntology = retrievalManagerLexGrid.getLatestNcboOntology(3003);
		ClassBean classBean = retrievalManagerLexGrid.findRootConcept(ncboOntology);
		System.out.println("Root concept is " + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);

	}

	public void testFindOBOProperties() throws Exception {
		System.out.println("testFindOBOProperties()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);

		List<String> properties = retrievalManagerLexGrid.findProperties(3000);
		System.out.println("Properties for cell ontology are :");
		for (String str : properties) {
			System.out.println(str);
		}
		System.out.println("\n");
		assertTrue(properties != null);
	}

	public void testOBOFindConcept() throws Exception {
		System.out.println("testOBOFindConcept()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);

		NcboOntology ncboOntology = retrievalManagerLexGrid.getLatestNcboOntology(3000);
		String conceptID = "CL:0000255";
		ClassBean classBean = retrievalManagerLexGrid.findConcept(ncboOntology, conceptID);
		System.out.println("Concept " + conceptID + " of cell ontology is " + classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}

	public void testOBOFindChildren() throws Exception {
		System.out.println("testOBOFindChildren()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);

		NcboOntology ncboOntology = retrievalManagerLexGrid.getLatestNcboOntology(3000);
		String conceptID = "CL:0000255";
		ArrayList<ClassBean> classBeans = retrievalManagerLexGrid.findChildren(ncboOntology,
				conceptID);
		System.out.println("Children of concept " + conceptID + " of cell ontology are :");
		for (ClassBean classBean : classBeans) {
			System.out.println(classBean);
		}
		System.out.println("\n");
		assertTrue(classBeans != null && classBeans.size() > 0);
	}
	
	public void testOBOFindParent() throws Exception {
		System.out.println("testOBOFindParent()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);

		NcboOntology ncboOntology = retrievalManagerLexGrid.getLatestNcboOntology(3000);
		String conceptID = "CL:0000255";
		ArrayList<ClassBean> classBeans = retrievalManagerLexGrid.findParent(ncboOntology,
				conceptID);
		System.out.println("Parent of concept " + conceptID + " of cell ontology are :");
		for (ClassBean classBean : classBeans) {
			System.out.println(classBean);
		}
		System.out.println("\n");
		assertTrue(classBeans != null && classBeans.size() > 0);
	}	

	public void testOBOFindPathToRoot() throws Exception {
		System.out.println("testOBOFindPathToRoot()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);

		NcboOntology ncboOntology = retrievalManagerLexGrid.getLatestNcboOntology(3000);
		String conceptID = "CL:0000255";
		ArrayList<ClassBean> classBeans = retrievalManagerLexGrid.findPathToRoot(ncboOntology,
				conceptID);
		System.out.println("Paths to root for concept " + conceptID + " of cell ontology are :");
		for (ClassBean classBean : classBeans) {
			System.out.println(classBean);
		}
		System.out.println("\n");
		assertTrue(classBeans != null && classBeans.size() > 0);
	}	
	
	public void testFindConceptNameContains() throws Exception {
		System.out.println("testFindConceptNameContains()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);

		ArrayList<Integer> ontologyIds = (ArrayList<Integer>) Arrays.asList(3000);
		ArrayList<String> query = (ArrayList<String>) Arrays.asList("eukaryotic");
		ArrayList<SearchResultBean> searchResultBeans = retrievalManagerLexGrid
				.findConceptNameContains(ontologyIds, query, false, 100);

		System.out.println("Results of searching for 'eukaryotic' in the cell ontology is :");
		for (SearchResultBean srb : searchResultBeans) {
			List<ClassBean> beans = srb.getNameSearchResult();
			for (ClassBean bean : beans)
				System.out.println(bean);
		}

		System.out.println("\n");
		assertTrue(searchResultBeans.isEmpty() != true);
	}

	public void testFindConceptNameExact() throws Exception {
		System.out.println("testFindConceptNameExact()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);

		ArrayList<Integer> ontologyIds = (ArrayList<Integer>) Arrays.asList(3000);
		ArrayList<String> query = (ArrayList<String>) Arrays.asList("eukaryotic cell");
		ArrayList<SearchResultBean> searchResultBeans = retrievalManagerLexGrid
				.findConceptNameExact(ontologyIds, query, false, 100);

		System.out.println("Results of searching for 'eukaryotic cell' in the cell ontology is :");
		for (SearchResultBean srb : searchResultBeans) {
			List<ClassBean> beans = srb.getNameSearchResult();
			for (ClassBean bean : beans)
				System.out.println(bean);
		}
		System.out.println("\n");
		assertTrue(searchResultBeans.isEmpty() != true);
	}
	
	public void testFindConceptPropertyContains() throws Exception {
		System.out.println("testFindConceptPropertyContains()");
		retrievalManagerLexGrid = (OntologyRetrievalManagerWrapperLexGridImpl) applicationContext
				.getBean("ontologyRetrievalManagerWrapperLexGrid",
						OntologyRetrievalManagerWrapperLexGridImpl.class);

		ArrayList<Integer> ontologyIds = (ArrayList<Integer>) Arrays.asList(3000);
		ArrayList<String> query = (ArrayList<String>) Arrays.asList("beta cell");
		String[] properties= {"synonym"};
		ArrayList<SearchResultBean> searchResultBeans = retrievalManagerLexGrid
				.findConceptPropertyContains(ontologyIds, query, properties, false, 100);

		System.out.println("Results of searching for 'beta cell' in the cell ontology is :");
		for (SearchResultBean srb : searchResultBeans) {
			List<ClassBean> beans = srb.getPropertyValueSearchResult();
			for (ClassBean bean : beans)
				System.out.println(bean);
		}
		System.out.println("\n");
		assertTrue(searchResultBeans.isEmpty() != true);
	}	
	

	
}
