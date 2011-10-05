package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.manager.load.impl.OntologyLoadManagerProtegeImpl;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.PhysicalDirectoryFilePathHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * Tests loading ontologies into Protege using the
 * OntologyLoadManagerProtegeImpl
 * 
 * @author Benjamin Dai
 */
public class OntologyLoaderProtegeImplTest extends AbstractBioPortalTest {

	private final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private final static String DB_URL = "jdbc:mysql://localhost/protege";

	private final static String PROTEGE_USER = "protege_user";
	private final static String PROTEGE_PASSWORD = "protege_user$123";

	// Size test for pizza - streaming and non-streaming
	private final static Integer TEST_STREAM_SIZE = 100000;
	private final static Integer TEST_NOT_STREAM_SIZE = 200000;

	private final static Integer TEST_STREAM_ID = 100000;
	private final static Integer TEST_NOT_STREAM_ID = 200000;

	// Test ontology URIs
	private final static String TEST_PIZZA_OWL_PATH = "test/sample_data/pizza.owl";
	private final static String TEST_PIZZA_DISPLAY_NAME = "Pizza";

	// Pizza sample data
	private final static String TABLE_NONSTREAM = "pizza_table_nonstream2";

	private final static String SOURCE_OWL_URI = "file:///c:/pizza.owl";
	private final static String ERRORS_URI = "file:///c:/pizza_errors.txt";

	// OMV sample data
	private final static String SOURCE_OMVOWL_URI = "file:///c:/OMV_v2.3.owl";
	private final static String TABLE_OMV_NONSTREAM = "omv_table_nonstream2";

	@Autowired
	OntologyService ontologyService;

	@Autowired
	OntologyLoadManagerProtegeImpl loadManagerProtege;

	//
	// Non-Streaming Fast load
	@Test
	public void testNoStreamPizzaLoad() throws Exception {
		System.out.println("Starting testNoStreamPizzaLoad");
		loadManagerProtege.setProtegeBigFileThreshold(TEST_NOT_STREAM_SIZE);

		OntologyBean ontologyBean = createOntolgyBeanBase();
		// populate file field in ontologyBean
		ontologyBean.setFilePath(TEST_PIZZA_OWL_PATH);
		// create - pass FileHandler
		ontologyService.createOntologyOrView(ontologyBean,
				getFilePathHandler(ontologyBean));

		try {
			loadOntology(ontologyBean, TEST_PIZZA_OWL_PATH);
		} catch (Exception exc) {
			System.out.println("Exception: " + exc.getMessage());
			exc.printStackTrace();
			fail("Pizza load failed: " + exc.getMessage());
		}
	}

	// Streaming load of Pizza Ontology - Much slower than the non-streaming
	@Test
	public void testStreamPizzaLoad() {
		System.out.println("Starting testStreamPizzaLoad");

		loadManagerProtege.setProtegeBigFileThreshold(TEST_STREAM_SIZE);

		OntologyBean ontologyBean = new OntologyBean(false);
		ontologyBean.setId(TEST_STREAM_ID);

		try {
			loadManagerProtege
					.loadOntology(new URI(TEST_PIZZA_OWL_PATH), ontologyBean);
		} catch (Exception exc) {
			System.out.println("Exception: " + exc.getMessage());
			exc.printStackTrace();
			fail("Pizza load failed: " + exc.getMessage());
		}
	}

