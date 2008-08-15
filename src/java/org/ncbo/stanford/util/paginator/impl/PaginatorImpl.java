package org.ncbo.stanford.util.paginator.impl;

import org.ncbo.stanford.util.paginator.Paginatable;
import org.ncbo.stanford.util.paginator.Paginator;

/**
 * 
 * @author Michael Dorf
 * 
 * Default implementation of the Paginator interface. Provides functionality to
 * paginate over any set.
 * 
 * @param <E> -
 *            type of objects to paginate over
 */
public class PaginatorImpl<E> implements Paginator<E> {

	private Paginatable<E> originalList;
	private int pagesize;
	private static final String INVALID_PAGESIZE = "Pagesize must be a positive integer.";
	public static final int DEFAULT_PAGESIZE = 50;

	public PaginatorImpl(final Paginatable<E> originalList, final int pagesize)
			throws IllegalArgumentException {
		if (pagesize <= 0) {
			throw new IllegalArgumentException(INVALID_PAGESIZE);
		}

		this.originalList = originalList;
		this.pagesize = pagesize;
	}

	public PaginatorImpl(final Paginatable<E> originalList) {
		this(originalList, DEFAULT_PAGESIZE);
	}

	public Page<E> getFirstPage() {
		Page<E> result = null;

		if (originalList != null && originalList.size() > 0) {
			result = new Page<E>(1, getTotalPage(), pagesize, iterateFrom(0));
		}

		return result;
	}

	public Page<E> getLastPage() {
		Page<E> result = null;

		if (originalList != null && originalList.size() > 0) {
			final int totalPage = getTotalPage();
			final int startIndex = (totalPage - 1) * pagesize;
			result = new Page<E>(totalPage, totalPage, pagesize,
					iterateFrom(startIndex));
		}

		return result;
	}

	public Page<E> getNextPage(final Page<E> currentPage) {
		if (currentPage == null) {
			return getFirstPage();
		}

		if (currentPage.isLastPage()) {
			return currentPage;
		}

		Page<E> result = null;

		if (originalList != null) {
			result = new Page<E>(currentPage.getPageNum() + 1, currentPage
					.getTotalPage(), pagesize, iterateFrom(currentPage
					.getPageNum()
					* pagesize));
		}

		return result;
	}

	public Page<E> getPrevPage(final Page<E> currentPage) {
		if (currentPage == null) {
			return getFirstPage();
		}

		if (currentPage.isFirstPage()) {
			return currentPage;
		}

		Page<E> result = null;

		if (originalList != null) {
			result = new Page<E>(currentPage.getPageNum() - 1, currentPage
					.getTotalPage(), pagesize, iterateFrom((currentPage
					.getPageNum() - 2)
					* pagesize));
		}

		return result;
	}

	private Paginatable<E> iterateFrom(final int fromIndex) {
		final int totalSize = originalList.size();

		int toIndex = fromIndex + pagesize;

		if (toIndex > totalSize) {
			toIndex = totalSize;
		}

		return originalList.sublist(fromIndex, toIndex);
	}

	private int getTotalPage() {
		if (originalList == null || originalList.size() <= 0) {
			return 0;
		}

		final int totalSize = originalList.size();

		return ((totalSize - 1) / pagesize) + 1;
	}
}
