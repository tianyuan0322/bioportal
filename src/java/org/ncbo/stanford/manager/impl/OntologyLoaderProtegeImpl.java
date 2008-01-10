package org.ncbo.stanford.manager.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.OntologyLoader;
import org.ncbo.stanford.service.ontology.impl.OntologyServiceImpl;

import edu.stanford.smi.protegex.owl.database.CreateOWLDatabaseFromFileProjectPlugin;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protege.storage.database.*;

import java.net.*;
import java.util.ArrayList;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protege.util.PropertyList; //import edu.stanford.smi.protege.util.URIUtilities;
//import edu.stanford.smi.protegex.owl.model.ProtegeCls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * OntologyLoaderProtegeImpl is a class that loads ontologies into a Protege
 * repository. It is an implementation of the OntologyLoader interface. Thus,
 * this class should never be directly invoked by the BioPortal service layer.
 * 
 * @author Benjamin Dai
 * 
 */
public class OntologyLoaderProtegeImpl implements OntologyLoader {

	private static final Log log = LogFactory
			.getLog(OntologyLoaderProtegeImpl.class);

	private final static int BIG_PROTEGE_FILE = 500000; // Threshold in bytes
														// indicating
	// a big ontology file.
	// This should be in a
	// configuration file.
	private final static String SPACE = " ";
	private final static String TABLE_SUFFIX = "_table";

	// Hard-coded! Sample data for development - TODO - refactor to
	// configuration file and appropriate settings
	private final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private final static String DB_URL = "jdbc:mysql://localhost/protege";

	private final static String PROTEGE_USER = "protege_user";
	private final static String PROTEGE_PASSWORD = "protege_user$123";

	/**
	 * Loads the specified ontology into the BioPortal repository.  If the ontology is missing it's source file and identifier, an exception 
	 * will be thrown.
	 * 
	 * @param ontology	the ontology to load.
	 * 
	 * @exception	FileNotFoundException	the ontology file to be loaded was not found.
	 * @exception	Exception catch all for all other ontlogy file load errors.
	 */
	public void loadOntology(OntologyBean ontology) throws FileNotFoundException, Exception {
		File ontologyFile = new File(ontology.getFilePath());
		
		loadOntology(ontology.getId(), ontologyFile);
	}
	
	/**
	 * Loads the specified ontology into the BioPortal repository. If the
	 * specified ontology identifier already exists, overwrite the ontology with
	 * the new ontology file.
	 * 
	 * @param ontologyID
	 *            the ontology id for the specified ontology file.
	 * @param ontologyFile
	 *            the file containing the ontlogy to be loaded.
	 * 
	 * @exception FileNotFoundException
	 *                the ontology file to be loaded was not found.
	 * @exception Exception
	 *                catch all for all other ontlogy file load errors.
	 */
	public void loadOntology(Integer ontologyID, File ontologyFile)
			throws FileNotFoundException, Exception {

		if (ontologyFile == null) {
			log.error("Missing ontlogy file to load.");
			throw (new FileNotFoundException("Missing ontology file to load"));
		}

		// Setup ontology URI and table name
		URI ontologyURI = ontologyFile.toURI();
		String tableName = Integer.toString(ontologyID) + TABLE_SUFFIX;

		if (log.isDebugEnabled()) {
			log.debug("Loading ontology file: "
					+ ontologyFile.getAbsoluteFile() + " size: "
					+ ontologyFile.length());
			log.debug("URI: " + ontologyURI.toString());
			log.debug("Ontology table name: " + tableName);
		}

		// If the ontology file is small, use the fast non-streaming Protege
		// load code.
		if (ontologyFile.length() < BIG_PROTEGE_FILE) {
			if (log.isDebugEnabled())
				log.debug("Loading small ontology file: "
						+ ontologyFile.getName());

			OWLModel fileModel = ProtegeOWL
					.createJenaOWLModelFromURI(ontologyURI.toString());

			List errors = new ArrayList();
			Project fileProject = fileModel.getProject();
			OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
			PropertyList sources = PropertyList.create(fileProject
					.getInternalProjectKnowledgeBase());

			DatabaseKnowledgeBaseFactory.setSources(sources, JDBC_DRIVER,
					DB_URL, tableName, PROTEGE_USER, PROTEGE_PASSWORD);
			factory.saveKnowledgeBase(fileModel, sources, errors);

			// next lines of code are not needed - just testing the import
			OWLModel om = (OWLModel) fileProject.getKnowledgeBase();

			OWLNamedClass country = om.getOWLNamedClass("Country");
			for (Object o : country.getInstances(true)) {
				System.out.println("" + o + " is an instance of Country");
			}

		} else {
			// If the ontology file is big, use the streaming Protege load
			// approach.

			if (log.isDebugEnabled())
				log.debug("Loading big ontology file: "
						+ ontologyFile.getName());

			CreateOWLDatabaseFromFileProjectPlugin creator = new CreateOWLDatabaseFromFileProjectPlugin();
			creator.setKnowledgeBaseFactory(new OWLDatabaseKnowledgeBaseFactory());
			creator.setDriver(JDBC_DRIVER);
			creator.setURL(DB_URL);
			creator.setTable(tableName);
			creator.setUsername(PROTEGE_USER);
			creator.setPassword(PROTEGE_PASSWORD);

			System.out.println("path: " + ontologyURI.getPath());
			creator.setOntologyFileURI(ontologyURI);

			creator.setUseExistingSources(true);

			Project p = creator.createProject();

		}
	}
}
