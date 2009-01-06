package org.ncbo.stanford.bean;

import org.ncbo.stanford.domain.generated.NcboLCategory;

public class CategoryBean {

	private Integer id;
	private Integer parentId;
	private String name;
	private String oboFoundryName;

	/**
	 * Populates the OntologyBean with data from a NcboOntology
	 * 
	 * @param ncboOntology
	 */
	public void populateFromEntity(NcboLCategory ncboCategory) {
		if (ncboCategory != null) {
			this.setId(ncboCategory.getId());
			this.setName(ncboCategory.getName());
			this.setOboFoundryName(ncboCategory.getOboFoundryName());

			if (ncboCategory.getNcboLCategory() != null) {
				this.setParentId(ncboCategory.getNcboLCategory().getId());
			}
		}
	}

	/**
	 * Populates the OntologyBean with data from a NcboOntology
	 * 
	 * @param ncboOntology
	 */
	public void populateToEntity(NcboLCategory ncboCategory) {
		if (ncboCategory != null) {
			ncboCategory.setId(this.getId());
			ncboCategory.setName(this.getName());
			ncboCategory.setOboFoundryName(ncboCategory.getOboFoundryName());

			if (this.getParentId() != null) {
				NcboLCategory parentCategory = new NcboLCategory();
				parentCategory.setId(this.getParentId());
				ncboCategory.setNcboLCategory(parentCategory);
			}
		}
	}

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