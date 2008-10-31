package lucene.manager;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import lucene.wrapper.IndexWriterWrapper;

public interface LuceneSearchManager {
	// in BioPortal, use OntologyBean as the argument
	public void indexOntology(IndexWriterWrapper writer, ResultSet rs)
			throws SQLException, IOException;
}
