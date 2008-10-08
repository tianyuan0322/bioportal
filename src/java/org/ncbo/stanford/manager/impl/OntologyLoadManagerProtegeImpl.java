package org.ncbo.stanford.manager.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.OntologyLoadManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.query.api.QueryApi;
import edu.stanford.smi.protege.query.api.QueryConfiguration;
import edu.stanford.smi.protege.query.indexer.IndexMechanism;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.database.CreateOWLDatabaseFromFileProjectPlugin;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;

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
	public void loadOntology(URI ontologyUri, OntologyBean ob) throws Exception {
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
		Integer ontologyId = ob.getId();
		String tableName = getTableName(ontologyId);

		// Clear knowledgebase cache for this item
		protegeKnowledgeBases.remove(ontologyId);

		Project dbProject = null;

		if (ob.getFormat().contains(ApplicationConstants.FORMAT_OWL)) {
			if (ontologyFile.length() < protegeBigFileThreshold) {
				log.debug("Using non-streaming mode. Ontology: "
						+ ob.getDisplayLabel() + " (" + ob.getId() + ")");

				ApplicationProperties.setBoolean(
						"protege.owl.parser.convert.file.merge.mode", true);

				OWLModel owlModel = ProtegeOWL
						.createJenaOWLModelFromURI(ontologyUri.toString());
				Project fileProject = owlModel.getProject();

				OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
				PropertyList sources = PropertyList.create(fileProject
						.getInternalProjectKnowledgeBase());

				OWLDatabaseKnowledgeBaseFactory.setSources(sources,
						protegeJdbcDriver, protegeJdbcUrl, tableName,
						protegeJdbcUsername, protegeJdbcPassword);

				factory.saveKnowledgeBase(fileProject.getKnowledgeBase(),
						sources, errors);
				fileProject.dispose();

				dbProject = Project.createBuildProject(factory, errors);
				OWLDatabaseKnowledgeBaseFactory.setSources(dbProject
						.getSources(), protegeJdbcDriver, protegeJdbcUrl,
						tableName, protegeJdbcUsername, protegeJdbcPassword);

				dbProject.createDomainKnowledgeBase(factory, errors, true);

				try {
					FactoryUtils.writeOntologyAndPrefixInfo(
							(OWLModel) dbProject.getKnowledgeBase(), errors);
				} catch (AlreadyImportedException e) {
					e.printStackTrace();
					log
							.error(
									"Error at loadOntology: Already Imported Exception",
									e);
				}
			} else {
				// If the ontology file is big, use the streaming Protege load
				// approach.
				log.debug("Using streaming mode. Ontology: "
						+ ob.getDisplayLabel() + " (" + ob.getId() + ")");
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
				creator.setMergeImportMode(true);
				dbProject = creator.createProject();
				dbProject.save(errors);
			}
		} else {
			// PROTEGE .pprj format
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

			dbProject = Project.createNewProject(factory, errors);
			DatabaseKnowledgeBaseFactory.setSources(dbProject.getSources(),
					protegeJdbcDriver, protegeJdbcUrl, tableName,
					protegeJdbcUsername, protegeJdbcPassword);
			dbProject.createDomainKnowledgeBase(factory, errors, true);
			dbProject.save(errors);
		}

		// If errors are found during the load, log the errors and throw an
		// exception.
		if (errors.size() > 0) {
			log.error(errors);
			throw new Exception("Error during loading "
					+ ontologyUri.toString());
		}

		if (dbProject != null) {
			createIndex(dbProject.getKnowledgeBase(), ob);
			dbProject.dispose();
		}
	}

	/**
	 * Creates a Lucene index for a given ontology version. The ontology record
	 * must exist.
	 * 
	 * @param ob -
	 *            populated ontology bean.
	 */
	@SuppressWarnings("unchecked")
	public void indexOntology(OntologyBean ob) {
		createIndex(getKnowledgeBase(ob), ob);
	}

	private void createIndex(KnowledgeBase kb, OntologyBean ob) {
		QueryConfiguration config = new QueryConfiguration(kb);
		config.setBaseIndexPath(getIndexPath(ob));
		Set<Slot> searchableSlots = config.getSearchableSlots();

		try { // this should unneccessary but it doesn't hurt.
			Slot synonymSlot = getSynonymSlot(kb, ob.getSynonymSlot());

			if (synonymSlot == null) {
				searchableSlots.add(synonymSlot);
			}

			searchableSlots.add(getPreferredNameSlot(kb, ob
					.getPreferredNameSlot()));
			config.setSearchableSlots(searchableSlots);
		} catch (Throwable t) {
		}

		config.setIndexers(EnumSet.of(IndexMechanism.STANDARD_INDICIES));

		QueryApi api = new QueryApi(kb);
		installLuceneIndexMechanism(api, ob);
		api.index(config);
	}
}
