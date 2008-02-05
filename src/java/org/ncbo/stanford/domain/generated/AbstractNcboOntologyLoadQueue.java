package org.ncbo.stanford.domain.generated;

import java.util.Date;

/**
 * AbstractNcboOntologyLoadQueue entity provides the base persistence definition
 * of the NcboOntologyLoadQueue entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboOntologyLoadQueue implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private NcboOntologyVersion ncboOntologyVersion;
	private NcboLStatus ncboLStatus;
	private String errorMessage;
	private Date dateCreated;
	private Date dateProcessed;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyLoadQueue() {
	}

	/** minimal constructor */
	public AbstractNcboOntologyLoadQueue(
			NcboOntologyVersion ncboOntologyVersion, NcboLStatus ncboLStatus,
			Date dateCreated) {
		this.ncboOntologyVersion = ncboOntologyVersion;
		this.ncboLStatus = ncboLStatus;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public AbstractNcboOntologyLoadQueue(
			NcboOntologyVersion ncboOntologyVersion, NcboLStatus ncboLStatus,
			String errorMessage, Date dateCreated, Date dateProcessed) {
		this.ncboOntologyVersion = ncboOntologyVersion;
		this.ncboLStatus = ncboLStatus;
		this.errorMessage = errorMessage;
		this.dateCreated = dateCreated;
		this.dateProcessed = dateProcessed;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboOntologyVersion getNcboOntologyVersion() {
		return this.ncboOntologyVersion;
	}

	public void setNcboOntologyVersion(NcboOntologyVersion ncboOntologyVersion) {
		this.ncboOntologyVersion = ncboOntologyVersion;
	}

	public NcboLStatus getNcboLStatus() {
		return this.ncboLStatus;
	}

	public void setNcboLStatus(NcboLStatus ncboLStatus) {
		this.ncboLStatus = ncboLStatus;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateProcessed() {
		return this.dateProcessed;
	}

	public void setDateProcessed(Date dateProcessed) {
		this.dateProcessed = dateProcessed;
	}

}