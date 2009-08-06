package org.ncbo.stanford.domain.generated;

import java.util.Date;

/**
 * AbstractNcboUsageLog entity provides the base persistence definition of the
 * NcboUsageLog entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboUsageLog implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer hashCode;
	private String applicationId;
	private String requestUrl;
	private String httpMethod;
	private String resourceParameters;
	private String requestParameters;
	private Integer userId;
	private Integer hitCount;
	private Date dateAccessed;

	// Constructors

	/** default constructor */
	public AbstractNcboUsageLog() {
	}

	/** minimal constructor */
	public AbstractNcboUsageLog(Integer hashCode, Integer hitCount,
			Date dateAccessed) {
		this.hashCode = hashCode;
		this.hitCount = hitCount;
		this.dateAccessed = dateAccessed;
	}

	/** full constructor */
	public AbstractNcboUsageLog(Integer hashCode, String applicationId,
			String requestUrl, String httpMethod, String resourceParameters,
			String requestParameters, Integer userId, Integer hitCount,
			Date dateAccessed) {
		this.hashCode = hashCode;
		this.applicationId = applicationId;
		this.requestUrl = requestUrl;
		this.httpMethod = httpMethod;
		this.resourceParameters = resourceParameters;
		this.requestParameters = requestParameters;
		this.userId = userId;
		this.hitCount = hitCount;
		this.dateAccessed = dateAccessed;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getHashCode() {
		return this.hashCode;
	}

	public void setHashCode(Integer hashCode) {
		this.hashCode = hashCode;
	}

	public String getApplicationId() {
		return this.applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getRequestUrl() {
		return this.requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getHttpMethod() {
		return this.httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getResourceParameters() {
		return this.resourceParameters;
	}

	public void setResourceParameters(String resourceParameters) {
		this.resourceParameters = resourceParameters;
	}

	public String getRequestParameters() {
		return this.requestParameters;
	}

	public void setRequestParameters(String requestParameters) {
		this.requestParameters = requestParameters;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getHitCount() {
		return this.hitCount;
	}

	public void setHitCount(Integer hitCount) {
		this.hitCount = hitCount;
	}

	public Date getDateAccessed() {
		return this.dateAccessed;
	}

	public void setDateAccessed(Date dateAccessed) {
		this.dateAccessed = dateAccessed;
	}

}