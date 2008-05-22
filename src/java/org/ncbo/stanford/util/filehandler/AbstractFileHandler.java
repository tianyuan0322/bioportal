package org.ncbo.stanford.util.filehandler;

import org.ncbo.stanford.bean.OntologyBean;

/**
 * An abstract class to contain common functionality for all file handlers
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractFileHandler implements FileHandler {

	public String getOntologyFilePath(String ontologyFilePath,
			OntologyBean ontologyBean, String filename) {
		return getFullOntologyDirPath(ontologyFilePath, ontologyBean) + "/"
				+ filename;
	}
	
	public String getFullOntologyDirPath(String ontologyFilePath,
			OntologyBean ontologyBean) {
		return ontologyFilePath + getOntologyDirPath(ontologyBean);
	}

	public String getOntologyDirPath(OntologyBean ontologyBean) {
		return "/" + ontologyBean.getOntologyId() + "/"
				+ ontologyBean.getInternalVersionNumber();
	}
}
