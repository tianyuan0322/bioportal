package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.PropertyBean;
import org.ncbo.stanford.manager.retrieval.impl.OntologyRetrievalManagerLexGridImpl;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.paginator.impl.Page;
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
	
	ConceptService conceptService;
	/**
	 * Sets person.
	 */
	@Resource(name="conceptService")
	public void setConceptService(ConceptService conceptService) {
	    this.conceptService = conceptService;
	    System.out.println("Autowiring of concept service.....");
	    
	}
	   
	@Test
	public void testFindOBORootConceptCell() throws Exception {
		System.out.println("testFindOBORootConceptCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology, 
				maxNumChildren, false);
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
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				maxNumChildren, true);
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
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				maxNumChildren, false);
		System.out.println("Root concept is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindGenericOwlRootConcept() throws Exception {
		System.out.println("testFindGenericOwlRootConcept()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OWL_PIZZA_DISPLAY_LABEL);
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				maxNumChildren, false);
		System.out.println("Root concept is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindLexGridXMLRootConcept() throws Exception {
		System.out.println("testFindLexGridXMLRootConcept()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.LEXGRID_DISPLAY_LABEL);
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				maxNumChildren, false);
		System.out.println("Root concept is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean != null);
	}

	@Test
	public void testFindUMLSRootConcept() throws Exception {
		System.out.println("testFindUMLSRootConcept()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.UMLS_DISPLAY_LABEL);
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findRootConcept(ncboOntology,
				maxNumChildren, false);
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
		List<PropertyBean> properties = retrievalManager.findProperties(ncboOntology);
		System.out.println("Properties for cell ontology are :");
		for (PropertyBean prop : properties) {
			System.out.println(prop);
		}
		System.out.println("\n");
		assertTrue(properties != null);
	}

	@Test
	public void testMTHCH_FindConcept() throws Exception {
		System.out.println("testMTHCH_FindConcept()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(
						OntologyLoaderLexGridImplTest.UMLS_MTHCH_DISPLAY_LABEL);
		//String conceptID = "CL:0000255";
		String conceptID = "Level 1: 0001T-9999T";
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
		System.out.println("Concept " + conceptID + " of MTHCH ontology is \n"
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}
	
	
	@Test
	public void testOBOFindConceptCell() throws Exception {
		System.out.println("testOBOFindConceptCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		//String conceptID = "CL:0000255";
		//String conceptID = "CL:0000254";
		String conceptID = "CL:0000037";
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
		System.out.println("Concept " + conceptID + " of cell ontology is \n"
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}

	@Test
	public void testOBOFindConceptObsoleteCell() throws Exception {
		System.out.println("testOBOFindConceptObsoleteCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		//String conceptID = "CL:0000255";
		String conceptID = "CL:0000045";
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
		System.out.println("Concept " + conceptID + " of cell ontology is \n"
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}
	
	@Test
	public void testOBOFindConceptWithIntersection() throws Exception {
		System.out.println("testOBOFindConceptWithIntersection()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		//String conceptID = "CL:0000255";
		String conceptID = "CL:0000763";
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
		System.out.println("Concept " + conceptID + " of cell ontology is \n"
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}
	
	@Test
	public void testOBOFindConceptAnonymous() throws Exception {
		System.out.println("testOBOFindConceptAnonymous()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		//String conceptID = "CL:0000255";
		String conceptID = "_Anon1";
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
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
		//String conceptID = "CL:0000255";
		String conceptID = "CL:0000045";
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, true, false, false);
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
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
		System.out.println("Concept " + conceptID + " of cell ontology is \n"
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean == null);
	}

	@Test
	public void testOBOFindCorrectedConceptCell() throws Exception {
		System.out.println("testOBOFindCorrectedConceptCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		String conceptID = "CL_0000255";
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
		String newId = conceptID.replace("_", ":");
		System.out.println("Concept " + conceptID + " of cell ontology is \n"
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(newId));
	}
	
	@Test
	public void testOBOFindFullIdConceptCell() throws Exception {
		System.out.println("testOBOFindCorrectedConceptCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		String conceptID = "http://purl.bioontology.org/ontology/CL/CL_0000548";
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
		String newId = "CL:0000548";
		System.out.println("Concept " + conceptID + " of cell ontology is \n"
				+ classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(newId));
	}
	
	@Test
	public void testOBOFindConceptTwoRelationDictyostelium() throws Exception {
		System.out.println("testOBOFindConceptTwoRelationDictyostelium()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_DICTYOSTELIUM_DISPLAY_LABEL);
		String conceptID = "DDANAT:0000004";
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
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
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
		System.out.println("Concept " + conceptID
				+ " of Dictyostelium ontology(obsolete) is \n" + classBean);
		System.out.println("\n");
		assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
	}

	@Test
	public void testLexGridXMLFindConceptOrphanedHL7() throws Exception {
		System.out.println("testLexGridXMLFindConceptOrphanedHL7()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.LEXGRID_HL7_DISPLAY_LABEL);
		String conceptID = "19944:abcCodes";
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
		System.out.println("Concept " + conceptID
				+ " of HL7 ontology(obsolete) is \n" + classBean);
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
		int maxNumChildren = 1000;
		ClassBean classBean = retrievalManager.findConcept(ncboOntology,
				conceptID, maxNumChildren, false, false, false);
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
	public void testOBOFindPathFromRootIncludingChildrenCorrectedConceptCell()
			throws Exception {
		System.out
				.println("testOBOFindPathFromRootIncludingChildrenCorrectedConceptCell()");

		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		String conceptID = "CL_0000255";
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
	public void testHL7FindPathFromRootForRootConceptIncludingChildren()
			throws Exception {
		System.out
				.println("testHL7FindPathFromRootForRootConceptIncludingChildren()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.LEXGRID_HL7_DISPLAY_LABEL);
		String conceptID = "10128:ER";
		ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology,
				conceptID, false);
		System.out.println("Paths to root for concept " + conceptID
				+ " of HL7 ontology are :");
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

		int pageSize = 100;
		int pageNum = 1;
		int offset = 0;
		boolean hasMoreResults = true;
		while (hasMoreResults) {
			System.out.println("Retrieving results starting at " + pageNum);
			int maxNumChildren = 1000;
			Page<ClassBean> beans = retrievalManager.findAllConcepts(
					ncboOntology, maxNumChildren, pageSize, pageNum);
			if (beans == null || beans.getNumResultsPage() <= pageSize) {
				hasMoreResults = false;

				if (beans != null) {
					System.out.println("Retrieved results till "
							+ (offset + beans.getNumResultsPage()));
				}
			}

			offset += pageSize;
		}

		System.out.println("\n");
		assertTrue(offset != 0);
	}
	
	@Test
	public void testOBOGetAllConceptsWithMaxChildren() throws Exception {
		System.out.println("testOBOGetAllConcepts()");
		
		OntologyBean ncboOntology = retrievalManager
				.getOntologyBeanByDisplayNameAndOntologyId(
						OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
						OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);

		int pageSize = 10;
		int pageNum = 1;
		int offset = 0;
		int maxChildren = 3;
		boolean hasMoreResults = true;
		while (hasMoreResults) {
			System.out.println("Retrieving results starting at " + pageNum);
			Page<ClassBean> bean_page = conceptService.findAllConcepts(ncboOntology.getId(),
					maxChildren, pageSize, pageNum);
			if (bean_page == null || bean_page.getNumResultsPage() <= pageSize) {
				hasMoreResults = false;

				if (bean_page != null) {
					Iterator<ClassBean> iter= bean_page.getContents().iterator();
					while (iter.hasNext()) {
						ClassBean bean= iter.next();
						System.out.println(bean);
						
					}
					System.out.println("Retrieved results till "
							+ (offset + bean_page.getNumResultsPage()));
				}
			}

			offset += pageSize;
		}

		System.out.println("\n");
		assertTrue(offset != 0);
	}	

	@Test
	public void testNonHierarchicalUMLSGetAllConcepts() throws Exception {
		System.out.println("testNonHierarchicalUMLSGetAllConcepts()");

		OntologyBean ncboOntology = retrievalManager
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.UMLS_NOHIERACHY_DISPLAY_LABEL);

		int pageSize = 3000;
		int pageNum = 1;
		int offset = 0;
		boolean hasMoreResults = true;
		while (hasMoreResults) {
			System.out.println("Retrieving results starting at page " + pageNum);
			int maxNumChildren = 1000;
			Page<ClassBean> beans = retrievalManager.findAllConcepts(
					ncboOntology, maxNumChildren, pageSize, pageNum);
			if (beans == null || beans.getNumResultsPage() < pageSize) {
				hasMoreResults = false;
			}
			if (beans != null) {
				System.out.println("Retrieved results till "
						+ ((pageNum-1)*pageSize + beans.getNumResultsPage()));
			}
			pageNum++;
			
		}

		System.out.println("\n");
		assertTrue(pageNum > 1);
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
