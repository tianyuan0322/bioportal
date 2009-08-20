package org.ncbo.stanford.manager.diff.impl;

/**
 * Provides the functionality to compare two Protege ontologies
 * 
 * @author Natasha Noy
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.diff.OntologyDiffManager;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.util.diff.DiffUtils;
import org.ncbo.stanford.util.difffile.pathhandler.DiffFilePathHandler;
import org.ncbo.stanford.util.difffile.pathhandler.impl.DiffFilePathHandlerImpl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.prompt.promptDiff.PromptDiff;
import edu.stanford.smi.protegex.prompt.promptDiff.structures.ResultTable;

public class OntologyDiffManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologyDiffManager {

	public static final String FORMAT_RDF = "rdf";
	public static final String FORMAT_TXT = "txt";
	public static final String FORMAT_DEFAULT = "txt";

	private static final Log log = LogFactory
			.getLog(OntologyDiffManagerProtegeImpl.class);

	private OntologyMetadataManager ontologyMetadataManagerProtege;

	/**
	 * Creates a diff between two ontology versions. Calls Prompt to do that and
	 * saves the diff in a file
	 * 
	 * @param ontologyVersionOld,
	 *            ontologyVersionNew
	 * 
	 * @throws Exception
	 */
	public void createDiff(OntologyBean ontologyVersionOld,
			OntologyBean ontologyVersionNew) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("In create diff for " + ontologyVersionOld.getId()
					+ " and " + ontologyVersionNew.getId());
		}

		KnowledgeBase oldKb = getKnowledgeBase(ontologyVersionOld);
		KnowledgeBase newKb = getKnowledgeBase(ontologyVersionNew);

		PromptDiff promptDiff = new PromptDiff();
		promptDiff.runDiff(oldKb, newKb);

		saveDiffToFiles(ontologyVersionOld, ontologyVersionNew, promptDiff
				.getResultsTable());
	}

	/**
	 * Creates a diff between the ontology that has just been loaded and its
	 * previous version(if one exists in BioPortal). Calls Prompt to do that and
	 * saves the diff in a file
	 * 
	 * @param ontologyBean
	 * 
	 * @throws Exception
	 */

	public void createDiffForTwoLatestVersions(Integer ontologyId)
			throws Exception {
		List<OntologyBean> allVersions = ontologyMetadataManagerProtege
				.findAllOntologyOrViewVersionsById(ontologyId);

		// get a list of version ids, filtering out ontologies that are not
		// active
		// (want to compare only active ontologies
		List<Integer> versionIds = new ArrayList<Integer>(allVersions.size());

		for (OntologyBean ontologyVersion : allVersions) {
			if (ontologyVersion.getStatusId().equals(
					StatusEnum.STATUS_READY.getStatus())) {
				versionIds.add(ontologyVersion.getId());
			}
		}

		// there are fewer than two active versions -- nothing to compare
		if (versionIds.size() < 2)
			return;

		Collections.sort(versionIds);

		Integer newVersionId = versionIds.get(versionIds.size() - 1); // latest
		// version
		Integer oldVersionId = versionIds.get(versionIds.size() - 2); // previous
		// version

		OntologyBean newVersion = ontologyMetadataManagerProtege
				.findOntologyOrViewVersionById(newVersionId);
		OntologyBean oldVersion = ontologyMetadataManagerProtege
				.findOntologyOrViewVersionById(oldVersionId);

		if (newVersion == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + newVersionId + ")");
		}

		if (oldVersion == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + oldVersionId + ")");
		}

		createDiff(oldVersion, newVersion);
	}

	/**
	 * Returns a list of all diffs that are available for a given ontology in
	 * the following format: <versionId1, versionId2>, <versionId2,
	 * versionId3>...
	 * 
	 * @param onotlogyId
	 */
	public List<ArrayList<String>> getAllDiffsForOntology(Integer ontologyId) {
		DiffFilePathHandler diffPathFileHandler = new DiffFilePathHandlerImpl();

		String[] diffList = diffPathFileHandler
				.getListOfDiffDirsForOntologyId(ontologyId);

		if (diffList == null)
			return null;

		List<ArrayList<String>> diffsForOntology = new ArrayList<ArrayList<String>>(
				diffList.length);

		for (int i = 0; i < diffList.length; i++) {
			diffsForOntology
					.add(getVerisonIdArrayFromDirectoryName(diffList[i]));
		}

		return diffsForOntology;
	}

	/**
	 * Returns the file for the diff between two ontology versions in th
	 * specified format (in any order)
	 * 
	 * @param ontologyVersionId1,
	 *            ontologyVersionId2
	 * @throws FileNotFoundException
	 */
	public File getDiffFileForOntologyVersions(Integer ontologyVersionId1,
			Integer ontologyVersionId2, String format)
			throws FileNotFoundException, Exception {
		return getDiffFileForOntologyVersionsInFormat(ontologyVersionId1,
				ontologyVersionId2, format);
	}

	// Non-interface methods

	/**
	 * Converts a directory name of the form versionId1_versionId2 to an array
	 * <versionId1, versionId2>
	 * 
	 * @param dirName
	 * @return
	 */
	private ArrayList<String> getVerisonIdArrayFromDirectoryName(String dirName) {
		DiffFilePathHandler diffPathFileHandler = new DiffFilePathHandlerImpl();
		ArrayList<String> versionIdArray = diffPathFileHandler
				.getVerisonIdArrayFromDirectoryName(dirName);

		return versionIdArray;
	}

	/**
	 * Saves the ResultTable in different file formats
	 */
	private void saveDiffToFiles(OntologyBean ontologyVersionOld,
			OntologyBean ontologyVersionNew, ResultTable resultsTable)
			throws Exception {
		String diffFileName = getDiffFileName(ontologyVersionOld
				.getOntologyId(), ontologyVersionOld.getId(),
				ontologyVersionNew.getId(), true);

		saveToRDFFile(resultsTable, diffFileName, ontologyVersionOld.getUrn(),
				ontologyVersionNew.getUrn());
		saveToTabDelimitedTextFile(resultsTable, diffFileName);
	}

	/**
	 * Saves the ResultTable in tab-delimited format
	 */
	private void saveToTabDelimitedTextFile(ResultTable resultsTable,
			String diffFileName) {
		DiffUtils.saveToTextFile(resultsTable, diffFileName
				+ getFileExtensionFromFormat(FORMAT_TXT), true);
		// resultsTable.saveToFile(diffFileName + getFileExtensionFromFormat
		// (FORMAT_TXT), true, true, true, true, false, false, false, true, 0);
	}

	/**
	 * Saves the ResultTable in RDF format
	 */
	private void saveToRDFFile(ResultTable resultsTable, String diffFileName,
			String oldNameSpace, String newNameSpace) throws Exception {
		// resultsTable.saveToRDF(diffFileName
		// + getFileExtensionFromFormat(FORMAT_RDF),
		// new URL(oldNameSpace), new URL(newNameSpace));
		DiffUtils.saveToRDFFile(resultsTable, diffFileName);
	}

	// The methods for file and directory management
	// When we use a DAO or other ways to handle metadata, these methods will be
	// replaced with the
	// ones that get information from there.
	// This set of methods determines if a diff between two files exists based
	// on their ids.
	// It creates appropriate directory structure when necessary as well.

	/**
	 * Returns a file with the diff for these two versions Throws a
	 * FileNotFoundExtension if the diff file does not exists
	 */
	private File getDiffFileForOntologyVersionsInFormat(
			Integer ontologyVersionId1, Integer ontologyVersionId2,
			String format) throws FileNotFoundException, Exception {

		String diffFileName = getDiffFileName(ontologyVersionId1,
				ontologyVersionId2);

		if (diffFileName == null) {
			log.error("Missing diff file to download.");
			throw new FileNotFoundException("Missing diff file to load");
		}

		if (format == null)
			format = FORMAT_DEFAULT;
		String fullFileName = diffFileName + getFileExtensionFromFormat(format);
		File file = new File(fullFileName);

		if (file == null) {
			log.error("Missing diff file to download.");
			throw new FileNotFoundException("Missing diff file to load");
		}

		return file;
	}

	/**
	 * Returns the file name without the extension if the diff for these two
	 * versions exists. We assume that the diff exists if the directory for it
	 * exists. Tries two permutations of the ontology ids, v1-v2 and v2-v1
	 * 
	 * @param ontologyVersionId1
	 * @param ontologyVersionId2
	 * @return
	 * @throws FileNotFoundException
	 */
	private String getDiffFileName(Integer ontologyVersionId1,
			Integer ontologyVersionId2) throws FileNotFoundException, Exception {
		OntologyBean ontologyVersion1 = ontologyMetadataManagerProtege
				.findOntologyOrViewVersionById(ontologyVersionId1);
		OntologyBean ontologyVersion2 = ontologyMetadataManagerProtege
				.findOntologyOrViewVersionById(ontologyVersionId2);

		if (ontologyVersion1 == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersion1 + ")");
		}

		if (ontologyVersion2 == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + ontologyVersion2 + ")");
		}

		Integer ontologyId1 = ontologyVersion1.getOntologyId();
		Integer ontologyId2 = ontologyVersion2.getOntologyId();

		if (!ontologyId1.equals(ontologyId2)) {
			log.error("Ontology version ids are from different ontologies.");
			throw new Exception(
					"Ontology version ids are from different ontologies");
		}

		Integer ontologyId = ontologyId1;

		String diffFileName = getDiffFileName(ontologyId, ontologyVersionId1,
				ontologyVersionId2, false);

		if (diffFileName == null) {
			// try the different order
			diffFileName = getDiffFileName(ontologyId, ontologyVersionId2,
					ontologyVersionId1, false);
		}

		if (diffFileName == null) {
			log.error("Missing diff file to download.");
			throw new FileNotFoundException("Missing ontology file to load");
		}

		return diffFileName;
	}

	/**
	 * Will return the file name for the diff (without the extension) Returns
	 * null if the directory for that diff does not exist
	 * 
	 * @param ontologyId
	 * @param ontologyVersionId1
	 * @param ontologyVersionId2
	 * @param createDir
	 * @return
	 */

	private String getDiffFileName(Integer ontologyId,
			Integer ontologyVersionId1, Integer ontologyVersionId2,
			boolean createDir) {
		DiffFilePathHandler diffPathFileHandler = new DiffFilePathHandlerImpl();
		String diffDirName = diffPathFileHandler
				.getDiffDirNameForOntologyVersions(ontologyId,
						ontologyVersionId1, ontologyVersionId2, createDir);

		if (createDir) // the directory was created in getDiffDirName if it
			// didn't exist
			return diffPathFileHandler.getFileName(diffDirName,
					ontologyVersionId1, ontologyVersionId2);

		// did not want to create a directory (createDir == null)
		if (diffDirName == null) // the directory does not exist
			return null;

		return diffPathFileHandler.getFileName(diffDirName, ontologyVersionId1,
				ontologyVersionId2);
	}

	private String getFileExtensionFromFormat(String format) {
		return "." + format;
	}

	public boolean diffExists(OntologyBean ontologyVersionOld,
			OntologyBean ontologyVersionNew) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/**
	 * @param ontologyMetadataManagerProtege
	 *            the ontologyMetadataManagerProtege to set
	 */
	public void setOntologyMetadataManagerProtege(
			OntologyMetadataManager ontologyMetadataManagerProtege) {
		this.ontologyMetadataManagerProtege = ontologyMetadataManagerProtege;
	}
}
