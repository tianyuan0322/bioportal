package org.ncbo.stanford.util.filehandler.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.util.filehandler.AbstractFileHandler;

/**
 * An implementation of FileHandler interface, where the ontology is uploaded
 * from a given physical directory (useful for testing)
 * 
 * @author Michael Dorf
 * 
 */
public class PhysicalDirectoryFileHandler extends AbstractFileHandler {

	private File file;

	public PhysicalDirectoryFileHandler(File file) {
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.util.filehandler.FileHandler#processOntologyFileUpload(java.lang.String,
	 *      org.ncbo.stanford.domain.generated.NcboOntologyVersion)
	 */
	public void processOntologyFileUpload(String ontologyFilePath,
			NcboOntologyVersion ontologyVersion) throws Exception {
		File outputDirectories = new File(getFullOntologyDirPath(
				ontologyFilePath, ontologyVersion));
		outputDirectories.mkdirs();

		File outputFile = new File(getOntologyFilePath(ontologyFilePath,
				ontologyVersion, file.getName()));
		outputFile.createNewFile();

		FileOutputStream outputStream = new FileOutputStream(outputFile);
		InputStream inputStream = new FileInputStream(file);

		int c;

		while ((c = inputStream.read()) != -1) {
			outputStream.write(c);
		}

		inputStream.close();
		outputStream.close();
	}

	// added by cyoun
	public void processOntologyFileUpload(String ontologyFilePath,
			OntologyBean ontologyBean) throws Exception {
		
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
