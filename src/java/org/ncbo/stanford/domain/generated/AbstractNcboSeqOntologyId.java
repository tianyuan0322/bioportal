package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboSeqOntologyId entity provides the base persistence definition of
 * the NcboSeqOntologyId entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboSeqOntologyId implements java.io.Serializable {

	// Fields

	private Integer id;

	// Constructors

	/** default constructor */
	public AbstractNcboSeqOntologyId() {
	}

	/** full constructor */
	public AbstractNcboSeqOntologyId(Integer id) {
		this.id = id;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}