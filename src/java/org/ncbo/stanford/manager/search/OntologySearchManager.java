package org.ncbo.stanford.manager.search;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

/**
 * Provides an interface to manage API specific search-related functionality
 * 
 * @author Michael Dorf
 * 
 */
public interface OntologySearchManager {
	public void indexOntology(LuceneIndexWriterWrapper writer,
			OntologyBean ontology) throws Exception;
}
