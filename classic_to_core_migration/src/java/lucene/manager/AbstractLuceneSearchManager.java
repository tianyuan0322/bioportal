package lucene.manager;

import java.io.IOException;

import lucene.bean.LuceneSearchDocument;
import lucene.bean.LuceneSearchField;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

public abstract class AbstractLuceneSearchManager implements
		LuceneSearchManager {

	protected void addDocument(IndexWriter writer,
			LuceneSearchDocument searchDoc) throws IOException {
		Document doc = new Document();

		addField(doc, searchDoc.getOntologyId());
		addField(doc, searchDoc.getRecordType());
		addField(doc, searchDoc.getFrameName());
		addField(doc, searchDoc.getContents());
		addField(doc, searchDoc.getLiteralContents());

		writer.addDocument(doc);
	}

	private void addField(Document doc, LuceneSearchField field) {
		doc.add(new Field(field.getLabel(), field.getContents(), field
				.getStore(), field.getIndex()));
	}
}
