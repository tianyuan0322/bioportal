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
	private NcboOntology ncboOntology;
	private NcboUser ncboUser;
	private NcboLStatus ncboLStatus;
	private Integer internalVersionNumber;
	private String versionNumber;
	private String versionStatus;
	private String filePath;
	private Byte isRemote;
	private Byte isReviewed;
	private Date dateReleased;
	private Date dateCreated;
	private Set ncboOntologyVersionMetadatas = new HashSet(0);
	private Set ncboOntologyFiles = new HashSet(0);
	private Set ncboOntologyLoadQueues = new HashSet(0);
	private Set ncboOntologyCategories = new HashSet(0);
	private Set ncboOntologyAdditionalVersionMetadatas = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyVersion() {
	}

	/** minimal constructor */
	public AbstractNcboOntologyVersion(NcboOntology ncboOntology,
			NcboUser ncboUser, NcboLStatus ncboLStatus,
			Integer internalVersionNumber, String versionNumber, Byte isRemote,
			Byte isReviewed, Date dateReleased, Date dateCreated) {
		this.ncboOntology = ncboOntology;
		this.ncboUser = ncboUser;
		this.ncboLStatus = ncboLStatus;
		this.internalVersionNumber = internalVersionNumber;
		this.versionNumber = versionNumber;
		this.isRemote = isRemote;
		this.isReviewed = isReviewed;
		this.dateReleased = dateReleased;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public AbstractNcboOntologyVersion(NcboOntology ncboOntology,
			NcboUser ncboUser, NcboLStatus ncboLStatus,
			Integer internalVersionNumber, String versionNumber,
			String versionStatus, String filePath, Byte isRemote,
			Byte isReviewed, Date dateReleased, Date dateCreated,
			Set ncboOntologyVersionMetadatas, Set ncboOntologyFiles,
			Set ncboOntologyLoadQueues, Set ncboOntologyCategories,
			Set ncboOntologyAdditionalVersionMetadatas) {
		this.ncboOntology = ncboOntology;
		this.ncboUser = ncboUser;
		this.ncboLStatus = ncboLStatus;
		this.internalVersionNumber = internalVersionNumber;
		this.versionNumber = versionNumber;
		this.versionStatus = versionStatus;
		this.filePath = filePath;
		this.isRemote = isRemote;
		this.isReviewed = isReviewed;
		this.dateReleased = dateReleased;
		this.dateCreated = dateCreated;
		this.ncboOntologyVersionMetadatas = ncboOntologyVersionMetadatas;
		this.ncboOntologyFiles = ncboOntologyFiles;
		this.ncboOntologyLoadQueues = ncboOntologyLoadQueues;
		this.ncboOntologyCategories = ncboOntologyCategories;
		this.ncboOntologyAdditionalVersionMetadatas = ncboOntologyAdditionalVersionMetadatas;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboOntology getNcboOntology() {
		return this.ncboOntology;
	}

	public void setNcboOntology(NcboOntology ncboOntology) {
		this.ncboOntology = ncboOntology;
	}

	public NcboUser getNcboUser() {
		return this.ncboUser;
	}

	public void setNcboUser(NcboUser ncboUser) {
		this.ncboUser = ncboUser;
	}

	public NcboLStatus getNcboLStatus() {
		return this.ncboLStatus;
	}

	public void setNcboLStatus(NcboLStatus ncboLStatus) {
		this.ncboLStatus = ncboLStatus;
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

	public Set getNcboOntologyVersionMetadatas() {
		return this.ncboOntologyVersionMetadatas;
	}

	public void setNcboOntologyVersionMetadatas(Set ncboOntologyVersionMetadatas) {
		this.ncboOntologyVersionMetadatas = ncboOntologyVersionMetadatas;
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

	public Set getNcboOntologyAdditionalVersionMetadatas() {
		return this.ncboOntologyAdditionalVersionMetadatas;
	}

	public void setNcboOntologyAdditionalVersionMetadatas(
			Set ncboOntologyAdditionalVersionMetadatas) {
		this.ncboOntologyAdditionalVersionMetadatas = ncboOntologyAdditionalVersionMetadatas;
	}

}