package org.ncbo.stanford.domain.generated;

/**
 * NcboOntologyAdditionalVersionMetadata entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboOntologyAdditionalVersionMetadata extends
		AbstractNcboOntologyAdditionalVersionMetadata implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboOntologyAdditionalVersionMetadata() {
	}

	/** minimal constructor */
	public NcboOntologyAdditionalVersionMetadata(
			NcboOntologyVersion ncboOntologyVersion,
			NcboLAdditionalMetadata ncboLAdditionalMetadata) {
		super(ncboOntologyVersion, ncboLAdditionalMetadata);
	}

	/** full constructor */
	public NcboOntologyAdditionalVersionMetadata(
			NcboOntologyVersion ncboOntologyVersion,
			NcboLAdditionalMetadata ncboLAdditionalMetadata,
			String additionalMetadataValue) {
		super(ncboOntologyVersion, ncboLAdditionalMetadata,
				additionalMetadataValue);
	}

}
