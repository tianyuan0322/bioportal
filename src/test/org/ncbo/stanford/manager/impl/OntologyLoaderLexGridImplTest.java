package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.manager.load.impl.OntologyLoadManagerLexGridImpl;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.PhysicalDirectoryFilePathHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests loading ontologies into LexGrid using the
 * OntologyLoadManagerLexGridImpl
 * 
 * @author Pradip Kanjamala
 */
public class OntologyLoaderLexGridImplTest extends AbstractBioPortalTest {

	// Test ontology URIs
	public final static String OWL_PIZZA_PATHNAME = "test/sample_data/pizza.owl";
	public final static String OWL_PIZZA_URN_VERSION = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#|version 1.3";
	public final static String OWL_PIZZA_DISPLAY_LABEL = "pizza.owl";

	public final static String OBO_CELL_PATHNAME = "test/sample_data/cell.obo";
	public final static String OBO_CELL_URN_VERSION = "urn:lsid:bioontology.org:cell|UNASSIGNED";
	public final static String OBO_CELL_DISPLAY_LABEL = "cell";
	public final static Integer OBO_CELL_ONTOLOGY_ID = 5000;

	public final static String OBO_FUNGAL_PATHNAME = "test/sample_data/fungal_anatomy.obo";
	public final static String OBO_FUNGAL_URN_VERSION = "urn:lsid:bioontology.org:fungal|UNASSIGNED";
	public final static String OBO_FUNGAL_DISPLAY_LABEL = "fungal";

	public final static String OBO_CELL_OLD_PATHNAME = "test/sample_data/cell_old.obo";
	public final static String OBO_CELL_OLD_URN_VERSION = "urn:lsid:bioontology.org:cell|UNASSIGNED";
	public final static String 	OBO_CELL_OLD_DISPLAY_LABEL = "cell_old";
	public final static Integer OBO_CELL_OLD_ONTOLOGY_ID = 5000;

	public final static String OBO_DICTYOSTELIUM_PATHNAME = "test/sample_data/dictyostelium_anatomy.obo";
	public final static String OBO_DICTYOSTELIUM_URN_VERSION = "urn:lsid:bioontology.org:dictyostelium|UNASSIGNED";
	public final static String OBO_DICTYOSTELIUM_DISPLAY_LABEL = "DICTYOSTELIUM_ANATOMY";

	public final static String OBO_INFECTIOUS_DISEASE_PATHNAME = "test/sample_data/infectious_disease.obo";
	public final static String OBO_INFECTIOUS_DISEASE_URN_VERSION = "urn:lsid:bioontology.org:infectious_discease|UNASSIGNED";
	public final static String OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL = "INFECTIOUS_DISEASE";

	public final static String LEXGRID_XML_PATHNAME = "test/sample_data/Automobiles.xml";
	public final static String LEXGRID_XML_URN_VERSION = "urn:oid:11.11.0.1|1.0";
	public final static String LEXGRID_DISPLAY_LABEL = "Automobiles.xml";

	public final static String UMLS_PATHNAME = "test/sample_data/sampleUMLS-AIR/";
	public final static String UMLS_URN_VERSION = "urn:oid:2.16.840.1.113883.6.110|1993.bvt";
	public final static String UMLS_DISPLAY_LABEL = "AIR";

	@Autowired
	OntologyService ontologyService;

	@Autowired
	OntologyLoadManagerLexGridImpl loadManagerLexGrid;

	@Test
	public void testLoadOboCell() throws Exception {
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadOboCell().................. BEGIN");
		OntologyBean ontologyBean = this.createOntolgyBeanOboCell();
		// populate file field in ontologyBean
		ontologyBean.setFilePath(OBO_CELL_PATHNAME);
		// create - pass FileHandler
		ontologyService.createOntologyOrView(ontologyBean,
				getFilePathHandler(ontologyBean));
		if (ontologyBean != null)
			System.out.println("Created OntologyBean with ID = "
					+ ontologyBean.getId()+" and ontology id="+ontologyBean.getOntologyId());
		// load
		loadOntology(ontologyBean, OBO_CELL_PATHNAME);
		assertTrue(ontologyBean.getCodingScheme() != null);
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadOboCell().................... END");
	}

