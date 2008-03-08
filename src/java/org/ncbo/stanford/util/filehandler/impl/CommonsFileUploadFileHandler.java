package org.ncbo.stanford.util.filehandler.impl;

import java.io.File;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.util.filehandler.AbstractFileHandler;
import org.ncbo.stanford.util.filehandler.FileHandler;

public class CommonsFileUploadFileHandler extends AbstractFileHandler implements
		FileHandler {

	private FileItem file;

	public CommonsFileUploadFileHandler(FileItem file) {
		this.file = file;
	}

	public void processOntologyFileUpload(String ontologyFilePath,
			NcboOntologyVersion ontologyVersion) throws Exception {
		File outputDirectories = new File(getOntologyDirPath(ontologyFilePath,
				ontologyVersion));
		outputDirectories.mkdirs();

		File outputFile = new File(getOntologyFilePath(ontologyFilePath,
				ontologyVersion, file.getName()));
		outputFile.createNewFile();
		file.write(outputFile);
	}

	public String getName() {
		return file.getName();
	}
}
