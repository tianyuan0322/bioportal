package org.ncbo.stanford.manager.search;

import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

/**
 * Provides an interface to manage API specific search-related functionality
 * 
 * @author Michael Dorf
 * 
 */
public interface OntologySearchManager {
	public void indexOntology(LuceneIndexWriterWrapper writer,
			VNcboOntology ontology) throws Exception;
}
