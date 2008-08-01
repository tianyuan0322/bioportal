package org.ncbo.stanford.manager.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
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
	@SuppressWarnings("unchecked")
	public void loadOntology(URI ontologyUri, OntologyBean ontology)
			throws Exception {

		File ontologyFile = new File(ontologyUri.getPath());
		String filePath = ontologyUri.getPath();

		if (ontologyFile == null) {
			log.error("Missing ontology file to load: " + filePath);
			throw new FileNotFoundException("Missing ontology file to load: "
					+ filePath);
		}

		log.debug("Loading ontology file: " + ontologyFile.getName());

		// If the ontology file is small, use the fast non-streaming Protege
		// load code.
		List errors = new ArrayList();
		String tableName = getTableName(ontology.getId());
		
		if (ontology.getFormat().contains("OWL")) {
			if (ontologyFile.length() < protegeBigFileThreshold) {
				OWLModel owlModel = ProtegeOWL
						.createJenaOWLModelFromInputStream(new FileInputStream(
								ontologyFile));

				PropertyList sources = PropertyList.create(owlModel
						.getProject().getInternalProjectKnowledgeBase());

				
				DatabaseKnowledgeBaseFactory.setSources(sources,
						protegeJdbcDriver, protegeJdbcUrl, tableName,
						protegeJdbcUsername, protegeJdbcPassword);

				OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
				factory.saveKnowledgeBase(owlModel, sources, errors);

				// save memory
				owlModel.dispose();
				
				// If errors are found during the load, log the errors and throw an
				// exception.
				if (errors.size() > 0) {
					log.error(errors);
					throw new Exception("Error during loading "
							+ ontologyUri.toString());
				}
			} else {
				// If the ontology file is big, use the streaming Protege load
				// approach.
				CreateOWLDatabaseFromFileProjectPlugin creator = new CreateOWLDatabaseFromFileProjectPlugin();
				creator
						.setKnowledgeBaseFactory(new OWLDatabaseKnowledgeBaseFactory());
				creator.setDriver(protegeJdbcDriver);
				creator.setURL(protegeJdbcUrl);
				creator.setTable(tableName);
				creator.setUsername(protegeJdbcUsername);
				creator.setPassword(protegeJdbcPassword);
				creator.setOntologyInputSource(ontologyUri);
				creator.setUseExistingSources(true);
				Project p = creator.createProject(); 
			    p.save(errors);
			    
			    // save memory
			    p.dispose();
			}
		} else {


			Project fileProject = Project.loadProjectFromFile(filePath, errors);
			DatabaseKnowledgeBaseFactory factory = new DatabaseKnowledgeBaseFactory();
			PropertyList sources = PropertyList.create(fileProject
					.getInternalProjectKnowledgeBase());
			DatabaseKnowledgeBaseFactory.setSources(sources, protegeJdbcDriver,
					protegeJdbcUrl, tableName, protegeJdbcUsername,
					protegeJdbcPassword);

			factory.saveKnowledgeBase(fileProject.getKnowledgeBase(), sources,
					errors);

			fileProject.dispose();

			Project dbProject = Project.createNewProject(factory, errors);
			DatabaseKnowledgeBaseFactory.setSources(dbProject.getSources(),
					protegeJdbcDriver, protegeJdbcUrl, tableName,
					protegeJdbcUsername, protegeJdbcPassword);

			dbProject.createDomainKnowledgeBase(factory, errors, true);

			dbProject.save(errors);
			dbProject.dispose();
		}

	}
}
