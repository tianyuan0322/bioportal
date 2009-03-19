/*
 * Contributor(s): Natasha Noy noy@smi.stanford.edu
 */
package org.ncbo.stanford.util.difffile.pathhandler;

import java.util.ArrayList;

/**
 * An interface to handle diff files. 
 * 
 * @author Natasha Noy
 * 
 */
public interface DiffFilePathHandler {
	public String getDiffDirNameForOntologyVersions(Integer ontologyId, Integer ontologyVersionIdOld,
			Integer ontologyVersionIdNew, boolean createDir);
	
	public String [] getListOfDiffDirsForOntologyId(Integer ontologyId);
	
	public String getFileName(String diffDirName, Integer ontologyVersionId1,
			Integer ontologyVersionId2);

	public ArrayList<String> getVerisonIdArrayFromDirectoryName(String dirName);
}
