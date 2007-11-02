package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboOntologyAdditionalMetadata entity provides the base persistence
 * definition of the NcboOntologyAdditionalMetadata entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboOntologyAdditionalMetadata implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private NcboOntology ncboOntology;
	private NcboLAdditionalMetadata ncboLAdditionalMetadata;
	private String additionalMetadataValue;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyAdditionalMetadata() {
	}

	/** minimal constructor */
	public AbstractNcboOntologyAdditionalMetadata(NcboOntology ncboOntology,
			NcboLAdditionalMetadata ncboLAdditionalMetadata) {
		this.ncboOntology = ncboOntology;
		this.ncboLAdditionalMetadata = ncboLAdditionalMetadata;
	}

	/** full constructor */
	public AbstractNcboOntologyAdditionalMetadata(NcboOntology ncboOntology,
			NcboLAdditionalMetadata ncboLAdditionalMetadata,
			String additionalMetadataValue) {
		this.ncboOntology = ncboOntology;
		this.ncboLAdditionalMetadata = ncboLAdditionalMetadata;
		this.additionalMetadataValue = additionalMetadataValue;
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

	public NcboLAdditionalMetadata getNcboLAdditionalMetadata() {
		return this.ncboLAdditionalMetadata;
	}

	public void setNcboLAdditionalMetadata(
			NcboLAdditionalMetadata ncboLAdditionalMetadata) {
		this.ncboLAdditionalMetadata = ncboLAdditionalMetadata;
	}

	public String getAdditionalMetadataValue() {
		return this.additionalMetadataValue;
	}

	public void setAdditionalMetadataValue(String additionalMetadataValue) {
		this.additionalMetadataValue = additionalMetadataValue;
	}

}