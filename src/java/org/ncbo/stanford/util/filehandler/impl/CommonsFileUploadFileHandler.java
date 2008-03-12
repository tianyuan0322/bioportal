package org.ncbo.stanford.util.filehandler.impl;

import java.io.File;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.util.filehandler.AbstractFileHandler;

/**
 * An implementation of FileHandler interface, where the ontology is uploaded
 * using Commons FileUpload component
 * 
 * @author Michael Dorf
 * 
 */
public class CommonsFileUploadFileHandler extends AbstractFileHandler {

	private FileItem file;

	public CommonsFileUploadFileHandler(FileItem file) {
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
		file.write(outputFile);
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
