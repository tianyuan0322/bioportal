/**
 * 
 */
package org.ncbo.stanford.bean.concept;

import java.util.Collection;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.util.paginator.Paginatable;
import org.ncbo.stanford.util.paginator.impl.PaginatableList;

/**
 * @author s.reddy
 * 
 */
public class ClassBeanResultListBean extends PaginatableList<ClassBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClassBeanResultListBean() {
		super();
	}

	/**
	 * @param c
	 */
	public ClassBeanResultListBean(Collection<ClassBean> c) {
		super(c);
	}

	/**
	 * @param create
	 *            object with initialCapacity
	 */
	public ClassBeanResultListBean(int initialCapacity) {
		super(initialCapacity);
	}

	public Paginatable<ClassBean> sublist(int fromIndex, int toIndex) {
		return new ClassBeanResultListBean(super.subList(fromIndex, toIndex));
	}
}
