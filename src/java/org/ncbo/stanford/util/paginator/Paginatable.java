package org.ncbo.stanford.util.paginator;

import java.util.Collection;
import java.util.Iterator;

/**
 * An interface that implements a paginatable result set
 * 
 * @author Michael Dorf
 * 
 * @param <E>
 */
public interface Paginatable<E> extends Collection<E> {

	/**
	 * List iterator
	 * 
	 * @return
	 */
	public Iterator<E> iterator();

	/**
	 * A partial list of results
	 * 
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public Paginatable<E> sublist(int fromIndex, int toIndex);

	/**
	 * Size of the list
	 * 
	 * @return
	 */
	public int size();
}
