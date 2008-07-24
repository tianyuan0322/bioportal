package org.ncbo.stanford.domain.custom.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Class that represents the view of ontology with metadata. It is mappped to a
 * database view v_ncbo_ontology
 * 
 * @author Michael Dorf
 * 
 */
public class VNcboOntology {

	// Fields

	private Integer id;
	private Integer ontologyId;
	private Integer userId;
	private Integer internalVersionNumber;
	private String versionNumber;
	private String versionStatus;
	private String filePath;
	private Byte isRemote;
	private Byte isReviewed;
	private Integer statusId;
	private Date dateCreated;
	private Date dateReleased;
	private String oboFoundryId;
	private Byte isManual;
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

	private List<String> filenames = new ArrayList<String>(0);
	private List<Integer> categoryIds = new ArrayList<Integer>(0);

	// Property accessors

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the ontologyId
	 */
	public Integer getOntologyId() {
		return ontologyId;
	}

	/**
	 * @param ontologyId
	 *            the ontologyId to set
	 */
	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * @return the internalVersionNumber
	 */
	public Integer getInternalVersionNumber() {
		return internalVersionNumber;
	}

	/**
	 * @param internalVersionNumber
	 *            the internalVersionNumber to set
	 */
	public void setInternalVersionNumber(Integer internalVersionNumber) {
		this.internalVersionNumber = internalVersionNumber;
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @param versionNumber
	 *            the versionNumber to set
	 */
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	/**
	 * @return the versionStatus
	 */
	public String getVersionStatus() {
		return versionStatus;
	}

	/**
	 * @param versionStatus
	 *            the versionStatus to set
	 */
	public void setVersionStatus(String versionStatus) {
		this.versionStatus = versionStatus;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath
	 *            the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the isRemote
	 */
	public Byte getIsRemote() {
		return isRemote;
	}

	/**
	 * @param isRemote
	 *            the isRemote to set
	 */
	public void setIsRemote(Byte isRemote) {
		this.isRemote = isRemote;
	}

	/**
	 * @return the isReviewed
	 */
	public Byte getIsReviewed() {
		return isReviewed;
	}

	/**
	 * @param isReviewed
	 *            the isReviewed to set
	 */
	public void setIsReviewed(Byte isReviewed) {
		this.isReviewed = isReviewed;
	}

	/**
	 * @return the statusId
	 */
	public Integer getStatusId() {
		return statusId;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the dateReleased
	 */
	public Date getDateReleased() {
		return dateReleased;
	}

	/**
	 * @param dateReleased
	 *            the dateReleased to set
	 */
	public void setDateReleased(Date dateReleased) {
		this.dateReleased = dateReleased;
	}

	/**
	 * @return the displayLabel
	 */
	public String getDisplayLabel() {
		return displayLabel;
	}

	/**
	 * @param displayLabel
	 *            the displayLabel to set
	 */
	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * @param contactName
	 *            the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * @return the contactEmail
	 */
	public String getContactEmail() {
		return contactEmail;
	}

	/**
	 * @param contactEmail
	 *            the contactEmail to set
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	/**
	 * @return the homepage
	 */
	public String getHomepage() {
		return homepage;
	}

	/**
	 * @param homepage
	 *            the homepage to set
	 */
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	/**
	 * @return the documentation
	 */
	public String getDocumentation() {
		return documentation;
	}

	/**
	 * @param documentation
	 *            the documentation to set
	 */
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	/**
	 * @return the publication
	 */
	public String getPublication() {
		return publication;
	}

	/**
	 * @param publication
	 *            the publication to set
	 */
	public void setPublication(String publication) {
		this.publication = publication;
	}

	/**
	 * @return the urn
	 */
	public String getUrn() {
		return urn;
	}

	/**
	 * @param urn
	 *            the urn to set
	 */
	public void setUrn(String urn) {
		this.urn = urn;
	}

	/**
	 * @return the codingScheme
	 */
	public String getCodingScheme() {
		return codingScheme;
	}

	/**
	 * @param codingScheme
	 *            the codingScheme to set
	 */
	public void setCodingScheme(String codingScheme) {
		this.codingScheme = codingScheme;
	}

	/**
	 * @return the isFoundry
	 */
	public Byte getIsFoundry() {
		return isFoundry;
	}

	/**
	 * @param isFoundry
	 *            the isFoundry to set
	 */
	public void setIsFoundry(Byte isFoundry) {
		this.isFoundry = isFoundry;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof VNcboOntology))
			return false;
		VNcboOntology castOther = (VNcboOntology) other;

		return ((this.getId() == castOther.getId()) || (this.getId() != null
				&& castOther.getId() != null && this.getId().equals(
				castOther.getId())))
				&& ((this.getOntologyId() == castOther.getOntologyId()) || (this
						.getOntologyId() != null
						&& castOther.getOntologyId() != null && this
						.getOntologyId().equals(castOther.getOntologyId())))
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

	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addFilename(String filename) {
		return filenames.add(filename);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addFilenames(Collection<? extends String> c) {
		return filenames.addAll(c);
	}

	/**
	 * @return the filenames
	 */
	public List<String> getFilenames() {
		return filenames;
	}

	/**
	 * @param filenames
	 *            the filenames to set
	 */
	public void setFilenames(List<String> filenames) {
		this.filenames = filenames;
	}

	/**
	 * @return the categoryIds
	 */
	public List<Integer> getCategoryIds() {
		return categoryIds;
	}

	/**
	 * @param categoryIds the categoryIds to set
	 */
	public void setCategoryIds(List<Integer> categoryIds) {
		this.categoryIds = categoryIds;
	}
	
	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addCategoryId(Integer categoryId) {
		return categoryIds.add(categoryId);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addcategoryIds(Collection<? extends Integer> c) {
		return categoryIds.addAll(c);
	}

	/**
	 * @return the oboFoundryId
	 */
	public String getOboFoundryId() {
		return oboFoundryId;
	}

	/**
	 * @param oboFoundryId the oboFoundryId to set
	 */
	public void setOboFoundryId(String oboFoundryId) {
		this.oboFoundryId = oboFoundryId;
	}

	/**
	 * @return the isManual
	 */
	public Byte getIsManual() {
		return isManual;
	}

	/**
	 * @param isManual the isManual to set
	 */
	public void setIsManual(Byte isManual) {
		this.isManual = isManual;
	}
}