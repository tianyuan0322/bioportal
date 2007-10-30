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
	private String version;
	private String filePath;
	private String filename;
	private Byte isCurrent;
	private Byte isRemote;
	private Byte isReviewed;
	private Date dateReleased;
	private Date dateCreated;
	private Set ncboOntologyAdditionalMetadatas = new HashSet(0);
	private Set ncboOntologies = new HashSet(0);
	private Set ncboOntologyCategories = new HashSet(0);
	private Set ncboOntologyMetadatas = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboOntology() {
	}

	/** minimal constructor */
	public AbstractNcboOntology(NcboUser ncboUser, String version,
			Byte isCurrent, Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated) {
		this.ncboUser = ncboUser;
		this.version = version;
		this.isCurrent = isCurrent;
		this.isRemote = isRemote;
		this.isReviewed = isReviewed;
		this.dateReleased = dateReleased;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public AbstractNcboOntology(NcboUser ncboUser, NcboOntology ncboOntology,
			String version, String filePath, String filename, Byte isCurrent,
			Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated, Set ncboOntologyAdditionalMetadatas,
			Set ncboOntologies, Set ncboOntologyCategories,
			Set ncboOntologyMetadatas) {
		this.ncboUser = ncboUser;
		this.ncboOntology = ncboOntology;
		this.version = version;
		this.filePath = filePath;
		this.filename = filename;
		this.isCurrent = isCurrent;
		this.isRemote = isRemote;
		this.isReviewed = isReviewed;
		this.dateReleased = dateReleased;
		this.dateCreated = dateCreated;
		this.ncboOntologyAdditionalMetadatas = ncboOntologyAdditionalMetadatas;
		this.ncboOntologies = ncboOntologies;
		this.ncboOntologyCategories = ncboOntologyCategories;
		this.ncboOntologyMetadatas = ncboOntologyMetadatas;
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

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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

	public Set getNcboOntologyMetadatas() {
		return this.ncboOntologyMetadatas;
	}

	public void setNcboOntologyMetadatas(Set ncboOntologyMetadatas) {
		this.ncboOntologyMetadatas = ncboOntologyMetadatas;
	}

}