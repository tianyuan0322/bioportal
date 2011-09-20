package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboUserOntologyLicense entity provides the base persistence
 * definition of the NcboUserOntologyLicense entity. @author MyEclipse
 * Persistence Tools
 */

public abstract class AbstractNcboUserOntologyLicense implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private NcboUser ncboUser;
	private Integer ontologyId;
	private String licenseText;

	// Constructors

	/** default constructor */
	public AbstractNcboUserOntologyLicense() {
	}

	/** minimal constructor */
	public AbstractNcboUserOntologyLicense(NcboUser ncboUser, Integer ontologyId) {
		this.ncboUser = ncboUser;
		this.ontologyId = ontologyId;
	}

	/** full constructor */
	public AbstractNcboUserOntologyLicense(NcboUser ncboUser,
			Integer ontologyId, String licenseText) {
		this.ncboUser = ncboUser;
		this.ontologyId = ontologyId;
		this.licenseText = licenseText;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboUser getNcboUser() {
		return this.ncboUser;
	}

	public void setNcboUser(NcboUser ncboUser) {
		this.ncboUser = ncboUser;
	}

	public Integer getOntologyId() {
		return this.ontologyId;
	}

	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	public String getLicenseText() {
		return this.licenseText;
	}

	public void setLicenseText(String licenseText) {
		this.licenseText = licenseText;
	}

}