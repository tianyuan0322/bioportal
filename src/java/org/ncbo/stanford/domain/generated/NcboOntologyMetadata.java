package org.ncbo.stanford.domain.generated;

/**
 * NcboOntologyMetadata entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboOntologyMetadata extends AbstractNcboOntologyMetadata
		implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboOntologyMetadata() {
	}

	/** minimal constructor */
	public NcboOntologyMetadata(NcboOntology ncboOntology, String displayLabel,
			String format, Byte isFoundry) {
		super(ncboOntology, displayLabel, format, isFoundry);
	}

	/** full constructor */
	public NcboOntologyMetadata(NcboOntology ncboOntology, String displayLabel,
			String format, String contactName, String contactEmail,
			String homepage, String documentation, String publication,
			Byte isFoundry) {
		super(ncboOntology, displayLabel, format, contactName, contactEmail,
				homepage, documentation, publication, isFoundry);
	}

}
