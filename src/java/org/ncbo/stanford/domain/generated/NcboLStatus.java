package org.ncbo.stanford.domain.generated;

import java.util.Set;

/**
 * NcboLStatus entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboLStatus extends AbstractNcboLStatus implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboLStatus() {
	}

	/** minimal constructor */
	public NcboLStatus(Integer id, String status) {
		super(id, status);
	}

	/** full constructor */
	public NcboLStatus(Integer id, String status, String description,
			Set ncboOntologyVersions, Set ncboOntologyLoadQueues) {
		super(id, status, description, ncboOntologyVersions,
				ncboOntologyLoadQueues);
	}

}
