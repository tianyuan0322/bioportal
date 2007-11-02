package org.ncbo.stanford.domain.generated;

/**
 * NcboOntologyAdditionalMetadata entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboOntologyAdditionalMetadata extends
		AbstractNcboOntologyAdditionalMetadata implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboOntologyAdditionalMetadata() {
	}

	/** minimal constructor */
	public NcboOntologyAdditionalMetadata(
			NcboOntologyVersion ncboOntologyVersion,
			NcboLAdditionalMetadata ncboLAdditionalMetadata) {
		super(ncboOntologyVersion, ncboLAdditionalMetadata);
	}

	/** full constructor */
	public NcboOntologyAdditionalMetadata(
			NcboOntologyVersion ncboOntologyVersion,
			NcboLAdditionalMetadata ncboLAdditionalMetadata,
			String additionalMetadataValue) {
		super(ncboOntologyVersion, ncboLAdditionalMetadata,
				additionalMetadataValue);
	}

}
