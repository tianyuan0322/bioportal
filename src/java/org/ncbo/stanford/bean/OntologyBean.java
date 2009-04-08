package org.ncbo.stanford.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.domain.generated.NcboLCategory;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboOntologyCategory;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.domain.generated.NcboOntologyVersionMetadata;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;

public class OntologyBean {

	private Integer id;
	private Integer ontologyId;
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
	private Byte isFoundry;
	private String synonymSlot;
	private String preferredNameSlot;

	// category id(s)
	private List<Integer> categoryIds = new ArrayList<Integer>(0);

	// file name(s)
	private List<String> filenames = new ArrayList<String>(0);

	// source fileItem
	private FileItem fileItem;

	// destination directory
	private String filePath;

	/**
	 * Populates the OntologyBean with data from a NcboOntology View
	 * 
	 * @param ncboOntology
	 */
	public void populateFromEntity(VNcboOntology ncboOntology) {
		if (ncboOntology != null) {
			setId(ncboOntology.getId());
			setOntologyId(ncboOntology.getOntologyId());
			setUserId(ncboOntology.getUserId());
			setInternalVersionNumber(ncboOntology.getInternalVersionNumber());
			setVersionNumber(ncboOntology.getVersionNumber());
			setVersionStatus(ncboOntology.getVersionStatus());
			setFilePath(ncboOntology.getFilePath());
			setIsRemote(ncboOntology.getIsRemote());
			setIsReviewed(ncboOntology.getIsReviewed());
			setStatusId(ncboOntology.getStatusId());
			setDateCreated(ncboOntology.getDateCreated());
			setDateReleased(ncboOntology.getDateReleased());
			setOboFoundryId(ncboOntology.getOboFoundryId());
			setIsManual(ncboOntology.getIsManual());
			setDisplayLabel(ncboOntology.getDisplayLabel());
			setDescription(ncboOntology.getDescription());
			setAbbreviation(ncboOntology.getAbbreviation());
			setFormat(ncboOntology.getFormat());
			setContactName(ncboOntology.getContactName());
			setContactEmail(ncboOntology.getContactEmail());
			setHomepage(ncboOntology.getHomepage());
			setDocumentation(ncboOntology.getDocumentation());
			setPublication(ncboOntology.getPublication());
			setUrn(ncboOntology.getUrn());
			setCodingScheme(ncboOntology.getCodingScheme());
			setIsFoundry(ncboOntology.getIsFoundry());
			setTargetTerminologies(ncboOntology.getTargetTerminologies());
			setSynonymSlot(ncboOntology.getSynonymSlot());
			setPreferredNameSlot(ncboOntology.getPreferredNameSlot());

			populateFilenamesFromEntity(ncboOntology);
			populateCategoryIdsFromEntity(ncboOntology);
		}
	}

	private void populateFilenamesFromEntity(VNcboOntology ncboOntology) {
		Collection<NcboOntologyFile> files = ncboOntology
				.getNcboOntologyFiles();

		for (NcboOntologyFile f : files) {
			filenames.add(f.getFilename());
		}
	}

	private void populateCategoryIdsFromEntity(VNcboOntology ncboOntology) {
		Collection<NcboOntologyCategory> categories = ncboOntology
				.getNcboOntologyCategories();

		for (NcboOntologyCategory c : categories) {
			categoryIds.add(c.getNcboLCategory().getId());
		}
	}

