package org.ncbo.stanford.manager.search;

import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

public interface OntologySearchManager {
	public void indexOntology(LuceneIndexWriterWrapper writer,
			VNcboOntology ontology) throws Exception;
}
