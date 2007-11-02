package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboUserRole entity provides the base persistence definition of the
 * NcboUserRole entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboUserRole implements java.io.Serializable {

	// Fields

	private Integer id;
	private NcboLRole ncboLRole;
	private NcboUser ncboUser;

	// Constructors

	/** default constructor */
	public AbstractNcboUserRole() {
	}

	/** full constructor */
	public AbstractNcboUserRole(NcboLRole ncboLRole, NcboUser ncboUser) {
		this.ncboLRole = ncboLRole;
		this.ncboUser = ncboUser;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboLRole getNcboLRole() {
		return this.ncboLRole;
	}

	public void setNcboLRole(NcboLRole ncboLRole) {
		this.ncboLRole = ncboLRole;
	}

	public NcboUser getNcboUser() {
		return this.ncboUser;
	}

	public void setNcboUser(NcboUser ncboUser) {
		this.ncboUser = ncboUser;
	}

}