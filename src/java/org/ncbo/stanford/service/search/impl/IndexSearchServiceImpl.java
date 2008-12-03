package org.ncbo.stanford.service.search.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryParser.QueryParser;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.service.search.AbstractSearchService;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

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

	public void indexOntology(Integer ontologyId) throws Exception {
		indexOntology(ontologyId, true, true);
	}

	public void indexOntology(Integer ontologyId, boolean doBackup,
			boolean doOptimize) throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findLatestActiveOntologyVersion(ontologyId);

		if (ontology != null) {
			try {
				LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
						indexPath, analyzer);
				writer.setMergeFactor(indexMergeFactor);
				writer.setMaxMergeDocs(indexMaxMergeDocs);

				indexOntology(writer, ontology, doBackup, doOptimize);

				closeWriter(writer, true);
			} catch (Exception e) {
				handleException(ontology, e, false);
			}
		}
	}

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
			} catch (Exception e) {
				handleException(ontology, e, true);
			}
		}

		optimizeIndex(writer);
		closeWriter(writer, true);

		if (log.isDebugEnabled()) {
			long stop = System.currentTimeMillis(); // stop timing
			log.debug("Finished indexing all ontologies in "
					+ (double) (stop - start) / 1000 / 60 / 60 + " hours.");
		}
	}

	public void removeOntology(Integer ontologyId) throws Exception {
		removeOntology(ontologyId, true, true);
	}

	public void removeOntology(Integer ontologyId, boolean doBackup,
			boolean doOptimize) throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findLatestOntologyVersion(ontologyId);

		if (ontology != null) {
			try {
				LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
						indexPath, analyzer);
				removeOntology(writer, ontology, doBackup, doOptimize);
				closeWriter(writer, true);
			} catch (Exception e) {
				handleException(ontology, e, false);
			}
		}
	}

	public void backupIndex() throws Exception {
		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
				indexPath, analyzer);
		backupIndex(writer);
		closeWriter(writer, false);
	}

	public void optimizeIndex() throws Exception {
		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
				indexPath, analyzer);
		optimizeIndex(writer);
		closeWriter(writer, false);
	}

	private void closeWriter(LuceneIndexWriterWrapper writer,
			boolean reloadCache) throws IOException {
		writer.closeWriter();
		writer = null;

		if (reloadCache) {
			reloadCache();
		}
	}

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
				log.error(e);
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
			removeOntology(writer, ontology, doBackup, false);

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

	private void removeOntology(LuceneIndexWriterWrapper writer,
			VNcboOntology ontology, boolean doBackup, boolean doOptimize)
			throws Exception {
		Integer ontologyId = ontology.getOntologyId();
		String displayLabel = ontology.getDisplayLabel();

		if (doBackup) {
			backupIndex(writer);
		}

		writer.removeOntology(ontologyId);

		if (doOptimize) {
			optimizeIndex(writer);
		}

		if (log.isDebugEnabled()) {
			log.debug("Removed ontology from index: " + displayLabel + " (Id: "
					+ ontology.getId() + ", Ontology Id: " + ontologyId
					+ ", Format: " + ontology.getFormat() + ")");
		}
	}

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
					/ 1000 + " seconds.");
		}
	}

	private void handleException(VNcboOntology ontology, Exception e,
			boolean ignoreErrors) throws Exception {
		Throwable t = e.getCause();
		String msg = null;
		String className = (t == null) ? "" : t.getClass().getName();

		if (e instanceof LBParameterException
				|| (t != null && (className
						.equals("com.mysql.jdbc.exceptions.MySQLSyntaxErrorException") || className
						.equals("com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException")))) {
			msg = "Ontology " + ontology.getDisplayLabel() + " (Id: "
					+ ontology.getId() + ", Ontology Id: "
					+ ontology.getOntologyId()
					+ ") does not exist in the backend store";
		}

		if (ignoreErrors && msg != null) {
			log.error(msg + "\n");
		} else if (ignoreErrors) {
			log.error(e);
			e.printStackTrace();
		} else if (msg != null) {
			throw new Exception(msg);
		} else {
			throw e;
		}
	}

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
