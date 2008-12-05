package org.ncbo.stanford.service.search;

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
	 * Index a given ontology
	 * 
	 * @param ontologyId
	 * @throws Exception
	 */
	public void indexOntology(Integer ontologyId) throws Exception;

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
	 * Remove an ontology from index
	 * 
	 * @param ontologyId
	 * @throws Exception
	 */
	public void removeOntology(Integer ontologyId) throws Exception;

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
