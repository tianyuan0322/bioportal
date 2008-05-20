package org.ncbo.stanford.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.util.filehandler.FileHandler;

public class OntologyBean {

	private Integer id;
	private Integer ontologyId;
	private Integer internalVersionNumber;
	private Integer parentId;
	private Integer userId;
	private String versionNumber;
	private String versionStatus;
	private String filePath;
	private Byte isCurrent;
	private Byte isRemote;
	private Byte isReviewed;
	private Integer statusId;
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
	private List<Integer> categoryIds = new ArrayList<Integer>(0);
	private List<String> filenames = new ArrayList<String>(0);
	

	/**
	 * Populates the OntologyBean with data from a NcboOntology View
	 * 
	 * @param ncboOntology
	 */
	public void populateFromEntity(NcboOntology ncboOntology) {
		this.setId(ncboOntology.getId());
		this.setOntologyId(ncboOntology.getOntologyId());
		this.setParentId(ncboOntology.getParentId());
		this.setUserId(ncboOntology.getUserId());
		this.setInternalVersionNumber(ncboOntology.getInternalVersionNumber());
		this.setVersionNumber(ncboOntology.getVersionNumber());
		this.setVersionStatus(ncboOntology.getVersionStatus());
		this.setFilePath(ncboOntology.getFilePath());
		this.setIsCurrent(ncboOntology.getIsCurrent());
		this.setIsRemote(ncboOntology.getIsRemote());
		this.setIsReviewed(ncboOntology.getIsReviewed());
		this.setDateCreated(ncboOntology.getDateCreated());
		this.setDateReleased(ncboOntology.getDateReleased());
		this.setDisplayLabel(ncboOntology.getDisplayLabel());
		this.setFormat(ncboOntology.getFormat());
		this.setContactName(ncboOntology.getContactName());
		this.setContactEmail(ncboOntology.getContactEmail());
		this.setHomepage(ncboOntology.getHomepage());
		this.setDocumentation(ncboOntology.getDocumentation());
		this.setPublication(ncboOntology.getPublication());
		this.setUrn(ncboOntology.getUrn());
		this.setCodingScheme(ncboOntology.getCodingScheme());
		this.setIsFoundry(ncboOntology.getIsFoundry());
		
		addFilenames(ncboOntology.getFilenames());		
	}


	/**
	 * Populates the OntologyBean to a NcboOntologyVersion Entity
	 * 
	 * @param ncboOntology
	 */
	public void populateToEntity(NcboOntologyVersion ontologyVersion) {

		// all the business logic regarding OntologyVersionId and OntologyId is in OntologyBean layer
		ontologyVersion.setId(this.getId());
		ontologyVersion.setOntologyId(this.getOntologyId());
		
		// Set Parent Object
		Integer parentId = this.getParentId();

		if (parentId != null) {
			NcboOntologyVersion parentOntology = new NcboOntologyVersion();
			parentOntology.setId(parentId);
			
			ontologyVersion.setNcboOntologyVersion(parentOntology);
		}
		
		// Set User Object
		NcboUser ncboUser = new NcboUser();
		ncboUser.setId(this.getUserId());
		ontologyVersion.setNcboUser(ncboUser);

		ontologyVersion.setVersionNumber(this.getVersionNumber());
		ontologyVersion.setIsCurrent(this.getIsCurrent());
		ontologyVersion.setIsRemote(this.getIsRemote());
		ontologyVersion.setIsReviewed(this.getIsReviewed());
		ontologyVersion.setDateCreated(this.getDateCreated());
		ontologyVersion.setDateReleased(this.getDateReleased());
	
		// NcboStatus Object
		NcboLStatus status = new NcboLStatus();
		status.setId(this.getStatusId());
		ontologyVersion.setNcboLStatus(status);
	
		ontologyVersion.setFilePath(this.getFilePath());
			
	}
	
	/**
	 * Populates the OntologyBean to a NcboOntologyMetadata Entity
	 * 
	 * @param ncboOntology
	 */
	public void populateToEntity(NcboOntologyMetadata metadata) {
				
		NcboOntologyVersion ncboOntologyVersion = new NcboOntologyVersion();
		ncboOntologyVersion.setInternalVersionNumber(this.getInternalVersionNumber());
		metadata.setNcboOntologyVersion(ncboOntologyVersion);
		
		metadata.setContactEmail(this.getContactEmail());
		metadata.setContactName(this.getContactName());
		metadata.setDisplayLabel(this.getDisplayLabel());
		metadata.setDocumentation(this.getDocumentation());
		metadata.setFormat(this.getFormat());
		metadata.setHomepage(this.getHomepage());
		metadata.setIsFoundry(this.getIsFoundry());
		metadata.setPublication(this.getPublication());
		metadata.setUrn(this.getUrn());
		
	}
	
	/**
	 * Populates the OntologyBean to a NcboOntologyFile Entity
	 * 
	 * @param ncboOntology
	 */
	public void populateToEntity(List<NcboOntologyFile> ontologyFileList) {
				
		
		List<String> fileNameList = this.getFilenames();


		for (String fileName : fileNameList) {
			
			NcboOntologyFile ontologyFile = new NcboOntologyFile();
			ontologyFile.setFilename(fileName);
			
			// TODO - validate this!!!
			NcboOntologyVersion ontologyVersion = new NcboOntologyVersion();
			this.populateToEntity(ontologyVersion);
			ontologyFile.setNcboOntologyVersion(ontologyVersion);
			
			ontologyFileList.add(ontologyFile);

		}
		
	}
	
	// TODO - populate to Category - NcboOntologyCategory
	// TODO - populate to loadQueue - NcboOntologyLoadQueue
	
	public String toString() {
		return "Id: " + getId() + " Name: " + getDisplayLabel() + " Ver: "
				+ getInternalVersionNumber();
	}

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
	 * @return the isCurrent
	 */
	public Byte getIsCurrent() {
		return isCurrent;
	}

	/**
	 * @param isCurrent
	 *            the isCurrent to set
	 */
	public void setIsCurrent(Byte isCurrent) {
		this.isCurrent = isCurrent;
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
	 * @return the parentId
	 */
	public Integer getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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
	public boolean addCategory(Integer arg0) {
		return categoryIds.add(arg0);
	}

	/**
	 * @param o
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
	 * @param filenames the filenames to set
	 */
	public void setFilenames(List<String> filenames) {
		this.filenames = filenames;
	}

	/**
	 * @return the codingScheme
	 */
	public String getCodingScheme() {
		return codingScheme;
	}

	/**
	 * @param codingScheme the codingScheme to set
	 */
	public void setCodingScheme(String codingScheme) {
		this.codingScheme = codingScheme;
	}


	public Integer getStatusId() {
		return statusId;
	}


	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}


}
