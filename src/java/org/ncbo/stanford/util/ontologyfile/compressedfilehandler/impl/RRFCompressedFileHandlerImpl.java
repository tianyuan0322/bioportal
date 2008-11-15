/**
 * 
 */
package org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.AbstractCompressedFileHandler;

/**
 * @author Michael Dorf
 * 
 */
public class RRFCompressedFileHandlerImpl extends AbstractCompressedFileHandler {

	protected RRFCompressedFileHandlerImpl() {
	}

	public List<String> handle(File outputFile, OntologyBean ontologyBean)
			throws FileNotFoundException, IOException {
		List<String> relevantFiles = super.handle(outputFile, ontologyBean);
		String uploadedFile = relevantFiles.get(0);
		relevantFiles = new ArrayList<String>(0);
		relevantFiles.add(uploadedFile);
		int indexOfLastFileSeparator = uploadedFile.toString().lastIndexOf(
				System.getProperty("file.separator"));
		
		if (indexOfLastFileSeparator == -1) {
			indexOfLastFileSeparator = 0;
		}

		String directoryName = uploadedFile.substring(0,
				indexOfLastFileSeparator);
		relevantFiles.add(directoryName);

		return relevantFiles;
	}
}
