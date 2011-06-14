package org.ncbo.stanford.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.bean.acl.UserAcl;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;

public class OntologyBean {

	public static final String DEFAULT_SYNONYM_SLOT = "http://www.w3.org/2004/02/skos/core#altLabel";
	public static final String DEFAULT_PREFERRED_NAME_SLOT = "http://www.w3.org/2004/02/skos/core#prefLabel";
	public static final String DEFAULT_DEFINITION_SLOT = "http://www.w3.org/2004/02/skos/core#definition";
	public static final String DEFAULT_AUTHOR_SLOT = "http://purl.org/dc/elements/1.1/creator";

	private Integer id;
	private Integer ontologyId;
	// virtual view id(s) on the virtual ontology
	private List<Integer> virtualViewIds = new ArrayList<Integer>(0);
	private Integer internalVersionNumber;
	private Integer userId;
	private String versionNumber;
	private String versionStatus;
	private Byte isRemote;
	private Byte isReviewed;
	private Integer statusId;
	private Date dateCreated;
	private Date dateReleased;
	private String oboFoundryId;
	private Byte isManual;
	private String displayLabel;
	private String description;
	private String abbreviation;
	private String format;
	private String contactName;
	private String contactEmail;
	private String homepage;
	private String documentation;
	private String publication;
	private String urn;
	private String codingScheme;
	private String targetTerminologies;
	private Byte isFlat;
	private Byte isFoundry;
	private Byte isMetadataOnly;
	private String synonymSlot;
	private String preferredNameSlot;
	private String documentationSlot;
	private String authorSlot;
	private String slotWithUniqueValue;
	private Integer preferredMaximumSubclassLimit;
	private UserAcl userAcl = new UserAcl(0);

	private boolean isView = false;

	// category id(s)
	private List<Integer> categoryIds = new ArrayList<Integer>(0);

	// group id(s)
	private List<Integer> groupIds = new ArrayList<Integer>(0);

	// file name(s)
	private List<String> filenames = new ArrayList<String>(0);

	// source fileItem
	private FileItem fileItem;

	// destination directory
	private String filePath;

	// Download location of ontology
	private String downloadLocation;

	// views on this ontology version
	private List<Integer> hasViews = new ArrayList<Integer>(0);

	// view specific properties
	private List<Integer> viewOnOntologyVersionId = new ArrayList<Integer>(0);
	private String viewDefinition;
	private String viewDefinitionLanguage;
	private String viewGenerationEngine;

	public OntologyBean(boolean isView) {
		this.isView = isView;
	}

	/**
	 * Checks whether user has access to this ontology (the user id is present
	 * in this ontology's ACL)
	 * 
	 * @param ontologyId
	 * @return
	 */
	public boolean isInAcl(Integer userId) {
		return userAcl.containsKey(userId);
	}

	/**
	 * Adds an user Id to this ontology's access list (ACL)
	 * 
	 * @param userId
	 * @param isOwner
	 */
	public void addUserToAcl(Integer userId, Boolean isOwner) {
		userAcl.put(userId, isOwner);
	}

	/**
	 * Populates the OntologyBean to a NcboOntologyFile Entity. OntologyVersion
	 * should have been populated from OntologyBean before making this call.
	 * 
	 * @param ontologyFileList
	 */

	public void populateToFileEntity(List<NcboOntologyFile> ontologyFileList) {
		for (String fileName : filenames) {
			NcboOntologyFile ontologyFile = new NcboOntologyFile();
			ontologyFile.setFilename(fileName);
			ontologyFile.setOntologyVersionId(id);
			ontologyFileList.add(ontologyFile);
		}
	}

	/**
	 * NOTE: This method should replicate the
	 * {@link #populateDefaultStatus(NcboLStatus)} method an is to be used in
	 * the ontological metadata implementation
	 * 
	 * Returns default status, i.e. "1"(waiting) for local upload,
	 * "5"(notapplicable) for remote.
	 */
	public Integer getDefaultStatus() {
		if (this.isMetadataOnly()) {
			return StatusEnum.STATUS_NOTAPPLICABLE.getStatus();
		} else {
			return StatusEnum.STATUS_WAITING.getStatus();
		}
	}