	@Test
	public void testLoadOboCellOld() throws Exception {
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadOboCellOld().................. BEGIN");
		OntologyBean ontologyBean = this.createOntolgyBeanOboCellOld();
		// populate file field in ontologyBean
		ontologyBean.setFilePath(OBO_CELL_OLD_PATHNAME);
		// create - pass FileHandler
		ontologyService.createOntologyOrView(ontologyBean,
				getFilePathHandler(ontologyBean));

		if (ontologyBean != null)
			System.out.println("Created OntologyBean with ID = "
					+ ontologyBean.getId()+" and ontology id="+ontologyBean.getOntologyId());
		// load
		loadOntology(ontologyBean, OBO_CELL_OLD_PATHNAME);
		assertTrue(ontologyBean.getCodingScheme() != null);
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadOboCellOld().................... END");
	}

	@Test
	public void testLoadDictytostelium() throws Exception {
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadDictytostelium().................. BEGIN");
		OntologyBean ontologyBean = this.createOntolgyBeanOBODictytostelium();
		// populate file field in ontologyBean
		ontologyBean.setFilePath(OBO_DICTYOSTELIUM_PATHNAME);
		// create - pass FileHandler
		ontologyService.createOntologyOrView(ontologyBean,
				getFilePathHandler(ontologyBean));

		if (ontologyBean != null)
			System.out.println("Created OntologyBean with ID = "
					+ ontologyBean.getId());
		// load
		loadOntology(ontologyBean, OBO_DICTYOSTELIUM_PATHNAME);
		assertTrue(ontologyBean.getCodingScheme() != null);
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadDictytostelium().................... END");
	}

	@Test
	public void testLoadInfectiousDisease() throws Exception {
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadInfectiousDisease().................. BEGIN");
		OntologyBean ontologyBean = this
				.createOntolgyBeanOBOInfectiousDisease();
		// populate file field in ontologyBean
		ontologyBean.setFilePath(OBO_INFECTIOUS_DISEASE_PATHNAME);
		// create - pass FileHandler
		ontologyService.createOntologyOrView(ontologyBean,
				getFilePathHandler(ontologyBean));

		if (ontologyBean != null)
			System.out.println("Created OntologyBean with ID = "
					+ ontologyBean.getId());
		// load
		loadOntology(ontologyBean, OBO_INFECTIOUS_DISEASE_PATHNAME);
		assertTrue(ontologyBean.getCodingScheme() != null);
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadInfectiousDisease().................... END");
	}

	@Test
	public void testLoadGenericOwl() throws Exception {
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadGenericOwl().................. BEGIN");
		OntologyBean ontologyBean = this.createOntolgyBeanGenericOWL();
		// populate file related field in ontologyBean
		ontologyBean.setFilePath(OWL_PIZZA_PATHNAME);
		// create - pass FileHandler
		ontologyService.createOntologyOrView(ontologyBean,
				getFilePathHandler(ontologyBean));
		if (ontologyBean != null)
			System.out.println("Created OntologyBean with ID = "
					+ ontologyBean.getId());
		// load
		loadOntology(ontologyBean, OWL_PIZZA_PATHNAME);
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadGenericOwl().................... END");
		assertTrue(ontologyBean.getCodingScheme() != null);
	}

	@Test
	public void testLoadLexGridXML() throws Exception {
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadLexGridXML().................. BEGIN");
		OntologyBean ontologyBean = this.createOntolgyBeanLexgridXML();
		// populate file related field in ontologyBean
		ontologyBean.setFilePath(LEXGRID_XML_PATHNAME);
		// create - pass FileHandler
		ontologyService.createOntologyOrView(ontologyBean,
				getFilePathHandler(ontologyBean));

		if (ontologyBean != null)
			System.out.println("Created OntologyBean with ID = "
					+ ontologyBean.getId());
		// load
		loadOntology(ontologyBean, LEXGRID_XML_PATHNAME);
		assertTrue(ontologyBean.getCodingScheme() != null);
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadLexGridXML().................... END");
	}

