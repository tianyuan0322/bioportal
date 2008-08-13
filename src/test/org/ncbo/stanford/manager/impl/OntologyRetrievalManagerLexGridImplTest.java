package org.ncbo.stanford.manager.impl;

import java.util.Arrays;
import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;

/**
 * Ensure that the OntologyLoaderLexGridImplTest testcase which is used to load
 * the ontologies has been run before this testcase can be used.
 * 
 * @author Pradip Kanjamala
 */
public class OntologyRetrievalManagerLexGridImplTest extends AbstractBioPortalTest {

    // Test ontology display labels
    private final static String TEST_OWL_DISPLAY_LABEL = "pizza.owl";
    private final static String TEST_OBO_DISPLAY_LABEL = "cell";
    private final static String TEST_LEXGRID_DISPLAY_LABEL = "Automobiles.xml";
    private final static String TEST_UMLS_DISPLAY_LABEL = "AIR";

    OntologyRetrievalManagerLexGridImpl retrievalManager;

    public void testInitialize() {
        retrievalManager = getRetrievalManagerLexGrid();
        System.out.println("Initializing retrievalManagerLexGrid");

    }


    public void testFindOBORootConcept() throws Exception {
        System.out.println("testFindOBORootConcept()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
        System.out.println("Root concept is " + classBean);
        System.out.println("\n");
        assertTrue(classBean != null);
    }
    
    public void testFindOBORootConceptCell() throws Exception {
        System.out.println("testFindOBORootConceptCell()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(1005);
        ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
        System.out.println("Root concept is " + classBean);
        System.out.println("\n");
        assertTrue(classBean != null);
    }
    
    public void testFindOBORootConceptCellOld() throws Exception {
        System.out.println("testFindOBORootConceptCellOld()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(1006);
        ClassBean classBean = retrievalManager.findRootConcept(ncboOntology);
        System.out.println("Root concept is " + classBean);
        System.out.println("\n");
        assertTrue(classBean != null);
    }

    public void estFindOBORootConceptTAO() throws Exception {
        System.out.println("testFindOBORootConceptTAO()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(1066);
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
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        List<String> properties = retrievalManager.findProperties(ncboOntology);
        System.out.println("Properties for cell ontology are :");
        for (String str : properties) {
            System.out.println(str);
        }
        System.out.println("\n");
        assertTrue(properties != null);
    }


    public void testOBOFindConcept() throws Exception {
        System.out.println("testOBOFindConcept()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        ClassBean classBean = retrievalManager.findConcept(ncboOntology, conceptID);
        System.out.println("Concept " + conceptID + " of cell ontology is " + classBean);
        System.out.println("\n");
        assertTrue(classBean.getId().equalsIgnoreCase(conceptID));
    }

    public void testOBOFindChildren() throws Exception {
        System.out.println("testOBOFindChildren()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        List<ClassBean> classBeans = retrievalManager.findChildren(ncboOntology, conceptID);
        System.out.println("Children of concept " + conceptID + " of cell ontology are :");
        for (ClassBean classBean : classBeans) {
            System.out.println(classBean);
        }
        System.out.println("\n");
        assertTrue(classBeans != null && classBeans.size() > 0);
    }

    public void testOBOFindParent() throws Exception {
        System.out.println("testOBOFindParent()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        List<ClassBean> classBeans = retrievalManager.findParent(ncboOntology, conceptID);
        System.out.println("Parent of concept " + conceptID + " of cell ontology are :");
        for (ClassBean classBean : classBeans) {
            System.out.println(classBean);
        }
        System.out.println("\n");
        assertTrue(classBeans != null && classBeans.size() > 0);
    }

    public void testOBOFindPathToRoot() throws Exception {
        System.out.println("testOBOFindPathToRoot()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        ClassBean pathBean = retrievalManager.findPathToRoot(ncboOntology, conceptID, true);
        System.out.println("Paths to root for concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }

    public void testOBOFindPathFromRoot() throws Exception {
        System.out.println("testOBOFindPathFromRoot()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, true);
        System.out.println("Paths from root to concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }
    
    
    public void testOBOFindPathFromRootForRootConcept() throws Exception {
        System.out.println("testOBOFindPathFromRootForRootConcept()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        String conceptID = "CL:0000000";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, true);
        System.out.println("Paths from root to concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }
    
    /*
    public void testOBOFindPathToRootMouse() throws Exception {
        System.out.println("testOBOFindPathToRoot()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(1045);
        String conceptID = "MPATH:360";
        ClassBean pathBean = retrievalManager.findPathToRoot(ncboOntology, conceptID, true);
        System.out.println("Paths to root for concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }
*/
    
    public void testOBOFindPathFromRootIncludingChildren() throws Exception {
        System.out.println("testOBOFindPathFromRootIncludingChildren()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        String conceptID = "CL:0000255";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, false);
        System.out.println("Paths to root for concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }
    
    public void testOBOFindPathFromRootForRootConceptIncludingChildren() throws Exception {
        System.out.println("testOBOFindPathFromRootForRootConceptIncludingChildren()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        String conceptID = "CL:0000000";
        ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, false);
        System.out.println("Paths to root for concept " + conceptID + " of cell ontology are :");
        System.out.println(pathBean);
        System.out.println("\n");
        assertTrue(pathBean != null);
    }    

    public void testFindConceptNameContains() throws Exception {
        System.out.println("testFindConceptNameContains()");
        retrievalManager = getRetrievalManagerLexGrid();
        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        List<VNcboOntology> ontologyVersionIds = (List<VNcboOntology>) Arrays.asList(ncboOntology);
        String query = "eukaryotic";
        //String query = "colony forming unit hematopoietic";
        List<SearchResultBean> searchResultBeans = retrievalManager.findConceptNameContains(ontologyVersionIds, query,
                false, 100);

        System.out.println("Results of searching for 'eukaryotic' in the cell ontology is :");
        for (SearchResultBean srb : searchResultBeans) {
            List<ClassBean> beans = srb.getNames();
            for (ClassBean bean : beans)
                System.out.println(bean);
        }

        System.out.println("\n");
        assertTrue(searchResultBeans.isEmpty() != true);
    }

    public void testFindConceptNameExact() throws Exception {
        System.out.println("testFindConceptNameExact()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        List<VNcboOntology> ontologyVersionIds = (List<VNcboOntology>) Arrays.asList(ncboOntology);
        String query = "eukaryotic cell";
        List<SearchResultBean> searchResultBeans = retrievalManager.findConceptNameExact(ontologyVersionIds, query,
                false, 100);

        System.out.println("Results of searching for 'eukaryotic cell' in the cell ontology is :");
        for (SearchResultBean srb : searchResultBeans) {
            List<ClassBean> beans = srb.getNames();
            for (ClassBean bean : beans)
                System.out.println(bean);
        }
        System.out.println("\n");
        assertTrue(searchResultBeans.isEmpty() != true);
    }

    public void testFindConceptPropertyContains() throws Exception {
        System.out.println("testFindConceptPropertyContains()");
        retrievalManager = getRetrievalManagerLexGrid();

        VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(TEST_OBO_DISPLAY_LABEL);
        List<VNcboOntology> ontologyVersionIds = (List<VNcboOntology>) Arrays.asList(ncboOntology);
        String query = "beta cell";
        String[] properties = { "synonym" };
        List<SearchResultBean> searchResultBeans = retrievalManager.findConceptPropertyContains(ontologyVersionIds,
                query, false, 100);

        System.out.println("Results of searching for 'beta cell' in the cell ontology is :");
        for (SearchResultBean srb : searchResultBeans) {
            List<ClassBean> beans = srb.getProperties();
            for (ClassBean bean : beans)
                System.out.println(bean);
        }
        System.out.println("\n");
        assertTrue(searchResultBeans.isEmpty() != true);
    }
    
  public void estOBOFindPathFromRootDictyostelium() throws Exception {
  System.out.println("testOBOFindPathFromRootDictyostelium()");
  retrievalManager = getRetrievalManagerLexGrid();

  VNcboOntology ncboOntology = retrievalManager.getLatestNcboOntology(1016);
  //String conceptID = "DDANAT:0000037";
  //String conceptID = "DDANAT:0010081";
  String conceptID = "DDANAT:0000097";
  ClassBean pathBean = retrievalManager.findPathFromRoot(ncboOntology, conceptID, false);
  System.out.println("Paths from root to concept " + conceptID + " of Dictyostelium  ontology are :");
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
