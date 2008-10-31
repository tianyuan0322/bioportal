package lucene.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import lucene.bean.LuceneSearchDocument;

public interface LuceneSearchManager {
	// in BioPortal, use OntologyBean as the argument
	public Collection<LuceneSearchDocument> generateLuceneDocuments(ResultSet rs)
			throws SQLException;
}
