package org.ncbo.stanford.util.paginator.impl;

import java.io.Serializable;
import java.util.Iterator;

import org.ncbo.stanford.util.paginator.Paginatable;

/**
 * A class that encapsulates a single page of paginated results. It keeps track
 * of the current page number, the size and the resultset for the current page.
 * 
 * @author Michael Dorf
 * 
 * @param <E>
 */
public class Page<E> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7375888859434573070L;

	private int pageNum;
	private int numPages;
	private int pageSize;
	private int numResultsPage;
	private int numResultsTotal;
	private Paginatable<E> contents;

	public Page(final int pageNum, final int numPages) {
		this.pageNum = (pageNum > numPages) ? numPages : pageNum;
		this.numPages = numPages;
	}

	public Page(final int pageNum, final int numPages, final int pageSize,
			final int numResultsTotal, final Paginatable<E> contents) {
		this(pageNum, numPages);
		this.pageSize = pageSize;
		this.numResultsTotal = numResultsTotal;
		this.contents = contents;
		this.numResultsPage = (contents != null) ? contents.size() : 0;
	}

	/**
	 * @return the pageNum
	 */
	public int getPageNum() {
		return pageNum;
	}

	/**
	 * @return the numPages
	 */
	public int getNumPages() {
		return numPages;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the numResultsPage
	 */
	public int getNumResultsPage() {
		return numResultsPage;
	}

	/**
	 * @return the numResultsTotal
	 */
	public int getNumResultsTotal() {
		return numResultsTotal;
	}

	/**
	 * @return the contents
	 */
	public Paginatable<E> getContents() {
		return contents;
	}

	public boolean isFirstPage() {
		return pageNum == 1;
	}

	public boolean isLastPage() {
		return pageNum == numPages;
	}

	/**
	 * Determines whether two paginatable lists are equal
	 * 
	 * @param o
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Page)) {
			return false;
		}

		final Page page = (Page) o;

		if (pageNum != page.pageNum) {
			return false;
		}

		if (pageSize != page.pageSize) {
			return false;
		}

		if (numPages != page.numPages) {
			return false;
		}

		if (contents != null ? !isListEqual(contents, page.contents)
				: page.contents != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result;
		result = pageNum;
		result = 29 * result + numPages;
		result = 29 * result + pageSize;
		result = 29 * result + (contents != null ? listHashCode(contents) : 0);

		return result;
	}

	/**
	 * Determines whether two paginatable lists are equal
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean isListEqual(final Paginatable<E> a, final Paginatable<E> b) {
		if (a == b || a.equals(b)) {
			return true;
		}

		final Iterator<E> ia = a.iterator();
		final Iterator<E> ib = b.iterator();

		while (ia.hasNext() && ib.hasNext()) {
			final Object oa = ia.next();
			final Object ob = ib.next();

			if (!oa.equals(ob)) {
				return false;
			}
		}

		if (ia.hasNext() || ib.hasNext()) {
			return false;
		}

		return true;
	}

	private int listHashCode(final Paginatable<E> a) {
		int result = 0;

		for (Iterator<E> iterator = a.iterator(); iterator.hasNext();) {
			final Object o = iterator.next();
			result = 29 * result + o.hashCode();
		}

		return result;
	}

	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("Page ").append(pageNum).append(" of ").append(numPages);
		sb.append("\n");

		for (Iterator<E> it = contents.iterator(); it.hasNext();) {
			final Object o = it.next();
			sb.append(o).append("\n");
		}

		return sb.toString();
	}
}