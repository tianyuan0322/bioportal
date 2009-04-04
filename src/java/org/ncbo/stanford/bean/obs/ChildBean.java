package org.ncbo.stanford.bean.obs;

public class ChildBean {

	private String localConceptId;
	private int level;

	/**
	 * @return the localConceptId
	 */
	public String getLocalConceptId() {
		return localConceptId;
	}

	/**
	 * @param localConceptId
	 *            the localConceptId to set
	 */
	public void setLocalConceptId(String localConceptId) {
		this.localConceptId = localConceptId;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	public String toString() {
		return "[localConceptID: " + this.getLocalConceptId() + ", level: "
				+ this.getLevel() + "]";
	}
}
