/**
 *
 */
package org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
		String uploadedFile = relevantFiles.get(0);

		for (String filename : uncompressedFilenames) {
			if (filename.toLowerCase().endsWith(
					ApplicationConstants.PROTEGE_EXTENSION)) {
				relevantFiles.add(filename);
			}

			if (filename.toLowerCase().endsWith(
					ApplicationConstants.OWL_EXTENSION)) {
				relevantFiles.add(filename);
			}
		}

		if (relevantFiles.size() > 1) {
			String targetFile = StringUtils.stripEnd(uploadedFile,
					ApplicationConstants.ZIP_EXTENSION);

			String owlFile = null;
			for (String filename : relevantFiles) {
				String owlFileStripped = StringUtils.stripEnd(filename,
						ApplicationConstants.OWL_EXTENSION);
				// Also check for PPRJ extension
				owlFileStripped = StringUtils.stripEnd(owlFileStripped,
						ApplicationConstants.PROTEGE_EXTENSION);
				if (owlFileStripped.equalsIgnoreCase(targetFile)) {
					owlFile = filename;
				}
			}

			// This is totally random, but nothing better to do
			if (owlFile == null) {
				// Use 1 because 0 is always the zip file
				owlFile = relevantFiles.get(1);
			}

			relevantFiles = new ArrayList<String>(0);
			relevantFiles.add(uploadedFile);
			relevantFiles.add(owlFile);
		}

		return relevantFiles;
	}
}
