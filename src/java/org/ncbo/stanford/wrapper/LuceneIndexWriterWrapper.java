package org.ncbo.stanford.wrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.ncbo.stanford.bean.search.SearchField;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.util.helper.reflection.ReflectionHelper;

public class LuceneIndexWriterWrapper {
	public static final MaxFieldLength MAX_FIELD_LENGTH = IndexWriter.MaxFieldLength.LIMITED;
	private IndexWriter writer;
	private Analyzer analyzer;
	private Directory indexDir;
	private Integer mergeFactor;
	private Integer maxMergeDocs;
	private AtomicInteger activeWrites = new AtomicInteger(0);

	/**
	 * @param indexDir
	 * @param analyzer
	 * @param create
	 * @throws IOException
	 */
	public LuceneIndexWriterWrapper(Directory indexDir, Analyzer analyzer,
			boolean create, Integer mergeFactor, Integer maxMergeDocs)
			throws IOException {
		super();
		this.indexDir = indexDir;
		this.analyzer = analyzer;
		this.mergeFactor = mergeFactor;
		this.maxMergeDocs = maxMergeDocs;

		if (create) {
			initWriter(create);
		}
	}

	/**
	 * @param indexDir
	 * @param analyzer
	 * @throws IOException
	 */
	public LuceneIndexWriterWrapper(Directory indexDir, Analyzer analyzer)
			throws IOException {
		this(indexDir, analyzer, false, null, null);
	}

	public void commit() throws IOException {
		writer.commit();
	}

	public void optimize() throws IOException {
		writer.optimize();
	}

	public void deleteDocuments(Query query) throws IOException {
		writer.deleteDocuments(query);
	}

	public void deleteDocuments(Term term) throws IOException {
		writer.deleteDocuments(term);
	}

	public void addDocuments(List<SearchIndexBean> indexBeans)
			throws IOException {
		for (SearchIndexBean indexBean : indexBeans) {
			Document doc = new Document();
			addFields(doc, indexBean);
			writer.addDocument(doc);
		}
	}

	public void closeWriter() throws IOException {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

	/**
	 * The methods closeWriterIfInactive and initWriter must be synchronized to
	 * ensure the active write counters work correctly
	 * 
	 * @throws IOException
	 */
	public synchronized void closeWriterIfInactive() throws IOException {
		int writers = activeWrites.decrementAndGet();

		if (writers == 0) {
			writer.close();
			writer = null;
		}
	}

	private synchronized void initWriter(boolean create) throws IOException {
		if (writer == null) {
			try {
				writer = new IndexWriter(indexDir, analyzer, create,
						MAX_FIELD_LENGTH);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				writer = new IndexWriter(indexDir, analyzer, true,
						MAX_FIELD_LENGTH);
			}

			if (mergeFactor != null) {
				writer.setMergeFactor(mergeFactor);
			}

			if (maxMergeDocs != null) {
				writer.setMaxMergeDocs(maxMergeDocs);
			}
		}
		activeWrites.incrementAndGet();
	}

	private void addFields(Document doc, SearchIndexBean indexBean) {
		List<java.lang.reflect.Field> result = ReflectionHelper
				.getAllNonStaticFields(indexBean);

		for (java.lang.reflect.Field field : result) {
			field.setAccessible(true);
			SearchField value;

			try {
				value = (SearchField) field.get(indexBean);
				addField(doc, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private void addField(Document doc, SearchField field) {
		doc.add(new Field(field.getLabel(), field.getContents(), field
				.getStore(), field.getIndex()));
	}

	public void setMergeFactor(Integer mergeFactor) {
		writer.setMergeFactor(mergeFactor);
	}

	public void setMaxMergeDocs(Integer maxMergeDocs) {
		writer.setMaxMergeDocs(maxMergeDocs);
	}

	public void initWriter() throws IOException {
		initWriter(false);
	}
}
