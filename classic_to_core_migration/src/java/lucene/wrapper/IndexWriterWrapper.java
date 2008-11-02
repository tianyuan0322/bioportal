package lucene.wrapper;

import java.io.IOException;

import lucene.bean.LuceneSearchDocument;
import lucene.bean.LuceneSearchField;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.LockObtainFailedException;

public class IndexWriterWrapper {
	private IndexWriter writer;

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
		writer = new IndexWriter(indexPath, analyzer, create,
				IndexWriter.MaxFieldLength.LIMITED);
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

	public void optimize() throws IOException {
		writer.optimize();
	}

	public void closeWriter() throws IOException {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

	private void addFields(Document doc, LuceneSearchDocument searchDoc) {
		addField(doc, searchDoc.getOntologyId());
		addField(doc, searchDoc.getRecordType());
		addField(doc, searchDoc.getFrameName());
		addField(doc, searchDoc.getContents());
		addField(doc, searchDoc.getLiteralContents());
	}

	private void addField(Document doc, LuceneSearchField field) {
		doc.add(new Field(field.getLabel(), field.getContents(), field
				.getStore(), field.getIndex()));
	}
}
