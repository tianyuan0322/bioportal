package org.ncbo.stanford.util.paginator;

import org.ncbo.stanford.util.paginator.impl.Page;

/**
 * Interface that allows paginating over any result sets
 * 
 * @author Michael Dorf
 * 
 * @param <E>
 */
public interface Paginator<E> {

	/**
	 * Returns the entire list of results
	 * 
	 * @return
	 */
	public Page<E> getAll();
	
	/**
	 * Returns the first page of results
	 * 
	 * @return
	 */
	public Page<E> getFirstPage();

	/**
	 * Returns the last page of results
	 * 
	 * @return
	 */
	public Page<E> getLastPage();

	/**
	 * Returns the next page of results
	 * 
	 * @param currentPage
	 * @return
	 */
	public Page<E> getNextPage(Page<E> currentPage);

	/**
	 * Returns the previous page of results
	 * 
	 * @param currentPage
	 * @return
	 */
	public Page<E> getPrevPage(Page<E> currentPage);
}
