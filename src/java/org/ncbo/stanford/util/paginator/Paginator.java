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
	 * Returns the current results page
	 * 
	 * @param currentPageNum
	 * @return
	 */
	public Page<E> getCurrentPage(Integer currentPageNum);

	/**
	 * Returns the next page of results
	 * 
	 * @param currentPageNum
	 * @return
	 */
	public Page<E> getNextPage(Integer currentPageNum);

	/**
	 * Returns the previous page of results
	 * 
	 * @param currentPageNum
	 * @return
	 */
	public Page<E> getPrevPage(Integer currentPageNum);

	/**
	 * Returns the total number of pages
	 * 
	 * @return
	 */
	public int getNumPages();
}
