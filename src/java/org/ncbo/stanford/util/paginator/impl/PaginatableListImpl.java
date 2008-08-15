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
public class PaginatableListImpl<E> extends ArrayList<E> implements
		Paginatable<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1434613282526950613L;

	/**
	 * 
	 */
	public PaginatableListImpl() {
		super();
	}

	/**
	 * @param c
	 */
	public PaginatableListImpl(Collection<? extends E> c) {
		super(c);
	}

	/**
	 * @param initialCapacity
	 */
	public PaginatableListImpl(int initialCapacity) {
		super(initialCapacity);
	}

	public Iterator<E> iterator() {
		return super.iterator();
	}

	public int size() {
		return super.size();
	}

	public Paginatable<E> sublist(int fromIndex, int toIndex) {
		return new PaginatableListImpl<E>(super.subList(fromIndex, toIndex));
	}
}
