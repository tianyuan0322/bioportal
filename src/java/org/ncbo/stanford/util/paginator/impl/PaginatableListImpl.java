package org.ncbo.stanford.util.paginator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.ncbo.stanford.util.paginator.Paginatable;

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
