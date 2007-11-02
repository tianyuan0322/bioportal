package org.ncbo.stanford.domain.generated;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * AbstractNcboOntology entity provides the base persistence definition of the
 * NcboOntology entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboOntology implements java.io.Serializable {

	// Fields

	private Integer id;
	private NcboUser ncboUser;
	private NcboOntology ncboOntology;
	private Integer internalVersionNumber;
	private String versionNumber;
	private String versionStatus;
	private String filePath;
	private Byte isCurrent;
	private Byte isRemote;
	private Byte isReviewed;
	private Date dateReleased;
	private Date dateCreated;
	private Set ncboOntologyMetadatas = new HashSet(0);
	private Set ncboOntologyFiles = new HashSet(0);
	private Set ncboOntologyAdditionalMetadatas = new HashSet(0);
	private Set ncboOntologies = new HashSet(0);
	private Set ncboOntologyCategories = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboOntology() {
	}

	/** minimal constructor */
	public AbstractNcboOntology(NcboUser ncboUser,
			Integer internalVersionNumber, String versionNumber,
			Byte isCurrent, Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated) {
		this.ncboUser = ncboUser;
		this.internalVersionNumber = internalVersionNumber;
		this.versionNumber = versionNumber;
		this.isCurrent = isCurrent;
		this.isRemote = isRemote;
		this.isReviewed = isReviewed;
		this.dateReleased = dateReleased;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public AbstractNcboOntology(NcboUser ncboUser, NcboOntology ncboOntology,
			Integer internalVersionNumber, String versionNumber,
			String versionStatus, String filePath, Byte isCurrent,
			Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated, Set ncboOntologyMetadatas, Set ncboOntologyFiles,
			Set ncboOntologyAdditionalMetadatas, Set ncboOntologies,
			Set ncboOntologyCategories) {
		this.ncboUser = ncboUser;
		this.ncboOntology = ncboOntology;
		this.internalVersionNumber = internalVersionNumber;
		this.versionNumber = versionNumber;
		this.versionStatus = versionStatus;
		this.filePath = filePath;
		this.isCurrent = isCurrent;
		this.isRemote = isRemote;
		this.isReviewed = isReviewed;
		this.dateReleased = dateReleased;
		this.dateCreated = dateCreated;
		this.ncboOntologyMetadatas = ncboOntologyMetadatas;
		this.ncboOntologyFiles = ncboOntologyFiles;
		this.ncboOntologyAdditionalMetadatas = ncboOntologyAdditionalMetadatas;
		this.ncboOntologies = ncboOntologies;
		this.ncboOntologyCategories = ncboOntologyCategories;
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

	public NcboOntology getNcboOntology() {
		return this.ncboOntology;
	}

	public void setNcboOntology(NcboOntology ncboOntology) {
		this.ncboOntology = ncboOntology;
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

	public Set getNcboOntologyAdditionalMetadatas() {
		return this.ncboOntologyAdditionalMetadatas;
	}

	public void setNcboOntologyAdditionalMetadatas(
			Set ncboOntologyAdditionalMetadatas) {
		this.ncboOntologyAdditionalMetadatas = ncboOntologyAdditionalMetadatas;
	}

	public Set getNcboOntologies() {
		return this.ncboOntologies;
	}

	public void setNcboOntologies(Set ncboOntologies) {
		this.ncboOntologies = ncboOntologies;
	}

	public Set getNcboOntologyCategories() {
		return this.ncboOntologyCategories;
	}

	public void setNcboOntologyCategories(Set ncboOntologyCategories) {
		this.ncboOntologyCategories = ncboOntologyCategories;
	}

}