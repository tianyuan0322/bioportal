package org.ncbo.stanford.domain.generated;

import java.util.Set;

/**
 * NcboLAdditionalMetadata entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboLAdditionalMetadata extends AbstractNcboLAdditionalMetadata
		implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboLAdditionalMetadata() {
	}

	/** minimal constructor */
	public NcboLAdditionalMetadata(Integer id, String name) {
		super(id, name);
	}

	/** full constructor */
	public NcboLAdditionalMetadata(Integer id, String name,
			Set ncboOntologyAdditionalVersionMetadatas) {
		super(id, name, ncboOntologyAdditionalVersionMetadatas);
	}

}
