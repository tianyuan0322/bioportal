package org.ncbo.stanford.util.paginator.impl;

import org.ncbo.stanford.util.paginator.Paginatable;
import org.ncbo.stanford.util.paginator.Paginator;

/**
 * 
 * @author Michael Dorf
 * 
 *         Default implementation of the Paginator interface. Provides
 *         functionality to paginate over any set.
 * 
 * @param <E>
 *            - type of objects to paginate over
 */
public class PaginatorImpl<E> implements Paginator<E> {

	private Paginatable<E> originalList;
	private Paginatable<E> pageList;
	private int pageSize;
	private int totalSize;
	private static final String INVALID_PAGESIZE = "Pagesize must be a positive integer.";
	public static final int DEFAULT_PAGESIZE = 50;

	public PaginatorImpl(final Paginatable<E> pageList, final int pageSize,
			final int pageNum, final int totalSize) {
		if (pageSize < 0) {
			throw new IllegalArgumentException(INVALID_PAGESIZE);
		}

		this.pageList = pageList;
		this.totalSize = totalSize;
		this.pageSize = pageSize;
	}

	public PaginatorImpl(final Paginatable<E> originalList, final int pageSize)
			throws IllegalArgumentException {
		if (pageSize < 0) {
			throw new IllegalArgumentException(INVALID_PAGESIZE);
		}

		this.originalList = originalList;
		this.totalSize = originalList.size();
		this.pageSize = pageSize;
	}

	public PaginatorImpl(final Paginatable<E> originalList) {
		this(originalList, DEFAULT_PAGESIZE);
	}

	public Page<E> getAll() {
		Page<E> result = null;

		if (originalList != null && totalSize > 0) {
			result = new Page<E>(1, 1, totalSize, totalSize, originalList);
		}

		return result;
	}

	public Page<E> getFirstPage() {
		Page<E> result = null;

		if (originalList != null && totalSize > 0) {
			result = new Page<E>(1, getNumPages(), pageSize, totalSize,
					iterateFrom(0));
		}

		return result;
	}

	public Page<E> getLastPage() {
		Page<E> result = null;

		if (originalList != null && totalSize > 0) {
			final int totalPage = getNumPages();
			final int startIndex = (totalPage - 1) * pageSize;
			result = new Page<E>(totalPage, totalPage, pageSize, totalSize,
					iterateFrom(startIndex));
		}

		return result;
	}

	public Page<E> getCurrentPage(final Integer currentPageNum) {
		int numPages = getNumPages();
		Page<E> result = null;
		Page<E> currentPage = new Page<E>(currentPageNum, numPages);

		if (pageList != null && totalSize > 0) {
			result = new Page<E>(currentPage.getPageNum(), numPages, pageSize,
					totalSize, pageList);
		}

		return result;
	}

	public Page<E> getNextPage(final Integer currentPageNum) {
		int numPages = getNumPages();
		Page<E> result = null;
		Page<E> currentPage = new Page<E>(currentPageNum, numPages);

		if (currentPage.isLastPage()) {
			result = getLastPage();
		} else if (originalList != null && totalSize > 0) {
			result = new Page<E>(currentPage.getPageNum() + 1, numPages,
					pageSize, totalSize, iterateFrom(currentPage.getPageNum()
							* pageSize));
		}

		return result;
	}

	public Page<E> getPrevPage(final Integer currentPageNum) {
		int numPages = getNumPages();
		Page<E> result = null;
		Page<E> currentPage = new Page<E>(currentPageNum, numPages);

		if (currentPage.isFirstPage()) {
			result = getFirstPage();
		} else if (originalList != null && totalSize > 0) {
			result = new Page<E>(currentPage.getPageNum() - 1, numPages,
					pageSize, totalSize,
					iterateFrom((currentPage.getPageNum() - 2) * pageSize));
		}

		return result;
	}

	public int getNumPages() {
		if ((originalList == null && pageList == null) || totalSize <= 0) {
			return 0;
		}

		return ((totalSize - 1) / pageSize) + 1;
	}

	private Paginatable<E> iterateFrom(final int fromIndex) {
		int toIndex = fromIndex + pageSize;

		if (toIndex > totalSize) {
			toIndex = totalSize;
		}

		return originalList.sublist(fromIndex, toIndex);
	}
}
