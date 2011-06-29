package org.ncbo.stanford.manager.load.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.owlapi.preprocess.OWL2Preprocessor;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.load.OntologyLoadManager;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;
import org.ncbo.stanford.util.protege.ProtegeUtil;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.ApplicationProperties;
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
	protected OntologyMetadataManager ontologyMetadataManager;

	private static final Log log = LogFactory
			.getLog(OntologyLoadManagerProtegeImpl.class);

	private static final String OWL2_PROCESSED_FILENAME_SUFFIX = "_OWL2_processed";
	private static final String OWL2_REMOVED_FILENAME_SUFFIX = "_OWL2_removed";

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
	public void loadOntology(URI ontologyURI, OntologyBean ob) throws Exception {
		File ontologyFile = new File(ontologyURI.getPath());
		String filePath = ontologyURI.getPath();
		URI outputURI = null;

		if (ontologyFile == null) {
			log.error("Missing ontology file to load: " + filePath);
			throw new FileNotFoundException("Missing ontology file to load: "
					+ filePath);
		}

		if (log.isDebugEnabled()) {
			log.debug("Loading ontology file: " + ontologyFile.getName());
		}

		boolean isOwl = ob.getFormat()
				.contains(ApplicationConstants.FORMAT_OWL);

		if (!ontologyURI.toString().endsWith(".pprj") && isOwl) {
			File outputFile = stripOWL2Constructs(ontologyFile, ob);
			outputURI = outputFile.toURI();
		} else {
			outputURI = ontologyURI;
		}

		// If the ontology file is small, use the fast non-streaming Protege
		// load code.
		List errors = new ArrayList();
		Integer ontologyVersionId = ob.getId();

		// Clear knowledgebase cache for this item
		protegeKnowledgeBases.remove(ontologyVersionId);

		if (isOwl) {
			loadOWL(outputURI, ob, errors);
		} else {
			loadFrames(ontologyURI, ob, errors);
		}

		// If errors are found during the load, log the errors and throw an
		// exception.
		if (errors.size() > 0) {
			log.error(errors);
			throw new Exception("Error during loading "
					+ ontologyURI.toString() + ". Errors: " + errors.toString()
					+ ".");
		}
	}

	/**
	 * Removes OWL2 constructs from the ontology, so it can be parsed by Protege
	 * 
	 * @param ontologyFile
	 * @param ob
	 * @return
	 * @throws Exception
	 */
	private File stripOWL2Constructs(File ontologyFile, OntologyBean ob)
			throws Exception {
		String ontologyDir = AbstractFilePathHandler.getFullOntologyDirPath(ob);
		String[] splitFilename = AbstractFilePathHandler
				.splitFilename(ontologyFile.getName());
		String outputFilename = splitFilename[0]
				+ OWL2_PROCESSED_FILENAME_SUFFIX
				+ (StringHelper.isNullOrNullString(splitFilename[1]) ? "" : ".")
				+ splitFilename[1];
		String removalFilename = splitFilename[0]
				+ OWL2_REMOVED_FILENAME_SUFFIX
				+ (StringHelper.isNullOrNullString(splitFilename[1]) ? "" : ".")
				+ splitFilename[1];

		File outputFile = new File(ontologyDir + "/" + outputFilename);
		File removalFile = new File(ontologyDir + "/" + removalFilename);

		boolean removed = new OWL2Preprocessor(ontologyFile, outputFile,
				removalFile).run();

		return ((removed) ? outputFile : ontologyFile);
	}

	@SuppressWarnings("unchecked")
	private void loadOWL(URI ontologyUri, OntologyBean ob, Collection errors)
			throws Exception {
		Project dbProject;
		if (ontologyUri.toString().endsWith(".pprj")) {
			if (ProtegeUtil.isOWLProject(ontologyUri)) {
				// get the OWL file corresponding to this pprj file
				ontologyUri = ProtegeUtil.getOWLFileUri(ontologyUri);
			} else {
				log
						.error("Error at parsing "
								+ ob.getDisplayLabel()
								+ " ("
								+ ob.getId()
								+ "). Declared ontology format is OWL, but the content is not.");
				return;
			}
		}

		File ontologyFile = new File(ontologyUri);

		if (ontologyFile.length() < protegeBigFileThreshold) {
			dbProject = loadOWLStreaming(ontologyUri, ob, errors);
		} else {
			dbProject = loadOWLNonStreaming(ontologyUri, ob, errors);
		}
		if (setOntologyBeanVersion(ob, dbProject)) {
			ontologyMetadataManager.saveOntologyOrView(ob);
		}
		dbProject.dispose();
	}

	@SuppressWarnings("unchecked")
	private Project loadOWLStreaming(URI ontologyUri, OntologyBean ob,
			Collection errors) throws Exception {
		log.debug("Using streaming mode. OWL Ontology: " + ob.getDisplayLabel()
				+ " (" + ob.getId() + ")");
		Project dbProject = null;

		try {
			CreateOWLDatabaseFromFileProjectPlugin creator = new CreateOWLDatabaseFromFileProjectPlugin();
			creator
					.setKnowledgeBaseFactory(new OWLDatabaseKnowledgeBaseFactory());
			creator.setDriver(protegeJdbcDriver);
			creator.setURL(protegeJdbcUrl);
			creator.setTable(getTableName(ob.getId()));
			creator.setUsername(protegeJdbcUsername);
			creator.setPassword(protegeJdbcPassword);
			creator.setOntologyInputSource(ontologyUri);
			creator.setUseExistingSources(true);
			creator.setMergeImportMode(true);
			dbProject = creator.createProject();
			dbProject.save(errors);
			return dbProject;
		} catch (Throwable e) {
			e.printStackTrace();

			if (dbProject != null) {
				dbProject.dispose();
			}

			throw new Exception(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Project loadOWLNonStreaming(URI ontologyUri, OntologyBean ob,
			Collection errors) throws Exception {
		log.debug("Using non-streaming mode. OWL Ontology: "
				+ ob.getDisplayLabel() + " (" + ob.getId() + ")");

		Project dbProject = null;
		OWLDatabaseKnowledgeBaseFactory factory;

		try {
			// needed to merge all imports into one DB table
			ApplicationProperties.setBoolean(
					"protege.owl.parser.convert.file.merge.mode", true);
			String tableName = getTableName(ob.getId());

			OWLModel owlModel = ProtegeOWL
					.createJenaOWLModelFromURI(ontologyUri.toString());
			Project fileProject = owlModel.getProject();

			factory = new OWLDatabaseKnowledgeBaseFactory();
			PropertyList sources = PropertyList.create(fileProject
					.getInternalProjectKnowledgeBase());

			OWLDatabaseKnowledgeBaseFactory.setSources(sources,
					protegeJdbcDriver, protegeJdbcUrl, tableName,
					protegeJdbcUsername, protegeJdbcPassword);

			factory.saveKnowledgeBase(fileProject.getKnowledgeBase(), sources,
					errors);
			fileProject.dispose();

			dbProject = Project.createBuildProject(factory, errors);
			OWLDatabaseKnowledgeBaseFactory.setSources(dbProject.getSources(),
					protegeJdbcDriver, protegeJdbcUrl, tableName,
					protegeJdbcUsername, protegeJdbcPassword);
			dbProject.createDomainKnowledgeBase(factory, errors, true);
			return dbProject;
		} catch (Throwable e) {
			e.printStackTrace();

			if (dbProject != null) {
				dbProject.dispose();
			}

			throw new Exception(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadFrames(URI ontologyUri, OntologyBean ob, Collection errors) {
		log.debug("Parsing Frames ontology: " + ob.getDisplayLabel() + " ("
				+ ob.getId() + ")");

		Project dbProject = null;
		String tableName = getTableName(ob.getId());

		Project fileProject = Project.loadProjectFromURI(ontologyUri, errors);

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

		if (dbProject != null) {
			dbProject.dispose();
		}
	}

	public void cleanup(OntologyBean ontologyBean) throws Exception {
		Integer ontologyVersionId = ontologyBean.getId();

		if (ontologyVersionId == null) {
			throw new Exception(
					"cleanup method called with invalid ontologyBean: ontologyVersionId is null!");
		}

		String tableName = getTableName(ontologyVersionId);
		// Clear knowledgebase cache for this item
		protegeKnowledgeBases.remove(ontologyVersionId);

		Exception ex = null;
		Connection c = null;

		try {
			c = DriverManager.getConnection(protegeJdbcUrl,
					protegeJdbcUsername, protegeJdbcPassword);
			Statement stmt;
			stmt = c.createStatement();
			stmt.execute("DROP TABLE IF EXISTS " + tableName);
		} catch (Exception e) {
			// e.printStackTrace();
			ex = e;
		} finally {
			try {
				if (c != null && (!c.isClosed())) {
					c.close();
				}
			} catch (SQLException e) {
				// e.printStackTrace();
				ex = e;
			}

			if (ex != null) {
				throw ex;
			}
		}
	}

	private boolean setOntologyBeanVersion(OntologyBean ontologyBean,
			Project dbProject) {
		String version = "";
		boolean didSet = false;
		OWLModel owlModel_ = (OWLModel) dbProject.getKnowledgeBase();
		for (Iterator i = owlModel_.getDefaultOWLOntology().getVersionInfo()
				.iterator(); i.hasNext();) {
			String newVersion = i.next().toString().trim();
			if (StringUtils.isBlank(version)
					&& StringUtils.isNotBlank(newVersion)) {
				version = newVersion;
			}
		}

		// The ontologyBean doesn't have a version...try and get a version
		// number from the source
		if (StringUtils.isNotBlank(version)) {
			ontologyBean.setVersionNumber(version);
			didSet = true;
		} else if (StringUtils.isBlank(ontologyBean.getVersionNumber())) {
			ontologyBean.setVersionNumber(ApplicationConstants.UNKNOWN);
			didSet = true;
		}

		return didSet;
	}

	public OntologyMetadataManager getOntologyMetadataManager() {
		return ontologyMetadataManager;
	}

	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}
}
