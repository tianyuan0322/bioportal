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
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.AbstractCompressedFileHandler;

/**
 * @author Michael Dorf
 * 
 */
public class OWLCompressedFileHandlerImpl extends AbstractCompressedFileHandler {

	protected OWLCompressedFileHandlerImpl() {
	}

	public List<String> handle(File outputFile, OntologyBean ontologyBean)
			throws FileNotFoundException, IOException {
		List<String> relevantFiles = super.handle(outputFile, ontologyBean);
		boolean hasProjectFile = false;
		String uploadedFile = relevantFiles.get(0);

		for (String filename : uncompressedFilenames) {
			if (filename.toLowerCase().endsWith(
					ApplicationConstants.PROTEGE_EXTENSION)) {
				relevantFiles = new ArrayList<String>(0);
				relevantFiles.add(uploadedFile);
				relevantFiles.add(filename);
				hasProjectFile = true;
				break;
			}

			if (filename.toLowerCase().endsWith(
					ApplicationConstants.OWL_EXTENSION)) {
				relevantFiles.add(filename);
			}
		}

		if (!hasProjectFile && relevantFiles.size() > 1) {
			String owlFile = relevantFiles.get(1);
			relevantFiles = new ArrayList<String>(0);
			relevantFiles.add(uploadedFile);
			relevantFiles.add(owlFile);
		}

		return relevantFiles;
	}
}
