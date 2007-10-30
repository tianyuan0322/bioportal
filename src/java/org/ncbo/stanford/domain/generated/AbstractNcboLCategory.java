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
	private Set ncboOntologyCategories = new HashSet(0);
	private Set ncboLCategories = new HashSet(0);

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
			String name, Set ncboOntologyCategories, Set ncboLCategories) {
		this.id = id;
		this.ncboLCategory = ncboLCategory;
		this.name = name;
		this.ncboOntologyCategories = ncboOntologyCategories;
		this.ncboLCategories = ncboLCategories;
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

	public Set getNcboOntologyCategories() {
		return this.ncboOntologyCategories;
	}

	public void setNcboOntologyCategories(Set ncboOntologyCategories) {
		this.ncboOntologyCategories = ncboOntologyCategories;
	}

	public Set getNcboLCategories() {
		return this.ncboLCategories;
	}

	public void setNcboLCategories(Set ncboLCategories) {
		this.ncboLCategories = ncboLCategories;
	}

}