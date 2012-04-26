package org.ncbo.stanford.sparql.bean;

import java.util.Date;

import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;

public class ProcessInfo extends AbstractSPARQLBean {

	private static final long serialVersionUID = -4920943656148334898L;
	private static final String PREFIX = ApplicationConstants.MAPPING_PREFIX;
	private static final String ID_PREFIX = ApplicationConstants.MAPPING_ID_PREFIX;

	/**
	 * Default no-arg constructor.
	 */
	public ProcessInfo() {
		super(ID_PREFIX, null);
	}
	
	@IRI(PREFIX + "comment")
	protected String comment;

	@IRI(PREFIX + "mapping_source")
	protected String mappingSource;

	@IRI(PREFIX + "mapping_source_name")
	protected String mappingSourceName;

	@IRI(PREFIX + "mapping_source_contact_info")
	protected String mappingSourceContactInfo;

	@IRI(PREFIX + "mapping_source_site")
	protected URI mappingSourceSite;

	@IRI(PREFIX + "mapping_source_algorithm")
	protected String mappingSourceAlgorithm;

	@IRI(PREFIX + "date")
	protected Date date;

	@IRI(PREFIX + "submitted_by")
	protected Integer submittedBy;

	@IRI(PREFIX + "mapping_type")
	protected String mappingType;

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the submittedBy
	 */
	public Integer getSubmittedBy() {
		return submittedBy;
	}

	/**
	 * @param submittedBy2
	 *            the submittedBy to set
	 */
	public void setSubmittedBy(Integer submittedBy2) {
		this.submittedBy = submittedBy2;
	}

	/**
	 * @return the mappingSource
	 */
	public String getMappingSource() {
		return mappingSource;
	}

	/**
	 * @param mappingSource
	 *            the mappingSource to set
	 */
	public void setMappingSource(String mappingSource) {
		this.mappingSource = mappingSource;
	}

	/**
	 * @return the mappingSourceContactInfo
	 */
	public String getMappingSourcecontactInfo() {
		return mappingSourceContactInfo;
	}

	/**
	 * @param mappingSourceContactInfo
	 *            the mappingSourceContactInfo to set
	 */
	public void setMappingSourcecontactInfo(String mappingSourcecontactInfo) {
		this.mappingSourceContactInfo = mappingSourcecontactInfo;
	}

	/**
	 * @return the mappingSourceSite
	 */
	public URI getMappingSourceSite() {
		return mappingSourceSite;
	}

	/**
	 * @param mappingSourceSite
	 *            the mappingSourceSite to set
	 */
	public void setMappingSourceSite(URI mappingSourceSite) {
		this.mappingSourceSite = mappingSourceSite;
	}

	/**
	 * @return the mappingSourceAlgorithm
	 */
	public String getMappingSourceAlgorithm() {
		return mappingSourceAlgorithm;
	}

	/**
	 * @param mappingSourceAlgorithm
	 *            the mappingSourceAlgorithm to set
	 */
	public void setMappingSourceAlgorithm(String mappingSourceAlgorithm) {
		this.mappingSourceAlgorithm = mappingSourceAlgorithm;
	}

	/**
	 * @return the mappingType
	 */
	public String getMappingType() {
		return mappingType;
	}

	/**
	 * @param mappingType
	 *            the mappingType to set
	 */
	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}

	/**
	 * @return the mappingSourceName
	 */
	public String getMappingSourceName() {
		return mappingSourceName;
	}

	/**
	 * @param mappingSourceName
	 *            the mappingSourceName to set
	 */
	public void setMappingSourceName(String mappingSourceName) {
		this.mappingSourceName = mappingSourceName;
	}
}
