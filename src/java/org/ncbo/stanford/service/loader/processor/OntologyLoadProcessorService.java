package org.ncbo.stanford.service.loader.processor;

import java.io.IOException;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.filehandler.FileHandler;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface to load an ontology into BioPortal.
 * 
 * @author Michael Dorf
 * 
 */
@Transactional
public interface OntologyLoadProcessorService {

	/**
	 * Load an ontology into BioPortal and populate the load queue
	 * 
	 * @param ontologyFile
	 * @param ontologyBean
	 * @throws IOException
	 */
	public void processOntologyLoad(FileHandler ontologyFile,
			OntologyBean ontologyBean) throws Exception;
}
