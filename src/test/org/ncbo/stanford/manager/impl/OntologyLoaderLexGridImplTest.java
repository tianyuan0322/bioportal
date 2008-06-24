package org.ncbo.stanford.manager.impl;

import java.io.File;
import java.util.Date;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.ontology.OntologyServiceTest;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.PhysicalDirectoryFilePathHandlerImpl;

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
	
	private final static String TEST_OBO_PATHNAME = "test/sample_data/cell.obo";
	private final static String TEST_OBO_URN_VERSION = "urn:lsid:bioontology.org:cell|UNASSIGNED";
	private final static String TEST_OBO_DISPLAY_LABEL = "cell";
	
	private final static String TEST_LEXGRID_XML_PATHNAME = "test/sample_data/Automobiles.xml";
	private final static String TEST_LEXGRID_XML_URN_VERSION = "urn:oid:11.11.0.1|1.0";
	private final static String TEST_LEXGRID_DISPLAY_LABEL = "Automobiles.xml";
	
	private final static String TEST_UMLS_PATHNAME = "test/sample_data/sampleUMLS-AIR/";
	private final static String TEST_UMLS_URN_VERSION = "urn:oid:2.16.840.1.113883.6.110|1993.bvt";
	private final static String TEST_UMLS_DISPLAY_LABEL = "AIR";

	OntologyLoadManagerLexGridImpl loadManagerLexGrid;


	public void testSimpleMetadataLookup() throws Exception {
		CustomNcboOntologyVersionDAO ncboOntologyVersionDAO = (CustomNcboOntologyVersionDAO) applicationContext
				.getBean("NcboOntologyVersionDAO",
						CustomNcboOntologyVersionDAO.class);
		NcboOntologyMetadata ncboMetadata = ncboOntologyVersionDAO
				.findOntologyMetadataById(15910);
		assertTrue(ncboMetadata != null);

	}

	public void testLoadObo() throws Exception {

		System.out.println("OntologyLoaderLexGridImplTest: testLoadObo().................. BEGIN");

		OntologyBean ontologyBean = this.createOntolgyBeanOBO();
		
		// populate file field in ontologyBean
		ontologyBean.setFilePath(TEST_OBO_PATHNAME);
		
		// create - pass FileHandler
		getOntologyService().createOntology(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
		
		if (ontologyBean != null)
			System.out.println("Created OntologyBean with ID = " + ontologyBean.getId());
		
		// load
		loadOntology(ontologyBean, TEST_OBO_PATHNAME);
				
		assertTrue(ontologyBean.getCodingScheme() != null);
		
		System.out.println("OntologyLoaderLexGridImplTest: testLoadObo().................... END");
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

		OntologyBean ontologyBean = this.createOntolgyBeanLexgridXML();
		
		// populate file related field in ontologyBean
		ontologyBean.setFilePath(TEST_UMLS_PATHNAME + "sampleUMLS-AIR.zip");
		
		// create - pass FileHandler
		getOntologyService().createOntology(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
		
		if (ontologyBean != null)
			System.out.println("Created OntologyBean with ID = " + ontologyBean.getId());
		
		// load
		loadUMLSOntology(ontologyBean, TEST_LEXGRID_XML_PATHNAME);


		assertTrue(ontologyBean.getCodingScheme() != null);
		
		System.out.println("OntologyLoaderLexGridImplTest: testLoadUMLS().................... END");

	}
	
	
	private OntologyBean createOntolgyBeanOBO() {
		
		OntologyBean bean = createOntolgyBeanBase();
		
		bean.setFormat(ApplicationConstants.FORMAT_OBO);
		bean.setCodingScheme(TEST_OBO_URN_VERSION);
		bean.setDisplayLabel("cell");
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
		
		//bean.setOntologyId(3000);
		// OntologyId gets automatically generated.
		bean.setFormat(ApplicationConstants.FORMAT_OBO);
		bean.setCodingScheme(TEST_OBO_URN_VERSION);
		bean.setDisplayLabel(TEST_OBO_DISPLAY_LABEL);
		bean.setUserId(12564);
		bean.setVersionNumber("1.0");
		bean.setStatusId(StatusEnum.STATUS_WAITING.getStatus());
		bean.setVersionStatus("pre-production");
		bean.setIsCurrent(new Byte("1"));
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
		
		OntologyService service = (OntologyService) applicationContext.getBean(
				"ontologyService", OntologyService.class);
	
		return service;
	}
		
		
	private void loadOntology( OntologyBean ontologyBean, String filePath) throws Exception {
	
		
		loadManagerLexGrid = (OntologyLoadManagerLexGridImpl) applicationContext.getBean(
				"ontologyLoadManagerLexGrid", OntologyLoadManagerLexGridImpl.class);
		
		System.out.println("___Loading Ontology....... BEGIN : " + filePath);
		
		loadManagerLexGrid.loadOntology(new File(filePath).toURI(), ontologyBean);
		
		System.out.println("___Loading Ontology........ END : " + filePath);
	}

	private void loadUMLSOntology( OntologyBean ontologyBean, String filePath) throws Exception {
	
		
		loadManagerLexGrid = (OntologyLoadManagerLexGridImpl) applicationContext.getBean(
				"ontologyLoadManagerLexGrid", OntologyLoadManagerLexGridImpl.class);
		
		System.out.println("___Loading Ontology....... BEGIN : " + filePath);
		
		// UMLS ONLY
		loadManagerLexGrid.setTargetTerminologies("AIR");
		loadManagerLexGrid.loadOntology(new File(filePath).toURI(), ontologyBean);
		
		System.out.println("___Loading Ontology........ END : " + filePath);
	}

}
