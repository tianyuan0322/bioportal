package org.ncbo.stanford.util.ontologyfile.compressedfilehandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;

public interface CompressedFileHandler {
	
	public List<String> handle(File outputFile, OntologyBean ontologyBean) throws FileNotFoundException, IOException;

}
