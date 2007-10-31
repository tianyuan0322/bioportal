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
	public NcboOntologyAdditionalMetadata(NcboOntology ncboOntology,
			NcboLAdditionalMetadata ncboLAdditionalMetadata) {
		super(ncboOntology, ncboLAdditionalMetadata);
	}

	/** full constructor */
	public NcboOntologyAdditionalMetadata(NcboOntology ncboOntology,
			NcboLAdditionalMetadata ncboLAdditionalMetadata,
			String additionalMetadataValue) {
		super(ncboOntology, ncboLAdditionalMetadata, additionalMetadataValue);
	}

}
