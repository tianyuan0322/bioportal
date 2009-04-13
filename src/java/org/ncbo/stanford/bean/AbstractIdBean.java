package org.ncbo.stanford.bean;

public class AbstractIdBean {

	private String id = null;
	private boolean isUmls = false;

	/**
	 * @param id
	 */
	public AbstractIdBean(String id) {
		super();
		this.id = id;

		try {
			Integer.parseInt(id);
		} catch (NumberFormatException e) {
			isUmls = true;
		}
	}

	/**
	 * @return the id
	 */
	protected String getId() {
		return id;
	}

	/**
	 * @return the isUmls
	 */
	public boolean isUmls() {
		return isUmls;
	}
}
