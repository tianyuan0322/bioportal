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
	private int pageSize;
	private static final String INVALID_PAGESIZE = "Pagesize must be a positive integer.";
	public static final int DEFAULT_PAGESIZE = 50;

	public PaginatorImpl(final Paginatable<E> originalList, final int pageSize)
			throws IllegalArgumentException {
		if (pageSize < 0) {
			throw new IllegalArgumentException(INVALID_PAGESIZE);
		}

		this.originalList = originalList;
		this.pageSize = pageSize;
	}

	public PaginatorImpl(final Paginatable<E> originalList) {
		this(originalList, DEFAULT_PAGESIZE);
	}

	public Page<E> getAll() {
		Page<E> result = null;
		int resultSize;

		if (originalList != null && (resultSize = originalList.size()) > 0) {
			result = new Page<E>(1, 1, resultSize, resultSize, originalList);
		}

		return result;
	}

	public Page<E> getFirstPage() {
		Page<E> result = null;
		int resultSize;

		if (originalList != null && (resultSize = originalList.size()) > 0) {
			result = new Page<E>(1, getNumPages(), pageSize, resultSize,
					iterateFrom(0));
		}

		return result;
	}

	public Page<E> getLastPage() {
		Page<E> result = null;
		int resultSize;

		if (originalList != null && (resultSize = originalList.size()) > 0) {
			final int totalPage = getNumPages();
			final int startIndex = (totalPage - 1) * pageSize;
			result = new Page<E>(totalPage, totalPage, pageSize, resultSize,
					iterateFrom(startIndex));
		}

		return result;
	}

	public Page<E> getNextPage(final Integer currentPageNum) {
		int numPages = getNumPages();
		Page<E> result = null;
		Page<E> currentPage = new Page<E>(currentPageNum, numPages);
		int resultSize;

		if (currentPage.isLastPage()) {
			result = getLastPage();
		} else if (originalList != null
				&& (resultSize = originalList.size()) > 0) {
			result = new Page<E>(currentPage.getPageNum() + 1, numPages,
					pageSize, resultSize, iterateFrom(currentPage.getPageNum()
							* pageSize));
		}

		return result;
	}

	public Page<E> getPrevPage(final Integer currentPageNum) {
		int numPages = getNumPages();
		Page<E> result = null;
		Page<E> currentPage = new Page<E>(currentPageNum, numPages);
		int resultSize;

		if (currentPage.isFirstPage()) {
			result = getFirstPage();
		} else if (originalList != null
				&& (resultSize = originalList.size()) > 0) {
			result = new Page<E>(currentPage.getPageNum() - 1, numPages,
					pageSize, resultSize,
					iterateFrom((currentPage.getPageNum() - 2) * pageSize));
		}

		return result;
	}

	public int getNumPages() {
		if (originalList == null || originalList.size() <= 0) {
			return 0;
		}

		final int totalSize = originalList.size();

		return ((totalSize - 1) / pageSize) + 1;
	}

	private Paginatable<E> iterateFrom(final int fromIndex) {
		final int totalSize = originalList.size();

		int toIndex = fromIndex + pageSize;

		if (toIndex > totalSize) {
			toIndex = totalSize;
		}

		return originalList.sublist(fromIndex, toIndex);
	}
}
