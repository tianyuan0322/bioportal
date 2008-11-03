package lucene.manager;

import java.sql.ResultSet;

import lucene.wrapper.IndexWriterWrapper;

public interface LuceneSearchManager {
	// in BioPortal, use OntologyBean as the argument
	public void indexOntology(IndexWriterWrapper writer, ResultSet rs)
			throws Exception;
}
