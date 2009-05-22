package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboOntologyFile entity provides the base persistence definition of
 * the NcboOntologyFile entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboOntologyFile implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer ontologyVersionId;
	private String filename;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyFile() {
	}

	/** full constructor */
	public AbstractNcboOntologyFile(Integer ontologyVersionId, String filename) {
		this.ontologyVersionId = ontologyVersionId;
		this.filename = filename;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOntologyVersionId() {
		return this.ontologyVersionId;
	}

	public void setOntologyVersionId(Integer ontologyVersionId) {
		this.ontologyVersionId = ontologyVersionId;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}