package org.ncbo.stanford.service.loader.processor;

import java.io.IOException;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.util.filehandler.FileHandler;

/**
 * Interface to load an ontology into BioPortal.
 * 
 * @author Michael Dorf
 * 
 */
public interface OntologyLoadProcessorService {

	/**
	 * Load an ontology into BioPortal and populate the load queue
	 * 
	 * @param ontologyFile
	 * @param ontologyBean
	 * @throws IOException
	 */
	public NcboOntologyVersion processOntologyLoad(FileHandler ontologyFile,
			OntologyBean ontologyBean) throws Exception;
}
