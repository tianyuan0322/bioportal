package lucene.wrapper;

import java.io.IOException;

import lucene.bean.LuceneSearchDocument;
import lucene.bean.LuceneSearchField;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.LockObtainFailedException;

public class IndexWriterWrapper {
	public static final MaxFieldLength MAX_FIELD_LENGTH = IndexWriter.MaxFieldLength.LIMITED;
	private IndexWriter writer;
	private String indexPath;

	// private Analyzer analyzer;

	/**
	 * @param indexPath
	 * @param analyzer
	 * @param create
	 * @throws IOException
	 * @throws LockObtainFailedException
	 * @throws CorruptIndexException
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
	 * @throws LockObtainFailedException
	 * @throws CorruptIndexException
	 */
	public IndexWriterWrapper(String indexPath, Analyzer analyzer)
			throws IOException {
		super();
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

	public void backupIndex(String backupPath) throws IOException {
		IndexReader reader = IndexReader.open(indexPath);
		IndexWriter backupWriter = new IndexWriter(backupPath, writer
				.getAnalyzer(), true, MAX_FIELD_LENGTH);

		backupWriter.addIndexes(new IndexReader[] { reader });
		backupWriter.close();
		reader.close();
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