	@Test
	public void testLoadUMLS() throws Exception {
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadUMLS().................. BEGIN");
		OntologyBean ontologyBean = this.createOntolgyBeanUMLS();
		// populate file related field in ontologyBean
		ontologyBean.setFilePath(UMLS_PATHNAME + "sampleUMLS-AIR.zip");
		// create - pass FileHandler
		ontologyService.createOntologyOrView(ontologyBean,
				getFilePathHandler(ontologyBean));
		if (ontologyBean != null)
			System.out.println("Created OntologyBean with ID = "
					+ ontologyBean.getId());
		// load
		loadUMLSOntology(ontologyBean, UMLS_PATHNAME);
		assertTrue(ontologyBean.getCodingScheme() != null);
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadUMLS().................... END");
	}

	@Test
	public void testLoadOboFungal() throws Exception {
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadOboFungal().................. BEGIN");
		OntologyBean ontologyBean = this.createOntolgyBeanOboFungal();
		// populate file field in ontologyBean
		ontologyBean.setFilePath(OBO_FUNGAL_PATHNAME);
		// create - pass FileHandler
		ontologyService.createOntologyOrView(ontologyBean,
				getFilePathHandler(ontologyBean));
		if (ontologyBean != null)
			System.out.println("Created OntologyBean with ID = "
					+ ontologyBean.getId());
		// load
		loadOntology(ontologyBean, OBO_FUNGAL_PATHNAME);
		assertTrue(ontologyBean.getCodingScheme() != null);
		System.out
				.println("OntologyLoaderLexGridImplTest: testLoadOboFungal().................... END");
	}

	@Test
	public void testLoadAndCleanup() throws Exception {
		System.out.println("testLoadAndCleanup()");

		OntologyBean ob = loadManagerLexGrid
				.getLatestOntologyBean(OBO_FUNGAL_DISPLAY_LABEL);
		loadManagerLexGrid.cleanup(ob);
	}

	private OntologyBean createOntolgyBeanOboCell() {
		OntologyBean bean = createOntolgyBeanBase();
		bean.setOntologyId(5000);
		bean.setFormat(ApplicationConstants.FORMAT_OBO);
		bean.setCodingScheme(OBO_CELL_URN_VERSION);
		bean.setDisplayLabel(OBO_CELL_DISPLAY_LABEL);
		bean.setContactEmail("obo@email.com");
		bean.setContactName("OBO Name");
		return bean;
	}

	private OntologyBean createOntolgyBeanOboFungal() {
		OntologyBean bean = createOntolgyBeanBase();
		bean.setFormat(ApplicationConstants.FORMAT_OBO);
		bean.setCodingScheme(OBO_FUNGAL_URN_VERSION);
		bean.setDisplayLabel(OBO_FUNGAL_DISPLAY_LABEL);
		bean.setContactEmail("obo@email.com");
		bean.setContactName("OBO Name");
		return bean;
	}

	private OntologyBean createOntolgyBeanOboCellOld() {
		OntologyBean bean = createOntolgyBeanBase();
		bean.setFormat(ApplicationConstants.FORMAT_OBO);
		bean.setOntologyId(5000);
		bean.setCodingScheme(OBO_CELL_OLD_URN_VERSION);
		bean.setDisplayLabel(OBO_CELL_OLD_DISPLAY_LABEL);
		bean.setContactEmail("obo@email.com");
		bean.setContactName("OBO Name");
		return bean;
	}

	private OntologyBean createOntolgyBeanOBODictytostelium() {
		OntologyBean bean = createOntolgyBeanBase();
		bean.setFormat(ApplicationConstants.FORMAT_OBO);
		bean.setCodingScheme(OBO_DICTYOSTELIUM_URN_VERSION);
		bean.setDisplayLabel(OBO_DICTYOSTELIUM_DISPLAY_LABEL);
		bean.setContactEmail("obo@email.com");
		bean.setContactName("OBO Name");
		return bean;
	}

