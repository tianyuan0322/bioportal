package org.ncbo.stanford.domain.generated;

import java.util.Date;
import java.util.Set;

/**
 * NcboOntologyVersion entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboOntologyVersion extends AbstractNcboOntologyVersion implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboOntologyVersion() {
	}

	/** minimal constructor */
	public NcboOntologyVersion(NcboOntology ncboOntology, NcboUser ncboUser,
			NcboLStatus ncboLStatus, Integer internalVersionNumber,
			String versionNumber, Byte isRemote, Byte isReviewed,
			Date dateReleased, Date dateCreated) {
		super(ncboOntology, ncboUser, ncboLStatus, internalVersionNumber,
				versionNumber, isRemote, isReviewed, dateReleased, dateCreated);
	}

	/** full constructor */
	public NcboOntologyVersion(NcboOntology ncboOntology, NcboUser ncboUser,
			NcboLStatus ncboLStatus, Integer internalVersionNumber,
			String versionNumber, String versionStatus, String filePath,
			Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated, Set ncboOntologyVersionMetadatas,
			Set ncboOntologyFiles, Set ncboOntologyLoadQueues,
			Set ncboOntologyCategories,
			Set ncboOntologyAdditionalVersionMetadatas) {
		super(ncboOntology, ncboUser, ncboLStatus, internalVersionNumber,
				versionNumber, versionStatus, filePath, isRemote, isReviewed,
				dateReleased, dateCreated, ncboOntologyVersionMetadatas,
				ncboOntologyFiles, ncboOntologyLoadQueues,
				ncboOntologyCategories, ncboOntologyAdditionalVersionMetadatas);
	}

}
