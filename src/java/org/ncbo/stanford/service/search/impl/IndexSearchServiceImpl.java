package org.ncbo.stanford.service.search.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.service.search.AbstractSearchService;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;
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

	public static final MaxFieldLength MAX_FIELD_LENGTH = IndexWriter.MaxFieldLength.LIMITED;
	private static final String OLD_BACKUP_FILE_EXTENSION = "bak";

	private String indexBackupPath;
	private int indexMergeFactor;
	private int indexMaxMergeDocs;

	// non-injected properties
	private LuceneIndexWriterWrapper writer = null;

	@PostConstruct
	public void initWriter() {
		try {
			writer = new LuceneIndexWriterWrapper(indexDir, analyzer, false,
					indexMergeFactor, indexMaxMergeDocs);
		} catch (IOException e) {
			e.printStackTrace();
			log
					.error("Could not initialize LuceneIndexWriterWrapper at startup: "
							+ e);
		}
	}

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

		if (doBackup) {
			backupIndex();
		}

		// close writer on the existing index so it can be removed
		writer.closeWriter();
		LuceneIndexWriterWrapper writerAll = new LuceneIndexWriterWrapper(
				indexDir, analyzer, true, indexMergeFactor, indexMaxMergeDocs);

		List<OntologyBean> ontologies = ontologyMetadataManager
				.findLatestActiveOntologyOrOntologyViewVersions();

		for (OntologyBean ontology : ontologies) {
			try {
				indexOntology(writerAll, ontology, false, false);

				// commit changes to writer so they are visible to the searcher
				writerAll.commit();
			} catch (Exception e) {
				log.error("An error occurred while indexing ontology: "
						+ ontology);
				e.printStackTrace();
				log.error(e);
			}
		}

		if (doOptimize) {
			writerAll.optimize();
		}

		writerAll.closeWriter();

		if (log.isInfoEnabled()) {
			long stop = System.currentTimeMillis(); // stop timing
			log.info("Finished indexing all ontologies in "
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
		List<OntologyBean> ontologies = ontologyMetadataManager
				.findLatestActiveOntologyOrOntologyViewVersions(ontologyIdList);

		removeOntologies(writer, ontologyIdList, doBackup, false);

		if (!ontologies.isEmpty()) {
			indexOntologies(writer, ontologies, false, doOptimize);
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
		removeOntologies(writer, ontologyIds, doBackup, doOptimize);
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
	public void removeOntologies(LuceneIndexWriterWrapper writer,
			List<Integer> ontologyIds, boolean doBackup, boolean doOptimize)
			throws Exception {
		if (doBackup) {
			backupIndex();
		}

		if (ontologyIds.size() == 1) {
			Term ontologyIdTerm = generateOntologyIdTerm(ontologyIds.get(0));
			writer.deleteDocuments(ontologyIdTerm);
		} else {
			Query ontologyIdsQuery = generateOntologyIdsQuery(ontologyIds);
			writer.deleteDocuments(ontologyIdsQuery);
		}

		emptySearchCache();

		if (doOptimize) {
			optimizeIndex();
		}

		if (log.isDebugEnabled()) {
			log.debug("Removed ontologies from index: " + ontologyIds);
		}
	}

	/**
	 * Create a backup of the existing search index
	 * 
	 * @throws Exception
	 */
	public void backupIndex() throws Exception {
		long start = 0;
		long stop = 0;

		if (log.isDebugEnabled()) {
			log.debug("Backing up index...");
			start = System.currentTimeMillis();
		}

		backupIndexByFileCopy(indexBackupPath);

		if (log.isDebugEnabled()) {
			stop = System.currentTimeMillis(); // stop timing
			log.debug("Finished backing up index in " + (double) (stop - start)
					/ 1000 + " seconds.");
		}
	}

	/**
	 * Run an optimization command on the existing index
	 * 
	 * @throws Exception
	 */
	public void optimizeIndex() throws Exception {
		optimizeIndex(writer);
	}

	/**
	 * Run an optimization command on the existing index
	 * 
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
			removeOntology(writer, ontologyId, doBackup, false);

			if (log.isDebugEnabled()) {
				log.info("Adding ontology to index: "
						+ getOntologyDisplay(ontologyVersionId, ontologyId,
								displayLabel, format));
				start = System.currentTimeMillis();
			}

			mgr.indexOntology(writer, ontology);

			if (log.isDebugEnabled()) {
				stop = System.currentTimeMillis(); // stop timing
				log.info("Finished indexing ontology: "
						+ getOntologyDisplay(ontologyVersionId, ontologyId,
								displayLabel, format) + " in "
						+ (double) (stop - start) / 1000 / 60 + " minutes.\n");
			}

			emptySearchCache();

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
				backupIndex();
			}

			for (OntologyBean ontology : ontologies) {
				try {
					indexOntology(writer, ontology, false, false);
				} catch (Exception e) {
					log.error("An error occurred while indexing ontology: "
							+ ontology);
					e.printStackTrace();
					log.error(e);
				}
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
			Integer ontologyId, boolean doBackup, boolean doOptimize)
			throws Exception {
		List<Integer> ontologyIds = new ArrayList<Integer>(1);
		ontologyIds.add(ontologyId);
		removeOntologies(writer, ontologyIds, doBackup, doOptimize);
	}

	private void backupIndexByFileCopy(String backupPath) throws IOException {
		// for the moment, can't backup index unless it's located on a
		// filesystem
		if (!(indexDir instanceof FSDirectory)) {
			return;
		}

		File indexFilePath = ((FSDirectory) indexDir).getFile();
		String[] children = indexFilePath.list();
		File backupFilePath = new File(backupPath);

		if (!backupFilePath.exists()) {
			backupFilePath.mkdirs();
		} else {
			renameBackupIndex(backupFilePath);
		}

		for (int i = 0; i < children.length; i++) {
			File f = new File(indexFilePath, children[i]);

			if (f.isFile()
					&& !f.getName().equalsIgnoreCase(
							IndexWriter.WRITE_LOCK_NAME)) {
				AbstractFilePathHandler.copyFile(f, new File(backupFilePath
						+ File.separator + f.getName()));
			}
		}

		removeOldBackup(backupFilePath);
	}

	private void renameBackupIndex(File backupFilePath) throws IOException {
		if (backupFilePath.exists()) {
			String[] children = backupFilePath.list();

			for (int i = 0; i < children.length; i++) {
				File f = new File(backupFilePath, children[i]);

				if (f.isFile()) {
					f.renameTo(new File(backupFilePath, f.getName() + "."
							+ OLD_BACKUP_FILE_EXTENSION));
				}
			}
		}
	}

	private void removeOldBackup(File backupFilePath) {
		if (backupFilePath.exists()) {
			String[] children = backupFilePath.list();

			for (int i = 0; i < children.length; i++) {
				File f = new File(backupFilePath, children[i]);

				if (f.isFile() && isOldBackupFile(f.getName())) {
					f.delete();
				}
			}
		}
	}

	private boolean isOldBackupFile(String filename) {
		return filename.toLowerCase().endsWith(OLD_BACKUP_FILE_EXTENSION);
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
