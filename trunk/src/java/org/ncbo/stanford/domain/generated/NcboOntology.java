package org.ncbo.stanford.domain.generated;

import java.util.Set;

/**
 * NcboOntology entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboOntology extends AbstractNcboOntology implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboOntology() {
	}

	/** minimal constructor */
	public NcboOntology(Byte isManual) {
		super(isManual);
	}

	/** full constructor */
	public NcboOntology(String oboFoundryId, Byte isManual,
			Set ncboOntologyVersions) {
		super(oboFoundryId, isManual, ncboOntologyVersions);
	}

}
