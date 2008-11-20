package org.ncbo.stanford.bean.search;

import java.util.Collection;

import lucene.bean.LuceneSearchBean;

import org.ncbo.stanford.util.paginator.impl.PaginatableList;

public class SearchResultListBean extends PaginatableList<LuceneSearchBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1067248571020887697L;

	/**
	 * 
	 */
	public SearchResultListBean() {
		super();
	}

	/**
	 * @param c
	 */
	public SearchResultListBean(Collection<LuceneSearchBean> c) {
		super(c);
	}

	/**
	 * @param initialCapacity
	 */
	public SearchResultListBean(int initialCapacity) {
		super(initialCapacity);
	}
}
