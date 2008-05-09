package org.ncbo.stanford.manager.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.wrapper.impl.OntologyLoadManagerWrapperProtegeImpl;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.filehandler.FileHandler;
import org.ncbo.stanford.util.filehandler.impl.PhysicalDirectoryFileHandler;

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
 * OntologyLoadManagerWrapperProtegeImpl
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
	private final static String TEST_OWL_URI = "/apps/bmir.apps/bioportal_resources/files/pizza.owl";

	// Pizza sample data
	private final static String TABLE_NONSTREAM = "pizza_table_nonstream2";

	private final static String SOURCE_OWL_URI = "file:///c:/pizza.owl";
	private final static String ERRORS_URI = "file:///c:/pizza_errors.txt";

	// OMV sample data
	private final static String SOURCE_OMVOWL_URI = "file:///c:/OMV_v2.3.owl";
	private final static String TABLE_OMV_NONSTREAM = "omv_table_nonstream2";

	//
	// Non-Streaming Fast load
public void testNoStreamPizzaLoad() {
		System.out.println("Starting testNoStreamPizzaLoad");

		OntologyLoadManagerWrapperProtegeImpl loadManagerProtege = (OntologyLoadManagerWrapperProtegeImpl) applicationContext
				.getBean("ontologyLoadManagerWrapperProtege",
						OntologyLoadManagerWrapperProtegeImpl.class);

		loadManagerProtege.setProtegeBigFileThreshold(TEST_NOT_STREAM_SIZE);

		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.setId(TEST_NOT_STREAM_ID);

		try {
			loadManagerProtege
					.loadOntology(new URI(TEST_OWL_URI), ontologyBean);
		} catch (Exception exc) {
			System.out.println("Exception: " + exc.getMessage());
			exc.printStackTrace();
			fail("Pizza load failed: " + exc.getMessage());
		}
	}


	// Streaming load of Pizza Ontology - Much slower than the non-streaming

	public void StreamPizzaLoad() {
		System.out.println("Starting testStreamPizzaLoad");

		OntologyLoadManagerWrapperProtegeImpl loadManagerProtege = (OntologyLoadManagerWrapperProtegeImpl) applicationContext
				.getBean("ontologyLoadManagerWrapperProtege",
						OntologyLoadManagerWrapperProtegeImpl.class);

		loadManagerProtege.setProtegeBigFileThreshold(TEST_STREAM_SIZE);

		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.setId(TEST_STREAM_ID);

		try {
			loadManagerProtege
					.loadOntology(new URI(TEST_OWL_URI), ontologyBean);
		} catch (Exception exc) {
			System.out.println("Exception: " + exc.getMessage());
			exc.printStackTrace();
			fail("Pizza load failed: " + exc.getMessage());
		}
	}

	// Testing Protege api with ontology queries to verify validity.
	public void BasicPizzaNonStreamingLoad() {
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

		} catch (URISyntaxException exc) {
			fail("Invalid ontology file URI: " + SOURCE_OWL_URI);
		} catch (Exception exc) {
			exc.printStackTrace();
			fail("General exception: " + exc.getMessage());
		}
	}

	// Fast load of medium complexity ontology OMV.
	public void MediumNonStreamingLoad() {
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

			/*
			 * Project dbProject = Project.createNewProject(factory, errors);
			 * DatabaseKnowledgeBaseFactory.setSources(dbProject.getSources(),
			 * JDBC_DRIVER, DB_URL, TABLE_OMV_NONSTREAM, PROTEGE_USER,
			 * PROTEGE_PASSWORD);
			 * 
			 * dbProject.createDomainKnowledgeBase(factory, errors, true);
			 * dbProject.setProjectURI(URIUtilities.createURI(ERRORS_OMV_URI));
			 * dbProject.save(errors);
			 * 
			 * displayErrors(errors); //optional // return (OWLModel)
			 * dbProject.getKnowledgeBase();
			 */

		} catch (URISyntaxException exc) {
			fail("Invalid ontology file URI: " + SOURCE_OMVOWL_URI);
		} catch (Exception exc) {
			exc.printStackTrace();
			fail("General exception: " + exc.getMessage());
		}
	}

	/*
	 * // Fast load of medium complexity ontology FMA public void
	 * testComplexNonStreamingLoad(){ try { OWLModel fileModel =
	 * ProtegeOWL.createJenaOWLModelFromURI(SOURCE_FMAOWL_URI);
	 * 
	 * List errors = new ArrayList(); Project fileProject =
	 * fileModel.getProject(); OWLDatabaseKnowledgeBaseFactory factory = new
	 * OWLDatabaseKnowledgeBaseFactory(); PropertyList sources =
	 * PropertyList.create(fileProject.getInternalProjectKnowledgeBase());
	 * 
	 * DatabaseKnowledgeBaseFactory.setSources(sources, JDBC_DRIVER, DB_URL,
	 * TABLE_FMA_NONSTREAM, PROTEGE_USER, PROTEGE_PASSWORD);
	 * factory.saveKnowledgeBase(fileModel, sources, errors); // Optional error
	 * dump of load displayErrors(errors); //optional if (!errors.isEmpty()) {
	 * fail("There were errors: " + errors.size()); return; } } catch
	 * (URISyntaxException exc) { fail("Invalid ontology file URI: " +
	 * SOURCE_FMAOWL_URI); } catch (Exception exc) { exc.printStackTrace();
	 * fail("General exception: " + exc.getMessage()); } }
	 */
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
}
