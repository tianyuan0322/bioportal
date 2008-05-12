package org.ncbo.stanford.domain.generated;

import java.util.Date;

/**
 * VNcboOntologyId entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class VNcboOntologyId extends AbstractVNcboOntologyId implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public VNcboOntologyId() {
	}

	/** minimal constructor */
	public VNcboOntologyId(Integer id, Integer ontologyId, Integer userId,
			Integer internalVersionNumber, String versionNumber,
			Byte isCurrent, Byte isRemote, Byte isReviewed, Date dateCreated,
			Date dateReleased, String displayLabel, String format,
			Byte isFoundry) {
		super(id, ontologyId, userId, internalVersionNumber, versionNumber,
				isCurrent, isRemote, isReviewed, dateCreated, dateReleased,
				displayLabel, format, isFoundry);
	}

	/** full constructor */
	public VNcboOntologyId(Integer id, Integer ontologyId, Integer parentId,
			Integer userId, Integer internalVersionNumber,
			String versionNumber, String versionStatus, String filePath,
			Byte isCurrent, Byte isRemote, Byte isReviewed, Date dateCreated,
			Date dateReleased, String displayLabel, String format,
			String contactName, String contactEmail, String homepage,
			String documentation, String publication, String urn,
			String codingScheme, Byte isFoundry) {
		super(id, ontologyId, parentId, userId, internalVersionNumber,
				versionNumber, versionStatus, filePath, isCurrent, isRemote,
				isReviewed, dateCreated, dateReleased, displayLabel, format,
				contactName, contactEmail, homepage, documentation,
				publication, urn, codingScheme, isFoundry);
	}

}
