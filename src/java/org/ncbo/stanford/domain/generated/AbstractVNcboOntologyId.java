package org.ncbo.stanford.domain.generated;

import java.util.Date;

/**
 * AbstractVNcboOntologyId entity provides the base persistence definition of
 * the VNcboOntologyId entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractVNcboOntologyId implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer ontologyId;
	private Integer parentId;
	private Integer userId;
	private Integer internalVersionNumber;
	private String versionNumber;
	private String versionStatus;
	private String filePath;
	private Byte isCurrent;
	private Byte isRemote;
	private Byte isReviewed;
	private Date dateCreated;
	private Date dateReleased;
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

	// Constructors

	/** default constructor */
	public AbstractVNcboOntologyId() {
	}

	/** minimal constructor */
	public AbstractVNcboOntologyId(Integer id, Integer ontologyId,
			Integer userId, Integer internalVersionNumber,
			String versionNumber, Byte isCurrent, Byte isRemote,
			Byte isReviewed, Date dateCreated, Date dateReleased,
			String displayLabel, String format, Byte isFoundry) {
		this.id = id;
		this.ontologyId = ontologyId;
		this.userId = userId;
		this.internalVersionNumber = internalVersionNumber;
		this.versionNumber = versionNumber;
		this.isCurrent = isCurrent;
		this.isRemote = isRemote;
		this.isReviewed = isReviewed;
		this.dateCreated = dateCreated;
		this.dateReleased = dateReleased;
		this.displayLabel = displayLabel;
		this.format = format;
		this.isFoundry = isFoundry;
	}

	/** full constructor */
	public AbstractVNcboOntologyId(Integer id, Integer ontologyId,
			Integer parentId, Integer userId, Integer internalVersionNumber,
			String versionNumber, String versionStatus, String filePath,
			Byte isCurrent, Byte isRemote, Byte isReviewed, Date dateCreated,
			Date dateReleased, String displayLabel, String format,
			String contactName, String contactEmail, String homepage,
			String documentation, String publication, String urn,
			String codingScheme, Byte isFoundry) {
		this.id = id;
		this.ontologyId = ontologyId;
		this.parentId = parentId;
		this.userId = userId;
		this.internalVersionNumber = internalVersionNumber;
		this.versionNumber = versionNumber;
		this.versionStatus = versionStatus;
		this.filePath = filePath;
		this.isCurrent = isCurrent;
		this.isRemote = isRemote;
		this.isReviewed = isReviewed;
		this.dateCreated = dateCreated;
		this.dateReleased = dateReleased;
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
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOntologyId() {
		return this.ontologyId;
	}

	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	public Integer getParentId() {
		return this.parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getInternalVersionNumber() {
		return this.internalVersionNumber;
	}

	public void setInternalVersionNumber(Integer internalVersionNumber) {
		this.internalVersionNumber = internalVersionNumber;
	}

	public String getVersionNumber() {
		return this.versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getVersionStatus() {
		return this.versionStatus;
	}

	public void setVersionStatus(String versionStatus) {
		this.versionStatus = versionStatus;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Byte getIsCurrent() {
		return this.isCurrent;
	}

	public void setIsCurrent(Byte isCurrent) {
		this.isCurrent = isCurrent;
	}

	public Byte getIsRemote() {
		return this.isRemote;
	}

	public void setIsRemote(Byte isRemote) {
		this.isRemote = isRemote;
	}

	public Byte getIsReviewed() {
		return this.isReviewed;
	}

	public void setIsReviewed(Byte isReviewed) {
		this.isReviewed = isReviewed;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateReleased() {
		return this.dateReleased;
	}

	public void setDateReleased(Date dateReleased) {
		this.dateReleased = dateReleased;
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

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AbstractVNcboOntologyId))
			return false;
		AbstractVNcboOntologyId castOther = (AbstractVNcboOntologyId) other;

		return ((this.getId() == castOther.getId()) || (this.getId() != null
				&& castOther.getId() != null && this.getId().equals(
				castOther.getId())))
				&& ((this.getOntologyId() == castOther.getOntologyId()) || (this
						.getOntologyId() != null
						&& castOther.getOntologyId() != null && this
						.getOntologyId().equals(castOther.getOntologyId())))
				&& ((this.getParentId() == castOther.getParentId()) || (this
						.getParentId() != null
						&& castOther.getParentId() != null && this
						.getParentId().equals(castOther.getParentId())))
				&& ((this.getUserId() == castOther.getUserId()) || (this
						.getUserId() != null
						&& castOther.getUserId() != null && this.getUserId()
						.equals(castOther.getUserId())))
				&& ((this.getInternalVersionNumber() == castOther
						.getInternalVersionNumber()) || (this
						.getInternalVersionNumber() != null
						&& castOther.getInternalVersionNumber() != null && this
						.getInternalVersionNumber().equals(
								castOther.getInternalVersionNumber())))
				&& ((this.getVersionNumber() == castOther.getVersionNumber()) || (this
						.getVersionNumber() != null
						&& castOther.getVersionNumber() != null && this
						.getVersionNumber()
						.equals(castOther.getVersionNumber())))
				&& ((this.getVersionStatus() == castOther.getVersionStatus()) || (this
						.getVersionStatus() != null
						&& castOther.getVersionStatus() != null && this
						.getVersionStatus()
						.equals(castOther.getVersionStatus())))
				&& ((this.getFilePath() == castOther.getFilePath()) || (this
						.getFilePath() != null
						&& castOther.getFilePath() != null && this
						.getFilePath().equals(castOther.getFilePath())))
				&& ((this.getIsCurrent() == castOther.getIsCurrent()) || (this
						.getIsCurrent() != null
						&& castOther.getIsCurrent() != null && this
						.getIsCurrent().equals(castOther.getIsCurrent())))
				&& ((this.getIsRemote() == castOther.getIsRemote()) || (this
						.getIsRemote() != null
						&& castOther.getIsRemote() != null && this
						.getIsRemote().equals(castOther.getIsRemote())))
				&& ((this.getIsReviewed() == castOther.getIsReviewed()) || (this
						.getIsReviewed() != null
						&& castOther.getIsReviewed() != null && this
						.getIsReviewed().equals(castOther.getIsReviewed())))
				&& ((this.getDateCreated() == castOther.getDateCreated()) || (this
						.getDateCreated() != null
						&& castOther.getDateCreated() != null && this
						.getDateCreated().equals(castOther.getDateCreated())))
				&& ((this.getDateReleased() == castOther.getDateReleased()) || (this
						.getDateReleased() != null
						&& castOther.getDateReleased() != null && this
						.getDateReleased().equals(castOther.getDateReleased())))
				&& ((this.getDisplayLabel() == castOther.getDisplayLabel()) || (this
						.getDisplayLabel() != null
						&& castOther.getDisplayLabel() != null && this
						.getDisplayLabel().equals(castOther.getDisplayLabel())))
				&& ((this.getFormat() == castOther.getFormat()) || (this
						.getFormat() != null
						&& castOther.getFormat() != null && this.getFormat()
						.equals(castOther.getFormat())))
				&& ((this.getContactName() == castOther.getContactName()) || (this
						.getContactName() != null
						&& castOther.getContactName() != null && this
						.getContactName().equals(castOther.getContactName())))
				&& ((this.getContactEmail() == castOther.getContactEmail()) || (this
						.getContactEmail() != null
						&& castOther.getContactEmail() != null && this
						.getContactEmail().equals(castOther.getContactEmail())))
				&& ((this.getHomepage() == castOther.getHomepage()) || (this
						.getHomepage() != null
						&& castOther.getHomepage() != null && this
						.getHomepage().equals(castOther.getHomepage())))
				&& ((this.getDocumentation() == castOther.getDocumentation()) || (this
						.getDocumentation() != null
						&& castOther.getDocumentation() != null && this
						.getDocumentation()
						.equals(castOther.getDocumentation())))
				&& ((this.getPublication() == castOther.getPublication()) || (this
						.getPublication() != null
						&& castOther.getPublication() != null && this
						.getPublication().equals(castOther.getPublication())))
				&& ((this.getUrn() == castOther.getUrn()) || (this.getUrn() != null
						&& castOther.getUrn() != null && this.getUrn().equals(
						castOther.getUrn())))
				&& ((this.getCodingScheme() == castOther.getCodingScheme()) || (this
						.getCodingScheme() != null
						&& castOther.getCodingScheme() != null && this
						.getCodingScheme().equals(castOther.getCodingScheme())))
				&& ((this.getIsFoundry() == castOther.getIsFoundry()) || (this
						.getIsFoundry() != null
						&& castOther.getIsFoundry() != null && this
						.getIsFoundry().equals(castOther.getIsFoundry())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getId() == null ? 0 : this.getId().hashCode());
		result = 37
				* result
				+ (getOntologyId() == null ? 0 : this.getOntologyId()
						.hashCode());
		result = 37 * result
				+ (getParentId() == null ? 0 : this.getParentId().hashCode());
		result = 37 * result
				+ (getUserId() == null ? 0 : this.getUserId().hashCode());
		result = 37
				* result
				+ (getInternalVersionNumber() == null ? 0 : this
						.getInternalVersionNumber().hashCode());
		result = 37
				* result
				+ (getVersionNumber() == null ? 0 : this.getVersionNumber()
						.hashCode());
		result = 37
				* result
				+ (getVersionStatus() == null ? 0 : this.getVersionStatus()
						.hashCode());
		result = 37 * result
				+ (getFilePath() == null ? 0 : this.getFilePath().hashCode());
		result = 37 * result
				+ (getIsCurrent() == null ? 0 : this.getIsCurrent().hashCode());
		result = 37 * result
				+ (getIsRemote() == null ? 0 : this.getIsRemote().hashCode());
		result = 37
				* result
				+ (getIsReviewed() == null ? 0 : this.getIsReviewed()
						.hashCode());
		result = 37
				* result
				+ (getDateCreated() == null ? 0 : this.getDateCreated()
						.hashCode());
		result = 37
				* result
				+ (getDateReleased() == null ? 0 : this.getDateReleased()
						.hashCode());
		result = 37
				* result
				+ (getDisplayLabel() == null ? 0 : this.getDisplayLabel()
						.hashCode());
		result = 37 * result
				+ (getFormat() == null ? 0 : this.getFormat().hashCode());
		result = 37
				* result
				+ (getContactName() == null ? 0 : this.getContactName()
						.hashCode());
		result = 37
				* result
				+ (getContactEmail() == null ? 0 : this.getContactEmail()
						.hashCode());
		result = 37 * result
				+ (getHomepage() == null ? 0 : this.getHomepage().hashCode());
		result = 37
				* result
				+ (getDocumentation() == null ? 0 : this.getDocumentation()
						.hashCode());
		result = 37
				* result
				+ (getPublication() == null ? 0 : this.getPublication()
						.hashCode());
		result = 37 * result
				+ (getUrn() == null ? 0 : this.getUrn().hashCode());
		result = 37
				* result
				+ (getCodingScheme() == null ? 0 : this.getCodingScheme()
						.hashCode());
		result = 37 * result
				+ (getIsFoundry() == null ? 0 : this.getIsFoundry().hashCode());
		return result;
	}

}