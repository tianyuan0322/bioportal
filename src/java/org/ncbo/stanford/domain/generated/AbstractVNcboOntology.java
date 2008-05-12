package org.ncbo.stanford.domain.generated;

/**
 * AbstractVNcboOntology entity provides the base persistence definition of the
 * VNcboOntology entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractVNcboOntology implements java.io.Serializable {

	// Fields

	private VNcboOntologyId id;

	// Constructors

	/** default constructor */
	public AbstractVNcboOntology() {
	}

	/** full constructor */
	public AbstractVNcboOntology(VNcboOntologyId id) {
		this.id = id;
	}

	// Property accessors

	public VNcboOntologyId getId() {
		return this.id;
	}

	public void setId(VNcboOntologyId id) {
		this.id = id;
	}

}