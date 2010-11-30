package org.ncbo.stanford.service.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.service.search.AbstractSearchService;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of the IndexSearchService
 * 
 * @author Michael Dorf
 * 
 */
@Transactional
public class IndexSearchServiceImpl extends AbstractSearchService implements
		IndexSearchService {

	private static final Log log = LogFactory
			.getLog(IndexSearchServiceImpl.class);
	private String indexBackupPath;
	private int indexMergeFactor;
	private int indexMaxMergeDocs;

	/**
	 * Recreate the index of all ontologies, overwriting the existing one
	 * 
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	public void indexAllOntologies(boolean doBackup, boolean doOptimize)
			throws Exception {
		long start = System.currentTimeMillis();

		List<OntologyBean> ontologies = ontologyMetadataManager
				.findLatestActiveOntologyOrOntologyViewVersions();

		if (doBackup) {
			backupIndex();
		}

		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
				indexPath, analyzer, true);
		writer.setMergeFactor(indexMergeFactor);
		writer.setMaxMergeDocs(indexMaxMergeDocs);

		for (OntologyBean ontology : ontologies) {
			try {
				indexOntology(writer, ontology, false, false);

				// commit changes to writer so they are visible to the searcher
				writer.commit();
			} catch (Exception e) {
				log.error(e);
			}
		}

		if (doOptimize) {
			optimizeIndex(writer);
		}

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
		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
				indexPath, analyzer);
		writer.setMergeFactor(indexMergeFactor);
		writer.setMaxMergeDocs(indexMaxMergeDocs);

		List<OntologyBean> ontologies = ontologyMetadataManager
				.findLatestActiveOntologyOrOntologyViewVersions(ontologyIdList);

		removeOntologies(writer, ontologyIdList, doBackup, false, true);

		if (!ontologies.isEmpty()) {
			indexOntologies(writer, ontologies, false, doOptimize);
		}

		closeWriter(writer);
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
		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
				indexPath, analyzer);
		removeOntologies(writer, ontologyIds, doBackup, doOptimize, true);
		closeWriter(writer);
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
			OntologyBean ontology, boolean doBackup, boolean doOptimize)
			throws Exception {
		Integer ontologyVersionId = ontology.getId();
		Integer ontologyId = ontology.getOntologyId();
		String format = ontology.getFormat();
		String displayLabel = ontology.getDisplayLabel();
		OntologySearchManager mgr = getOntologySearchManager(format);
		long start = 0;
		long stop = 0;

		if (mgr != null) {
			removeOntology(writer, ontologyId, doBackup, false, false);

			if (log.isDebugEnabled()) {
				log.debug("Adding ontology to index: "
						+ getOntologyDisplay(ontologyVersionId, ontologyId,
								displayLabel, format));
				start = System.currentTimeMillis();
			}

			mgr.indexOntology(writer, ontology);

			if (log.isDebugEnabled()) {
				stop = System.currentTimeMillis(); // stop timing
				log.debug("Finished indexing ontology: "
						+ getOntologyDisplay(ontologyVersionId, ontologyId,
								displayLabel, format) + " in "
						+ (double) (stop - start) / 1000 / 60 + " minutes.\n");
			}

			reloadSearchCache();

			if (doOptimize) {
				optimizeIndex(writer);
			}
		} else {
			throw new Exception("No hanlder was found for ontology: "
					+ getOntologyDisplay(ontologyVersionId, ontologyId,
							displayLabel, format));
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
			List<OntologyBean> ontologies, boolean doBackup, boolean doOptimize)
			throws Exception {
		if (!ontologies.isEmpty()) {
			if (doBackup) {
				backupIndex(writer);
			}

			for (OntologyBean ontology : ontologies) {
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
	 * @param ontologyId
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	private void removeOntology(LuceneIndexWriterWrapper writer,
			Integer ontologyId, boolean doBackup, boolean doOptimize,
			boolean reloadCache) throws Exception {
		List<Integer> ontologyIds = new ArrayList<Integer>(1);
		ontologyIds.add(ontologyId);
		removeOntologies(writer, ontologyIds, doBackup, doOptimize, reloadCache);
	}

	/**
	 * Remove given ontologies from index with options to backup and optimize
	 * the index
	 * 
	 * @param writer
	 * @param ontologyIds
	 * @param doBackup
	 * @param doOptimize
	 * @throws Exception
	 */
	private void removeOntologies(LuceneIndexWriterWrapper writer,
			List<Integer> ontologyIds, boolean doBackup, boolean doOptimize,
			boolean reloadCache) throws Exception {
		if (doBackup) {
			backupIndex(writer);
		}

		if (ontologyIds.size() == 1) {
			Term ontologyIdTerm = generateOntologyIdTerm(ontologyIds.get(0));
			writer.deleteDocuments(ontologyIdTerm);
		} else {
			Query ontologyIdsQuery = generateOntologyIdsQuery(ontologyIds);
			writer.deleteDocuments(ontologyIdsQuery);
		}

		if (reloadCache) {
			reloadSearchCache();
		}

		if (doOptimize) {
			optimizeIndex(writer);
		}

		if (log.isDebugEnabled()) {
			log.debug("Removed ontologies from index: " + ontologyIds);
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
}
