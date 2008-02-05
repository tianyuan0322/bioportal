package org.ncbo.stanford.domain.generated;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * AbstractNcboOntologyVersion entity provides the base persistence definition
 * of the NcboOntologyVersion entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboOntologyVersion implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private NcboUser ncboUser;
	private NcboOntologyVersion ncboOntologyVersion;
	private Integer ontologyId;
	private Integer internalVersionNumber;
	private String versionNumber;
	private String versionStatus;
	private String filePath;
	private Byte isCurrent;
	private Byte isRemote;
	private Byte isReviewed;
	private Date dateReleased;
	private Date dateCreated;
	private Integer statusId;
	private Set ncboOntologyMetadatas = new HashSet(0);
	private Set ncboOntologyFiles = new HashSet(0);
	private Set ncboOntologyLoadQueues = new HashSet(0);
	private Set ncboOntologyCategories = new HashSet(0);
	private Set ncboOntologyAdditionalMetadatas = new HashSet(0);
	private Set ncboOntologyVersions = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyVersion() {
	}

	/** minimal constructor */
	public AbstractNcboOntologyVersion(NcboUser ncboUser, Integer ontologyId,
			Integer internalVersionNumber, String versionNumber,
			Byte isCurrent, Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated, Integer statusId) {
		this.ncboUser = ncboUser;
		this.ontologyId = ontologyId;
		this.internalVersionNumber = internalVersionNumber;
		this.versionNumber = versionNumber;
		this.isCurrent = isCurrent;
		this.isRemote = isRemote;
		this.isReviewed = isReviewed;
		this.dateReleased = dateReleased;
		this.dateCreated = dateCreated;
		this.statusId = statusId;
	}

	/** full constructor */
	public AbstractNcboOntologyVersion(NcboUser ncboUser,
			NcboOntologyVersion ncboOntologyVersion, Integer ontologyId,
			Integer internalVersionNumber, String versionNumber,
			String versionStatus, String filePath, Byte isCurrent,
			Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated, Integer statusId, Set ncboOntologyMetadatas,
			Set ncboOntologyFiles, Set ncboOntologyLoadQueues,
			Set ncboOntologyCategories, Set ncboOntologyAdditionalMetadatas,
			Set ncboOntologyVersions) {
		this.ncboUser = ncboUser;
		this.ncboOntologyVersion = ncboOntologyVersion;
		this.ontologyId = ontologyId;
		this.internalVersionNumber = internalVersionNumber;
		this.versionNumber = versionNumber;
		this.versionStatus = versionStatus;
		this.filePath = filePath;
		this.isCurrent = isCurrent;
		this.isRemote = isRemote;
		this.isReviewed = isReviewed;
		this.dateReleased = dateReleased;
		this.dateCreated = dateCreated;
		this.statusId = statusId;
		this.ncboOntologyMetadatas = ncboOntologyMetadatas;
		this.ncboOntologyFiles = ncboOntologyFiles;
		this.ncboOntologyLoadQueues = ncboOntologyLoadQueues;
		this.ncboOntologyCategories = ncboOntologyCategories;
		this.ncboOntologyAdditionalMetadatas = ncboOntologyAdditionalMetadatas;
		this.ncboOntologyVersions = ncboOntologyVersions;
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

	public NcboOntologyVersion getNcboOntologyVersion() {
		return this.ncboOntologyVersion;
	}

	public void setNcboOntologyVersion(NcboOntologyVersion ncboOntologyVersion) {
		this.ncboOntologyVersion = ncboOntologyVersion;
	}

	public Integer getOntologyId() {
		return this.ontologyId;
	}

	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
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

	public Date getDateReleased() {
		return this.dateReleased;
	}

	public void setDateReleased(Date dateReleased) {
		this.dateReleased = dateReleased;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Integer getStatusId() {
		return this.statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public Set getNcboOntologyMetadatas() {
		return this.ncboOntologyMetadatas;
	}

	public void setNcboOntologyMetadatas(Set ncboOntologyMetadatas) {
		this.ncboOntologyMetadatas = ncboOntologyMetadatas;
	}

	public Set getNcboOntologyFiles() {
		return this.ncboOntologyFiles;
	}

	public void setNcboOntologyFiles(Set ncboOntologyFiles) {
		this.ncboOntologyFiles = ncboOntologyFiles;
	}

	public Set getNcboOntologyLoadQueues() {
		return this.ncboOntologyLoadQueues;
	}

	public void setNcboOntologyLoadQueues(Set ncboOntologyLoadQueues) {
		this.ncboOntologyLoadQueues = ncboOntologyLoadQueues;
	}

	public Set getNcboOntologyCategories() {
		return this.ncboOntologyCategories;
	}

	public void setNcboOntologyCategories(Set ncboOntologyCategories) {
		this.ncboOntologyCategories = ncboOntologyCategories;
	}

	public Set getNcboOntologyAdditionalMetadatas() {
		return this.ncboOntologyAdditionalMetadatas;
	}

	public void setNcboOntologyAdditionalMetadatas(
			Set ncboOntologyAdditionalMetadatas) {
		this.ncboOntologyAdditionalMetadatas = ncboOntologyAdditionalMetadatas;
	}

	public Set getNcboOntologyVersions() {
		return this.ncboOntologyVersions;
	}

	public void setNcboOntologyVersions(Set ncboOntologyVersions) {
		this.ncboOntologyVersions = ncboOntologyVersions;
	}

}