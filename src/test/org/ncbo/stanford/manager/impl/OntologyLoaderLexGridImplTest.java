package org.ncbo.stanford.manager.impl;

import java.io.File;
import java.util.Date;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.manager.wrapper.impl.OntologyLoadManagerWrapperLexGridImpl;
import org.ncbo.stanford.service.loader.processor.OntologyLoadProcessorService;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.filehandler.FileHandler;
import org.ncbo.stanford.util.filehandler.impl.PhysicalDirectoryFileHandler;

/**
 * Tests loading ontologies into LexGrid using the
 * OntologyLoadManagerWrapperLexGridImpl
 * 
 * @author Pradip Kanjamala
 */
public class OntologyLoaderLexGridImplTest extends AbstractBioPortalTest {

	// Test ontology URIs
	private final static String TEST_OWL_PATHNAME = "test/sample_data/pizza.owl";
	private final static String TEST_OWL_URN_VERSION = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#|version 1.3";
	private final static String TEST_OBO_PATHNAME = "test/sample_data/cell.obo";
	private final static String TEST_OBO_URN_VERSION = "urn:lsid:bioontology.org:cell|UNASSIGNED";
	private final static String TEST_LEXGRID_XML_PATHNAME = "test/sample_data/Automobiles.xml";
	private final static String TEST_LEXGRID_XML_URN_VERSION = "urn:oid:11.11.0.1|1.0";
	private final static String TEST_UMLS_PATHNAME = "test/sample_data/sampleUMLS-AIR/";
	private final static String TEST_UMLS_URN_VERSION = "urn:oid:2.16.840.1.113883.6.110|1993.bvt";

	// OntologyLoadManagerWrapperLexGridImpl loadManagerLexGrid = new
	// OntologyLoadManagerWrapperLexGridImpl();
	OntologyLoadManagerWrapperLexGridImpl loadManagerLexGrid;
	OntologyLoadProcessorService service;
	
	public void testSimpleMetadataLookup() throws Exception  {
		CustomNcboOntologyVersionDAO ncboOntologyVersionDAO = (CustomNcboOntologyVersionDAO) applicationContext.getBean(
				"NcboOntologyVersionDAO", CustomNcboOntologyVersionDAO.class);
		NcboOntologyMetadata ncboMetadata = ncboOntologyVersionDAO
		.findOntologyMetadataById(15910);
		assertTrue(ncboMetadata != null);

	}


	public void testLoadObo() throws Exception {
		System.out.println("Running testLoadObo");
		service = (OntologyLoadProcessorService) applicationContext.getBean(
				"ontologyLoadProcessorService", OntologyLoadProcessorService.class);

		loadManagerLexGrid = (OntologyLoadManagerWrapperLexGridImpl) applicationContext.getBean(
				"ontologyLoadManagerWrapperLexGrid", OntologyLoadManagerWrapperLexGridImpl.class);

		// Populate Ontology Bean
		OntologyBean ontology_bean = new OntologyBean();
		ontology_bean.setFormat(ApplicationConstants.FORMAT_OBO);
		ontology_bean.setCodingScheme(TEST_OBO_URN_VERSION);
		ontology_bean.setDisplayLabel("cell");
		ontology_bean.setUserId(12564);
		ontology_bean.setVersionNumber("1.0");
		ontology_bean.setIsCurrent(new Byte("1"));
		ontology_bean.setIsRemote(new Byte("0"));
		ontology_bean.setIsReviewed(new Byte("1"));
		ontology_bean.setDateCreated(new Date());
		ontology_bean.setDateReleased(new Date());
		ontology_bean.setContactEmail("obo@email.com");
		ontology_bean.setContactName("OBO Name");
		ontology_bean.setOntologyId(3000);
		ontology_bean.setIsFoundry(new Byte("0"));
		File inputFile = new File(TEST_OBO_PATHNAME);
		FileHandler ontologyFile = new PhysicalDirectoryFileHandler(inputFile);

		NcboOntologyVersion ncboOntologyVersion = service.processOntologyLoad(ontologyFile,
				ontology_bean);
		System.out.println("Created NcboOntologyVersion with id="+ncboOntologyVersion.getId());
		ontology_bean.setId(ncboOntologyVersion.getId());

		loadManagerLexGrid.loadOntology(new File(TEST_OBO_PATHNAME).toURI(), ontology_bean);
		System.out.println("Loaded OBO ontology="+TEST_OBO_PATHNAME);
		assertTrue(ontology_bean.getCodingScheme() != null);
	}

