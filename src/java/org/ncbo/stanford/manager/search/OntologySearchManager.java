package org.ncbo.stanford.manager.search;

import java.sql.ResultSet;

import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;


public interface OntologySearchManager {
	// in BioPortal, use OntologyBean as the argument
	public void indexOntology(LuceneIndexWriterWrapper writer, ResultSet rs)
			throws Exception;
}
