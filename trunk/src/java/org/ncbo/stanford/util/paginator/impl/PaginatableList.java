package org.ncbo.stanford.util.paginator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.ncbo.stanford.util.paginator.Paginatable;

/**
 * 
 * @author Michael Dorf
 * 
 * A default implementation of a java List. Allows to turn any list into a
 * paginatable list.
 * 
 * @param <E> -
 *            type of objects to paginate over
 * 
 */
public class PaginatableList<E> extends ArrayList<E> implements Paginatable<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1434613282526950613L;

	/**
	 * 
	 */
	public PaginatableList() {
		super();
	}

	/**
	 * @param c
	 */
	public PaginatableList(Collection<? extends E> c) {
		super(c);
	}

	/**
	 * @param initialCapacity
	 */
	public PaginatableList(int initialCapacity) {
		super(initialCapacity);
	}

	public Iterator<E> iterator() {
		return super.iterator();
	}

	public int size() {
		return super.size();
	}

	public Paginatable<E> sublist(int fromIndex, int toIndex) {
		return new PaginatableList<E>(super.subList(fromIndex, toIndex));
	}
}
