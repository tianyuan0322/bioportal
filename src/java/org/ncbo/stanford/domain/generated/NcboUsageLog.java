package org.ncbo.stanford.domain.generated;

import java.util.Date;

/**
 * NcboUsageLog entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboUsageLog extends AbstractNcboUsageLog implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboUsageLog() {
	}

	/** minimal constructor */
	public NcboUsageLog(Integer hashCode, Integer hitCount, Date dateAccessed) {
		super(hashCode, hitCount, dateAccessed);
	}

	/** full constructor */
	public NcboUsageLog(Integer hashCode, String applicationId,
			String requestUrl, String httpMethod, String resourceParameters,
			String requestParameters, Integer userId, Integer hitCount,
			Date dateAccessed) {
		super(hashCode, applicationId, requestUrl, httpMethod,
				resourceParameters, requestParameters, userId, hitCount,
				dateAccessed);
	}

}
