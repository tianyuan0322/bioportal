package org.ncbo.stanford.util.ontologyfile.pathhandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;

/**
 * An interface to handle ontology files. Allows different handler
 * implementations for common tasks (for example, using Commons FileUpload vs.
 * general FileInputStream)
 * 
 * @author Michael Dorf
 * 
 */
public interface FilePathHandler {

	
	//TODO - this is DUPLICATE. Clean up 
	/**
	 * Handle the ontology file upload
	 * 
	 * @param ontologyFilePath
	 * @param ontologyBean
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception 
	 */
	
	public List<String> processOntologyFileUpload(String ontologyFilePath,
			OntologyBean ontologyBean) throws FileNotFoundException,
			IOException, Exception;

	
	/**
	 * Handle the ontology file upload
	 * 
	 * @param ontologyBean
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception 
	 */
	public List<String> processOntologyFileUpload(OntologyBean ontologyBean)
			throws FileNotFoundException, IOException, Exception;	
	
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
	 * @param ontologyBean
	 * @param filename
	 * @return
	 */
	public String getOntologyFilePath(String ontologyFilePath,
			OntologyBean ontologyBean, String filename);

	/**
	 * Return the full ontology directory path (including prefix directory)
	 * 
	 * @param ontologyFilePath
	 * @param ontologyBean
	 * @return
	 */
	public String getFullOntologyDirPath(String ontologyFilePath,
			OntologyBean ontologyBean);


}
