package org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.AbstractCompressedFileHandler;

public class LEXGRID_XMLCompressedFileHandlerImpl extends
		AbstractCompressedFileHandler {

	protected LEXGRID_XMLCompressedFileHandlerImpl() {
	}

	public List<String> handle(File outputFile, OntologyBean ontologyBean)
			throws FileNotFoundException, IOException {
		List<String> relevantFiles = super.handle(outputFile, ontologyBean);

		return relevantFiles;
	}
}
