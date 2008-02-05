package org.ncbo.stanford.manager.wrapper.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.wrapper.AbstractOntologyManagerWrapperProtege;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.database.CreateOWLDatabaseFromFileProjectPlugin;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

public class OntologyLoadManagerWrapperProtegeImpl extends
		AbstractOntologyManagerWrapperProtege {

	private static final Log log = LogFactory
			.getLog(OntologyLoadManagerWrapperProtegeImpl.class);

	//
	// Public static methods
	//

	/**
	 * Loads the specified ontology into the BioPortal repository. If the
	 * ontology is missing it's source file and identifier, an exception will be
	 * thrown.
	 * 
	 * @param ontology
	 *            the ontology to load.
	 * 
	 * @exception FileNotFoundException
	 *                the ontology file to be loaded was not found.
	 * @exception Exception
	 *                catch all for all other ontlogy file load errors.
	 */
	public void loadOntology(OntologyBean ontology)
			throws FileNotFoundException, Exception {
		File ontologyFile = new File(ontology.getFilePath());

		loadOntology(ontology.getId(), ontologyFile);
	}

	/**
	 * Loads the specified ontology into the BioPortal repository. If the
	 * specified ontology identifier already exists, overwrite the ontology with
	 * the new ontology file.
	 * 
	 * @param ontologyId
	 *            the ontology id for the specified ontology file.
	 * @param ontologyFile
	 *            the file containing the ontlogy to be loaded.
	 * 
	 * @exception FileNotFoundException
	 *                the ontology file to be loaded was not found.
	 * @exception Exception
	 *                catch all for all other ontlogy file load errors.
	 */
	public void loadOntology(Integer ontologyId, File ontologyFile)
			throws FileNotFoundException, Exception {

		if (ontologyFile == null) {
			log.error("Missing ontlogy file to load.");
			throw (new FileNotFoundException("Missing ontology file to load"));
		}

		// Setup ontology URI and table name
		URI ontologyURI = ontologyFile.toURI();
		String tableName = getTableName(ontologyId);

		if (log.isDebugEnabled()) {
			log.debug("Loading ontology file: "
					+ ontologyFile.getAbsoluteFile() + " size: "
					+ ontologyFile.length());
			log.debug("URI: " + ontologyURI.toString());
			log.debug("Ontology table name: " + tableName);
		}

		// If the ontology file is small, use the fast non-streaming Protege
		// load code.
		if (ontologyFile.length() < protegeBigFileThreshold) {
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

			DatabaseKnowledgeBaseFactory.setSources(sources, protegeJdbcDriver,
					protegeJdbcUrl, tableName, protegeJdbcUsername,
					protegeJdbcPassword);
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
			creator
					.setKnowledgeBaseFactory(new OWLDatabaseKnowledgeBaseFactory());
			creator.setDriver(protegeJdbcDriver);
			creator.setURL(protegeJdbcUrl);
			creator.setTable(tableName);
			creator.setUsername(protegeJdbcUsername);
			creator.setPassword(protegeJdbcPassword);

			System.out.println("path: " + ontologyURI.getPath());
			creator.setOntologyFileURI(ontologyURI);

			creator.setUseExistingSources(true);

			Project p = creator.createProject();
		}
	}
}
