package org.ncbo.stanford.bean;

public class CategoryBean {

	private Integer id;
	private Integer parentId;
	private String name;
	private String oboFoundryName;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the parentId
	 */
	public Integer getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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
	 * @return the oboFoundryName
	 */
	public String getOboFoundryName() {
		return oboFoundryName;
	}

	/**
	 * @param oboFoundryName
	 *            the oboFoundryName to set
	 */
	public void setOboFoundryName(String oboFoundryName) {
		this.oboFoundryName = oboFoundryName;
	}
}