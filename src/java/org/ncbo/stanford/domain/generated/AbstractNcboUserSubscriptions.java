package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboUserSubscriptions entity provides the base persistence definition
 * of the NcboUserSubscriptions entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboUserSubscriptions implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private Integer userId;
	private String ontologyId;

	// Constructors

	/** default constructor */
	public AbstractNcboUserSubscriptions() {
	}

	/** minimal constructor */
	public AbstractNcboUserSubscriptions(Integer userId) {
		this.userId = userId;
	}

	/** full constructor */
	public AbstractNcboUserSubscriptions(Integer userId, String ontologyId) {
		this.userId = userId;
		this.ontologyId = ontologyId;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getOntologyId() {
		return this.ontologyId;
	}

	public void setOntologyId(String ontologyId) {
		this.ontologyId = ontologyId;
	}

}