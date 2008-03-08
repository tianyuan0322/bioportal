package org.ncbo.stanford.util.filehandler.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.util.filehandler.AbstractFileHandler;
import org.ncbo.stanford.util.filehandler.FileHandler;

public class PhysicalDirectoryFileHandler extends AbstractFileHandler implements
		FileHandler {

	private File file;

	public PhysicalDirectoryFileHandler(File file) {
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

		FileOutputStream outputStream = new FileOutputStream(outputFile);
		InputStream inputStream = new FileInputStream(file);

		int c;

		while ((c = inputStream.read()) != -1) {
			outputStream.write(c);
		}

		inputStream.close();
		outputStream.close();
	}

	public String getName() {
		return file.getName();
	}
}
