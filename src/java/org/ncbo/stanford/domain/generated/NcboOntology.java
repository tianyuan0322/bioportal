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
	public NcboOntology(NcboUser ncboUser, Integer internalVersionNumber,
			String versionNumber, Byte isCurrent, Byte isRemote,
			Byte isReviewed, Date dateReleased, Date dateCreated) {
		super(ncboUser, internalVersionNumber, versionNumber, isCurrent,
				isRemote, isReviewed, dateReleased, dateCreated);
	}

	/** full constructor */
	public NcboOntology(NcboUser ncboUser, NcboOntology ncboOntology,
			Integer internalVersionNumber, String versionNumber,
			String versionStatus, String filePath, Byte isCurrent,
			Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated, Set ncboOntologyMetadatas, Set ncboOntologyFiles,
			Set ncboOntologyAdditionalMetadatas, Set ncboOntologies,
			Set ncboOntologyCategories) {
		super(ncboUser, ncboOntology, internalVersionNumber, versionNumber,
				versionStatus, filePath, isCurrent, isRemote, isReviewed,
				dateReleased, dateCreated, ncboOntologyMetadatas,
				ncboOntologyFiles, ncboOntologyAdditionalMetadatas,
				ncboOntologies, ncboOntologyCategories);
	}

}
