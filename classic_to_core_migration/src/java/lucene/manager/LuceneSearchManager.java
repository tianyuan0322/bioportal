package lucene.manager;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.lucene.index.IndexWriter;

public interface LuceneSearchManager {
	// in BioPortal, use OntologyBean as the argument
	public void indexOntology(IndexWriter writer, ResultSet rs)
			throws SQLException, IOException;
}
