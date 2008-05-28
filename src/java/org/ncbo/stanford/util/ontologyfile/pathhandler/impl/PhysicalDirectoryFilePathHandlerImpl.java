package org.ncbo.stanford.util.ontologyfile.pathhandler.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.CompressedFileHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;

/**
 * An implementation of FileHandler interface, where the ontology is uploaded
 * from a given physical directory (useful for testing)
 * 
 * @author Michael Dorf
 * 
 */
public class PhysicalDirectoryFilePathHandlerImpl extends
		AbstractFilePathHandler {

	private File file;

	public PhysicalDirectoryFilePathHandlerImpl(
			CompressedFileHandler compressedFileHandler, File file) {
		super(compressedFileHandler);
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler#processOntologyFileUpload(java.lang.String,
	 *      org.ncbo.stanford.bean.OntologyBean)
	 */
	public List<String> processOntologyFileUpload(String ontologyFilePath,
			OntologyBean ontologyBean) throws FileNotFoundException,
			IOException, Exception {

		File outputDirectories = new File(getFullOntologyDirPath(
				ontologyFilePath, ontologyBean));
		outputDirectories.mkdirs();

		File outputFile = new File(getOntologyFilePath(ontologyFilePath,
				ontologyBean, file.getName()));
		outputFile.createNewFile();

		FileOutputStream outputStream = new FileOutputStream(outputFile);
		InputStream inputStream = new FileInputStream(file);

		int c;

		while ((c = inputStream.read()) != -1) {
			outputStream.write(c);
		}

		inputStream.close();
		outputStream.close();

		return compressedFileHandler.handle(outputFile, ontologyBean);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.util.filehandler.FileHandler#getName()
	 */
	public String getName() {
		return file.getName();
	}
}
