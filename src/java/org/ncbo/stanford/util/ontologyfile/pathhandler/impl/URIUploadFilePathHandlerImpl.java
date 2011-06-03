package org.ncbo.stanford.util.ontologyfile.pathhandler.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.loader.LoaderUtils;
import org.ncbo.stanford.util.ontologyfile.OntologyDescriptorParser;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.CompressedFileHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;

/**
 * An implementation of FileHandler interface, where the ontology is uploaded
 * from a URI
 * 
 * @author Pradip Kanjamala
 * 
 */
public class URIUploadFilePathHandlerImpl extends AbstractFilePathHandler {

	private URI uri;

	public URIUploadFilePathHandlerImpl(
			CompressedFileHandler compressedFileHandler, URI uri) {
		super(compressedFileHandler);
		this.uri = uri;
	}

	public List<String> processOntologyFileUpload(OntologyBean ontologyBean)
			throws FileNotFoundException, IOException {
		// place holder for return object
		List<String> fileNames = new ArrayList<String>(1);
		

		// validate inputfile
		String filePath = AbstractFilePathHandler
				.getFullOntologyDirPath(ontologyBean);
		String fileName = OntologyDescriptorParser.getFileName(uri.toString());

		if (StringUtils.isBlank(fileName) && StringUtils.isNotBlank(ontologyBean.getAbbreviation())) {
			fileName = ontologyBean.getAbbreviation().toLowerCase();
		}

		// continue only if there is input file
		if (filePath != null && fileName != null) {
			// now create output file
			File outputDirectories = new File(filePath);
			outputDirectories.mkdirs();

			File outputFile = new File(filePath, fileName);
			LoaderUtils.getContent( uri.toString(), outputFile);
//			BufferedOutputStream out = new BufferedOutputStream(
//					new FileOutputStream(outputFile));
//			BufferedInputStream in = new BufferedInputStream(LoaderUtils.getInputStream(uri.toString()));
//
//			byte data[] = new byte[ApplicationConstants.BUFFER_SIZE];
//			int count;
//						
//			while ((count = in.read(data, 0, ApplicationConstants.BUFFER_SIZE)) != -1) {
//				out.write(data, 0, count);
//			}
//			
//			in.close();
//			out.flush();
//			out.close();

			// validate output file
			if (!outputFile.exists()) {
				String errorMsg = MessageUtils
						.getMessage("msg.error.file.outputFileCreationError")
						+ " filePath =  "
						+ filePath
						+ " fileName =  "
						+ fileName;

				throw new FileNotFoundException(
						"Error! - URIUploadFilePathHandlerImpl(): processOntologyFileUpload - "
								+ errorMsg);
			}
			// Store the MD5 file
			LoaderUtils.storeMd5ToFile(ontologyBean, outputFile);
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
		return uri.toString();
	}
}
