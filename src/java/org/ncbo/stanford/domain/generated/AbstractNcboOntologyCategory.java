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
	private NcboOntologyVersion ncboOntologyVersion;
	private NcboLCategory ncboLCategory;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyCategory() {
	}

	/** full constructor */
	public AbstractNcboOntologyCategory(
			NcboOntologyVersion ncboOntologyVersion, NcboLCategory ncboLCategory) {
		this.ncboOntologyVersion = ncboOntologyVersion;
		this.ncboLCategory = ncboLCategory;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboOntologyVersion getNcboOntologyVersion() {
		return this.ncboOntologyVersion;
	}

	public void setNcboOntologyVersion(NcboOntologyVersion ncboOntologyVersion) {
		this.ncboOntologyVersion = ncboOntologyVersion;
	}

	public NcboLCategory getNcboLCategory() {
		return this.ncboLCategory;
	}

	public void setNcboLCategory(NcboLCategory ncboLCategory) {
		this.ncboLCategory = ncboLCategory;
	}

}