package org.ncbo.stanford.domain.generated;

import java.util.HashSet;
import java.util.Set;

/**
 * AbstractNcboLAdditionalMetadata entity provides the base persistence
 * definition of the NcboLAdditionalMetadata entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboLAdditionalMetadata implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private Set ncboOntologyAdditionalMetadatas = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboLAdditionalMetadata() {
	}

	/** minimal constructor */
	public AbstractNcboLAdditionalMetadata(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	/** full constructor */
	public AbstractNcboLAdditionalMetadata(Integer id, String name,
			Set ncboOntologyAdditionalMetadatas) {
		this.id = id;
		this.name = name;
		this.ncboOntologyAdditionalMetadatas = ncboOntologyAdditionalMetadatas;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set getNcboOntologyAdditionalMetadatas() {
		return this.ncboOntologyAdditionalMetadatas;
	}

	public void setNcboOntologyAdditionalMetadatas(
			Set ncboOntologyAdditionalMetadatas) {
		this.ncboOntologyAdditionalMetadatas = ncboOntologyAdditionalMetadatas;
	}

}