	/**
	 * Populates a NcboOntologyMetadata Entity from this ontologyBean.
	 * OntologyVersion should have been populated from OntologyBean before
	 * making this call.
	 * 
	 * @param ontologyVersion
	 */
	public void populateToMetadataEntity(NcboOntologyVersionMetadata metadata,
			NcboOntologyVersion ontologyVersion) {
		if (metadata != null) {
			Set<NcboOntologyVersionMetadata> ncboOntologyMetadataSet = new HashSet<NcboOntologyVersionMetadata>();

			metadata.setNcboOntologyVersion(ontologyVersion);

			if (displayLabel != null) {
				metadata
						.setDisplayLabel(StringHelper
								.isNullOrNullString(displayLabel) ? null
								: displayLabel);
			}

			if (format != null) {
				metadata
						.setFormat(StringHelper.isNullOrNullString(format) ? null
								: format);
			}

			if (description != null) {
				metadata.setDescription(StringHelper
						.isNullOrNullString(description) ? null : description);
			}

			if (abbreviation != null) {
				metadata
						.setAbbreviation(StringHelper
								.isNullOrNullString(abbreviation) ? null
								: abbreviation);
			}

			if (contactName != null) {
				metadata.setContactName(StringHelper
						.isNullOrNullString(contactName) ? null : contactName);
			}

			if (contactEmail != null) {
				metadata
						.setContactEmail(StringHelper
								.isNullOrNullString(contactEmail) ? null
								: contactEmail);
			}

			if (documentation != null) {
				metadata.setDocumentation(StringHelper
						.isNullOrNullString(documentation) ? null
						: documentation);
			}

			if (homepage != null) {
				metadata
						.setHomepage(StringHelper.isNullOrNullString(homepage) ? null
								: homepage);
			}

			if (isFoundry != null) {
				metadata.setIsFoundry(isFoundry);
			}

			if (publication != null) {
				metadata.setPublication(StringHelper
						.isNullOrNullString(publication) ? null : publication);
			}

			if (urn != null) {
				metadata.setUrn(StringHelper.isNullOrNullString(urn) ? null
						: urn);
			}

			if (codingScheme != null) {
				metadata
						.setCodingScheme(StringHelper
								.isNullOrNullString(codingScheme) ? null
								: codingScheme);
			}

			if (targetTerminologies != null) {
				metadata.setTargetTerminologies(StringHelper
						.isNullOrNullString(targetTerminologies) ? null
						: targetTerminologies);
			}

			if (synonymSlot != null) {
				metadata.setSynonymSlot(StringHelper
						.isNullOrNullString(synonymSlot) ? null : synonymSlot);
			}

			if (preferredNameSlot != null) {
				metadata.setPreferredNameSlot(StringHelper
						.isNullOrNullString(preferredNameSlot) ? null
						: preferredNameSlot);
			}

			ncboOntologyMetadataSet.add(metadata);
			ontologyVersion
					.setNcboOntologyVersionMetadatas(ncboOntologyMetadataSet);
		}
	}

	/**
	 * Populates NcboOntology Entity from this OntologyBean
	 * 
	 * @param ncboOntology
	 */
	public void populateToOntologyEntity(NcboOntology ont) {
		if (ont != null) {
			if (ontologyId != null) {
				ont.setId(ontologyId);
			}

			if (isManual != null) {
				ont.setIsManual(isManual);
			}

			// This overwrites the obo foundry id with nothing,
			// moving it to line 535 of OntologyServiceImpl since
			// it should only be set on new ontologies
			// ont.setOboFoundryId(getOboFoundryId());
		}
	}

	/**
	 * Populates NcboOntologyVersion Entity from this OntologyBean
	 * 
	 * @param ncboOntology
	 */
	public void populateToVersionEntity(NcboOntologyVersion ontologyVersion) {
		if (ontologyVersion != null) {
			if (id != null) {
				ontologyVersion.setId(id);
			}

			if (ontologyId != null) {
				NcboOntology ont = new NcboOntology();
				ont.setId(ontologyId);
				ontologyVersion.setNcboOntology(ont);
			}

			if (userId != null) {
				NcboUser ncboUser = new NcboUser();
				ncboUser.setId(userId);
				ontologyVersion.setNcboUser(ncboUser);
			}

			if (versionNumber != null) {
				ontologyVersion.setVersionNumber(StringHelper
						.isNullOrNullString(versionNumber) ? null
						: versionNumber);
			}

			// do not override versionStatus if blank
			if (versionStatus != null) {
				ontologyVersion.setVersionStatus(StringHelper
						.isNullOrNullString(versionStatus) ? null
						: versionStatus);
			}

			// do not override internalVersionNumber if blank
			if (internalVersionNumber != null) {
				ontologyVersion.setInternalVersionNumber(internalVersionNumber);
			}

			if (isRemote != null) {
				ontologyVersion.setIsRemote(isRemote);
			}

			if (isReviewed != null) {
				ontologyVersion.setIsReviewed(isReviewed);
			}

			ontologyVersion.setDateCreated((dateCreated != null) ? dateCreated
					: Calendar.getInstance().getTime());

			if (dateReleased != null) {
				ontologyVersion.setDateReleased(dateReleased);
			}

			// populate status, if necessary
			populateStatusToVersionEntity(ontologyVersion);

			// do not override filePath if blank
			if (filePath != null) {
				ontologyVersion.setFilePath(StringHelper
						.isNullOrNullString(filePath) ? null : filePath);
			}
		}
	}

	/**
	 * Populate status in the version entity
	 */
	private void populateStatusToVersionEntity(
			NcboOntologyVersion ontologyVersion) {
		NcboLStatus status = null;

		if (statusId != null) {
			status = new NcboLStatus();
			status.setId(statusId);
			ontologyVersion.setNcboLStatus(status);
		} else if (ontologyVersion.getNcboLStatus() == null) {
			status = new NcboLStatus();
			populateDefaultStatus(status);
			ontologyVersion.setNcboLStatus(status);
		}
	}