	private OntologyBean createOntolgyBeanOBOInfectiousDisease() {
		OntologyBean bean = createOntolgyBeanBase();
		bean.setFormat(ApplicationConstants.FORMAT_OBO);
		bean.setCodingScheme(OBO_INFECTIOUS_DISEASE_URN_VERSION);
		bean.setDisplayLabel(OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
		bean.setContactEmail("obo@email.com");
		bean.setContactName("OBO Name");
		return bean;
	}

	private OntologyBean createOntolgyBeanGenericOWL() {
		OntologyBean bean = createOntolgyBeanBase();
		bean.setFormat(ApplicationConstants.FORMAT_OWL_DL);
		bean.setCodingScheme(OWL_PIZZA_URN_VERSION);
		bean.setDisplayLabel(OWL_PIZZA_DISPLAY_LABEL);
		bean.setContactEmail("owl@email.com");
		bean.setContactName("Owl Name");
		return bean;
	}

	private OntologyBean createOntolgyBeanLexgridXML() {
		OntologyBean bean = createOntolgyBeanBase();
		bean.setFormat(ApplicationConstants.FORMAT_LEXGRID_XML);
		bean.setCodingScheme(LEXGRID_XML_URN_VERSION);
		bean.setDisplayLabel(LEXGRID_DISPLAY_LABEL);
		bean.setContactEmail("lexgrid@email.com");
		bean.setContactName("Lexgrid Name");
		return bean;
	}

	private OntologyBean createOntolgyBeanUMLS() {
		OntologyBean bean = createOntolgyBeanBase();
		bean.setFormat(ApplicationConstants.FORMAT_UMLS_RRF);
		bean.setCodingScheme(UMLS_URN_VERSION);
		bean.setDisplayLabel(UMLS_DISPLAY_LABEL);
		bean.setTargetTerminologies(UMLS_DISPLAY_LABEL);
		bean.setContactEmail("umls@email.com");
		bean.setContactName("Umls Name");
		return bean;
	}

	private OntologyBean createOntolgyBeanBase() {
		OntologyBean bean = new OntologyBean(false);
		// bean.setOntologyId(3000);
		// OntologyId gets automatically generated.
		bean.setIsManual(ApplicationConstants.FALSE);
		bean.setFormat(ApplicationConstants.FORMAT_OBO);
		bean.setCodingScheme(OBO_CELL_URN_VERSION);
		bean.setDisplayLabel(OBO_CELL_DISPLAY_LABEL);
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

	private void loadOntology(OntologyBean ontologyBean, String filePath)
			throws Exception {
		System.out.println("___Loading Ontology....... BEGIN : " + filePath);
		loadManagerLexGrid.loadOntology(new File(filePath).toURI(),
				ontologyBean);
		System.out.println("___Loading Ontology........ END : " + filePath);
	}

	private void loadUMLSOntology(OntologyBean ontologyBean, String filePath)
			throws Exception {
		System.out.println("___Loading Ontology....... BEGIN : " + filePath);
		// UMLS ONLY
		loadManagerLexGrid.loadOntology(new File(filePath).toURI(),
				ontologyBean);
		System.out.println("___Loading Ontology........ END : " + filePath);
	}

	public static FilePathHandler getFilePathHandler(OntologyBean ontologyBean)
			throws Exception {

		File inputFile = new File(ontologyBean.getFilePath());
		System.out.println("Testcase: getFilePathHandler() - inputfilepath = "
				+ ontologyBean.getFilePath());

		if (!inputFile.exists()) {
			System.out
					.println("Error! InputFile Not Found. Could not create filePathHanlder for input file.");
			throw new Exception(
					"Error! InputFile Not Found. Could not create filePathHanlder for input file.");
		}

		FilePathHandler filePathHandler = new PhysicalDirectoryFilePathHandlerImpl(
				CompressedFileHandlerFactory.createFileHandler(ontologyBean
						.getFormat()), inputFile);

		return filePathHandler;

	}
}
