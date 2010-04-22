/**
 * Exposes services that provide access to diff on ontologies
 * 
 * @author Natasha Noy
 */
package org.ncbo.stanford.service.diff;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public interface DiffService {

	/**
	 * Creates a diff between two ontology versions
	 * 
	 * @param newOntologyVersionId,
	 *            oldOntologyVersionId
	 * 
	 * @throws Exception
	 */
	public void createDiff(Integer newOntologyVersionId,
			Integer oldOntologyVersionId) throws Exception;
	
	/**
	 * Creates a diff between the ontology that has just been loaded and its
	 * previous active version(if one exists in BioPortal). Save the diff in a file
	 * 
	 * @param ontologyIds
	 *            the list of ontologies to process.
	 *             
	 * @throws Exception
	 */
	public void createDiffForLatestActiveOntologyVersionPair(List<Integer> ontologyIds)
			throws Exception;

	/**
	 * Create diffs between the active versions of an ontology
	 * 
	 * @param ontologyIds
	 *            the list of ontologies to process.
	 * 
	 * @throws Exception
	 */	
	public void createDiffForAllActiveVersionsOfOntology(List<Integer> ontologyIds)
	  throws Exception ;
	
	/**
	 * Return the list of all diff pairs for a given ontology id
	 * 
	 * @param ontologyId
	 * @return
	 * @throws Exception
	 */
	public List<ArrayList<String>> getAllDiffsForOntology(Integer ontologyId)
			throws Exception;

	/**
	 * Get file object for rdf-formated diff between specified ontology verisons
	 * 
	 * @param ontologyVersionId1,
	 *            ontologyVerisonId2
	 * @param format
	 * @return
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public File getDiffFileForOntologyVersions(Integer ontologyVerisonId1,
			Integer ontologyVersionId2, String format)
			throws FileNotFoundException, Exception;
	

}
