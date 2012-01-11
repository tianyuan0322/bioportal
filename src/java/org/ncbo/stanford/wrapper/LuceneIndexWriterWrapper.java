package org.ncbo.stanford.wrapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.ncbo.stanford.bean.search.SearchField;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.util.helper.reflection.ReflectionHelper;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;

public class LuceneIndexWriterWrapper {
	public static final MaxFieldLength MAX_FIELD_LENGTH = IndexWriter.MaxFieldLength.LIMITED;
	private static final String OLD_BACKUP_FILE_EXTENSION = "bak";
	private IndexWriter writer;
	private Directory indexDir;

	/**
	 * @param indexDir
	 * @param analyzer
	 * @param create
	 * @throws IOException
	 */
	public LuceneIndexWriterWrapper(Directory indexDir, Analyzer analyzer,
			boolean create) throws IOException {
		super();
		this.indexDir = indexDir;
		writer = new IndexWriter(indexDir, analyzer, create, MAX_FIELD_LENGTH);
	}

	/**
	 * @param indexDir
	 * @param analyzer
	 * @throws IOException
	 */
	public LuceneIndexWriterWrapper(Directory indexDir, Analyzer analyzer)
			throws IOException {
		super();
		this.indexDir = indexDir;
		writer = new IndexWriter(indexDir, analyzer, MAX_FIELD_LENGTH);
	}

	public void addDocument(SearchIndexBean indexBean) throws IOException {
		Document doc = new Document();

		addFields(doc, indexBean);
		writer.addDocument(doc);
	}

	public void deleteDocuments(Term term) throws IOException {
		writer.deleteDocuments(term);
	}

	public void deleteDocuments(Query query) throws IOException {
		writer.deleteDocuments(query);
	}

	public void setMergeFactor(Integer mergeFactor) {
		writer.setMergeFactor(mergeFactor);
	}

	public void setMaxMergeDocs(Integer maxMergeDocs) {
		writer.setMaxMergeDocs(maxMergeDocs);
	}

	public void optimize() throws IOException {
		writer.optimize();
	}

	public void commit() throws IOException {
		writer.commit();
	}

	public void expungeDeletes() throws IOException {
		writer.expungeDeletes();
	}

	public void closeWriter() throws IOException {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

	public void backupIndexByFileCopy(String backupPath) throws IOException {
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

	public void backupIndexByReading(String backupPath) throws IOException {
		File backupFilePath = new File(backupPath);

		if (backupFilePath.exists()) {
			renameBackupIndex(backupFilePath);
		}

		FSDirectory backupDir = FSDirectory.open(new File(backupPath));
		IndexReader reader = IndexReader.open(indexDir, false);
		IndexWriter backupWriter = new IndexWriter(backupDir, writer
				.getAnalyzer(), true, MAX_FIELD_LENGTH);

		backupWriter.addIndexes(new IndexReader[] { reader });
		backupWriter.close();
		reader.close();
		backupDir.close();

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
}
