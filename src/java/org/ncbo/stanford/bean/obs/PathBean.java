package org.ncbo.stanford.bean.obs;

public class PathBean extends AbstractConceptBean {

	private String path;

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public String toString() {
		return "[" + super.toString() + " path: " + path + "]";
	}
}
