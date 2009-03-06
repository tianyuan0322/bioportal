
package org.ncbo.stanford.util.difffile.pathhandler.impl;

import java.io.*;
import java.util.*;

import org.ncbo.stanford.util.*;
import org.ncbo.stanford.util.difffile.pathhandler.*;

/**
 * 
 * @author Natasha Noy
 *
 */

public class DiffFilePathHandlerImpl implements DiffFilePathHandler {
	
	public class DiffListFilter implements FilenameFilter {

		public boolean accept(File dir, String fileName) {
			return !(fileName.startsWith("."));
		}

	}

	private static final String SEPARATOR_IN_DIR_NAME = "_";
	private static final String SEPARATOR_IN_FILE_NAME = "_";

	/**
	 * Returns a directory path for the directory that has all the diffs for the given ontology
	 * If the directory does not exist, returns null
	 * 
	 * @param ontologyId
	 * @return
	 */
	public String [] getListOfDiffDirsForOntologyId(Integer ontologyId) {
		String dirPath = getDirPathForOntology (ontologyId);
		File diffDirectory = new File (dirPath);
		
		if (diffDirectory == null) return null;
		
		String [] diffList = diffDirectory.list(new DiffListFilter ());
		
		return diffList;
	}

	/**
	 * Checks if a directory for the diffs between two versions exists. If createDir is true, 
	 * creates the directory; if it is false, returns null if directory does not exist, directory 
	 * name otherwise
	 * 
	 * @param ontologyId
	 * @param ontologyVersionIdOld
	 * @param ontologyVersionIdNew
	 * @param createDir
	 * @return
	 */
	public String getDiffDirNameForOntologyVersions(Integer ontologyId,
			Integer ontologyVersionIdOld, Integer ontologyVersionIdNew,
			boolean createDir) {
		String dirPath = getDirPathForOntology (ontologyId) + "/" + 
						ontologyVersionIdOld + SEPARATOR_IN_DIR_NAME + ontologyVersionIdNew;

		File diffDirectory = new File (dirPath);
		
		if (createDir) 
			diffDirectory.mkdirs();
		else // we do not want to create a diff directory, we were trying to get the diff file
			if (!diffDirectory.exists()) {
				return null;
			}

		return dirPath;
	}

	/**
	 * Creates a file name given a directory id and the two version ids.
	 * Does not add the extension to the file
	 * 
	 * @param diffDirName
	 * @param ontologyVersionId1
	 * @param ontologyVersionId2
	 * @return
	 */
	
	public String getFileName(String diffDirName, Integer ontologyVersionId1,
			Integer ontologyVersionId2) {
		return diffDirName + "/" + ontologyVersionId1 + SEPARATOR_IN_FILE_NAME + ontologyVersionId2;
	}

	private String getDirPathForOntology(Integer ontologyId) {
		return MessageUtils.getMessage("bioportal.diff.filepath") + "/" + ontologyId;
	}

	/**
	 * Converts a directory name of the form versionId1_versionId2 to an array <versionId1, versionId2>
	 * 
	 * @param dirName
	 * @return
	 */
	public ArrayList<String> getVerisonIdArrayFromDirectoryName(String dirName) {
		ArrayList<String> versionIdArray = new ArrayList<String> (2);
		int separatorIndex = dirName.indexOf(SEPARATOR_IN_DIR_NAME);
		versionIdArray.add(dirName.substring(0, separatorIndex));
		versionIdArray.add(dirName.substring(separatorIndex + 1, dirName.length()));
		
		return versionIdArray;
	}


}
