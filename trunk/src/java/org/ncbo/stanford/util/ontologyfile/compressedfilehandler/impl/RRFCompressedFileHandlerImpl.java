/**
 * 
 */
package org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.AbstractCompressedFileHandler;

/**
 * @author Michael Dorf
 * 
 */
public class RRFCompressedFileHandlerImpl extends
		AbstractCompressedFileHandler {

	protected RRFCompressedFileHandlerImpl() {
	}

	public List<String> handle(File outputFile, OntologyBean ontologyBean)
			throws FileNotFoundException, IOException {
		List<String> relevantFiles = super.handle(outputFile, ontologyBean);

		return relevantFiles;
	}
}
