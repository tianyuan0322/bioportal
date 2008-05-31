package org.ncbo.stanford.util.ontologyfile.pathhandler;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.CompressedFileHandler;

/**
 * An abstract class to contain common functionality for all file handlers
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractFilePathHandler implements FilePathHandler {

	protected CompressedFileHandler compressedFileHandler;

	protected AbstractFilePathHandler(CompressedFileHandler compressedFileHandler) {
		this.compressedFileHandler = compressedFileHandler;
	}

	public String getOntologyFilePath(String ontologyFilePath,
			OntologyBean ontologyBean, String filename) {
		return getFullOntologyDirPath(ontologyFilePath, ontologyBean) + "/"
				+ filename;
	}

	public String getFullOntologyDirPath(String ontologyFilePath,
			OntologyBean ontologyBean) {
		return ontologyFilePath + ontologyBean.getOntologyDirPath();
	}
	
/*	public String getFullOntologyDirPath(String ontologyFilePath,
			OntologyBean ontologyBean) {
		return ontologyFilePath + getOntologyDirPath(ontologyBean);
	}

	public String getOntologyDirPath(OntologyBean ontologyBean) {
		return "/" + ontologyBean.getOntologyId() + "/"
				+ ontologyBean.getInternalVersionNumber();
	}*/
}
