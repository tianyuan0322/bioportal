package org.ncbo.stanford.util.paginator;

import org.ncbo.stanford.util.paginator.impl.Page;

public interface Paginator<E> {

	public Page<E> getFirstPage();

	public Page<E> getLastPage();

	public Page<E> getNextPage(Page<E> currentPage);

	public Page<E> getPrevPage(Page<E> currentPage);
}
