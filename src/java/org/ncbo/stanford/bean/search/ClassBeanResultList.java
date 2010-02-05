/**
 * 
 */
package org.ncbo.stanford.bean.search;

import java.util.Collection;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.util.paginator.Paginatable;
import org.ncbo.stanford.util.paginator.impl.PaginatableList;

/**
 * @author s.reddy
 * 
 */
public class ClassBeanResultList extends PaginatableList<ClassBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClassBeanResultList() {
		super();
	}

	/**
	 * @param c
	 */
	public ClassBeanResultList(Collection<ClassBean> c) {
		super(c);
	}

	/**
	 * @param create object with initialCapacity
	 */
	public ClassBeanResultList(int initialCapacity) {
		super(initialCapacity);
	}

	public Paginatable<ClassBean> sublist(int fromIndex, int toIndex) {
		ClassBeanResultList results = new ClassBeanResultList(super.subList(
				fromIndex, toIndex));

		return results;
	}

}
