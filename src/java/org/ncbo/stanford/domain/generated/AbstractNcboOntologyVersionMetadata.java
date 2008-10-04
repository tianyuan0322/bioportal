package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboOntologyVersionMetadata entity provides the base persistence
 * definition of the NcboOntologyVersionMetadata entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboOntologyVersionMetadata implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private NcboOntologyVersion ncboOntologyVersion;
	private String displayLabel;
	private String format;
	private String contactName;
	private String contactEmail;
	private String homepage;
	private String documentation;
	private String publication;
	private String urn;
	private String codingScheme;
	private Byte isFoundry;
	private String targetTerminologies;
	private String synonymSlot;
	private String preferredNameSlot;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyVersionMetadata() {
	}

	/** minimal constructor */
	public AbstractNcboOntologyVersionMetadata(
			NcboOntologyVersion ncboOntologyVersion, String displayLabel,
			String format, Byte isFoundry) {
		this.ncboOntologyVersion = ncboOntologyVersion;
		this.displayLabel = displayLabel;
		this.format = format;
		this.isFoundry = isFoundry;
	}

	/** full constructor */
	public AbstractNcboOntologyVersionMetadata(
			NcboOntologyVersion ncboOntologyVersion, String displayLabel,
			String format, String contactName, String contactEmail,
			String homepage, String documentation, String publication,
			String urn, String codingScheme, Byte isFoundry,
			String targetTerminologies, String synonymSlot,
			String preferredNameSlot) {
		this.ncboOntologyVersion = ncboOntologyVersion;
		this.displayLabel = displayLabel;
		this.format = format;
		this.contactName = contactName;
		this.contactEmail = contactEmail;
		this.homepage = homepage;
		this.documentation = documentation;
		this.publication = publication;
		this.urn = urn;
		this.codingScheme = codingScheme;
		this.isFoundry = isFoundry;
		this.targetTerminologies = targetTerminologies;
		this.synonymSlot = synonymSlot;
		this.preferredNameSlot = preferredNameSlot;
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

	public String getDisplayLabel() {
		return this.displayLabel;
	}

	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactEmail() {
		return this.contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getHomepage() {
		return this.homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getDocumentation() {
		return this.documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getPublication() {
		return this.publication;
	}

	public void setPublication(String publication) {
		this.publication = publication;
	}

	public String getUrn() {
		return this.urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	public String getCodingScheme() {
		return this.codingScheme;
	}

	public void setCodingScheme(String codingScheme) {
		this.codingScheme = codingScheme;
	}

	public Byte getIsFoundry() {
		return this.isFoundry;
	}

	public void setIsFoundry(Byte isFoundry) {
		this.isFoundry = isFoundry;
	}

	public String getTargetTerminologies() {
		return this.targetTerminologies;
	}

	public void setTargetTerminologies(String targetTerminologies) {
		this.targetTerminologies = targetTerminologies;
	}

	public String getSynonymSlot() {
		return this.synonymSlot;
	}

	public void setSynonymSlot(String synonymSlot) {
		this.synonymSlot = synonymSlot;
	}

	public String getPreferredNameSlot() {
		return this.preferredNameSlot;
	}

	public void setPreferredNameSlot(String preferredNameSlot) {
		this.preferredNameSlot = preferredNameSlot;
	}

}