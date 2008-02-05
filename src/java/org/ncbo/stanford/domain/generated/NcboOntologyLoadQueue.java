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
	public NcboOntologyLoadQueue(NcboOntologyVersion ncboOntologyVersion,
			NcboLStatus ncboLStatus, Date dateCreated) {
		super(ncboOntologyVersion, ncboLStatus, dateCreated);
	}

	/** full constructor */
	public NcboOntologyLoadQueue(NcboOntologyVersion ncboOntologyVersion,
			NcboLStatus ncboLStatus, String errorMessage, Date dateCreated,
			Date dateProcessed) {
		super(ncboOntologyVersion, ncboLStatus, errorMessage, dateCreated,
				dateProcessed);
	}

}
