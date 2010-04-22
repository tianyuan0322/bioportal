package org.ncbo.stanford.bean.notes;

public class AppliesToBean {
	private String fullId;
	private String type;
	
	public AppliesToBean(String fullId, String type) {
		this.fullId = fullId;
		this.type = type;
	}

	/**
	 * @return the fullId
	 */
	public String getFullId() {
		return fullId;
	}

	/**
	 * @param fullId
	 *            the fullId to set
	 */
	public void setFullId(String fullId) {
		this.fullId = fullId;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
