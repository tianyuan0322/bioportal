package org.ncbo.stanford.service.search;

import java.util.List;

/**
 * Service responsible for creating and maintaining BioPortal search (Lucene)
 * index
 * 
 * @author Michael Dorf
 * 
 */
public interface IndexSearchService {

	/**
	 * Recreate the index of all ontologies, overwriting the existing one
	 * 
	 * @throws Exception
	 */
	public void indexAllOntologies() throws Exception;

	/**
	 * Index a given ontology with options to backup and optimize index
	 * 
	 * @param ontologyId
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	public void indexOntology(Integer ontologyId, boolean doBackup,
			boolean doOptimize) throws Exception;

	/**
	 * Index given ontologies with options to backup and optimize index
	 * 
	 * @param ontologyIdList
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	public void indexOntologies(List<Integer> ontologyIdList, boolean doBackup,
			boolean doOptimize) throws Exception;

	/**
	 * Remove a given ontology from index with options to backup and optimize
	 * index
	 * 
	 * @param ontologyId
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	public void removeOntology(Integer ontologyId, boolean doBackup,
			boolean doOptimize) throws Exception;

	/**
	 * Remove given ontologies from index with options to backup and optimize
	 * index
	 * 
	 * @param ontologyIds
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	public void removeOntologies(List<Integer> ontologyIds, boolean doBackup,
			boolean doOptimize) throws Exception;

	/**
	 * Create a backup of the existing search index
	 * 
	 * @throws Exception
	 */
	public void backupIndex() throws Exception;

	/**
	 * Run an optimization command on the existing index
	 * 
	 * @throws Exception
	 */
	public void optimizeIndex() throws Exception;
}
