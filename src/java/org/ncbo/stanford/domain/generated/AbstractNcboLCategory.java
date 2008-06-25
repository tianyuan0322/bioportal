package org.ncbo.stanford.domain.generated;

import java.util.HashSet;
import java.util.Set;

/**
 * AbstractNcboLCategory entity provides the base persistence definition of the
 * NcboLCategory entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboLCategory implements java.io.Serializable {

	// Fields

	private Integer id;
	private NcboLCategory ncboLCategory;
	private String name;
	private String oboFoundryName;
	private Set ncboLCategories = new HashSet(0);
	private Set ncboOntologyCategories = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboLCategory() {
	}

	/** minimal constructor */
	public AbstractNcboLCategory(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	/** full constructor */
	public AbstractNcboLCategory(Integer id, NcboLCategory ncboLCategory,
			String name, String oboFoundryName, Set ncboLCategories,
			Set ncboOntologyCategories) {
		this.id = id;
		this.ncboLCategory = ncboLCategory;
		this.name = name;
		this.oboFoundryName = oboFoundryName;
		this.ncboLCategories = ncboLCategories;
		this.ncboOntologyCategories = ncboOntologyCategories;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboLCategory getNcboLCategory() {
		return this.ncboLCategory;
	}

	public void setNcboLCategory(NcboLCategory ncboLCategory) {
		this.ncboLCategory = ncboLCategory;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOboFoundryName() {
		return this.oboFoundryName;
	}

	public void setOboFoundryName(String oboFoundryName) {
		this.oboFoundryName = oboFoundryName;
	}

	public Set getNcboLCategories() {
		return this.ncboLCategories;
	}

	public void setNcboLCategories(Set ncboLCategories) {
		this.ncboLCategories = ncboLCategories;
	}

	public Set getNcboOntologyCategories() {
		return this.ncboOntologyCategories;
	}

	public void setNcboOntologyCategories(Set ncboOntologyCategories) {
		this.ncboOntologyCategories = ncboOntologyCategories;
	}

}