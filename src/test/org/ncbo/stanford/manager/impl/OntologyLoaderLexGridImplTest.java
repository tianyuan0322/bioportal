package org.ncbo.stanford.manager.impl;

import java.io.File;
import java.util.Date;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.domain.generated.NcboOntologyVersionMetadata;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.manager.load.impl.OntologyLoadManagerLexGridImpl;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.ontology.OntologyServiceTest;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * Tests loading ontologies into LexGrid using the
 * OntologyLoadManagerLexGridImpl
 * 
 * @author Pradip Kanjamala
 */
public class OntologyLoaderLexGridImplTest extends AbstractBioPortalTest {

    // Test ontology URIs
    private final static String TEST_OWL_PATHNAME = "test/sample_data/pizza.owl";
    private final static String TEST_OWL_URN_VERSION = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#|version 1.3";
    private final static String TEST_OWL_DISPLAY_LABEL = "pizza.owl";

    private final static String TEST_OBO_CELL_PATHNAME = "test/sample_data/cell.obo";
    private final static String TEST_OBO_CELL_URN_VERSION = "urn:lsid:bioontology.org:cell|UNASSIGNED";
    private final static String TEST_OBO_CELL_DISPLAY_LABEL = "cell";
    
    private final static String TEST_OBO_FUNGAL_PATHNAME = "test/sample_data/fungal_anatomy.obo";
    private final static String TEST_OBO_FUNGAL_URN_VERSION = "urn:lsid:bioontology.org:fungal|UNASSIGNED";
    private final static String TEST_OBO_FUNGAL_DISPLAY_LABEL = "fungal";    

    private final static String TEST_OBO_CELL_OLD_PATHNAME = "test/sample_data/cell_old.obo";
    private final static String TEST_OBO_CELL_OLD_URN_VERSION = "urn:lsid:bioontology.org:cell|UNASSIGNED";
    private final static String TEST_OBO_CELL_OLD_DISPLAY_LABEL = "cell_old";

    private final static String TEST_OBO_DICTYOSTELIUM_PATHNAME = "test/sample_data/dictyostelium_anatomy.obo";
    private final static String TEST_OBO_DICTYOSTELIUM_URN_VERSION = "urn:lsid:bioontology.org:dictyostelium|UNASSIGNED";
    private final static String TEST_OBO_DICTYOSTELIUM_DISPLAY_LABEL = "DICTYOSTELIUM_ANATOMY";

    private final static String TEST_OBO_INFECTIOUS_DISEASE_PATHNAME = "test/sample_data/infectious_disease.obo";
    private final static String TEST_OBO_INFECTIOUS_DISEASE_URN_VERSION = "urn:lsid:bioontology.org:infectious_discease|UNASSIGNED";
    private final static String TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL = "INFECTIOUS_DISEASE";
    
    private final static String TEST_LEXGRID_XML_PATHNAME = "test/sample_data/Automobiles.xml";
    private final static String TEST_LEXGRID_XML_URN_VERSION = "urn:oid:11.11.0.1|1.0";
    private final static String TEST_LEXGRID_DISPLAY_LABEL = "Automobiles.xml";

    private final static String TEST_UMLS_PATHNAME = "test/sample_data/sampleUMLS-AIR/";
    private final static String TEST_UMLS_URN_VERSION = "urn:oid:2.16.840.1.113883.6.110|1993.bvt";
    private final static String TEST_UMLS_DISPLAY_LABEL = "AIR";

    OntologyLoadManagerLexGridImpl loadManagerLexGrid;

    public void simpleMetadataLookup() throws Exception {
        CustomNcboOntologyVersionDAO ncboOntologyVersionDAO = (CustomNcboOntologyVersionDAO) applicationContext
                .getBean("NcboOntologyVersionDAO", CustomNcboOntologyVersionDAO.class);
        NcboOntologyVersionMetadata ncboMetadata = ncboOntologyVersionDAO.findOntologyMetadataById(15910);
        assertTrue(ncboMetadata != null);

    }

