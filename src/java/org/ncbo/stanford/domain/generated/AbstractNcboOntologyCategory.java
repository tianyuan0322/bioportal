package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboOntologyCategory entity provides the base persistence definition
 * of the NcboOntologyCategory entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboOntologyCategory implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private NcboOntology ncboOntology;
	private NcboLCategory ncboLCategory;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyCategory() {
	}

	/** full constructor */
	public AbstractNcboOntologyCategory(NcboOntology ncboOntology,
			NcboLCategory ncboLCategory) {
		this.ncboOntology = ncboOntology;
		this.ncboLCategory = ncboLCategory;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboOntology getNcboOntology() {
		return this.ncboOntology;
	}

	public void setNcboOntology(NcboOntology ncboOntology) {
		this.ncboOntology = ncboOntology;
	}

	public NcboLCategory getNcboLCategory() {
		return this.ncboLCategory;
	}

	public void setNcboLCategory(NcboLCategory ncboLCategory) {
		this.ncboLCategory = ncboLCategory;
	}

}