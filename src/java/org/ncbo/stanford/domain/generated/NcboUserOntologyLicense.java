package org.ncbo.stanford.domain.generated;

/**
 * NcboUserOntologyLicense entity. @author MyEclipse Persistence Tools
 */
public class NcboUserOntologyLicense extends AbstractNcboUserOntologyLicense
		implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboUserOntologyLicense() {
	}

	/** minimal constructor */
	public NcboUserOntologyLicense(NcboUser ncboUser, Integer ontologyId) {
		super(ncboUser, ontologyId);
	}

	/** full constructor */
	public NcboUserOntologyLicense(NcboUser ncboUser, Integer ontologyId,
			String licenseText) {
		super(ncboUser, ontologyId, licenseText);
	}

}
