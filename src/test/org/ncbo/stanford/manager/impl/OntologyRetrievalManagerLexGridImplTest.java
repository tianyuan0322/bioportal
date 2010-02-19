package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
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

	@Autowired
	OntologyRetrievalManagerLexGridImpl retrievalManager;

	@Test
	public void testFindOBORootConceptCell() throws Exception {
		System.out.println("testFindOBORootConceptCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				false);
		System.out.println("Root concept is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindOBORootConceptLightCell() throws Exception {
		System.out.println("testFindOBORootConceptLightCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				true);
		System.out.println("Root concept is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindOBORootConceptCellOld() throws Exception {
		System.out.println("testFindOBORootConceptCellOld()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_OLD_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_OLD_ONTOLOGY_ID);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				false);
		System.out.println("Root concept is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindGenericOwlRootConcept() throws Exception {
		System.out.println("testFindGenericOwlRootConcept()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OWL_PIZZA_DISPLAY_LABEL);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				false);
		System.out.println("Root concept is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindLexGridXMLRootConcept() throws Exception {
		System.out.println("testFindLexGridXMLRootConcept()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.LEXGRID_DISPLAY_LABEL);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				false);
		System.out.println("Root concept is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindUMLSRootConcept() throws Exception {
		System.out.println("testFindUMLSRootConcept()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.UMLS_DISPLAY_LABEL);
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				false);
		System.out.println("Root concept is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindOBOProperties() throws Exception {
		System.out.println("testFindOBOProperties()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
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

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		String conceptID = "CL:0000255";
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, false, false, true);
		System.out.println("Concept " + conceptID + " of cell ontology is \n"
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}

	@Test
	public void testOBOFindLightConceptCell() throws Exception {
		System.out.println("testOBOFindConceptCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		String conceptID = "CL:0000255";
		ClassBean classBean = retrievalManager.findConceptLight(ncboOntology,
				conceptID);
		System.out.println("Concept " + conceptID + " of cell ontology is \n"
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}

	@Test
	public void testOBOFindConceptNonExistentCell() throws Exception {
		System.out.println("testOBOFindConceptNonExistentCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		String conceptID = "CL:ABCDXYZ";
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, false, false, true);
		System.out.println("Concept " + conceptID + " of cell ontology is \n"
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean == null);
	}

	@Test
	public void testOBOFindConceptTwoRelationDictyostelium() throws Exception {
		System.out.println("testOBOFindConceptTwoRelationDictyostelium()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_DICTYOSTELIUM_DISPLAY_LABEL);
		String conceptID = "DDANAT:0000004";
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, false, false, true);
		System.out.println("Concept " + conceptID
				+ " of Dictyostelium ontology(2 relations) is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}

	@Test
	public void testOBOFindConceptOrphanedDictyostelium() throws Exception {
		System.out.println("testOBOFindConceptOrphanedDictyostelium()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_DICTYOSTELIUM_DISPLAY_LABEL);
		String conceptID = "DDANAT:0000430";
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, false, false, true);
		System.out.println("Concept " + conceptID
				+ " of Dictyostelium ontology(obsolete) is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}

	@Test
	public void testUMLSFindConceptBothDirectionalNamesPopulated()
			throws Exception {
		System.out
				.println("testUMLSFindConceptBothDirectionalNamesPopulated()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.UMLS_DISPLAY_LABEL);
		String conceptID = "MFART";
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, false, false, true);
		System.out
				.println("Concept "
						+ conceptID
						+ " of UMLS AIR ontology that has both directionalNames polulated \n"
						+ classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}

	@Test
	public void testOBOFindChildrenCell() throws Exception {
		System.out.println("testOBOFindChildrenCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
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

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
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

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
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

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
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

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
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

		OntologyBean ncboOntology1 = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		OntologyBean ncboOntology2 = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
		List<OntologyBean> ontologyVersions = (List<OntologyBean>) Arrays
				.asList(ncboOntology1, ncboOntology2);

		int count = retrievalManager.findConceptCount(ontologyVersions);
		System.out.println("Total number of concepts= " + count);
		System.out.println("\n");
		assertTrue(count > 0);
	}

	@Test
	public void testOBOFindPathFromRootIncludingChildrenCell() throws Exception {
		System.out.println("testOBOFindPathFromRootIncludingChildrenCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		String conceptID = "CL:0000255";
		// String conceptID = "CL:0000003";
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

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
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

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
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

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
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

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_DICTYOSTELIUM_DISPLAY_LABEL);
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

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_DICTYOSTELIUM_DISPLAY_LABEL);
		String conceptID = "DDANAT:0010001";
		ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology,
				conceptID, false);
		System.out.println("Paths to root for concept " + conceptID
				+ " of Dictyostelium ontology are :");
		System.out.println(pathBean);
		System.out.println("\n");
		assertTrue(pathBean != null);
	}

	@Test
	public void testOBOGetAllConcepts() throws Exception {
		System.out.println("testOBOGetAllConcepts()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		int offset = 0;
		int limit = 100;
		boolean hasMoreResults = true;
		while (hasMoreResults) {
			System.out.println("Retrieving results starting at " + offset);
			List<ClassBean> beans = retrievalManager.findAllConcepts(
					ncboOntology, offset, limit);
			if (beans == null || beans.size() != limit) {
				hasMoreResults = false;
				if (beans != null) {
					System.out.println("Retrieved results till "
							+ (offset + beans.size()));
				}
			}
			offset += limit;
		}
		System.out.println("\n");
		assertTrue(offset != 0);
	}

	@Test
	public void testRefresh() throws Exception {
		System.out.println("testRefresh()");
		assertTrue(retrievalManager.refresh());
	}

	@Test
	public void testHasParent() throws Exception {
		System.out.println("testHasParent()");
		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		assertTrue(retrievalManager.hasParent(ncboOntology, "CL:0000255",
				"CL:0000000"));

	}

	@Test
	public void testListAllClasses() throws Exception {
		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		Iterator<ClassBean> cbIt = retrievalManager
				.listAllClasses(ncboOntology);
		int numClasses = 0;
		for (; cbIt.hasNext();) {
			numClasses++;
			ClassBean cb = cbIt.next();
		}
		System.out.println("Found " + numClasses + " classes.");
	}

}
