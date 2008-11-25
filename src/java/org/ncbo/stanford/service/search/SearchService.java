package org.ncbo.stanford.service.search;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.search.Query;
import org.ncbo.stanford.bean.search.SearchResultListBean;

public interface SearchService {

	public void indexAllOntologies() throws Exception;

	public void indexOntology(Integer ontologyId) throws Exception;

	public void indexOntology(Integer ontologyId, boolean doBackup)
			throws Exception;

	public void removeOntology(Integer ontologyId) throws Exception;

	public void backupIndex() throws Exception;

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
