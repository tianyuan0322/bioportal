package org.ncbo.stanford.util.ontologyfile.pathhandler.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.CompressedFileHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;

/**
 * An implementation of FileHandler interface, where the ontology is uploaded
 * using Commons FileUpload component
 * 
 * @author Michael Dorf
 * 
 */
public class CommonsFileUploadFilePathHandlerImpl extends
		AbstractFilePathHandler {

	private FileItem file;

	public CommonsFileUploadFilePathHandlerImpl(
			CompressedFileHandler compressedFileHandler, FileItem file) {
		super(compressedFileHandler);
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler#processOntologyFileUpload(java.lang.String,
	 *      org.ncbo.stanford.bean.OntologyBean)
	 */
	public List<String> processOntologyFileUpload(
			OntologyBean ontologyBean) throws FileNotFoundException,
			IOException, Exception {
			
		String filePath = ontologyBean.getFilePath();
		String fileName = ontologyBean.getFileItem().getName();
		File outputDirectories = new File(filePath);
		outputDirectories.mkdirs();

		File outputFile = new File(filePath, fileName);
		outputFile.createNewFile();
		file.write(outputFile);

		return compressedFileHandler.handle(outputFile, ontologyBean);
	}	

	//TODO - clean up the DUPLICATE
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
		file.write(outputFile);

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
