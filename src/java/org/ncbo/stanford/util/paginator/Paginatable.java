package org.ncbo.stanford.util.paginator;

import java.util.Iterator;

public interface Paginatable<E> {
	public Iterator<E> iterator();

	public Paginatable<E> sublist(int fromIndex, int toIndex);

	public int size();
}
