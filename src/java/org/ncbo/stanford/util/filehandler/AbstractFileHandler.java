package org.ncbo.stanford.util.filehandler;

import org.ncbo.stanford.domain.generated.NcboOntologyVersion;

/**
 * An abstract class to contain common functionality for all file handlers
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractFileHandler implements FileHandler {

	public String getOntologyFilePath(String ontologyFilePath,
			NcboOntologyVersion ontologyVersion, String filename) {
		return getFullOntologyDirPath(ontologyFilePath, ontologyVersion) + "/"
				+ filename;
	}

	public String getFullOntologyDirPath(String ontologyFilePath,
			NcboOntologyVersion ontologyVersion) {
		return ontologyFilePath + getOntologyDirPath(ontologyVersion);
	}

	public String getOntologyDirPath(NcboOntologyVersion ontologyVersion) {
		return "/" + ontologyVersion.getOntologyId() + "/"
				+ ontologyVersion.getInternalVersionNumber();
	}
}
