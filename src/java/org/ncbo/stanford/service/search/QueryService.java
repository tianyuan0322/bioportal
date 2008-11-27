package org.ncbo.stanford.service.search;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.search.Query;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.util.paginator.impl.Page;

public interface QueryService {

	public Page<SearchBean> executeQuery(String expr,
			boolean includeProperties, boolean isExactMatch, Integer pageSize,
			Integer pageNum) throws IOException;

	public Page<SearchBean> executeQuery(String expr,
			boolean includeProperties, boolean isExactMatch) throws IOException;

	public Page<SearchBean> executeQuery(String expr,
			Collection<Integer> ontologyIds, boolean includeProperties,
			boolean isExactMatch, Integer pageSize, Integer pageNum)
			throws IOException;

	public Page<SearchBean> executeQuery(String expr,
			Collection<Integer> ontologyIds, boolean includeProperties,
			boolean isExactMatch) throws IOException;

	public Page<SearchBean> executeQuery(Query query, Integer pageSize,
			Integer pageNum) throws IOException;

	public Page<SearchBean> executeQuery(Query query) throws IOException;

	public Query generateLuceneSearchQuery(Collection<Integer> ontologyIds,
			String expr, boolean includeProperties, boolean isExactMatch)
			throws IOException;
}
