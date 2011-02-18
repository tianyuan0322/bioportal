package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboOntologyAcl entity provides the base persistence definition of
 * the NcboOntologyAcl entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboOntologyAcl implements java.io.Serializable {

	// Fields

	private Integer id;
	private NcboUser ncboUser;
	private Integer ontologyId;
	private Boolean isOwner;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyAcl() {
	}

	/** full constructor */
	public AbstractNcboOntologyAcl(NcboUser ncboUser, Integer ontologyId,
			Boolean isOwner) {
		this.ncboUser = ncboUser;
		this.ontologyId = ontologyId;
		this.isOwner = isOwner;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboUser getNcboUser() {
		return this.ncboUser;
	}

	public void setNcboUser(NcboUser ncboUser) {
		this.ncboUser = ncboUser;
	}

	public Integer getOntologyId() {
		return this.ontologyId;
	}

	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	public Boolean getIsOwner() {
		return this.isOwner;
	}

	public void setIsOwner(Boolean isOwner) {
		this.isOwner = isOwner;
	}

}