	// Testing Protege api with ontology queries to verify validity.
	@Test
	public void testBasicPizzaNonStreamingLoad() {
		try {
			System.out.println("Starting testBasicPizzaNonStreamingLoad");
			OWLModel fileModel = ProtegeOWL
					.createJenaOWLModelFromURI(SOURCE_OWL_URI);

			List errors = new ArrayList();
			Project fileProject = fileModel.getProject();
			OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
			PropertyList sources = PropertyList.create(fileProject
					.getInternalProjectKnowledgeBase());

			DatabaseKnowledgeBaseFactory.setSources(sources, JDBC_DRIVER,
					DB_URL, TABLE_NONSTREAM, PROTEGE_USER, PROTEGE_PASSWORD);
			factory.saveKnowledgeBase(fileModel, sources, errors);

			// next lines of code are not needed - just testing the import
			OWLModel om = (OWLModel) fileProject.getKnowledgeBase();

			OWLNamedClass country = om.getOWLNamedClass("Country");
			for (Object o : country.getInstances(true)) {
				System.out.println("" + o + " is an instance of Country");
			}

			System.out.println("Parents of Country");
			for (Iterator it = country.getSuperclasses(true).iterator(); it
					.hasNext();) {
				System.out.println("parent> " + it.next());
			}

			// Optional error dump of load
			displayErrors(errors); // optional
			if (!errors.isEmpty()) {
				fail("There were errors: " + errors.size());
				return;
			}

			Project dbProject = Project.createNewProject(factory, errors);
			DatabaseKnowledgeBaseFactory.setSources(dbProject.getSources(),
					JDBC_DRIVER, DB_URL, TABLE_NONSTREAM, PROTEGE_USER,
					PROTEGE_PASSWORD);

			dbProject.createDomainKnowledgeBase(factory, errors, true);
			dbProject.setProjectURI(URIUtilities.createURI(ERRORS_URI));
			dbProject.save(errors);

			displayErrors(errors); // optional
			// return (OWLModel) dbProject.getKnowledgeBase();
		} catch (Exception exc) {
			exc.printStackTrace();
			fail("General exception: " + exc.getMessage());
		}
	}

	// Fast load of medium complexity ontology OMV.
	@Test
	public void testMediumNonStreamingLoad() {
		try {
			OWLModel fileModel = ProtegeOWL
					.createJenaOWLModelFromURI(SOURCE_OMVOWL_URI);

			List errors = new ArrayList();
			Project fileProject = fileModel.getProject();
			OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
			PropertyList sources = PropertyList.create(fileProject
					.getInternalProjectKnowledgeBase());

			DatabaseKnowledgeBaseFactory
					.setSources(sources, JDBC_DRIVER, DB_URL,
							TABLE_OMV_NONSTREAM, PROTEGE_USER, PROTEGE_PASSWORD);
			factory.saveKnowledgeBase(fileModel, sources, errors);

			// next lines of code are not needed - just testing the import
			OWLModel om = (OWLModel) fileProject.getKnowledgeBase();

			OWLNamedClass licenseModel = om.getOWLNamedClass("LicenseModel");
			for (Object o : licenseModel.getInstances(true)) {
				System.out.println("" + o + " is an instance of LicenseModel");
			}

			System.out.println("Parents of LicenseModel");
			for (Iterator it = licenseModel.getSuperclasses(true).iterator(); it
					.hasNext();) {
				System.out.println("parent> " + it.next());
			}

			// Optional error dump of load
			displayErrors(errors); // optional
			if (!errors.isEmpty()) {
				fail("There were errors: " + errors.size());
				return;
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			fail("General exception: " + exc.getMessage());
		}
	}

	//
	// Private methods
	//
	/**
	 * Outputs text representations of the specified List.
	 */
	private void displayErrors(List list) {
		int i = 0;
		for (Iterator it = list.iterator(); it.hasNext();) {
			System.out.println("ERROR: " + ++i + ") " + it.next());
		}
	}

	private OntologyBean createOntolgyBeanBase() {
		OntologyBean bean = new OntologyBean(false);
		// bean.setOntologyId(3000);
		// OntologyId gets automatically generated.
		bean.setIsManual(ApplicationConstants.FALSE);
		bean.setFormat(ApplicationConstants.FORMAT_OWL_DL);
		bean.setDisplayLabel(TEST_PIZZA_DISPLAY_NAME);
		bean.setUserIds(Arrays.asList(1000));
		//bean.setVersionNumber("1.0");
		bean.setStatusId(StatusEnum.STATUS_WAITING.getStatus());
		bean.setVersionStatus("pre-production");
		bean.setIsRemote(new Byte("0"));
		bean.setIsReviewed(new Byte("1"));
		bean.setDateCreated(new Date());
		bean.setDateReleased(new Date());
		bean.setContactEmail("protege@email.com");
		bean.setContactName("Protege Name");
		bean.setIsFoundry(new Byte("0"));
		return bean;
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

	private void loadOntology(OntologyBean ontologyBean, String filePath)
			throws Exception {
		System.out.println("___Loading Ontology....... BEGIN : " + filePath);
		loadManagerProtege.loadOntology(new File(filePath).toURI(),
				ontologyBean);
		System.out.println("___Loading Ontology........ END : " + filePath);
	}

}
