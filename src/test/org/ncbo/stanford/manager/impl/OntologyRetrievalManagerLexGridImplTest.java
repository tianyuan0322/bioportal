package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.service.concept.ConceptService;

/**
 * Ensure that the OntologyLoaderLexGridImplTest testcase which is used to load
 * the ontologies has been run before this testcase can be used.
 * 
 * @author Pradip Kanjamala
 */
public class OntologyRetrievalManagerLexGridImplTest extends AbstractBioPortalTest {

    // Test ontology display labels
    private final static String TEST_OWL_DISPLAY_LABEL = "pizza.owl";
    private final static String TEST_OBO_CELL_DISPLAY_LABEL = "cell";
    private final static String TEST_OBO_CELL_OLD_DISPLAY_LABEL = "cell_old";
    private final static String TEST_OBO_DICTYOSTELIUM_DISPLAY_LABEL = "DICTYOSTELIUM_ANATOMY";
    private final static String TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL = "INFECTIOUS_DISEASE";
    private final static String TEST_LEXGRID_DISPLAY_LABEL = "Automobiles.xml";
    private final static String TEST_UMLS_DISPLAY_LABEL = "AIR";

    OntologyRetrievalManagerLexGridImpl retrievalManager;

    public void testInitialize() {
        retrievalManager = getRetrievalManagerLexGrid();
        System.out.println("Initializing retrievalManagerLexGrid");

    }