	public void testLoadGenericOwl() throws Exception {
		service = (OntologyLoadProcessorService) applicationContext.getBean(
				"ontologyLoadProcessorService", OntologyLoadProcessorService.class);

		loadManagerLexGrid = (OntologyLoadManagerWrapperLexGridImpl) applicationContext.getBean(
				"ontologyLoadManagerWrapperLexGrid", OntologyLoadManagerWrapperLexGridImpl.class);
		System.out.println("Running testLoadGenericOwl");
		OntologyBean ontology_bean = new OntologyBean();
		ontology_bean.setFormat(ApplicationConstants.FORMAT_OWL_DL);
		ontology_bean.setCodingScheme(TEST_OWL_URN_VERSION);
		ontology_bean.setDisplayLabel("pizza.owl");
		ontology_bean.setUserId(12564);
		ontology_bean.setVersionNumber("1.0");
		ontology_bean.setIsCurrent(new Byte("1"));
		ontology_bean.setIsRemote(new Byte("0"));
		ontology_bean.setIsReviewed(new Byte("1"));
		ontology_bean.setDateCreated(new Date());
		ontology_bean.setDateReleased(new Date());
		ontology_bean.setContactEmail("owl@email.com");
		ontology_bean.setContactName("Owl Name");
		ontology_bean.setOntologyId(3001);
		ontology_bean.setIsFoundry(new Byte("0"));
		File inputFile = new File(TEST_OWL_PATHNAME);
		FileHandler ontologyFile = new PhysicalDirectoryFileHandler(inputFile);

		NcboOntologyVersion ncboOntologyVersion = service.processOntologyLoad(ontologyFile,
				ontology_bean);
		System.out.println("Created NcboOntologyVersion with id="+ncboOntologyVersion.getId());
		ontology_bean.setId(ncboOntologyVersion.getId());

		loadManagerLexGrid.loadOntology(new File(TEST_OWL_PATHNAME).toURI(), ontology_bean);
		System.out.println("Loaded OBO ontology="+TEST_OWL_PATHNAME);
		assertTrue(ontology_bean.getCodingScheme() != null);

	}

	public void testLoadLexGridXML() throws Exception {
		service = (OntologyLoadProcessorService) applicationContext.getBean(
				"ontologyLoadProcessorService", OntologyLoadProcessorService.class);

		loadManagerLexGrid = (OntologyLoadManagerWrapperLexGridImpl) applicationContext.getBean(
				"ontologyLoadManagerWrapperLexGrid", OntologyLoadManagerWrapperLexGridImpl.class);
		System.out.println("Running testLoadLexGridXML");
		OntologyBean ontology_bean = new OntologyBean();
		ontology_bean.setFormat(ApplicationConstants.FORMAT_LEXGRID_XML);
		ontology_bean.setCodingScheme(TEST_LEXGRID_XML_URN_VERSION);
		ontology_bean.setDisplayLabel("Automobiles.xml");
		ontology_bean.setUserId(12564);
		ontology_bean.setVersionNumber("1.0");
		ontology_bean.setIsCurrent(new Byte("1"));
		ontology_bean.setIsRemote(new Byte("0"));
		ontology_bean.setIsReviewed(new Byte("1"));
		ontology_bean.setDateCreated(new Date());
		ontology_bean.setDateReleased(new Date());
		ontology_bean.setContactEmail("lexgrid@email.com");
		ontology_bean.setContactName("Lexgrid Name");
		ontology_bean.setOntologyId(3002);
		ontology_bean.setIsFoundry(new Byte("0"));
		File inputFile = new File(TEST_LEXGRID_XML_PATHNAME);
		FileHandler ontologyFile = new PhysicalDirectoryFileHandler(inputFile);

		NcboOntologyVersion ncboOntologyVersion = service.processOntologyLoad(ontologyFile,
				ontology_bean);
		System.out.println("Created NcboOntologyVersion with id="+ncboOntologyVersion.getId());
		ontology_bean.setId(ncboOntologyVersion.getId());

		loadManagerLexGrid.loadOntology(new File(TEST_LEXGRID_XML_PATHNAME).toURI(), ontology_bean);
		System.out.println("Loaded ontology="+TEST_LEXGRID_XML_PATHNAME);
		assertTrue(ontology_bean.getCodingScheme() != null);

	}

	public void testLoadUMLS() throws Exception {
		service = (OntologyLoadProcessorService) applicationContext.getBean(
				"ontologyLoadProcessorService", OntologyLoadProcessorService.class);

		loadManagerLexGrid = (OntologyLoadManagerWrapperLexGridImpl) applicationContext.getBean(
				"ontologyLoadManagerWrapperLexGrid", OntologyLoadManagerWrapperLexGridImpl.class);
		System.out.println("Running testLoadUMLS");
		OntologyBean ontology_bean = new OntologyBean();
		ontology_bean.setFormat(ApplicationConstants.FORMAT_UMLS_RRF);
		ontology_bean.setCodingScheme(TEST_UMLS_URN_VERSION);
		ontology_bean.setDisplayLabel("AIR");
		ontology_bean.setUserId(12564);
		ontology_bean.setVersionNumber("1.0");
		ontology_bean.setIsCurrent(new Byte("1"));
		ontology_bean.setIsRemote(new Byte("0"));
		ontology_bean.setIsReviewed(new Byte("1"));
		ontology_bean.setDateCreated(new Date());
		ontology_bean.setDateReleased(new Date());
		ontology_bean.setContactEmail("umls@email.com");
		ontology_bean.setContactName("Umls Name");
		ontology_bean.setOntologyId(3003);
		ontology_bean.setIsFoundry(new Byte("0"));
		File inputFile = new File(TEST_UMLS_PATHNAME+"sampleUMLS-AIR.zip");
		FileHandler ontologyFile = new PhysicalDirectoryFileHandler(inputFile);

		NcboOntologyVersion ncboOntologyVersion = service.processOntologyLoad(ontologyFile,
				ontology_bean);
		System.out.println("Created NcboOntologyVersion with id="+ncboOntologyVersion.getId());
		ontology_bean.setId(ncboOntologyVersion.getId());

		loadManagerLexGrid.setTargetTerminologies("AIR");
		loadManagerLexGrid.loadOntology(new File(TEST_UMLS_PATHNAME).toURI(), ontology_bean);
		System.out.println("Loaded UMLS ontology="+TEST_UMLS_PATHNAME);
		assertTrue(ontology_bean.getCodingScheme() != null);

	}

}
