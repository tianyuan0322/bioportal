package lucene.wrapper;

import java.io.File;
import java.io.IOException;

import lucene.bean.LuceneSearchDocument;
import lucene.bean.LuceneSearchField;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;

public class IndexWriterWrapper {
	public static final MaxFieldLength MAX_FIELD_LENGTH = IndexWriter.MaxFieldLength.LIMITED;
	private static final String OLD_BACKUP_FILE_EXTENSION = "bak";
	private IndexWriter writer;
	private String indexPath;

	// private Analyzer analyzer;

	/**
	 * @param indexPath
	 * @param analyzer
	 * @param create
	 * @throws IOException
	 */
	public IndexWriterWrapper(String indexPath, Analyzer analyzer,
			boolean create) throws IOException {
		super();
		this.indexPath = indexPath;
		writer = new IndexWriter(indexPath, analyzer, create, MAX_FIELD_LENGTH);
	}

	/**
	 * @param indexPath
	 * @param analyzer
	 * @throws IOException
	 */
	public IndexWriterWrapper(String indexPath, Analyzer analyzer)
			throws IOException {
		super();
		this.indexPath = indexPath;
		writer = new IndexWriter(indexPath, analyzer,
				IndexWriter.MaxFieldLength.LIMITED);
	}

	public void addDocument(LuceneSearchDocument searchDoc) throws IOException {
		Document doc = new Document();

		addFields(doc, searchDoc);
		writer.addDocument(doc);
	}

	public void removeOntology(Integer ontologyId) throws IOException {
		Term term = new Term(LuceneSearchDocument.ONTOLOGY_ID_FIELD_LABEL,
				ontologyId.toString());
		writer.deleteDocuments(term);
	}

	public void setMergeFactor(Integer mergeFactor) {
		writer.setMergeFactor(mergeFactor);
	}

	public void optimize() throws IOException {
		writer.optimize();
	}

	public void closeWriter() throws IOException {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

	public void backupIndexByCopy(String backupPath) throws IOException {
		File indexFilePath = new File(indexPath);
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

		IndexReader reader = IndexReader.open(indexPath);
		IndexWriter backupWriter = new IndexWriter(backupPath, writer
				.getAnalyzer(), true, MAX_FIELD_LENGTH);

		backupWriter.addIndexes(new IndexReader[] { reader });
		backupWriter.close();
		reader.close();

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

	private void addFields(Document doc, LuceneSearchDocument searchDoc) {
		addField(doc, searchDoc.getOntologyId());
		addField(doc, searchDoc.getRecordType());
		addField(doc, searchDoc.getConceptId());
		addField(doc, searchDoc.getConceptIdShort());
		addField(doc, searchDoc.getPreferredName());
		addField(doc, searchDoc.getContents());
		addField(doc, searchDoc.getLiteralContents());
	}

	private void addField(Document doc, LuceneSearchField field) {
		doc.add(new Field(field.getLabel(), field.getContents(), field
				.getStore(), field.getIndex()));
	}
}
