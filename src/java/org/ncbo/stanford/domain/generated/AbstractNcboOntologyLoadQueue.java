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
	private NcboLStatus ncboLStatus;
	private Integer ontologyVersionId;
	private String errorMessage;
	private Date dateCreated;
	private Date dateProcessed;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyLoadQueue() {
	}

	/** minimal constructor */
	public AbstractNcboOntologyLoadQueue(NcboLStatus ncboLStatus,
			Integer ontologyVersionId, Date dateCreated) {
		this.ncboLStatus = ncboLStatus;
		this.ontologyVersionId = ontologyVersionId;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public AbstractNcboOntologyLoadQueue(NcboLStatus ncboLStatus,
			Integer ontologyVersionId, String errorMessage, Date dateCreated,
			Date dateProcessed) {
		this.ncboLStatus = ncboLStatus;
		this.ontologyVersionId = ontologyVersionId;
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

	public NcboLStatus getNcboLStatus() {
		return this.ncboLStatus;
	}

	public void setNcboLStatus(NcboLStatus ncboLStatus) {
		this.ncboLStatus = ncboLStatus;
	}

	public Integer getOntologyVersionId() {
		return this.ontologyVersionId;
	}

	public void setOntologyVersionId(Integer ontologyVersionId) {
		this.ontologyVersionId = ontologyVersionId;
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