package org.ncbo.stanford.bean.obs;

public class ChildBean extends AbstractConceptBean {

	private int level;

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
		return "[" + super.toString() + " level: " + level + "]";
	}
}
