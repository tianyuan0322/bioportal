package org.ncbo.stanford.bean.logging;

import java.sql.Timestamp;
import java.util.Date;

import org.ncbo.stanford.domain.custom.dao.CustomNcboLUsageEventTypeDAO;
import org.ncbo.stanford.domain.generated.NcboLUsageEventType;
import org.ncbo.stanford.domain.generated.NcboUsageLog;
import org.ncbo.stanford.util.helper.StringHelper;

import com.ibm.icu.util.Calendar;

public class UsageLoggingBean {
	private String applicationId;
	private String eventType;
	private String requestUrl;
	private String httpMethod;
	private Integer userId;
	private String apiKey;
	private String ipAddress;
	private Integer ontologyVersionId;
	private Integer ontologyId;
	private String ontologyName;
	private String conceptId;
	private String conceptName;
	private String searchQuery;
	private String searchParameters;
	private Integer numSearchResults;
	private Date dateAccessed;
	private Date startDateAccessed;
	private Date endDateAccessed;
	private boolean isEmpty = true;

	/**
	 * 
	 */
	public UsageLoggingBean() {
		super();
	}

	/**
	 * @param applicationId
	 * @param eventType
	 * @param requestUrl
	 * @param httpMethod
	 * @param userId
	 * @param apiKey
	 * @param ipAddress
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyName
	 * @param conceptId
	 * @param conceptName
	 * @param searchQuery
	 * @param searchParameters
	 * @param numSearchResults
	 * @param dateAccessed
	 */
	public UsageLoggingBean(String applicationId, String eventType,
			String requestUrl, String httpMethod, String userId,
			String apiKey, String ipAddress, String ontologyVersionId,
			String ontologyId, String ontologyName, String conceptId,
			String conceptName, String searchQuery, String searchParameters,
			String numSearchResults, Date dateAccessed) {
		super();
		this.applicationId = (StringHelper.isNullOrNullString(applicationId)) ? null
				: applicationId;
		this.eventType = (StringHelper.isNullOrNullString(eventType)) ? null
				: eventType;
		this.requestUrl = (StringHelper.isNullOrNullString(requestUrl)) ? null
				: requestUrl;
		this.httpMethod = (StringHelper.isNullOrNullString(httpMethod)) ? null
				: httpMethod;

		try {
			this.userId = Integer.parseInt(userId);
		} catch (NumberFormatException nfe) {
			this.userId = null;
		}

		this.apiKey = (StringHelper.isNullOrNullString(apiKey)) ? null
				: apiKey;
		this.ipAddress = (StringHelper.isNullOrNullString(ipAddress)) ? null
				: ipAddress;

		try {
			this.ontologyVersionId = Integer.parseInt(ontologyVersionId);
		} catch (NumberFormatException nfe) {
			this.ontologyVersionId = null;
		}

		try {
			this.ontologyId = Integer.parseInt(ontologyId);
		} catch (NumberFormatException nfe) {
			this.ontologyId = null;
		}

		this.ontologyName = (StringHelper.isNullOrNullString(ontologyName)) ? null
				: ontologyName;
		this.conceptId = (StringHelper.isNullOrNullString(conceptId)) ? null
				: conceptId;
		this.conceptName = (StringHelper.isNullOrNullString(conceptName)) ? null
				: conceptName;
		this.searchQuery = (StringHelper.isNullOrNullString(searchQuery)) ? null
				: searchQuery;
		this.searchParameters = (StringHelper
				.isNullOrNullString(searchParameters)) ? null
				: searchParameters;

		try {
			this.numSearchResults = Integer.parseInt(numSearchResults);
		} catch (NumberFormatException nfe) {
			this.numSearchResults = null;
		}

		this.dateAccessed = dateAccessed;
		this.isEmpty = false;
	}

	/**
	 * @param applicationId
	 * @param eventType
	 * @param requestUrl
	 * @param httpMethod
	 * @param userId
	 * @param apiKey
	 * @param ipAddress
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyName
	 * @param conceptId
	 * @param conceptName
	 * @param searchQuery
	 * @param searchParameters
	 * @param numSearchResults
	 */
	public UsageLoggingBean(String applicationId, String eventType,
			String requestUrl, String httpMethod, String userId,
			String apiKey, String ipAddress, String ontologyVersionId,
			String ontologyId, String ontologyName, String conceptId,
			String conceptName, String searchQuery, String searchParameters,
			String numSearchResults) {
		this(applicationId, eventType, requestUrl, httpMethod, userId,
				apiKey, ipAddress, ontologyVersionId, ontologyId,
				ontologyName, conceptId, conceptName, searchQuery,
				searchParameters, numSearchResults, Calendar.getInstance()
						.getTime());
	}

