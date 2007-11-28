package org.ncbo.stanford.domain.generated;

import java.util.HashSet;
import java.util.Set;

/**
 * AbstractNcboLAppTextDatatype entity provides the base persistence definition
 * of the NcboLAppTextDatatype entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboLAppTextDatatype implements
		java.io.Serializable {

	// Fields

	private String datatypeCode;
	private String datatype;
	private Set ncboAppTexts = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboLAppTextDatatype() {
	}

	/** minimal constructor */
	public AbstractNcboLAppTextDatatype(String datatypeCode, String datatype) {
		this.datatypeCode = datatypeCode;
		this.datatype = datatype;
	}

	/** full constructor */
	public AbstractNcboLAppTextDatatype(String datatypeCode, String datatype,
			Set ncboAppTexts) {
		this.datatypeCode = datatypeCode;
		this.datatype = datatype;
		this.ncboAppTexts = ncboAppTexts;
	}

	// Property accessors

	public String getDatatypeCode() {
		return this.datatypeCode;
	}

	public void setDatatypeCode(String datatypeCode) {
		this.datatypeCode = datatypeCode;
	}

	public String getDatatype() {
		return this.datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public Set getNcboAppTexts() {
		return this.ncboAppTexts;
	}

	public void setNcboAppTexts(Set ncboAppTexts) {
		this.ncboAppTexts = ncboAppTexts;
	}

}