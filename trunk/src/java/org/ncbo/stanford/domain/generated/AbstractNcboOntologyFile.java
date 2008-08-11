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
	private NcboOntologyVersion ncboOntologyVersion;
	private String filename;

	// Constructors

	/** default constructor */
	public AbstractNcboOntologyFile() {
	}

	/** full constructor */
	public AbstractNcboOntologyFile(NcboOntologyVersion ncboOntologyVersion,
			String filename) {
		this.ncboOntologyVersion = ncboOntologyVersion;
		this.filename = filename;
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

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}