	/**
	 * Populates a NcboUsageLog entity from this UsageLoggingBean.
	 * 
	 * @param usageLog
	 *            - the entity bean to populate
	 */
	public void populateToEntity(CustomNcboLUsageEventTypeDAO eventTypeDao,
			NcboUsageLog usageLog) {
		if (usageLog != null) {
			if (applicationId != null) {
				usageLog.setApplicationId(StringHelper
						.isNullOrNullString(applicationId) ? null
						: applicationId);
			}

			if (eventType != null) {
				NcboLUsageEventType dbEventType = eventTypeDao
						.findEventTypeByName(eventType);

				if (dbEventType != null) {
					usageLog.setNcboLUsageEventType(dbEventType);
				}
			}

			if (requestUrl != null) {
				usageLog.setRequestUrl(StringHelper
						.isNullOrNullString(requestUrl) ? null : requestUrl);
			}

			if (httpMethod != null) {
				usageLog.setHttpMethod(StringHelper
						.isNullOrNullString(httpMethod) ? null : httpMethod);
			}

			if (userId != null) {
				usageLog.setUserId(userId);
			}

			if (apiKey != null) {
				usageLog.setSessionId(StringHelper
						.isNullOrNullString(apiKey) ? null : apiKey);
			}

			if (ipAddress != null) {
				usageLog.setIpAddress(StringHelper
						.isNullOrNullString(ipAddress) ? null : ipAddress);
			}

			if (ontologyVersionId != null) {
				usageLog.setOntologyVersionId(ontologyVersionId);
			}

			if (ontologyId != null) {
				usageLog.setOntologyId(ontologyId);
			}

			if (ontologyName != null) {
				usageLog
						.setOntologyName(StringHelper
								.isNullOrNullString(ontologyName) ? null
								: ontologyName);
			}

			if (conceptId != null) {
				usageLog.setConceptId(StringHelper
						.isNullOrNullString(conceptId) ? null : conceptId);
			}

			if (conceptName != null) {
				usageLog.setConceptName(StringHelper
						.isNullOrNullString(conceptName) ? null : conceptName);
			}

			if (searchQuery != null) {
				usageLog.setSearchQuery(StringHelper
						.isNullOrNullString(searchQuery) ? null : searchQuery);
			}

			if (searchParameters != null) {
				usageLog.setSearchParameters(StringHelper
						.isNullOrNullString(searchParameters) ? null
						: searchParameters);
			}

			if (numSearchResults != null) {
				usageLog.setNumSearchResults(numSearchResults);
			}

			usageLog.setDateAccessed(new Timestamp(dateAccessed.getTime()));
		}
	}

	/**
	 * Populates this bean using the NcboUsageLog entity
	 * 
	 * @param usageLog
	 *            - the entity bean from which to populate
	 */
	public void populateFromEntity(NcboUsageLog usageLog) {
		if (usageLog != null) {
			setApplicationId(usageLog.getApplicationId());
			setEventType(usageLog.getNcboLUsageEventType().getEventName());
			setRequestUrl(usageLog.getRequestUrl());
			setHttpMethod(usageLog.getHttpMethod());
			setUserId(usageLog.getUserId());
			setApiKey(usageLog.getSessionId());
			setIpAddress(usageLog.getIpAddress());
			setOntologyVersionId(usageLog.getOntologyVersionId());
			setOntologyId(usageLog.getOntologyId());
			setOntologyName(usageLog.getOntologyName());
			setConceptId(usageLog.getConceptId());
			setConceptName(usageLog.getConceptName());
			setSearchQuery(usageLog.getSearchQuery());
			setSearchParameters(usageLog.getSearchParameters());
			setNumSearchResults(usageLog.getNumSearchResults());
			setDateAccessed(usageLog.getDateAccessed());
			this.isEmpty = false;
		}
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
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
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
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @param apiKey
	 *            the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the ontologyVersionId
	 */
	public Integer getOntologyVersionId() {
		return ontologyVersionId;
	}

	/**
	 * @param ontologyVersionId
	 *            the ontologyVersionId to set
	 */
	public void setOntologyVersionId(Integer ontologyVersionId) {
		this.ontologyVersionId = ontologyVersionId;
	}

	/**
	 * @return the ontologyId
	 */
	public Integer getOntologyId() {
		return ontologyId;
	}

	/**
	 * @param ontologyId
	 *            the ontologyId to set
	 */
	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	/**
	 * @return the ontologyName
	 */
	public String getOntologyName() {
		return ontologyName;
	}

	/**
	 * @param ontologyName
	 *            the ontologyName to set
	 */
	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}

	/**
	 * @return the conceptId
	 */
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * @param conceptId
	 *            the conceptId to set
	 */
	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * @return the conceptName
	 */
	public String getConceptName() {
		return conceptName;
	}

	/**
	 * @param conceptName
	 *            the conceptName to set
	 */
	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	/**
	 * @return the searchQuery
	 */
	public String getSearchQuery() {
		return searchQuery;
	}

	/**
	 * @param searchQuery
	 *            the searchQuery to set
	 */
	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	/**
	 * @return the searchParameters
	 */
	public String getSearchParameters() {
		return searchParameters;
	}

	/**
	 * @param searchParameters
	 *            the searchParameters to set
	 */
	public void setSearchParameters(String searchParameters) {
		this.searchParameters = searchParameters;
	}

	/**
	 * @return the numSearchResults
	 */
	public Integer getNumSearchResults() {
		return numSearchResults;
	}

	/**
	 * @param numSearchResults
	 *            the numSearchResults to set
	 */
	public void setNumSearchResults(Integer numSearchResults) {
		this.numSearchResults = numSearchResults;
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
	 * @return the startDateAccessed
	 */
	public Date getStartDateAccessed() {
		return startDateAccessed;
	}

	/**
	 * @param startDateAccessed
	 *            the startDateAccessed to set
	 */
	public void setStartDateAccessed(Date startDateAccessed) {
		this.startDateAccessed = startDateAccessed;
	}

	/**
	 * @return the endDateAccessed
	 */
	public Date getEndDateAccessed() {
		return endDateAccessed;
	}

	/**
	 * @param endDateAccessed
	 *            the endDateAccessed to set
	 */
	public void setEndDateAccessed(Date endDateAccessed) {
		this.endDateAccessed = endDateAccessed;
	}

	/**
	 * @return the isEmpty
	 */
	public boolean isEmpty() {
		return isEmpty;
	}
}
