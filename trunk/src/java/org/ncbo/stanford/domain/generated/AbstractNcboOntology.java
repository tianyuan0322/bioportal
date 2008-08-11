package org.ncbo.stanford.domain.generated;

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
	private String oboFoundryId;
	private Byte isManual;
	private Set ncboOntologyVersions = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboOntology() {
	}

	/** minimal constructor */
	public AbstractNcboOntology(Byte isManual) {
		this.isManual = isManual;
	}

	/** full constructor */
	public AbstractNcboOntology(String oboFoundryId, Byte isManual,
			Set ncboOntologyVersions) {
		this.oboFoundryId = oboFoundryId;
		this.isManual = isManual;
		this.ncboOntologyVersions = ncboOntologyVersions;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOboFoundryId() {
		return this.oboFoundryId;
	}

	public void setOboFoundryId(String oboFoundryId) {
		this.oboFoundryId = oboFoundryId;
	}

	public Byte getIsManual() {
		return this.isManual;
	}

	public void setIsManual(Byte isManual) {
		this.isManual = isManual;
	}

	public Set getNcboOntologyVersions() {
		return this.ncboOntologyVersions;
	}

	public void setNcboOntologyVersions(Set ncboOntologyVersions) {
		this.ncboOntologyVersions = ncboOntologyVersions;
	}

}