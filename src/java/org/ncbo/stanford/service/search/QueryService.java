package org.ncbo.stanford.service.search;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.search.Query;
import org.ncbo.stanford.bean.search.SearchResultListBean;

public interface QueryService {

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
