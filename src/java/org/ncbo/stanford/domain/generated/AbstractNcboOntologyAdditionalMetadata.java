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
	private NcboOntologyVersion ncboOntologyVersion;
	private NcboLAdditionalMetadata ncboLAdditionalMetadata;
	private String additionalMetadataValue;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyAdditionalMetadata() {
	}

	/** minimal constructor */
	public AbstractNcboOntologyAdditionalMetadata(
			NcboOntologyVersion ncboOntologyVersion,
			NcboLAdditionalMetadata ncboLAdditionalMetadata) {
		this.ncboOntologyVersion = ncboOntologyVersion;
		this.ncboLAdditionalMetadata = ncboLAdditionalMetadata;
	}

	/** full constructor */
	public AbstractNcboOntologyAdditionalMetadata(
			NcboOntologyVersion ncboOntologyVersion,
			NcboLAdditionalMetadata ncboLAdditionalMetadata,
			String additionalMetadataValue) {
		this.ncboOntologyVersion = ncboOntologyVersion;
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

	public NcboOntologyVersion getNcboOntologyVersion() {
		return this.ncboOntologyVersion;
	}

	public void setNcboOntologyVersion(NcboOntologyVersion ncboOntologyVersion) {
		this.ncboOntologyVersion = ncboOntologyVersion;
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