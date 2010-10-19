package org.ncbo.stanford.bean.mapping;

import java.util.Collection;

import org.ncbo.stanford.util.paginator.Paginatable;
import org.ncbo.stanford.util.paginator.impl.PaginatableList;

public class MappingBeanResultListBean extends
		PaginatableList<OneToOneMappingBean> {

	private static final long serialVersionUID = 815033586597984400L;

	public MappingBeanResultListBean() {
		super();
	}

	/**
	 * @param c
	 */
	public MappingBeanResultListBean(Collection<OneToOneMappingBean> c) {
		super(c);
	}

	/**
	 * @param create
	 *            object with initialCapacity
	 */
	public MappingBeanResultListBean(int initialCapacity) {
		super(initialCapacity);
	}

	public Paginatable<OneToOneMappingBean> sublist(int fromIndex, int toIndex) {
		return new MappingBeanResultListBean(super.subList(fromIndex, toIndex));
	}
}
