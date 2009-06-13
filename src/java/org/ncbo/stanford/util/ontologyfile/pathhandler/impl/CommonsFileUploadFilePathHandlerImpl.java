package org.ncbo.stanford.util.ontologyfile.pathhandler.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.MessageUtils;
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

	
	public static void main(String[] args) {
		String path = "C:\\apps\\Protege_3.4\\examples\\pizza\\pizza.owl";
//		String path = "pizza.owl";
		
		int ind = path.lastIndexOf(File.separatorChar);
		
		if (ind > -1) {
			path = path.substring(ind + 1);
		}
		
//		System.out.println("path separator: " + File.separatorChar);
		System.out.println("path: " + path);
		
		
	}
	
	
	public CommonsFileUploadFilePathHandlerImpl(
			CompressedFileHandler compressedFileHandler, FileItem file) {
		super(compressedFileHandler);
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler#processOntologyFileUpload(org.ncbo.stanford.bean.OntologyBean)
	 */
	public List<String> processOntologyFileUpload(OntologyBean ontologyBean)
			throws FileNotFoundException, IOException, Exception {

		// place holder for return object
		List<String> fileNames = new ArrayList<String>(1);

		// continue only if there is input fileItem to upload
		FileItem fileItem = ontologyBean.getFileItem();

		if (fileItem != null) {
			String filePath = AbstractFilePathHandler
					.getFullOntologyDirPath(ontologyBean);
			String fileName = fileItem.getName();
			int ind = fileName.lastIndexOf(File.separatorChar);
			
			if (ind > -1) {
				fileName = fileName.substring(ind + 1);
			}

			// validate input file
			if (file.getSize() == 0) {
				String errorMsg = MessageUtils
						.getMessage("msg.error.file.inputFileNotFoundError")
						+ " fileName =  " + fileName;

				throw new FileNotFoundException(
						"Error! - CommonsFileUploadFilePathHandlerImpl(): processOntologyFileUpload - "
								+ errorMsg);
			}

			// now create output file
			File outputDirectories = new File(filePath);
			outputDirectories.mkdirs();
			File outputFile = new File(filePath, fileName);
			file.write(outputFile);

			// validate output file
			if (!outputFile.exists()) {
				String errorMsg = MessageUtils
						.getMessage("msg.error.file.outputFileCreationError")
						+ " filePath =  "
						+ filePath
						+ " fileName =  "
						+ fileName;

				throw new FileNotFoundException(
						"Error! - CommonsFileUploadFilePathHandlerImpl(): processOntologyFileUpload - "
								+ errorMsg);
			}

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