    public void testLoadOboCell() throws Exception {
        System.out.println("OntologyLoaderLexGridImplTest: testLoadOboCell().................. BEGIN");
        OntologyBean ontologyBean = this.createOntolgyBeanOboCell();
        // populate file field in ontologyBean
        ontologyBean.setFilePath(TEST_OBO_CELL_PATHNAME);
        // create - pass FileHandler
        getOntologyService().createOntology(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
        if (ontologyBean != null)
            System.out.println("Created OntologyBean with ID = " + ontologyBean.getId());
        // load
        loadOntology(ontologyBean, TEST_OBO_CELL_PATHNAME);
        assertTrue(ontologyBean.getCodingScheme() != null);
        System.out.println("OntologyLoaderLexGridImplTest: testLoadOboCell().................... END");
    }


    
    
    public void testLoadOboCellOld() throws Exception {
        System.out.println("OntologyLoaderLexGridImplTest: testLoadOboCellOld().................. BEGIN");
        OntologyBean ontologyBean = this.createOntolgyBeanOboCellOld();
        // populate file field in ontologyBean
        ontologyBean.setFilePath(TEST_OBO_CELL_OLD_PATHNAME);
        // create - pass FileHandler
        getOntologyService().createOntology(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
        if (ontologyBean != null)
            System.out.println("Created OntologyBean with ID = " + ontologyBean.getId());
        // load
        loadOntology(ontologyBean, TEST_OBO_CELL_OLD_PATHNAME);
        assertTrue(ontologyBean.getCodingScheme() != null);
        System.out.println("OntologyLoaderLexGridImplTest: testLoadOboCellOld().................... END");
    }
    
    public void testLoadDictytostelium() throws Exception {
        System.out.println("OntologyLoaderLexGridImplTest: testLoadDictytostelium().................. BEGIN");
        OntologyBean ontologyBean = this.createOntolgyBeanOBODictytostelium();
        // populate file field in ontologyBean
        ontologyBean.setFilePath(TEST_OBO_DICTYOSTELIUM_PATHNAME);
        // create - pass FileHandler
        getOntologyService().createOntology(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
        if (ontologyBean != null)
            System.out.println("Created OntologyBean with ID = " + ontologyBean.getId());
        // load
        loadOntology(ontologyBean, TEST_OBO_DICTYOSTELIUM_PATHNAME);
        assertTrue(ontologyBean.getCodingScheme() != null);
        System.out.println("OntologyLoaderLexGridImplTest: testLoadDictytostelium().................... END");
    }    

    public void testLoadInfectiousDisease() throws Exception {
        System.out.println("OntologyLoaderLexGridImplTest: testLoadInfectiousDisease().................. BEGIN");
        OntologyBean ontologyBean = this.createOntolgyBeanOBOInfectiousDisease();
        // populate file field in ontologyBean
        ontologyBean.setFilePath(TEST_OBO_INFECTIOUS_DISEASE_PATHNAME);
        // create - pass FileHandler
        getOntologyService().createOntology(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
        if (ontologyBean != null)
            System.out.println("Created OntologyBean with ID = " + ontologyBean.getId());
        // load
        loadOntology(ontologyBean, TEST_OBO_INFECTIOUS_DISEASE_PATHNAME);
        assertTrue(ontologyBean.getCodingScheme() != null);
        System.out.println("OntologyLoaderLexGridImplTest: testLoadInfectiousDisease().................... END");
    } 
    
    public void testLoadGenericOwl() throws Exception {
        System.out.println("OntologyLoaderLexGridImplTest: testLoadGenericOwl().................. BEGIN");
        OntologyBean ontologyBean = this.createOntolgyBeanGenericOWL();
        // populate file related field in ontologyBean
        ontologyBean.setFilePath(TEST_OWL_PATHNAME);
        // create - pass FileHandler
        getOntologyService().createOntology(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
        if (ontologyBean != null)
            System.out.println("Created OntologyBean with ID = " + ontologyBean.getId());
        // load
        loadOntology(ontologyBean, TEST_OWL_PATHNAME);
        System.out.println("OntologyLoaderLexGridImplTest: testLoadGenericOwl().................... END");
        assertTrue(ontologyBean.getCodingScheme() != null);

    }

    public void testLoadLexGridXML() throws Exception {
        System.out.println("OntologyLoaderLexGridImplTest: testLoadLexGridXML().................. BEGIN");
        OntologyBean ontologyBean = this.createOntolgyBeanLexgridXML();
        // populate file related field in ontologyBean
        ontologyBean.setFilePath(TEST_LEXGRID_XML_PATHNAME);
        // create - pass FileHandler
        getOntologyService().createOntology(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
        if (ontologyBean != null)
            System.out.println("Created OntologyBean with ID = " + ontologyBean.getId());
        // load
        loadOntology(ontologyBean, TEST_LEXGRID_XML_PATHNAME);
        assertTrue(ontologyBean.getCodingScheme() != null);
        System.out.println("OntologyLoaderLexGridImplTest: testLoadLexGridXML().................... END");

    }

    public void testLoadUMLS() throws Exception {
        System.out.println("OntologyLoaderLexGridImplTest: testLoadUMLS().................. BEGIN");
        OntologyBean ontologyBean = this.createOntolgyBeanUMLS();
        // populate file related field in ontologyBean
        ontologyBean.setFilePath(TEST_UMLS_PATHNAME + "sampleUMLS-AIR.zip");
        // create - pass FileHandler
        getOntologyService().createOntology(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
        if (ontologyBean != null)
            System.out.println("Created OntologyBean with ID = " + ontologyBean.getId());
        // load
        loadUMLSOntology(ontologyBean, TEST_UMLS_PATHNAME);
        assertTrue(ontologyBean.getCodingScheme() != null);
        System.out.println("OntologyLoaderLexGridImplTest: testLoadUMLS().................... END");

    }
    
    public void testLoadOboFungal() throws Exception {
        System.out.println("OntologyLoaderLexGridImplTest: testLoadOboFungal().................. BEGIN");
        OntologyBean ontologyBean = this.createOntolgyBeanOboFungal();
        // populate file field in ontologyBean
        ontologyBean.setFilePath(TEST_OBO_FUNGAL_PATHNAME);
        // create - pass FileHandler
        getOntologyService().createOntology(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
        if (ontologyBean != null)
            System.out.println("Created OntologyBean with ID = " + ontologyBean.getId());
        // load
        loadOntology(ontologyBean, TEST_OBO_FUNGAL_PATHNAME);
        assertTrue(ontologyBean.getCodingScheme() != null);
        System.out.println("OntologyLoaderLexGridImplTest: testLoadOboFungal().................... END");
    }
    
    public void testLoadAndCleanup() throws Exception {
        System.out.println("testLoadAndCleanup()");
        OntologyLoadManagerLexGridImpl loadManagerLexGrid = getLoadManagerLexGrid();
        VNcboOntology ncboOntology = loadManagerLexGrid.getLatestNcboOntology(TEST_OBO_FUNGAL_DISPLAY_LABEL);
        OntologyBean ob= new OntologyBean();
        ob.populateFromEntity(ncboOntology);
        loadManagerLexGrid.cleanup(ob);        
    }
    

    private OntologyBean createOntolgyBeanOboCell() {
        OntologyBean bean = createOntolgyBeanBase();
        bean.setFormat(ApplicationConstants.FORMAT_OBO);
        bean.setCodingScheme(TEST_OBO_CELL_URN_VERSION);
        bean.setDisplayLabel(TEST_OBO_CELL_DISPLAY_LABEL);
        bean.setContactEmail("obo@email.com");
        bean.setContactName("OBO Name");
        return bean;
    }
    
    private OntologyBean createOntolgyBeanOboFungal() {
        OntologyBean bean = createOntolgyBeanBase();
        bean.setFormat(ApplicationConstants.FORMAT_OBO);
        bean.setCodingScheme(TEST_OBO_FUNGAL_URN_VERSION);
        bean.setDisplayLabel(TEST_OBO_FUNGAL_DISPLAY_LABEL);
        bean.setContactEmail("obo@email.com");
        bean.setContactName("OBO Name");
        return bean;
    }    

    private OntologyBean createOntolgyBeanOboCellOld() {
        OntologyBean bean = createOntolgyBeanBase();
        bean.setFormat(ApplicationConstants.FORMAT_OBO);
        bean.setCodingScheme(TEST_OBO_CELL_OLD_URN_VERSION);
        bean.setDisplayLabel(TEST_OBO_CELL_OLD_DISPLAY_LABEL);
        bean.setContactEmail("obo@email.com");
        bean.setContactName("OBO Name");
        return bean;
    }
    
    private OntologyBean createOntolgyBeanOBODictytostelium() {
        OntologyBean bean = createOntolgyBeanBase();
        bean.setFormat(ApplicationConstants.FORMAT_OBO);
        bean.setCodingScheme(TEST_OBO_DICTYOSTELIUM_URN_VERSION);
        bean.setDisplayLabel(TEST_OBO_DICTYOSTELIUM_DISPLAY_LABEL);
        bean.setContactEmail("obo@email.com");
        bean.setContactName("OBO Name");
        return bean;
    }    

    private OntologyBean createOntolgyBeanOBOInfectiousDisease() {
        OntologyBean bean = createOntolgyBeanBase();
        bean.setFormat(ApplicationConstants.FORMAT_OBO);
        bean.setCodingScheme(TEST_OBO_INFECTIOUS_DISEASE_URN_VERSION);
        bean.setDisplayLabel(TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
        bean.setContactEmail("obo@email.com");
        bean.setContactName("OBO Name");
        return bean;
    }   
    
    private OntologyBean createOntolgyBeanGenericOWL() {
        OntologyBean bean = createOntolgyBeanBase();
        bean.setFormat(ApplicationConstants.FORMAT_OWL_DL);
        bean.setCodingScheme(TEST_OWL_URN_VERSION);
        bean.setDisplayLabel(TEST_OWL_DISPLAY_LABEL);
        bean.setContactEmail("owl@email.com");
        bean.setContactName("Owl Name");
        return bean;
    }

    private OntologyBean createOntolgyBeanLexgridXML() {
        OntologyBean bean = createOntolgyBeanBase();
        bean.setFormat(ApplicationConstants.FORMAT_LEXGRID_XML);
        bean.setCodingScheme(TEST_LEXGRID_XML_URN_VERSION);
        bean.setDisplayLabel(TEST_LEXGRID_DISPLAY_LABEL);
        bean.setContactEmail("lexgrid@email.com");
        bean.setContactName("Lexgrid Name");
        return bean;
    }

    private OntologyBean createOntolgyBeanUMLS() {
        OntologyBean bean = createOntolgyBeanBase();
        bean.setFormat(ApplicationConstants.FORMAT_UMLS_RRF);
        bean.setCodingScheme(TEST_UMLS_URN_VERSION);
        bean.setDisplayLabel(TEST_UMLS_DISPLAY_LABEL);
        bean.setContactEmail("umls@email.com");
        bean.setContactName("Umls Name");
        return bean;
    }

    private OntologyBean createOntolgyBeanBase() {
        OntologyBean bean = new OntologyBean();
        // bean.setOntologyId(3000);
        // OntologyId gets automatically generated.
        bean.setIsManual(ApplicationConstants.FALSE);
        bean.setFormat(ApplicationConstants.FORMAT_OBO);
        bean.setCodingScheme(TEST_OBO_CELL_URN_VERSION);
        bean.setDisplayLabel(TEST_OBO_CELL_DISPLAY_LABEL);
        bean.setUserId(1000);
        bean.setVersionNumber("1.0");
        bean.setStatusId(StatusEnum.STATUS_WAITING.getStatus());
        bean.setVersionStatus("pre-production");
        bean.setIsRemote(new Byte("0"));
        bean.setIsReviewed(new Byte("1"));
        bean.setDateCreated(new Date());
        bean.setDateReleased(new Date());
        bean.setContactEmail("obo@email.com");
        bean.setContactName("OBO Name");
        bean.setIsFoundry(new Byte("0"));
        return bean;
    }

    private OntologyService getOntologyService() {
        OntologyService service = (OntologyService) applicationContext
                .getBean("ontologyService", OntologyService.class);
        return service;
    }

    private void loadOntology(OntologyBean ontologyBean, String filePath) throws Exception {
        loadManagerLexGrid = (OntologyLoadManagerLexGridImpl) applicationContext.getBean("ontologyLoadManagerLexGrid",
                OntologyLoadManagerLexGridImpl.class);
        System.out.println("___Loading Ontology....... BEGIN : " + filePath);
        loadManagerLexGrid.loadOntology(new File(filePath).toURI(), ontologyBean);
        System.out.println("___Loading Ontology........ END : " + filePath);
    }

    private void loadUMLSOntology(OntologyBean ontologyBean, String filePath) throws Exception {
        loadManagerLexGrid = (OntologyLoadManagerLexGridImpl) applicationContext.getBean("ontologyLoadManagerLexGrid",
                OntologyLoadManagerLexGridImpl.class);
        System.out.println("___Loading Ontology....... BEGIN : " + filePath);
        // UMLS ONLY
        loadManagerLexGrid.setTargetTerminologies("AIR");
        loadManagerLexGrid.loadOntology(new File(filePath).toURI(), ontologyBean);
        System.out.println("___Loading Ontology........ END : " + filePath);
    }
    
    private OntologyLoadManagerLexGridImpl getLoadManagerLexGrid() {
        loadManagerLexGrid = (OntologyLoadManagerLexGridImpl) applicationContext.getBean("ontologyLoadManagerLexGrid",
                OntologyLoadManagerLexGridImpl.class);

        return loadManagerLexGrid;
    }    

}
