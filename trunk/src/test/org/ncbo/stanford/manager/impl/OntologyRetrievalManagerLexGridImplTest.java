package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.manager.retrieval.impl.OntologyRetrievalManagerLexGridImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Ensure that the OntologyLoaderLexGridImplTest testcase which is used to load
 * the ontologies has been run before this testcase can be used.
 * 
 * @author Pradip Kanjamala
 */
public class OntologyRetrievalManagerLexGridImplTest extends
		AbstractBioPortalTest {

	// Test ontology display labels
	private final static String TEST_OWL_DISPLAY_LABEL = "pizza.owl";
	private final static String TEST_OBO_CELL_DISPLAY_LABEL = "cell";
	private final static String TEST_OBO_CELL_OLD_DISPLAY_LABEL = "cell_old";
	private final static String TEST_OBO_DICTYOSTELIUM_DISPLAY_LABEL = "DICTYOSTELIUM_ANATOMY";
	private final static String TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL = "INFECTIOUS_DISEASE";
	private final static String TEST_LEXGRID_DISPLAY_LABEL = "Automobiles.xml";
	private final static String TEST_UMLS_DISPLAY_LABEL = "AIR";

	@Autowired
	OntologyRetrievalManagerLexGridImpl retrievalManager;

	@Test
	public void testFindOBORootConceptCell() throws Exception {
		System.out.println("testFindOBORootConceptCell()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
		System.out.println("Root concept is " + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindOBORootConceptCellOld() throws Exception {
		System.out.println("testFindOBORootConceptCellOld()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_OLD_DISPLAY_LABEL);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
		System.out.println("Root concept is " + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindGenericOwlRootConcept() throws Exception {
		System.out.println("testFindGenericOwlRootConcept()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OWL_DISPLAY_LABEL);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
		System.out.println("Root concept is " + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindLexGridXMLRootConcept() throws Exception {
		System.out.println("testFindLexGridXMLRootConcept()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_LEXGRID_DISPLAY_LABEL);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
		System.out.println("Root concept is " + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindUMLSRootConcept() throws Exception {
		System.out.println("testFindUMLSRootConcept()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_UMLS_DISPLAY_LABEL);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
		System.out.println("Root concept is " + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindOBOProperties() throws Exception {
		System.out.println("testFindOBOProperties()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		List<String> properties = retrievalManager.findProperties(ncboOntology);
		System.out.println("Properties for cell ontology are :");
		for (String str : properties) {
			System.out.println(str);
		}
		System.out.println("\n");
		assertTrue(properties != null);
	}

	@Test
	public void testOBOFindConceptCell() throws Exception {
		System.out.println("testOBOFindConceptCell()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		String conceptID = "CL:0000255";
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID);
		System.out.println("Concept " + conceptID + " of cell ontology is "
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}

	@Test
	public void testOBOFindChildrenCell() throws Exception {
		System.out.println("testOBOFindChildrenCell()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		String conceptID = "CL:0000255";
		List<ClassBean> classBeans = retrievalManager.findChildren(
				ncboOntology, conceptID);
		System.out.println("Children of concept " + conceptID
				+ " of cell ontology are :");

		for (ClassBean classBean : classBeans) {
			System.out.println(classBean);
		}

		System.out.println("\n");
		assertTrue(classBeans != null && classBeans.size() > 0);
	}

	@Test
	public void testOBOFindParentCell() throws Exception {
		System.out.println("testOBOFindParentCell()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		String conceptID = "CL:0000255";
		List<ClassBean> classBeans = retrievalManager.findParent(ncboOntology,
				conceptID);
		System.out.println("Parent of concept " + conceptID
				+ " of cell ontology are :");
		for (ClassBean classBean : classBeans) {
			System.out.println(classBean);
		}

		System.out.println("\n");
		assertTrue(classBeans != null && classBeans.size() > 0);
	}

	@Test
	public void testOBOFindPathToRootCell() throws Exception {
		System.out.println("testOBOFindPathToRootCell()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		String conceptID = "CL:0000255";
		ClassBean pathBean = retrievalManager.findPathToRoot(ncboOntology,
				conceptID, true);
		System.out.println("Paths to root for concept " + conceptID
				+ " of cell ontology are :");
		System.out.println(pathBean);
		System.out.println("\n");
		assertTrue(pathBean != null);
	}

	@Test
	public void testOBOFindPathFromRootCell() throws Exception {
		System.out.println("testOBOFindPathFromRootCell()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		String conceptID = "CL:0000003";
		ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology,
				conceptID, true);
		System.out.println("Paths from root to concept " + conceptID
				+ " of cell ontology are :");
		System.out.println(pathBean);
		System.out.println("\n");
		assertTrue(pathBean != null);
	}

	@Test
	public void testOBOFindPathFromRootForRootConceptCell() throws Exception {
		System.out.println("testOBOFindPathFromRootForRootConceptCell()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		String conceptID = "CL:0000000";
		ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology,
				conceptID, true);
		System.out.println("Paths from root to concept " + conceptID
				+ " of cell ontology are :");
		System.out.println(pathBean);
		System.out.println("\n");
		assertTrue(pathBean != null);
	}

	@Test
	public void testConceptCount() throws Exception {
		System.out.println("testConceptCount");

		VNcboOntology ncboOntology1 = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		VNcboOntology ncboOntology2 = retrievalManager
				.getLatestNcboOntology(TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
		List<VNcboOntology> ontologyVersions = (List<VNcboOntology>) Arrays
				.asList(ncboOntology1, ncboOntology2);

		int count = retrievalManager.findConceptCount(ontologyVersions);
		System.out.println("Total number of concepts= " + count);
		System.out.println("\n");
		assertTrue(count > 0);
	}

	@Test
	public void testOBOFindPathFromRootIncludingChildrenCell() throws Exception {
		System.out.println("testOBOFindPathFromRootIncludingChildrenCell()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		String conceptID = "CL:0000255";
		ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology,
				conceptID, false);
		System.out.println("Paths to root for concept " + conceptID
				+ " of cell ontology are :");
		System.out.println(pathBean);
		System.out.println("\n");
		assertTrue(pathBean != null);
	}

	@Test
	public void testOBOFindPathFromRootForRootConceptIncludingChildrenCell()
			throws Exception {
		System.out
				.println("testOBOFindPathFromRootForRootConceptIncludingChildrenCell()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
		String conceptID = "CL:0000000";
		ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology,
				conceptID, false);
		System.out.println("Paths to root for concept " + conceptID
				+ " of cell ontology are :");
		System.out.println(pathBean);
		System.out.println("\n");
		assertTrue(pathBean != null);
	}

	@Test
	public void testOBOFindPathFromRootIncludingChildrenInfectiousDisease()
			throws Exception {
		System.out.println("testOBOFindPathFromRootInfectiousDisease()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
		String conceptID = "GO:0000005";
		ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology,
				conceptID, false);
		System.out.println("Paths from root to concept " + conceptID
				+ " of InfectiousDisease  ontology are :");
		System.out.println(pathBean);
		System.out.println("\n");
		assertTrue(pathBean != null);
	}

	@Test
	public void testOBOFindPathFromRootForRootConceptIncludingChildrenInfectiousDisease()
			throws Exception {
		System.out
				.println("testOBOFindPathFromRootForRootConceptIncludingChildrenInfectiousDisease()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
		String conceptID = "ID:0000003";
		ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology,
				conceptID, false);
		System.out.println("Paths to root for concept " + conceptID
				+ " of InfectiousDisease ontology are :");
		System.out.println(pathBean);
		System.out.println("\n");
		assertTrue(pathBean != null);
	}

	@Test
	public void testOBOFindPathFromRootIncludingChildrenDictyostelium()
			throws Exception {
		System.out.println("testOBOFindPathFromRootDictyostelium()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_DICTYOSTELIUM_DISPLAY_LABEL);
		// String conceptID = "DDANAT:0000037";
		// String conceptID = "DDANAT:0010081";
		String conceptID = "DDANAT:0000097";
		ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology,
				conceptID, false);
		System.out.println("Paths from root to concept " + conceptID
				+ " of Dictyostelium  ontology are :");
		System.out.println(pathBean);
		System.out.println("\n");
		assertTrue(pathBean != null);
	}

	@Test
	public void testOBOFindPathFromRootForRootConceptIncludingChildrenDictyostelium()
			throws Exception {
		System.out
				.println("testOBOFindPathFromRootForRootConceptIncludingChildrenDictyostelium()");

		VNcboOntology ncboOntology = retrievalManager
				.getLatestNcboOntology(TEST_OBO_DICTYOSTELIUM_DISPLAY_LABEL);
		String conceptID = "DDANAT:0010001";
		ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology,
				conceptID, false);
		System.out.println("Paths to root for concept " + conceptID
				+ " of Dictyostelium ontology are :");
		System.out.println(pathBean);
		System.out.println("\n");
		assertTrue(pathBean != null);
	}
}
