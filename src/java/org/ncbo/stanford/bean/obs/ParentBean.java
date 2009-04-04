package org.ncbo.stanford.bean.obs;

public class ParentBean {

	private String parentLocalConceptId;
	private int level;

	/**
	 * @return the parentLocalConceptId
	 */
	public String getParentLocalConceptId() {
		return parentLocalConceptId;
	}

	/**
	 * @param parentLocalConceptId
	 *            the parentLocalConceptId to set
	 */
	public void setParentLocalConceptId(String parentLocalConceptId) {
		this.parentLocalConceptId = parentLocalConceptId;
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
		return "[parentLocalConceptID: " + this.getParentLocalConceptId()
				+ ", level: " + this.getLevel() + "]";
	}
}
