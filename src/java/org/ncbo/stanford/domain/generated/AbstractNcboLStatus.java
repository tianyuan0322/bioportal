package org.ncbo.stanford.domain.generated;

import java.util.HashSet;
import java.util.Set;

/**
 * AbstractNcboLStatus entity provides the base persistence definition of the
 * NcboLStatus entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboLStatus implements java.io.Serializable {

	// Fields

	private Integer id;
	private String status;
	private String description;
	private Set ncboOntologyLoadQueues = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboLStatus() {
	}

	/** minimal constructor */
	public AbstractNcboLStatus(Integer id, String status) {
		this.id = id;
		this.status = status;
	}

	/** full constructor */
	public AbstractNcboLStatus(Integer id, String status, String description,
			Set ncboOntologyLoadQueues) {
		this.id = id;
		this.status = status;
		this.description = description;
		this.ncboOntologyLoadQueues = ncboOntologyLoadQueues;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set getNcboOntologyLoadQueues() {
		return this.ncboOntologyLoadQueues;
	}

	public void setNcboOntologyLoadQueues(Set ncboOntologyLoadQueues) {
		this.ncboOntologyLoadQueues = ncboOntologyLoadQueues;
	}

}