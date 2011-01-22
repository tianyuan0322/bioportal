package org.ncbo.stanford.bean.mapping;

import java.util.Collection;

import org.ncbo.stanford.util.paginator.Paginatable;
import org.ncbo.stanford.util.paginator.impl.PaginatableList;

public class MappingResultListBean extends
		PaginatableList<MappingBean> {

	private static final long serialVersionUID = 815033586597984400L;

	public MappingResultListBean() {
		super();
	}

	/**
	 * @param c
	 */
	public MappingResultListBean(Collection<MappingBean> c) {
		super(c);
	}

	/**
	 * @param create
	 *            object with initialCapacity
	 */
	public MappingResultListBean(int initialCapacity) {
		super(initialCapacity);
	}

	public Paginatable<MappingBean> sublist(int fromIndex, int toIndex) {
		return new MappingResultListBean(super.subList(fromIndex, toIndex));
	}
}
