package org.ncbo.stanford.util.filehandler;

import org.ncbo.stanford.domain.generated.NcboOntologyVersion;

public abstract class AbstractFileHandler implements FileHandler {

	public String getOntologyFilePath(String ontologyFilePath,
			NcboOntologyVersion ontologyVersion, String filename) {
		return getOntologyDirPath(ontologyFilePath, ontologyVersion) + "/"
				+ filename;
	}

	public String getOntologyDirPath(String ontologyFilePath,
			NcboOntologyVersion ontologyVersion) {
		return ontologyFilePath + "/" + ontologyVersion.getOntologyId() + "/"
				+ ontologyVersion.getInternalVersionNumber();
	}
}
