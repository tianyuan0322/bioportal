package org.ncbo.stanford.util.ontologyfile.pathhandler.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

	public List<String> processOntologyFileUpload(OntologyBean ontologyBean)
			throws FileNotFoundException, IOException, Exception {

		String filePath = AbstractFilePathHandler.getFullOntologyDirPath(ontologyBean);
		String fileName = file.getName();
		
		//TODO - remove this DEBUG stmt
		// DEBUG
		System.out.println("*** outputfilePath = " + filePath);
		System.out.println("*** outputfileName = " + fileName);
		
		File outputDirectories = new File(filePath);
		outputDirectories.mkdirs();

		File outputFile = new File(filePath, fileName);

		FileOutputStream outputStream = new FileOutputStream(outputFile);
		InputStream inputStream = new FileInputStream(file);

		int c;

		while ((c = inputStream.read()) != -1) {
			outputStream.write(c);
		}

		inputStream.close();
		outputStream.close();
		
		List<String> fileNames = new ArrayList<String>(1);
		if(outputFile.exists()) {
		
			fileNames = compressedFileHandler.handle(outputFile, ontologyBean);
		}
		
		return fileNames;
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
