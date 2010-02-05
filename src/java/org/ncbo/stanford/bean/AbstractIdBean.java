package org.ncbo.stanford.bean;

public abstract class AbstractIdBean {

	private Integer id = null;

	/**
	 * @param id
	 */
	public AbstractIdBean(Integer id) {
		super();
		this.id = id;
	}

	/**
	 * @return the id
	 */
	protected Integer getId() {
		return id;
	}
}
