package org.ncbo.stanford.domain.generated;

import java.util.Set;

/**
 * NcboLAppTextDatatype entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboLAppTextDatatype extends AbstractNcboLAppTextDatatype
		implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboLAppTextDatatype() {
	}

	/** minimal constructor */
	public NcboLAppTextDatatype(String datatypeCode, String datatype) {
		super(datatypeCode, datatype);
	}

	/** full constructor */
	public NcboLAppTextDatatype(String datatypeCode, String datatype,
			Set ncboAppTexts) {
		super(datatypeCode, datatype, ncboAppTexts);
	}

}