	/**
	 * Populates a NcboOntologyLoadQueue Entity from this ontologyBean.
	 * OntologyVersion should have been populated from OntologyBean before
	 * making this call.
	 * 
	 * @param NcboOntologyLoadQueue
	 *            , NcboOntologyVersion
	 */
	public void populateToLoadQueueEntity(NcboOntologyLoadQueue loadQueue,
			Integer ontologyVersionId) {
		if (loadQueue != null) {
			Set<NcboOntologyLoadQueue> ncboOntologyLoadQueueSet = new HashSet<NcboOntologyLoadQueue>();

			// OntologyVersion object
			loadQueue.setOntologyVersionId(ontologyVersionId);

			// Set NcboStatus
			NcboLStatus status = new NcboLStatus();
			populateDefaultStatus(status);
			loadQueue.setNcboLStatus(status);

			loadQueue.setDateCreated(Calendar.getInstance().getTime());

			ncboOntologyLoadQueueSet.add(loadQueue);
		}
	}

	public String toString() {
		final int max = 80;
		String viewDef = this.getViewDefinition();

		if (viewDef != null && viewDef.length() > max) {
			viewDef = viewDef.substring(0, max) + "...";
		}

		String name = isView ? "OntologyView " : "Ontology ";

		return name + "{Id: " + this.getId() + ", Ontology Id: "
				+ this.getOntologyId() + ", Virtual View Ids: "
				+ this.getVirtualViewIds() + ", Remote: " + this.getIsRemote()
				+ ", Obo Foundry Id: " + this.getOboFoundryId()
				+ ", Internal Version Number: "
				+ this.getInternalVersionNumber() + ", Date Created: "
				+ this.getDateCreated() + ", User Id: " + this.getUserId()
				+ ", Version Number: " + this.getVersionNumber()
				+ ", Version Status: " + this.getVersionStatus()
				+ ", Display Label: " + this.getDisplayLabel()
				+ ", Description: " + this.getDescription()
				+ ", Abbreviation: " + this.getAbbreviation()
				+ ", Format: "+ this.getFormat() 
				+ ", Download Location: "	+ this.getDownloadLocation()
				+ ", Contact Name: "	+ this.getContactName() 
				+ ", Contact Email: "	+ this.getContactEmail() 
				+ ", isFlat: " + this.getIsFlat()
				+ ", Foundry: " + this.getIsFoundry()
				+ ", IsMetadataOnly: " + this.getIsMetadataOnly()
				+ ", Coding Scheme: " + this.getCodingScheme()
				+ ", Target Terminologies: " + this.getTargetTerminologies()
				+ ", Synonym Slot: " + this.getSynonymSlot()
				+ ", Preferred Name Slot: " + this.getPreferredNameSlot()
				+ ", View Definition: " + viewDef
				+ ", View Definition Language: "
				+ this.getViewDefinitionLanguage()
				+ ", View Generation Engine: " + this.getViewGenerationEngine()
				+ ", View on Ontology Versions: "
				+ this.getViewOnOntologyVersionId() + ", ACL: "
				+ this.getUserAcl() + "}";
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
	 * @return the virtualViewIds
	 */
	public List<Integer> getVirtualViewIds() {
		return virtualViewIds;
	}

	/**
	 * @param virtualViewIds
	 *            the virtualViewIds to set
	 */
	public void setVirtualViewIds(List<Integer> virtualViewIds) {
		this.virtualViewIds = virtualViewIds;
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

	public String getDownloadLocation() {
		return downloadLocation;
	}

	public void setDownloadLocation(String downloadLocation) {
		this.downloadLocation = downloadLocation;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the abbreviation
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * @param abbreviation
	 *            the abbreviation to set
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
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
	 * 
	 * @return the isFlat
	 */
	public Byte getIsFlat() {
		return isFlat;
	}
   /**
    * Set isFlat
    * @param isFlat
    */
	public void setIsFlat(Byte isFlat) {
		this.isFlat = isFlat;
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

	public Byte getIsMetadataOnly() {
		return isMetadataOnly;
	}

	public void setIsMetadataOnly(Byte isMetadataOnly) {
		this.isMetadataOnly = isMetadataOnly;
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
	 * @param categoryIds
	 *            the categoryIds to set
	 */
	public void setCategoryIds(List<Integer> categoryIds) {
		this.categoryIds = categoryIds;
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addCategoryId(Integer arg0) {
		return categoryIds.add(arg0);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addCategoryIds(Collection<? extends Integer> c) {
		return categoryIds.addAll(c);
	}

	/**
	 * @return the groupIds
	 */
	public List<Integer> getGroupIds() {
		return groupIds;
	}

	/**
	 * @param groupIds
	 *            the groupIds to set
	 */
	public void setGroupIds(List<Integer> groupIds) {
		this.groupIds = groupIds;
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addGroupId(Integer arg0) {
		return groupIds.add(arg0);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addGroupIds(Collection<? extends Integer> c) {
		return groupIds.addAll(c);
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
	 * @param filenames
	 *            the filenames to set
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
	 * @param codingScheme
	 *            the codingScheme to set
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

	public String getOntologyDirPath() {
		return "/" + this.getOntologyId() + "/"
				+ this.getInternalVersionNumber();
	}

	/**
	 * @return the fileItem
	 */
	public FileItem getFileItem() {
		return fileItem;
	}

	/**
	 * @param fileItem
	 *            the fileItem to set
	 */
	public void setFileItem(FileItem fileItem) {
		this.fileItem = fileItem;
	}

	/**
	 * @return the oboFoundryId
	 */
	public String getOboFoundryId() {
		return oboFoundryId;
	}

	/**
	 * @param oboFoundryId
	 *            the oboFoundryId to set
	 */
	public void setOboFoundryId(String oboFoundryId) {
		this.oboFoundryId = oboFoundryId;
	}

	public boolean isMetadataOnly() {
		if (this.isRemote()
				&& this.getVersionNumber() != null
				&& this.getVersionNumber().equalsIgnoreCase(
						MessageUtils.getMessage("remote.ontology.version"))) {
			return true;
		} else if (this.getIsMetadataOnly() != null
				&& this.getIsMetadataOnly().equals(ApplicationConstants.TRUE)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Populate default status in the bean Status is "1"(waiting) for local
	 * upload, "5"(notapplicable) for remote.
	 */
	public void populateDefaultStatus(NcboLStatus status) {
		if (this.isMetadataOnly()) {
			status.setId(StatusEnum.STATUS_NOTAPPLICABLE.getStatus());
		} else {
			status.setId(StatusEnum.STATUS_WAITING.getStatus());
		}
	}

	/**
	 * @return the isRemote
	 */
	public boolean isRemote() {
		if (this.getIsRemote().equals(ApplicationConstants.TRUE)) {
			return true;
		}

		return false;
	}

	/**
	 * @return the isManual
	 */
	public Byte getIsManual() {
		return isManual;
	}

	/**
	 * @param isManual
	 *            the isManual to set
	 */
	public void setIsManual(Byte isManual) {
		this.isManual = isManual;
	}

	/**
	 * @return the targetTerminologies
	 */
	public String getTargetTerminologies() {
		return targetTerminologies;
	}

	/**
	 * @param targetTerminologies
	 *            the targetTerminologies to set
	 */
	public void setTargetTerminologies(String targetTerminologies) {
		this.targetTerminologies = targetTerminologies;
	}

	/**
	 * @return the synonymSlot
	 */
	public String getSynonymSlot() {
		return synonymSlot;
	}

	/**
	 * @param synonymSlot
	 *            the synonymSlot to set
	 */
	public void setSynonymSlot(String synonymSlot) {
		this.synonymSlot = synonymSlot;
	}

	/**
	 * @return the preferredNameSlot
	 */
	public String getPreferredNameSlot() {
		return preferredNameSlot;
	}

	/**
	 * @param preferredNameSlot
	 *            the preferredNameSlot to set
	 */
	public void setPreferredNameSlot(String preferredNameSlot) {
		this.preferredNameSlot = preferredNameSlot;
	}

	/**
	 * @return the documentationSlot
	 */
	public String getDocumentationSlot() {
		return documentationSlot;
	}

	/**
	 * @param documentationSlot
	 *            the documentationSlot to set
	 */
	public void setDocumentationSlot(String documentationSlot) {
		this.documentationSlot = documentationSlot;
	}

	/**
	 * @return the authorSlot
	 */
	public String getAuthorSlot() {
		return authorSlot;
	}

	/**
	 * @param authorSlot
	 *            the authorSlot to set
	 */
	public void setAuthorSlot(String authorSlot) {
		this.authorSlot = authorSlot;
	}

	/**
	 * @return the slotWithUniqueValue
	 */
	public String getSlotWithUniqueValue() {
		return slotWithUniqueValue;
	}

	/**
	 * @param slotWithUniqueValue
	 *            the slotWithUniqueValue to set
	 */
	public void setSlotWithUniqueValue(String slotWithUniqueValue) {
		this.slotWithUniqueValue = slotWithUniqueValue;
	}

	/**
	 * @return the preferredMaximumSubclassLimit
	 */
	public Integer getPreferredMaximumSubclassLimit() {
		return preferredMaximumSubclassLimit;
	}

	/**
	 * @param preferredMaximumSubclassLimit
	 *            the preferredMaximumSubclassLimit to set
	 */
	public void setPreferredMaximumSubclassLimit(
			Integer preferredMaximumSubclassLimit) {
		this.preferredMaximumSubclassLimit = preferredMaximumSubclassLimit;
	}

	/**
	 * @return the isView
	 */
	public boolean isView() {
		return isView;
	}

	/**
	 * @param isView
	 *            the isView to set
	 */
	public void setView(boolean isView) {
		this.isView = isView;
	}

	/**
	 * @return the hasViews
	 */
	public List<Integer> getHasViews() {
		return hasViews;
	}

	/**
	 * @param hasViews
	 *            the hasViews to set
	 */
	public void setHasViews(List<Integer> hasViews) {
		this.hasViews = hasViews;
	}

	/**
	 * @return the viewOnOntologyVersionId
	 */
	public List<Integer> getViewOnOntologyVersionId() {
		return viewOnOntologyVersionId;
	}

	/**
	 * @param viewOnOntologyVersionId
	 *            the viewOnOntologyVersionId to set
	 */
	public void setViewOnOntologyVersionId(List<Integer> viewOnOntologyVersionId) {
		this.viewOnOntologyVersionId = viewOnOntologyVersionId;
	}

	/**
	 * @return the viewDefinition
	 */
	public String getViewDefinition() {
		return viewDefinition;
	}

	/**
	 * @param viewDefinition
	 *            the viewDefinition to set
	 */
	public void setViewDefinition(String viewDefinition) {
		this.viewDefinition = viewDefinition;
	}

	/**
	 * @return the viewDefinitionLanguage
	 */
	public String getViewDefinitionLanguage() {
		return viewDefinitionLanguage;
	}

	/**
	 * @param viewDefinitionLanguage
	 *            the viewDefinitionLanguage to set
	 */
	public void setViewDefinitionLanguage(String viewDefinitionLanguage) {
		this.viewDefinitionLanguage = viewDefinitionLanguage;
	}

	/**
	 * @return the viewGenerationEngine
	 */
	public String getViewGenerationEngine() {
		return viewGenerationEngine;
	}

	/**
	 * @param viewGenerationEngine
	 *            the viewGenerationEngine to set
	 */
	public void setViewGenerationEngine(String viewGenerationEngine) {
		this.viewGenerationEngine = viewGenerationEngine;
	}

	/**
	 * @return the userAcl
	 */
	public Map<Integer, Boolean> getUserAcl() {
		return userAcl;
	}
}
