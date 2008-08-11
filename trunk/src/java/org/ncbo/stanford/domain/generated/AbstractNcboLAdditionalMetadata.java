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
	private Set ncboOntologyAdditionalVersionMetadatas = new HashSet(0);

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
			Set ncboOntologyAdditionalVersionMetadatas) {
		this.id = id;
		this.name = name;
		this.ncboOntologyAdditionalVersionMetadatas = ncboOntologyAdditionalVersionMetadatas;
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

	public Set getNcboOntologyAdditionalVersionMetadatas() {
		return this.ncboOntologyAdditionalVersionMetadatas;
	}

	public void setNcboOntologyAdditionalVersionMetadatas(
			Set ncboOntologyAdditionalVersionMetadatas) {
		this.ncboOntologyAdditionalVersionMetadatas = ncboOntologyAdditionalVersionMetadatas;
	}

}