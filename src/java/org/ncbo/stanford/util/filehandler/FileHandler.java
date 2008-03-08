package org.ncbo.stanford.util.filehandler;

import org.ncbo.stanford.domain.generated.NcboOntologyVersion;

public interface FileHandler {

	public void processOntologyFileUpload(String ontologyFilePath,
			NcboOntologyVersion ontologyVersion) throws Exception;

	public String getName();
	
	public String getOntologyFilePath(String ontologyFilePath,
			NcboOntologyVersion ontologyVersion, String filename);
		
	public String getOntologyDirPath(String ontologyFilePath,
			NcboOntologyVersion ontologyVersion);
}
