package org.ncbo.stanford.manager.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.OntologyLoadManager;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.database.CreateOWLDatabaseFromFileProjectPlugin;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * Provides the functionality to load an ontology into the Protege back-end
 * 
 * @author Michael Dorf
 * 
 */
public class OntologyLoadManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologyLoadManager {

	private static final Log log = LogFactory
			.getLog(OntologyLoadManagerProtegeImpl.class);

	//
	// Public static methods
	//

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
	public void loadOntology(URI ontologyUri, OntologyBean ontology)
			throws Exception {
		File ontologyFile = new File(ontologyUri.getPath());

		if (ontologyFile == null) {
			log.error("Missing ontology file to load.");
			throw new FileNotFoundException("Missing ontology file to load");
		}

		String tableName = getTableName(ontology.getId());

		if (log.isDebugEnabled()) {
			log.debug("Loading ontology file: "
					+ ontologyFile.getAbsoluteFile() + " size: "
					+ ontologyFile.length());
			log.debug("URI: " + ontologyUri.toString());
			log.debug("Ontology table name: " + tableName);
			log.debug("JDBC name: " + protegeJdbcDriver + " url: "
					+ protegeJdbcUrl);
		}

		// If the ontology file is small, use the fast non-streaming Protege
		// load code.
		if (ontologyFile.length() < protegeBigFileThreshold) {
			if (log.isDebugEnabled())
				log.debug("Loading small ontology file: "
						+ ontologyFile.getName());

			OWLModel fileModel = ProtegeOWL
					.createJenaOWLModelFromInputStream(new FileInputStream(
							ontologyFile));

			List errors = new ArrayList();
			Project fileProject = fileModel.getProject();
			OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
			PropertyList sources = PropertyList.create(fileProject
					.getInternalProjectKnowledgeBase());

			DatabaseKnowledgeBaseFactory.setSources(sources, protegeJdbcDriver,
					protegeJdbcUrl, tableName, protegeJdbcUsername,
					protegeJdbcPassword);
			factory.saveKnowledgeBase(fileModel, sources, errors);

			// If errors are found during the load, log the errors and throw an
			// exception.
			if (errors.size() > 0) {
				logErrors(errors);
				throw new Exception("Error during load of: "
						+ ontologyUri.toString());
			}
		} else {
			// If the ontology file is big, use the streaming Protege load
			// approach.
			if (log.isDebugEnabled()) {
				log.debug("Loading big ontology file: "
						+ ontologyUri.toString());
			}

			CreateOWLDatabaseFromFileProjectPlugin creator = new CreateOWLDatabaseFromFileProjectPlugin();
			creator
					.setKnowledgeBaseFactory(new OWLDatabaseKnowledgeBaseFactory());
			creator.setDriver(protegeJdbcDriver);
			creator.setURL(protegeJdbcUrl);
			creator.setTable(tableName);
			creator.setUsername(protegeJdbcUsername);
			creator.setPassword(protegeJdbcPassword);
			creator.setOntologyFileURI(ontologyUri);
			creator.setUseExistingSources(true);

			Project p = creator.createProject();
		}
	}

	//
	// Private methods
	//
	/**
	 * Outputs text representations of the specified List.
	 */
	private void logErrors(List list) {
		int i = 0;
		for (Iterator it = list.iterator(); it.hasNext();) {
			log.error(++i + ") " + it.next());
		}
	}
}
