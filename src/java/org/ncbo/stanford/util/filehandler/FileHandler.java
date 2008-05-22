package org.ncbo.stanford.util.filehandler;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;

/**
 * An interface to handle ontology files. Allows different handler
 * implementations for common tasks (for example, using Commons FileUpload vs.
 * general FileInputStream)
 * 
 * @author Michael Dorf
 * 
 */
public interface FileHandler {

	/**
	 * Handle the ontology file upload
	 * 
	 * @param ontologyFilePath
	 * @param ontologyVersion
	 * @throws Exception
	 */
	public void processOntologyFileUpload(String ontologyFilePath,
			OntologyBean ontologyBean) throws Exception;
	
	/**
	 * Get the name of the file
	 * 
	 * @return filename
	 */
	public String getName();

	/**
	 * Return the full path to the ontology file
	 * 
	 * @param ontologyFilePath
	 * @param ontologyVersion
	 * @param filename
	 * @return
	 */
	public String getOntologyFilePath(String ontologyFilePath,
			OntologyBean ontologyBean, String filename);
	
	/**
	 * Return the full ontology directory path (including prefix directory)
	 * 
	 * @param ontologyFilePath
	 * @param ontologyVersion
	 * @return
	 */
	public String getFullOntologyDirPath(String ontologyFilePath,
			OntologyBean ontologyBean);
	
	/**
	 * Return the partial ontology directory path (without prefix directory)
	 * 
	 * @param ontologyVersion
	 * @return
	 */
	public String getOntologyDirPath(OntologyBean ontologyBean);
}
