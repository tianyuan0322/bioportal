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
	private NcboOntology ncboOntology;
	private String filename;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyFile() {
	}

	/** full constructor */
	public AbstractNcboOntologyFile(NcboOntology ncboOntology, String filename) {
		this.ncboOntology = ncboOntology;
		this.filename = filename;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboOntology getNcboOntology() {
		return this.ncboOntology;
	}

	public void setNcboOntology(NcboOntology ncboOntology) {
		this.ncboOntology = ncboOntology;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}