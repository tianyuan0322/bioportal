package org.ncbo.stanford.domain.generated;

import java.util.HashSet;
import java.util.Set;

/**
 * AbstractNcboLRole entity provides the base persistence definition of the
 * NcboLRole entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboLRole implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private String description;
	private Set ncboUserRoles = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboLRole() {
	}

	/** minimal constructor */
	public AbstractNcboLRole(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	/** full constructor */
	public AbstractNcboLRole(Integer id, String name, String description,
			Set ncboUserRoles) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.ncboUserRoles = ncboUserRoles;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set getNcboUserRoles() {
		return this.ncboUserRoles;
	}

	public void setNcboUserRoles(Set ncboUserRoles) {
		this.ncboUserRoles = ncboUserRoles;
	}

}