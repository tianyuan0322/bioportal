package org.ncbo.stanford.domain.generated;

/**
 * NcboOntologyVersionMetadata entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboOntologyVersionMetadata extends
		AbstractNcboOntologyVersionMetadata implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboOntologyVersionMetadata() {
	}

	/** minimal constructor */
	public NcboOntologyVersionMetadata(NcboOntologyVersion ncboOntologyVersion,
			String displayLabel, String format, Byte isFoundry) {
		super(ncboOntologyVersion, displayLabel, format, isFoundry);
	}

	/** full constructor */
	public NcboOntologyVersionMetadata(NcboOntologyVersion ncboOntologyVersion,
			String displayLabel, String format, String contactName,
			String contactEmail, String homepage, String documentation,
			String publication, String urn, String codingScheme,
			Byte isFoundry, String targetTerminologies) {
		super(ncboOntologyVersion, displayLabel, format, contactName,
				contactEmail, homepage, documentation, publication, urn,
				codingScheme, isFoundry, targetTerminologies);
	}

}
