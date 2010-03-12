/**
 * 
 */
package org.ncbo.stanford.bean.concept;

import java.util.Collection;

import org.ncbo.stanford.util.paginator.Paginatable;
import org.ncbo.stanford.util.paginator.impl.PaginatableList;

/**
 * @author s.reddy
 *
 */
public class InstanceBeanResultListBean extends PaginatableList<InstanceBean> {
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InstanceBeanResultListBean() {
		super();
	}

	/**
	 * @param c
	 */
	public InstanceBeanResultListBean(Collection<InstanceBean> c) {
		super(c);
	}

	/**
	 * @param create
	 *            object with initialCapacity
	 */
	public InstanceBeanResultListBean(int initialCapacity) {
		super(initialCapacity);
	}

	public Paginatable<InstanceBean> sublist(int fromIndex, int toIndex) {
		return new InstanceBeanResultListBean(super.subList(fromIndex, toIndex));
	}


}
