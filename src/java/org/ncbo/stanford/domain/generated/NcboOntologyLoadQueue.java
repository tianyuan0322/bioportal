package org.ncbo.stanford.domain.generated;

import java.util.Date;

/**
 * NcboOntologyLoadQueue entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboOntologyLoadQueue extends AbstractNcboOntologyLoadQueue
		implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboOntologyLoadQueue() {
	}

	/** minimal constructor */
	public NcboOntologyLoadQueue(NcboLStatus ncboLStatus,
			Integer ontologyVersionId, Date dateCreated) {
		super(ncboLStatus, ontologyVersionId, dateCreated);
	}

	/** full constructor */
	public NcboOntologyLoadQueue(NcboLStatus ncboLStatus,
			Integer ontologyVersionId, String errorMessage, Date dateCreated,
			Date dateProcessed) {
		super(ncboLStatus, ontologyVersionId, errorMessage, dateCreated,
				dateProcessed);
	}

}
