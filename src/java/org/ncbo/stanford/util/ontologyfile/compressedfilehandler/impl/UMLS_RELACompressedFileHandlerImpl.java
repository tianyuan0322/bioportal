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
public class UMLS_RELACompressedFileHandlerImpl extends AbstractCompressedFileHandler {

	protected UMLS_RELACompressedFileHandlerImpl() {
	}

	public List<String> handle(File outputFile, OntologyBean ontologyBean)
			throws FileNotFoundException, IOException {
		List<String> relevantFiles = super.handle(outputFile, ontologyBean);
		String uploadedFile = relevantFiles.get(0);
		relevantFiles = new ArrayList<String>(0);
		relevantFiles.add(uploadedFile);
		relevantFiles.add(ApplicationConstants.DIR);

		return relevantFiles;
	}
}
