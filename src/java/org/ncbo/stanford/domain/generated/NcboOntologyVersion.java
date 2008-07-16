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
	public NcboOntologyVersion(NcboUser ncboUser, NcboLStatus ncboLStatus,
			Integer ontologyId, Integer internalVersionNumber,
			String versionNumber, Byte isRemote, Byte isReviewed,
			Date dateReleased, Date dateCreated) {
		super(ncboUser, ncboLStatus, ontologyId, internalVersionNumber,
				versionNumber, isRemote, isReviewed, dateReleased, dateCreated);
	}

	/** full constructor */
	public NcboOntologyVersion(NcboUser ncboUser, NcboLStatus ncboLStatus,
			NcboOntologyVersion ncboOntologyVersion, Integer ontologyId,
			Integer internalVersionNumber, String versionNumber,
			String versionStatus, String filePath, Byte isRemote,
			Byte isReviewed, Date dateReleased, Date dateCreated,
			Set ncboOntologyMetadatas, Set ncboOntologyFiles,
			Set ncboOntologyLoadQueues, Set ncboOntologyCategories,
			Set ncboOntologyAdditionalMetadatas, Set ncboOntologyVersions) {
		super(ncboUser, ncboLStatus, ncboOntologyVersion, ontologyId,
				internalVersionNumber, versionNumber, versionStatus, filePath,
				isRemote, isReviewed, dateReleased, dateCreated,
				ncboOntologyMetadatas, ncboOntologyFiles,
				ncboOntologyLoadQueues, ncboOntologyCategories,
				ncboOntologyAdditionalMetadatas, ncboOntologyVersions);
	}

}
