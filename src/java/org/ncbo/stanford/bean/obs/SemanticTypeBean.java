package org.ncbo.stanford.bean.obs;

public class SemanticTypeBean {

	private String id;
	private String semanticType;
	private String description;

	public String toString() {
		return "[id: " + this.getId() + "obsSemanticType: "
				+ this.getSemanticType() + ", description: "
				+ this.getDescription() + "]";
	}

	/**
	 * @return the semanticType
	 */
	public String getSemanticType() {
		return semanticType;
	}

	/**
	 * @param semanticType
	 *            the semanticType to set
	 */
	public void setSemanticType(String semanticType) {
		this.semanticType = semanticType;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param name
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}