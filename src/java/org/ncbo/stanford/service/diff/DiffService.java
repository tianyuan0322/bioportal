/**
 * Exposes services that provide access to diff on ontologies
 * 
 * @author Natasha Noy
  */
package org.ncbo.stanford.service.diff;

import java.io.*;
import java.util.*;

public interface DiffService {
	
	/**
	 * Return the list of all diff pairs for a given ontology id
	 * 
	 * @param ontologyId
	 * @return
	 * @throws Exception 
	 */
	public List<ArrayList<String>> getAllDiffsForOntology(Integer ontologyId) throws Exception;

	/**
	 * Get file object for rdf-formated diff between specified ontology verisons
	 * 
	 * @param ontologyVersionId1, ontologyVerisonId2
	 * @param format
	 * @return
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 */
	public File getDiffFileForOntologyVersions (Integer ontologyVerisonId1, Integer ontologyVersionId2, String format) throws FileNotFoundException, Exception;


}
