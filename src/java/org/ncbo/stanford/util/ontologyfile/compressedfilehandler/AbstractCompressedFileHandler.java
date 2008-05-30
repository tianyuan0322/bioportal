/**
 * 
 */
package org.ncbo.stanford.util.ontologyfile.compressedfilehandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * @author Michael Dorf
 * 
 */
public abstract class AbstractCompressedFileHandler implements
		CompressedFileHandler {

	public List<String> handle(File outputFile, OntologyBean ontologyBean)
			throws FileNotFoundException, IOException {
		List<String> relevantFiles = new ArrayList<String>(1);
		relevantFiles.add(outputFile.getName());

		return relevantFiles;
	}

	public String getCompositeFilename(String compressedFilename) {
		return compressedFilename + ApplicationConstants.COMPOSITE_FILENAME;
	}

	public String getCompositeFilePath(String compressedFilePath,
			String compressedFilename) {
		return compressedFilePath + File.separator
				+ getCompositeFilename(compressedFilename);
	}
}
