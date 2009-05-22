package org.ncbo.stanford.domain.generated;

import java.util.Date;

/**
 * AbstractNcboAppText entity provides the base persistence definition of the
 * NcboAppText entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboAppText implements java.io.Serializable {

	// Fields

	private String identifier;
	private NcboLAppTextDatatype ncboLAppTextDatatype;
	private String description;
	private String textContent;
	private String lastModifier;
	private Date dateCreated;
	private Date dateUpdated;

	// Constructors

	/** default constructor */
	public AbstractNcboAppText() {
	}

	/** minimal constructor */
	public AbstractNcboAppText(NcboLAppTextDatatype ncboLAppTextDatatype,
			Date dateCreated, Date dateUpdated) {
		this.ncboLAppTextDatatype = ncboLAppTextDatatype;
		this.dateCreated = dateCreated;
		this.dateUpdated = dateUpdated;
	}

	/** full constructor */
	public AbstractNcboAppText(NcboLAppTextDatatype ncboLAppTextDatatype,
			String description, String textContent, String lastModifier,
			Date dateCreated, Date dateUpdated) {
		this.ncboLAppTextDatatype = ncboLAppTextDatatype;
		this.description = description;
		this.textContent = textContent;
		this.lastModifier = lastModifier;
		this.dateCreated = dateCreated;
		this.dateUpdated = dateUpdated;
	}

	// Property accessors

	public String getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public NcboLAppTextDatatype getNcboLAppTextDatatype() {
		return this.ncboLAppTextDatatype;
	}

	public void setNcboLAppTextDatatype(
			NcboLAppTextDatatype ncboLAppTextDatatype) {
		this.ncboLAppTextDatatype = ncboLAppTextDatatype;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTextContent() {
		return this.textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public String getLastModifier() {
		return this.lastModifier;
	}

	public void setLastModifier(String lastModifier) {
		this.lastModifier = lastModifier;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateUpdated() {
		return this.dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

}