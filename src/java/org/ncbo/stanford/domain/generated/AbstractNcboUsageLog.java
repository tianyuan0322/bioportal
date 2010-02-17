package org.ncbo.stanford.domain.generated;

import java.sql.Timestamp;

/**
 * AbstractNcboUsageLog entity provides the base persistence definition of the
 * NcboUsageLog entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboUsageLog implements java.io.Serializable {

	// Fields

	private Integer id;
	private NcboLUsageEventType ncboLUsageEventType;
	private String applicationId;
	private String requestUrl;
	private String httpMethod;
	private Integer userId;
	private String sessionId;
	private String ipAddress;
	private Integer ontologyVersionId;
	private Integer ontologyId;
	private String ontologyName;
	private String conceptId;
	private String conceptName;
	private String searchQuery;
	private String searchParameters;
	private Integer numSearchResults;
	private Timestamp dateAccessed;

	// Constructors

	/** default constructor */
	public AbstractNcboUsageLog() {
	}

	/** minimal constructor */
	public AbstractNcboUsageLog(NcboLUsageEventType ncboLUsageEventType,
			Timestamp dateAccessed) {
		this.ncboLUsageEventType = ncboLUsageEventType;
		this.dateAccessed = dateAccessed;
	}

	/** full constructor */
	public AbstractNcboUsageLog(NcboLUsageEventType ncboLUsageEventType,
			String applicationId, String requestUrl, String httpMethod,
			Integer userId, String sessionId, String ipAddress,
			Integer ontologyVersionId, Integer ontologyId, String ontologyName,
			String conceptId, String conceptName, String searchQuery,
			String searchParameters, Integer numSearchResults,
			Timestamp dateAccessed) {
		this.ncboLUsageEventType = ncboLUsageEventType;
		this.applicationId = applicationId;
		this.requestUrl = requestUrl;
		this.httpMethod = httpMethod;
		this.userId = userId;
		this.sessionId = sessionId;
		this.ipAddress = ipAddress;
		this.ontologyVersionId = ontologyVersionId;
		this.ontologyId = ontologyId;
		this.ontologyName = ontologyName;
		this.conceptId = conceptId;
		this.conceptName = conceptName;
		this.searchQuery = searchQuery;
		this.searchParameters = searchParameters;
		this.numSearchResults = numSearchResults;
		this.dateAccessed = dateAccessed;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboLUsageEventType getNcboLUsageEventType() {
		return this.ncboLUsageEventType;
	}

	public void setNcboLUsageEventType(NcboLUsageEventType ncboLUsageEventType) {
		this.ncboLUsageEventType = ncboLUsageEventType;
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

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Integer getOntologyVersionId() {
		return this.ontologyVersionId;
	}

	public void setOntologyVersionId(Integer ontologyVersionId) {
		this.ontologyVersionId = ontologyVersionId;
	}

	public Integer getOntologyId() {
		return this.ontologyId;
	}

	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	public String getOntologyName() {
		return this.ontologyName;
	}

	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}

	public String getConceptId() {
		return this.conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	public String getConceptName() {
		return this.conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	public String getSearchQuery() {
		return this.searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public String getSearchParameters() {
		return this.searchParameters;
	}

	public void setSearchParameters(String searchParameters) {
		this.searchParameters = searchParameters;
	}

	public Integer getNumSearchResults() {
		return this.numSearchResults;
	}

	public void setNumSearchResults(Integer numSearchResults) {
		this.numSearchResults = numSearchResults;
	}

	public Timestamp getDateAccessed() {
		return this.dateAccessed;
	}

	public void setDateAccessed(Timestamp dateAccessed) {
		this.dateAccessed = dateAccessed;
	}

}