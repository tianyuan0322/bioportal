package org.ncbo.stanford.bean.logging;

import java.util.Date;

import org.ncbo.stanford.domain.generated.NcboUsageLog;
import org.ncbo.stanford.util.helper.StringHelper;

public class UsageLoggingBean {
	private String applicationId;
	private String requestUrl;
	private String httpMethod;
	private String resourceParameters;
	private String requestParameters;
	private Integer userId;
	private Integer hitCount;
	private Date dateAccessed;

	/**
	 * Populates a NcboUsageLog entity from this UsageLoggingBean.
	 * 
	 * @param usageLog -
	 *            the entity bean to populate
	 */
	public void populateToEntity(NcboUsageLog usageLog) {
		if (usageLog != null) {
			if (applicationId != null) {
				usageLog.setApplicationId(StringHelper
						.isNullOrNullString(applicationId) ? null
						: applicationId);
			}

			if (requestUrl != null) {
				usageLog.setRequestUrl(StringHelper
						.isNullOrNullString(requestUrl) ? null : requestUrl);
			}

			if (httpMethod != null) {
				usageLog.setHttpMethod(StringHelper
						.isNullOrNullString(httpMethod) ? null : httpMethod);
			}

			if (resourceParameters != null) {
				usageLog.setResourceParameters(StringHelper
						.isNullOrNullString(resourceParameters) ? null
						: resourceParameters);
			}

			if (requestParameters != null) {
				usageLog.setRequestParameters(StringHelper
						.isNullOrNullString(requestParameters) ? null
						: requestParameters);
			}

			if (userId != null) {
				usageLog.setUserId(userId);
			}
			
			usageLog.setHashCode(hashCode());
			usageLog.setHitCount(hitCount);
			usageLog.setDateAccessed(dateAccessed);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((applicationId == null) ? 0 : applicationId.hashCode());
		result = prime * result
				+ ((dateAccessed == null) ? 0 : dateAccessed.hashCode());
		result = prime * result
				+ ((httpMethod == null) ? 0 : httpMethod.hashCode());
		result = prime
				* result
				+ ((requestParameters == null) ? 0 : requestParameters
						.hashCode());
		result = prime * result
				+ ((requestUrl == null) ? 0 : requestUrl.hashCode());
		result = prime
				* result
				+ ((resourceParameters == null) ? 0 : resourceParameters
						.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final UsageLoggingBean other = (UsageLoggingBean) obj;
		if (applicationId == null) {
			if (other.applicationId != null)
				return false;
		} else if (!applicationId.equals(other.applicationId))
			return false;
		if (dateAccessed == null) {
			if (other.dateAccessed != null)
				return false;
		} else if (!dateAccessed.equals(other.dateAccessed))
			return false;
		if (httpMethod == null) {
			if (other.httpMethod != null)
				return false;
		} else if (!httpMethod.equals(other.httpMethod))
			return false;
		if (requestParameters == null) {
			if (other.requestParameters != null)
				return false;
		} else if (!requestParameters.equals(other.requestParameters))
			return false;
		if (requestUrl == null) {
			if (other.requestUrl != null)
				return false;
		} else if (!requestUrl.equals(other.requestUrl))
			return false;
		if (resourceParameters == null) {
			if (other.resourceParameters != null)
				return false;
		} else if (!resourceParameters.equals(other.resourceParameters))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	/**
	 * @return the applicationId
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * @param applicationId
	 *            the applicationId to set
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * @return the requestUrl
	 */
	public String getRequestUrl() {
		return requestUrl;
	}

	/**
	 * @param requestUrl
	 *            the requestUrl to set
	 */
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	/**
	 * @return the httpMethod
	 */
	public String getHttpMethod() {
		return httpMethod;
	}

	/**
	 * @param httpMethod
	 *            the httpMethod to set
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	/**
	 * @return the resourceParameters
	 */
	public String getResourceParameters() {
		return resourceParameters;
	}

	/**
	 * @param resourceParameters
	 *            the resourceParameters to set
	 */
	public void setResourceParameters(String resourceParameters) {
		this.resourceParameters = resourceParameters;
	}

	/**
	 * @return the requestParameters
	 */
	public String getRequestParameters() {
		return requestParameters;
	}

	/**
	 * @param requestParameters
	 *            the requestParameters to set
	 */
	public void setRequestParameters(String requestParameters) {
		this.requestParameters = requestParameters;
	}

	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * @return the dateAccessed
	 */
	public Date getDateAccessed() {
		return dateAccessed;
	}

	/**
	 * @param dateAccessed
	 *            the dateAccessed to set
	 */
	public void setDateAccessed(Date dateAccessed) {
		this.dateAccessed = dateAccessed;
	}

	/**
	 * @return the hitCount
	 */
	public Integer getHitCount() {
		return hitCount;
	}

	/**
	 * @param hitCount the hitCount to set
	 */
	public void setHitCount(Integer hitCount) {
		this.hitCount = hitCount;
	}
}
