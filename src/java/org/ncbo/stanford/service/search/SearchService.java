package org.ncbo.stanford.service.search;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Collection;

import org.apache.lucene.search.Query;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

public interface SearchService {

	public void indexAllOntologies() throws Exception;

	public void indexOntology(Integer ontologyId) throws Exception;

	public void indexOntology(Integer ontologyId, boolean doBackup)
			throws Exception;

	// TODO: in BP, replace rs with OntologyBean
	public void indexOntology(LuceneIndexWriterWrapper writer, ResultSet rs,
			boolean doBackup) throws Exception;

	public void removeOntology(Integer ontologyId) throws Exception;

	// TODO: in BP, replace rs with OntologyBean
	public void removeOntology(LuceneIndexWriterWrapper writer, ResultSet rs,
			boolean doBackup) throws Exception;

	public void backupIndex() throws Exception;

	public void backupIndex(LuceneIndexWriterWrapper writer) throws Exception;

	public SearchResultListBean executeQuery(String expr,
			boolean includeProperties, boolean isExactMatch) throws IOException;

	public SearchResultListBean executeQuery(String expr,
			Collection<Integer> ontologyIds, boolean includeProperties,
			boolean isExactMatch) throws IOException;

	public SearchResultListBean executeQuery(Query query) throws IOException;

	public Query generateLuceneSearchQuery(Collection<Integer> ontologyIds,
			String expr, boolean includeProperties, boolean isExactMatch)
			throws IOException;
}
