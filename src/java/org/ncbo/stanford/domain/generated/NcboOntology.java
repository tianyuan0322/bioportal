package org.ncbo.stanford.domain.generated;

import java.util.Date;
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
	public NcboOntology(NcboUser ncboUser, String version, Byte isCurrent,
			Byte isRemote, Byte isReviewed, Date dateReleased, Date dateCreated) {
		super(ncboUser, version, isCurrent, isRemote, isReviewed, dateReleased,
				dateCreated);
	}

	/** full constructor */
	public NcboOntology(NcboUser ncboUser, NcboOntology ncboOntology,
			String version, String filePath, String filename, Byte isCurrent,
			Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated, Set ncboOntologyAdditionalMetadatas,
			Set ncboOntologies, Set ncboOntologyCategories,
			Set ncboOntologyMetadatas) {
		super(ncboUser, ncboOntology, version, filePath, filename, isCurrent,
				isRemote, isReviewed, dateReleased, dateCreated,
				ncboOntologyAdditionalMetadatas, ncboOntologies,
				ncboOntologyCategories, ncboOntologyMetadatas);
	}

}
