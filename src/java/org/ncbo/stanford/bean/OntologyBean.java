package org.ncbo.stanford.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboLCategory;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntologyCategory;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.domain.generated.NcboUser;

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
		
		if (ncboOntology != null) {

			this.setId(ncboOntology.getId());
			this.setOntologyId(ncboOntology.getOntologyId());
			this.setParentId(ncboOntology.getParentId());
			this.setUserId(ncboOntology.getUserId());
			this.setInternalVersionNumber(ncboOntology
					.getInternalVersionNumber());
			this.setVersionNumber(ncboOntology.getVersionNumber());
			this.setVersionStatus(ncboOntology.getVersionStatus());
			this.setFilePath(ncboOntology.getFilePath());
			this.setIsCurrent(ncboOntology.getIsCurrent());
			this.setIsRemote(ncboOntology.getIsRemote());
			this.setIsReviewed(ncboOntology.getIsReviewed());
			this.setStatusId(ncboOntology.getStatusId());
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
	
			addCategoryIds(ncboOntology.getCategoryIds());

		}
	}

	/**
	 * Populates a NcboOntologyMetadata Entity from this ontologyBean.
	 * OntologyVersion should have been populated from OntologyBean before
	 * making this call.
	 * 
	 * @param ontologyVersion
	 */

	public void populateToMetadataEntity(NcboOntologyMetadata metadata, NcboOntologyVersion ontologyVersion) {

        if (metadata != null) {

        	metadata.setNcboOntologyVersion(ontologyVersion);
        	
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
		
	}	
	

	/**
	 * Populates NcboOntologyVersion Entity from this OntologyBean
	 * 
	 * @param ncboOntology
	 */
	
	public void populateToVersionEntity(NcboOntologyVersion ontologyVersion) {

        if (ontologyVersion != null) {

			// all the business logic regarding OntologyVersionId and OntologyId
			// is in OntologyBean layer
			ontologyVersion.setId(this.getId());
			ontologyVersion.setOntologyId(this.getOntologyId());

			// Set Parent Object
			Integer parentId = this.getParentId();

			if (parentId != null) {
				NcboOntologyVersion parentOntology = new NcboOntologyVersion();
				parentOntology.setId(parentId);

				ontologyVersion.setNcboOntologyVersion(parentOntology);
			}

			// Set User Object (retrieve User Object from the session)
			ontologyVersion.setNcboUser(getNcboUserFromSession());

			ontologyVersion.setVersionNumber(this.getVersionNumber());
			ontologyVersion.setInternalVersionNumber(this
					.getInternalVersionNumber());
			ontologyVersion.setIsCurrent(this.getIsCurrent());
			ontologyVersion.setIsRemote(this.getIsRemote());
			ontologyVersion.setIsReviewed(this.getIsReviewed());
			ontologyVersion.setDateCreated(this.getDateCreated());
			ontologyVersion.setDateReleased(this.getDateReleased());

			// NcboStatus Object
			NcboLStatus status = new NcboLStatus();
			status.setId(this.getStatusId());
			ontologyVersion.setNcboLStatus(status);

			// Set filePath
			ontologyVersion.setFilePath(this.getFilePath());

			// Set dateCreated
			ontologyVersion.setDateCreated(Calendar.getInstance().getTime());

			// // Set FileNames
			// ontologyVersion.setNcboOntologyFiles( new
			// HashSet<String>(this.getFilenames()));
			//		
			// // Set Categories
			// ontologyVersion.setNcboOntologyCategories(new
			// HashSet<Integer>(this.getCategoryIds()));

		}
		
		// DEBUG STATETMENT - to be removed later
		System.out.println("******************************");
		System.out.println("HTTP REQUEST: OntologyVersion");
		System.out.println("HTTP REQUEST" + this.getId());
		System.out.println("versionNumber = " + this.getVersionNumber());
		System.out.println("filePath = " + this.getFilePath());
		System.out.println("contactName = " + this.getContactName());
		System.out.println("contactEmail = " + this.getContactEmail());
		System.out.println("******************************");
				
	}
	
	
	
	/**
	 * Populates the OntologyBean to a NcboOntologyFile Entity.
	 * OntologyVersion should have been populated from OntologyBean before making this call.
	 * 
	 * @param ncboOntology
	 */
	
	public void populateToFileEntity(List<NcboOntologyFile> ontologyFileList, NcboOntologyVersion ontologyVersion) {		
		
		List<String> fileNameList = this.getFilenames();
		
		for (String fileName : fileNameList) {
			
			NcboOntologyFile ontologyFile = new NcboOntologyFile();
			ontologyFile.setFilename(fileName);
			ontologyFile.setNcboOntologyVersion(ontologyVersion);
			
			ontologyFileList.add(ontologyFile);
		}	
	}
	
	
	/**
	 * Populates the OntologyBean to a NcboOntologyCategory Entity.
	 * OntologyVersion should have been populated from OntologyBean before making this call.
	 * 
	 * @param ncboOntology
	 */
	
	public void populateToCategoryEntity(List<NcboOntologyCategory> ontologyCategoryList, NcboOntologyVersion ontologyVersion) {
				
		List<Integer> categoryIdList = this.getCategoryIds();
		for (Integer categoryId : categoryIdList) {
			
			NcboOntologyCategory ontologyCategory = new NcboOntologyCategory();
			
			NcboLCategory ncboLCategory = new NcboLCategory();
			ncboLCategory.setId(categoryId);
			ontologyCategory.setNcboLCategory(ncboLCategory);
			ontologyCategory.setNcboOntologyVersion(ontologyVersion);
			
			ontologyCategoryList.add(ontologyCategory);
		}
		
	}
	
	
	/**
	 * Populates a NcboOntologyLoadQueue Entity from this ontologyBean.
	 * OntologyVersion should have been populated from OntologyBean before making this call.
	 * 
	 * @param NcboOntologyLoadQueue, NcboOntologyVersion
	 */

	public void populateToLoadQueueEntity(NcboOntologyLoadQueue loadQueue, NcboOntologyVersion ontologyVersion) {

        if (loadQueue != null) {

        	// OntologyVersion object
    		loadQueue.setNcboOntologyVersion(ontologyVersion);
        	        	
    		// NcboStatus Object
    		NcboLStatus status = new NcboLStatus();
    		status.setId(this.getStatusId());
    		loadQueue.setNcboLStatus(status);
    		
    		loadQueue.setDateCreated(Calendar.getInstance().getTime());
        }
		
	}
	
	public String toString() {
		return "Id: " + this.getId() + 
				" OntologyId: " + this.getOntologyId() +
				" InternalVersionNumer: " + this.getInternalVersionNumber() +
				" OntologyId: " + this.getOntologyId() + 
				" VersionNumber: " + this.getVersionNumber() + 
				" VersionStatus: " + this.getVersionStatus() + 
				" Name: " + this.getDisplayLabel() + 
				" ContactName: " + this.getContactName() +
				" ContactEmail: " + this.getContactEmail();

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
	
	public String getOntologyDirPath() {
		return "/" + this.getOntologyId() + "/"
				+ this.getInternalVersionNumber();
	}

	//TODO - this is temporary code until UserBean is avail from the Session
	public NcboUser getNcboUserFromSession() {

		NcboUser ncboUser = new NcboUser();
		// ncboUser.setId(this.getUserId());
		// -----------------------------------------------------------------------

		UserBean bean = new UserBean();
		bean.setId(Integer.parseInt("2850"));
		bean.setUsername("myusername");
		bean.setPassword("mypassword");
		bean.setEmail("myemail@stanford.edu");
		bean.setFirstname("myfirstname");
		bean.setLastname("mylastname");
		bean.setPhone("123-456-7890");
		bean.setDateCreated(new Date());
		
		bean.populateToEntity(ncboUser);
		// -----------------------------------------------------------------------
		
		return ncboUser;
	}
}
