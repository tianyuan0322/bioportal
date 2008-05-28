/**
 * 
 */
package org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl;

import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.CompressedFileHandler;

/**
 * @author Michael Dorf
 * 
 */
public class CompressedFileHandlerFactory {
	public static CompressedFileHandler createFileHandler(String format) {
		CompressedFileHandler compressedFileHandler = null;

		if (format.equalsIgnoreCase(ApplicationConstants.FORMAT_OBO)) {
			compressedFileHandler = new OBOCompressedFileHandlerImpl();
		} else if (format.equalsIgnoreCase(ApplicationConstants.FORMAT_OWL_DL)
				|| format
						.equalsIgnoreCase(ApplicationConstants.FORMAT_OWL_FULL)) {
			compressedFileHandler = new OWLCompressedFileHandlerImpl();
		} else if (format.equalsIgnoreCase(ApplicationConstants.FORMAT_PROTEGE)) {
			compressedFileHandler = new PROTEGECompressedFileHandlerImpl();
		} else if (format
				.equalsIgnoreCase(ApplicationConstants.FORMAT_UMLS_RRF)) {
			compressedFileHandler = new UMLSCompressedFileHandlerImpl();
		}

		return compressedFileHandler;
	}
}
