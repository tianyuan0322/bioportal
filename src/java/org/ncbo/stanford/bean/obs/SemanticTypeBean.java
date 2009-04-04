package org.ncbo.stanford.bean.obs;

public class SemanticTypeBean {

	private String conceptId;
	private String localSemanticTypeId;
	private String name;

	public String toString() {
		return "[conceptId: " + this.getConceptId() + "obsSemanticType: "
				+ this.getLocalSemanticTypeId() + ", name: " + this.getName()
				+ "]";
	}

	/**
	 * @return the localSemanticTypeId
	 */
	public String getLocalSemanticTypeId() {
		return localSemanticTypeId;
	}

	/**
	 * @param localSemanticTypeId
	 *            the localSemanticTypeId to set
	 */
	public void setLocalSemanticTypeId(String localSemanticTypeId) {
		this.localSemanticTypeId = localSemanticTypeId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the conceptId
	 */
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * @param conceptId
	 *            the conceptId to set
	 */
	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}
}