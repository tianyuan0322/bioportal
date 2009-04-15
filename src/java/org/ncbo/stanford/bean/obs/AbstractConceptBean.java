package org.ncbo.stanford.bean.obs;

public abstract class AbstractConceptBean {

	protected String localConceptId;

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

	public String toString() {
		return "localConceptId: " + localConceptId;
	}
}