    public void testFindOBORootConceptCell() throws Exception {
        System.out.println("testFindOBORootConceptCell()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
        System.out.println("Root concept is " + classBean);
        System.out.println("\n");
        assertTrue(classBean != null);
    }

    public void testFindOBORootConceptCellOld() throws Exception {
        System.out.println("testFindOBORootConceptCellOld()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_OLD_DISPLAY_LABEL);
        ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
        System.out.println("Root concept is " + classBean);
        System.out.println("\n");
        assertTrue(classBean != null);
    }

    public void testFindGenericOwlRootConcept() throws Exception {
        System.out.println("testFindGenericOwlRootConcept()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OWL_DISPLAY_LABEL);
        ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
        System.out.println("Root concept is " + classBean);
        System.out.println("\n");
        assertTrue(classBean != null);
    }

    public void testFindLexGridXMLRootConcept() throws Exception {
        System.out.println("testFindLexGridXMLRootConcept()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_LEXGRID_DISPLAY_LABEL);
        ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
        System.out.println("Root concept is " + classBean);
        System.out.println("\n");
        assertTrue(classBean != null);
    }

    public void estFindUMLSRootConcept() throws Exception {
        System.out.println("testFindUMLSRootConcept()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_UMLS_DISPLAY_LABEL);
        ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
        System.out.println("Root concept is " + classBean);
        System.out.println("\n");
        assertTrue(classBean != null);
    }

    public void testFindOBOProperties() throws Exception {
        System.out.println("testFindOBOProperties()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        List<String> properties = retrievalManager.findProperties(ncboOntology);
        System.out.println("Properties for cell ontology are :");
        for (String str : properties) {
            System.out.println(str);
        }
        System.out.println("\n");
        assertTrue(properties != null);
    }

    public void testOBOFindConceptCell() throws Exception {
        System.out.println("testOBOFindConceptCell()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        ClassBean classBean = retrievalManager.findConcept(ncboOntology, conceptID);
        System.out.println("Concept " + conceptID + " of cell ontology is " + classBean);
        System.out.println("\n");
        assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
    }

    public void testOBOFindChildrenCell() throws Exception {
        System.out.println("testOBOFindChildrenCell()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        List<ClassBean> classBeans = retrievalManager.findChildren(ncboOntology, conceptID);
        System.out.println("Children of concept " + conceptID + " of cell ontology are :");
        for (ClassBean classBean : classBeans) {
            System.out.println(classBean);
        }
        System.out.println("\n");
        assertTrue(classBeans != null && classBeans.size() > 0);
    }

    public void testOBOFindParentCell() throws Exception {
        System.out.println("testOBOFindParentCell()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        List<ClassBean> classBeans = retrievalManager.findParent(ncboOntology, conceptID);
        System.out.println("Parent of concept " + conceptID + " of cell ontology are :");
        for (ClassBean classBean : classBeans) {
            System.out.println(classBean);
        }
        System.out.println("\n");
        assertTrue(classBeans != null && classBeans.size() > 0);
    }

    public void testOBOFindPathToRootCell() throws Exception {
        System.out.println("testOBOFindPathToRootCell()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        ClassBean pathBean = retrievalManager.findPathToRoot(ncboOntology, conceptID, true);
        System.out.println("Paths to root for concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }

    public void testOBOFindPathFromRootCell() throws Exception {
        System.out.println("testOBOFindPathFromRootCell()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        String conceptID = "CL:0000003";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, true);
        System.out.println("Paths from root to concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }

    public void testOBOFindPathFromRootForRootConceptCell() throws Exception {
        System.out.println("testOBOFindPathFromRootForRootConceptCell()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        String conceptID = "CL:0000000";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, true);
        System.out.println("Paths from root to concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }

    public void testConceptCount() throws Exception {
        System.out.println("testConceptCount");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology1 = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        VNcboOntology ncboOntology2 = retrievalManager.getLatestNcboOntology(TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
        List<VNcboOntology> ontologyVersions = (List<VNcboOntology>) Arrays.asList(ncboOntology1, ncboOntology2);
        
        
        int count= retrievalManager.findConceptCount(ontologyVersions);
        System.out.println("Total number of concepts= " + count);
        System.out.println("\n");
        assertTrue(count > 0);
    }
    

    public void testOBOFindPathFromRootIncludingChildrenCell() throws Exception {
        System.out.println("testOBOFindPathFromRootIncludingChildrenCell()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, false);
        System.out.println("Paths to root for concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }

    public void testOBOFindPathFromRootForRootConceptIncludingChildrenCell() throws Exception {
        System.out.println("testOBOFindPathFromRootForRootConceptIncludingChildrenCell()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        String conceptID = "CL:0000000";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, false);
        System.out.println("Paths to root for concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }

    public void testSearchConceptUsingConceptService() throws Exception {
        System.out.println("testSearchConceptUsingConceptService()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        ConceptService service = (ConceptService) applicationContext.getBean(
                "conceptService", ConceptService.class);

        String query = "hematopoietic";
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(new Integer(ncboOntology.getId()));
        List<SearchResultBean> searchResultBeans = service.findConceptNameContains(ids, query);

        System.out.println("Results of searching for '"+query+"' in the cell ontology is :");
        for (SearchResultBean srb : searchResultBeans) {
            List<ClassBean> beans = srb.getNames();
            for (ClassBean bean : beans)
                System.out.println(bean);
        }

        System.out.println("\n");
        assertTrue(searchResultBeans.isEmpty() != true);
    }
    
    public void testFindConceptNameContainsCell() throws Exception {
        System.out.println("testFindConceptNameContainsCell()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        List<VNcboOntology> ontologyVersionIds = (List<VNcboOntology>) Arrays.asList(ncboOntology);
        //String query = "eukaryotic";
        //String query = "colony forming unit hematopoietic";
        String query = "hematopoietic";
        List<SearchResultBean> searchResultBeans = retrievalManager.findConceptNameContains(ontologyVersionIds, query,
                false, 100);

        System.out.println("Results of searching for '"+query+"' in the cell ontology is :");
        for (SearchResultBean srb : searchResultBeans) {
            List<ClassBean> beans = srb.getNames();
            for (ClassBean bean : beans)
                System.out.println(bean);
        }

        System.out.println("\n");
        assertTrue(searchResultBeans.isEmpty() != true);
    }

    public void testFindConceptNameExactCell() throws Exception {
        System.out.println("testFindConceptNameExactCell()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        List<VNcboOntology> ontologyVersionIds = (List<VNcboOntology>) Arrays.asList(ncboOntology);
        String query = "eukaryotic cell";
        List<SearchResultBean> searchResultBeans = retrievalManager.findConceptNameExact(ontologyVersionIds, query,
                false, 100);

        System.out.println("Results of searching for '"+query+"' in the cell ontology is :");
        for (SearchResultBean srb : searchResultBeans) {
            List<ClassBean> beans = srb.getNames();
            for (ClassBean bean : beans)
                System.out.println(bean);
        }
        System.out.println("\n");
        assertTrue(searchResultBeans.isEmpty() != true);
    }

    public void testFindConceptPropertyContainsCell() throws Exception {
        System.out.println("testFindConceptPropertyContainsCell()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_CELL_DISPLAY_LABEL);
        List<VNcboOntology> ontologyVersionIds = (List<VNcboOntology>) Arrays.asList(ncboOntology);
        String query = "beta cell";
        String[] properties = { "synonym" };
        List<SearchResultBean> searchResultBeans = retrievalManager.findConceptPropertyContains(ontologyVersionIds,
                query, false, 100);

        System.out.println("Results of searching for '"+query+"' in the cell ontology is :");
        for (SearchResultBean srb : searchResultBeans) {
            List<ClassBean> beans = srb.getProperties();
            for (ClassBean bean : beans)
                System.out.println(bean);
        }
        System.out.println("\n");
        assertTrue(searchResultBeans.isEmpty() != true);
    }

    public void testOBOFindPathFromRootIncludingChildrenInfectiousDisease() throws Exception {
        System.out.println("testOBOFindPathFromRootInfectiousDisease()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
        String conceptID = "GO:0000005";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, false);
        System.out.println("Paths from root to concept " + conceptID + " of InfectiousDisease  ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }

    public void testOBOFindPathFromRootForRootConceptIncludingChildrenInfectiousDisease() throws Exception {
        System.out.println("testOBOFindPathFromRootForRootConceptIncludingChildrenInfectiousDisease()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
        String conceptID = "ID:0000003";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, false);
        System.out.println("Paths to root for concept " + conceptID + " of InfectiousDisease ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }
    
    public void testOBOFindPathFromRootIncludingChildrenDictyostelium() throws Exception {
        System.out.println("testOBOFindPathFromRootDictyostelium()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DICTYOSTELIUM_DISPLAY_LABEL);
        // String conceptID = "DDANAT:0000037";
        // String conceptID = "DDANAT:0010081";
        String conceptID = "DDANAT:0000097";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, false);
        System.out.println("Paths from root to concept " + conceptID + " of Dictyostelium  ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }

    public void testOBOFindPathFromRootForRootConceptIncludingChildrenDictyostelium() throws Exception {
        System.out.println("testOBOFindPathFromRootForRootConceptIncludingChildrenDictyostelium()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DICTYOSTELIUM_DISPLAY_LABEL);
        String conceptID = "DDANAT:0010001";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, false);
        System.out.println("Paths to root for concept " + conceptID + " of Dictyostelium ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }    

    private OntologyRetrievalManagerLexGridImpl getRetrievalManagerLexGrid() {
        OntologyRetrievalManagerLexGridImpl retrievalManagerLexGrid = (OntologyRetrievalManagerLexGridImpl) applicationContext
                .getBean("ontologyRetrievalManagerLexGrid", OntologyRetrievalManagerLexGridImpl.class);

        return retrievalManagerLexGrid;
    }

}
