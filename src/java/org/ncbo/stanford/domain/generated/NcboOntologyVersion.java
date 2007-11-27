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
	public NcboOntologyVersion(NcboUser ncboUser, Integer ontologyId,
			Integer internalVersionNumber, String versionNumber,
			Byte isCurrent, Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated) {
		super(ncboUser, ontologyId, internalVersionNumber, versionNumber,
				isCurrent, isRemote, isReviewed, dateReleased, dateCreated);
	}

	/** full constructor */
	public NcboOntologyVersion(NcboUser ncboUser,
			NcboOntologyVersion ncboOntologyVersion, Integer ontologyId,
			Integer internalVersionNumber, String versionNumber,
			String versionStatus, String filePath, Byte isCurrent,
			Byte isRemote, Byte isReviewed, Date dateReleased,
			Date dateCreated, Set ncboOntologyMetadatas, Set ncboOntologyFiles,
			Set ncboOntologyAdditionalMetadatas, Set ncboOntologyVersions,
			Set ncboOntologyCategories) {
		super(ncboUser, ncboOntologyVersion, ontologyId, internalVersionNumber,
				versionNumber, versionStatus, filePath, isCurrent, isRemote,
				isReviewed, dateReleased, dateCreated, ncboOntologyMetadatas,
				ncboOntologyFiles, ncboOntologyAdditionalMetadatas,
				ncboOntologyVersions, ncboOntologyCategories);
	}

}
