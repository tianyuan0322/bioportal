package org.ncbo.stanford.domain.generated;

import java.sql.Timestamp;

/**
 * NcboUsageLog entity. @author MyEclipse Persistence Tools
 */
public class NcboUsageLog extends AbstractNcboUsageLog implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboUsageLog() {
	}

	/** minimal constructor */
	public NcboUsageLog(NcboLUsageEventType ncboLUsageEventType,
			Timestamp dateAccessed) {
		super(ncboLUsageEventType, dateAccessed);
	}

	/** full constructor */
	public NcboUsageLog(NcboLUsageEventType ncboLUsageEventType,
			String applicationId, String requestUrl, String httpMethod,
			Integer userId, String sessionId, String ipAddress,
			Integer ontologyVersionId, Integer ontologyId, String ontologyName,
			String conceptId, String conceptName, String searchQuery,
			String searchParameters, Integer numSearchResults,
			Timestamp dateAccessed) {
		super(ncboLUsageEventType, applicationId, requestUrl, httpMethod,
				userId, sessionId, ipAddress, ontologyVersionId, ontologyId,
				ontologyName, conceptId, conceptName, searchQuery,
				searchParameters, numSearchResults, dateAccessed);
	}

}