	/**
	 * Populates the OntologyBean to a NcboOntologyFile Entity. OntologyVersion
	 * should have been populated from OntologyBean before making this call.
	 * 
	 * @param ncboOntology
	 */

	public void populateToFileEntity(List<NcboOntologyFile> ontologyFileList,
			NcboOntologyVersion ontologyVersion) {
		List<String> fileNameList = this.getFilenames();
		Set<NcboOntologyFile> ncboOntologyFileSet = new HashSet<NcboOntologyFile>();

		for (String fileName : fileNameList) {
			NcboOntologyFile ontologyFile = new NcboOntologyFile();
			ontologyFile.setFilename(fileName);
			ontologyFile.setNcboOntologyVersion(ontologyVersion);

			ontologyFileList.add(ontologyFile);
			ncboOntologyFileSet.add(ontologyFile);
		}

		ontologyVersion.setNcboOntologyFiles(ncboOntologyFileSet);
	}

	/**
	 * Populates the OntologyBean to a NcboOntologyCategory Entity.
	 * OntologyVersion should have been populated from OntologyBean before
	 * making this call.
	 * 
	 * @param ncboOntology
	 */
	public void populateToCategoryEntity(
			List<NcboOntologyCategory> ontologyCategoryList,
			NcboOntologyVersion ontologyVersion) {
		Set<NcboOntologyCategory> ncboOntologyCategorySet = new HashSet<NcboOntologyCategory>();

		for (Integer categoryId : categoryIds) {
			NcboOntologyCategory ontologyCategory = new NcboOntologyCategory();
			NcboLCategory ncboLCategory = new NcboLCategory();
			ncboLCategory.setId(categoryId);
			ontologyCategory.setNcboLCategory(ncboLCategory);
			ontologyCategory.setNcboOntologyVersion(ontologyVersion);

			ontologyCategoryList.add(ontologyCategory);
			ncboOntologyCategorySet.add(ontologyCategory);
		}

		ontologyVersion.setNcboOntologyCategories(ncboOntologyCategorySet);
	}

	/**
	 * Populates a NcboOntologyLoadQueue Entity from this ontologyBean.
	 * OntologyVersion should have been populated from OntologyBean before
	 * making this call.
	 * 
	 * @param NcboOntologyLoadQueue,
	 *            NcboOntologyVersion
	 */
	public void populateToLoadQueueEntity(NcboOntologyLoadQueue loadQueue,
			NcboOntologyVersion ontologyVersion) {
		if (loadQueue != null) {
			Set<NcboOntologyLoadQueue> ncboOntologyLoadQueueSet = new HashSet<NcboOntologyLoadQueue>();

			// OntologyVersion object
			loadQueue.setNcboOntologyVersion(ontologyVersion);

			// Set NcboStatus
			NcboLStatus status = new NcboLStatus();
			populateDefaultStatus(status);
			loadQueue.setNcboLStatus(status);

			loadQueue.setDateCreated(Calendar.getInstance().getTime());

			ncboOntologyLoadQueueSet.add(loadQueue);
			ontologyVersion.setNcboOntologyLoadQueues(ncboOntologyLoadQueueSet);
		}
	}

	public String toString() {
		return "{Id: " + this.getId() + ", Ontology Id: "
				+ this.getOntologyId() + ", Remote: " + this.getIsRemote()
				+ ", Obo Foundry Id: " + this.getOboFoundryId()
				+ ", Internal Version Number: "
				+ this.getInternalVersionNumber() + ", User Id: "
				+ this.getUserId() + ", Version Number: "
				+ this.getVersionNumber() + ", Version Status: "
				+ this.getVersionStatus() + ", Display Label: "
				+ this.getDisplayLabel() + ", Description: "
				+ this.getDescription() + ", Abbreviation: "
				+ this.getAbbreviation() + ", Format: " + this.getFormat()
				+ ", Contact Name: " + this.getContactName()
				+ ", Contact Email: " + this.getContactEmail() + ", Foundry: "
				+ this.getIsFoundry() + " Coding Scheme: "
				+ this.getCodingScheme() + ", Target Terminologies: "
				+ this.getTargetTerminologies() + ", Synonym Slot: "
				+ this.getSynonymSlot() + ", Preferred Name Slot: "
				+ this.getPreferredNameSlot() + "}";
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

	/**
	 * Populate default status in the bean Status is "1"(waiting) for local
	 * upload, "5"(notapplicable) for remote.
	 */
	public void populateDefaultStatus(NcboLStatus status) {
		if (this.isRemote()) {
			status.setId(new Integer(MessageUtils
					.getMessage("ncbo.status.notapplicable")));
		} else {
			status.setId(new Integer(MessageUtils
					.getMessage("ncbo.status.waiting")));
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
