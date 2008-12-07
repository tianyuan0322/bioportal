package org.ncbo.stanford.service.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.service.search.AbstractSearchService;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

/**
 * The default implementation of the IndexSearchService
 * 
 * @author Michael Dorf
 * 
 */
public class IndexSearchServiceImpl extends AbstractSearchService implements
		IndexSearchService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(IndexSearchServiceImpl.class);
	private String indexBackupPath;
	private int indexMergeFactor;
	private int indexMaxMergeDocs;
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	private Map<String, OntologySearchManager> ontologySearchHandlerMap = new HashMap<String, OntologySearchManager>(
			0);

	/**
	 * Recreate the index of all ontologies, overwriting the existing one
	 * 
	 * @throws Exception
	 */
	public void indexAllOntologies() throws Exception {
		long start = System.currentTimeMillis();
		List<VNcboOntology> ontologies = ncboOntologyVersionDAO
				.findLatestActiveOntologyVersions();
		backupIndex();

		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
				indexPath, analyzer, true);
		writer.setMergeFactor(indexMergeFactor);
		writer.setMaxMergeDocs(indexMaxMergeDocs);

		for (VNcboOntology ontology : ontologies) {
			try {
				indexOntology(writer, ontology, false, false);

				// commit changes to writer so they are visible to the searcher
				writer.commit();
			} catch (Exception e) {
				log.error(e);
			}
		}

		optimizeIndex(writer);
		closeWriter(writer);

		if (log.isDebugEnabled()) {
			long stop = System.currentTimeMillis(); // stop timing
			log.debug("Finished indexing all ontologies in "
					+ (double) (stop - start) / 1000 / 60 / 60 + " hours.");
		}
	}

	/**
	 * Index a given ontology with options to backup and optimize index
	 * 
	 * @param ontologyId
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	public void indexOntology(Integer ontologyId, boolean doBackup,
			boolean doOptimize) throws Exception {
		List<Integer> ontologyIds = new ArrayList<Integer>(1);
		ontologyIds.add(ontologyId);
		indexOntologies(ontologyIds, doBackup, doOptimize);
	}

	/**
	 * Index given ontologies with options to backup and optimize index
	 * 
	 * @param ontologyIdList
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	public void indexOntologies(List<Integer> ontologyIdList, boolean doBackup,
			boolean doOptimize) throws Exception {
		List<VNcboOntology> ontologies = ncboOntologyVersionDAO
				.findLatestActiveOntologyVersions(ontologyIdList);

		if (!ontologies.isEmpty()) {
			LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
					indexPath, analyzer);
			writer.setMergeFactor(indexMergeFactor);
			writer.setMaxMergeDocs(indexMaxMergeDocs);
			indexOntologies(writer, ontologies, doBackup, doOptimize);
			closeWriter(writer);
		}
	}

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
			boolean doOptimize) throws Exception {
		List<Integer> ontologyIds = new ArrayList<Integer>(1);
		ontologyIds.add(ontologyId);
		removeOntologies(ontologyIds, doBackup, doOptimize);
	}

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
			boolean doOptimize) throws Exception {
		List<VNcboOntology> ontologies = ncboOntologyVersionDAO
				.findLatestActiveOntologyVersions(ontologyIds);

		if (!ontologies.isEmpty()) {
			LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
					indexPath, analyzer);
			removeOntologies(writer, ontologies, doBackup, doOptimize, true);
			closeWriter(writer);
		}
	}

	/**
	 * Create a backup of the existing search index
	 * 
	 * @throws Exception
	 */
	public void backupIndex() throws Exception {
		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
				indexPath, analyzer);
		backupIndex(writer);
		closeWriter(writer);
	}

	/**
	 * Run an optimization command on the existing index
	 * 
	 * @throws Exception
	 */
	public void optimizeIndex() throws Exception {
		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
				indexPath, analyzer);
		optimizeIndex(writer);
		closeWriter(writer);
	}

	/**
	 * Index a given ontology with options to backup and optimize the index
	 * 
	 * @param writer
	 * @param ontology
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	private void indexOntology(LuceneIndexWriterWrapper writer,
			VNcboOntology ontology, boolean doBackup, boolean doOptimize)
			throws Exception {
		Integer ontologyVersionId = ontology.getId();
		Integer ontologyId = ontology.getOntologyId();
		String format = ontology.getFormat();
		String displayLabel = ontology.getDisplayLabel();
		OntologySearchManager mgr = getOntologySearchManager(format);
		long start = 0;
		long stop = 0;

		if (mgr != null) {
			removeOntology(writer, ontology, doBackup, false, false);

			if (log.isDebugEnabled()) {
				log.debug("Adding ontology to index: " + displayLabel
						+ " (Id: " + ontologyVersionId + ", Ontology Id: "
						+ ontologyId + ", Format: " + format + ")");
				start = System.currentTimeMillis();
			}

			mgr.indexOntology(writer, ontology);

			if (log.isDebugEnabled()) {
				stop = System.currentTimeMillis(); // stop timing
				log.debug("Finished indexing ontology: " + displayLabel
						+ " in " + (double) (stop - start) / 1000 / 60
						+ " minutes.\n");
			}

			reloadCache();

			if (doOptimize) {
				optimizeIndex(writer);
			}
		} else {
			throw new Exception("No hanlder was found for ontology: "
					+ ontology.getDisplayLabel() + " (Id: " + ontologyVersionId
					+ ", Ontology Id: " + ontologyId + ", Format: " + format
					+ ")");
		}
	}

	/**
	 * Index given ontologies with options to backup and optimize the index
	 * 
	 * @param writer
	 * @param ontologies
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	private void indexOntologies(LuceneIndexWriterWrapper writer,
			List<VNcboOntology> ontologies, boolean doBackup, boolean doOptimize)
			throws Exception {
		if (!ontologies.isEmpty()) {
			if (doBackup) {
				backupIndex(writer);
			}

			for (VNcboOntology ontology : ontologies) {
				indexOntology(writer, ontology, false, false);
			}

			if (doOptimize) {
				optimizeIndex(writer);
			}
		}
	}

	/**
	 * Remove an ontology from index with options to backup and optimize the
	 * index
	 * 
	 * @param writer
	 * @param ontology
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	private void removeOntology(LuceneIndexWriterWrapper writer,
			VNcboOntology ontology, boolean doBackup, boolean doOptimize,
			boolean reloadCache) throws Exception {
		List<VNcboOntology> ontologies = new ArrayList<VNcboOntology>(1);
		ontologies.add(ontology);
		removeOntologies(writer, ontologies, doBackup, doOptimize, reloadCache);
	}

	/**
	 * Remove given ontologies from index with options to backup and optimize
	 * the index
	 * 
	 * @param writer
	 * @param ontologies
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	private void removeOntologies(LuceneIndexWriterWrapper writer,
			List<VNcboOntology> ontologies, boolean doBackup,
			boolean doOptimize, boolean reloadCache) throws Exception {
		List<Integer> ontologyIdList = new ArrayList<Integer>(0);

		for (VNcboOntology ontology : ontologies) {
			ontologyIdList.add(ontology.getOntologyId());
		}

		if (doBackup) {
			backupIndex(writer);
		}

		if (ontologyIdList.size() == 1) {
			Term ontologyIdTerm = generateOntologyIdTerm(ontologyIdList.get(0));
			writer.deleteDocuments(ontologyIdTerm);
		} else {
			Query ontologyIdsQuery = generateOntologyIdsQuery(ontologyIdList);
			writer.deleteDocuments(ontologyIdsQuery);
		}

		if (reloadCache) {
			reloadCache();
		}

		if (doOptimize) {
			optimizeIndex(writer);
		}

		if (log.isDebugEnabled()) {
			log.debug("Removed ontologies from index: "
					+ getDebugDisplay(ontologies));
		}
	}

	/**
	 * Backup the index
	 * 
	 * @param writer
	 * @throws Exception
	 */
	private void backupIndex(LuceneIndexWriterWrapper writer) throws Exception {
		long start = 0;
		long stop = 0;

		if (log.isDebugEnabled()) {
			log.debug("Backing up index...");
			start = System.currentTimeMillis();
		}

		writer.backupIndexByFileCopy(indexBackupPath);
		// writer.backupIndexByReading(indexBackupPath);

		if (log.isDebugEnabled()) {
			stop = System.currentTimeMillis(); // stop timing
			log.debug("Finished backing up index in " + (double) (stop - start)
					/ 1000 + " seconds.");
		}
	}

	/**
	 * Otimize the index
	 * 
	 * @param writer
	 * @throws Exception
	 */
	private void optimizeIndex(LuceneIndexWriterWrapper writer)
			throws Exception {
		long start = 0;
		long stop = 0;

		if (log.isDebugEnabled()) {
			log.debug("Optimizing index...");
			start = System.currentTimeMillis();
		}

		writer.optimize();

		if (log.isDebugEnabled()) {
			stop = System.currentTimeMillis(); // stop timing
			log.debug("Finished optimizing index in " + (double) (stop - start)
					/ 1000 / 60 + " minutes.");
		}
	}

	/**
	 * Reload search results cache by re-running all queries in it and
	 * re-populating it with new results
	 */
	private void reloadCache() {
		long start = 0;
		long stop = 0;

		if (log.isDebugEnabled()) {
			log.debug("Reloading cache...");
			start = System.currentTimeMillis();
		}

		QueryParser parser = new QueryParser(
				SearchIndexBean.CONTENTS_FIELD_LABEL, analyzer);
		Set<String> queries = searchResultCache.getKeys();
		searchResultCache.clear();

		for (String queryStr : queries) {
			SearchResultListBean results = null;

			try {
				results = runQuery(parser.parse(queryStr));
			} catch (Exception e) {
				results = null;
				e.printStackTrace();
				log.error("Error while reloading cache: " + e);
			}

			if (results != null) {
				searchResultCache.put(queryStr, results);
			}
		}

		if (log.isDebugEnabled()) {
			stop = System.currentTimeMillis(); // stop timing
			log.debug("Finished reloading cache in " + (double) (stop - start)
					/ 1000 + " seconds.");
		}
	}

	/**
	 * Close the given writer and reload search results cache
	 * 
	 * @param writer
	 * @param reloadCache
	 * @throws IOException
	 */
	private void closeWriter(LuceneIndexWriterWrapper writer)
			throws IOException {
		writer.closeWriter();
		writer = null;
	}

	/**
	 * Returns the search manager for a given ontology format
	 * 
	 * @param format
	 * @return
	 */
	private OntologySearchManager getOntologySearchManager(String format) {
		return ontologyFormatHandlerMap.containsKey(format) ? ontologySearchHandlerMap
				.get(ontologyFormatHandlerMap.get(format))
				: null;
	}

	/**
	 * @param indexBackupPath
	 *            the indexBackupPath to set
	 */
	public void setIndexBackupPath(String indexBackupPath) {
		this.indexBackupPath = indexBackupPath;
	}

	/**
	 * @param indexMergeFactor
	 *            the indexMergeFactor to set
	 */
	public void setIndexMergeFactor(int indexMergeFactor) {
		this.indexMergeFactor = indexMergeFactor;
	}

	/**
	 * @param indexMaxMergeDocs
	 *            the indexMaxMergeDocs to set
	 */
	public void setIndexMaxMergeDocs(int indexMaxMergeDocs) {
		this.indexMaxMergeDocs = indexMaxMergeDocs;
	}

	/**
	 * @param ncboOntologyVersionDAO
	 *            the ncboOntologyVersionDAO to set
	 */
	public void setNcboOntologyVersionDAO(
			CustomNcboOntologyVersionDAO ncboOntologyVersionDAO) {
		this.ncboOntologyVersionDAO = ncboOntologyVersionDAO;
	}

	/**
	 * @param ontologyFormatHandlerMap
	 *            the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}

	/**
	 * @param ontologySearchHandlerMap
	 *            the ontologySearchHandlerMap to set
	 */
	public void setOntologySearchHandlerMap(
			Map<String, OntologySearchManager> ontologySearchHandlerMap) {
		this.ontologySearchHandlerMap = ontologySearchHandlerMap;
	}
}
