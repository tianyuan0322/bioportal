package org.ncbo.stanford.bean.mapping;

import java.util.Date;

import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.service.xml.converters.URIConverter;
import org.openrdf.model.URI;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

public abstract class AbstractMappingBean {

	@XStreamAlias("id")
	@XStreamConverter(URIConverter.class)
	private URI id;
	
	@XStreamAlias("date")
	private Date date;
	
	@XStreamAlias("submittedBy")
	private Integer submittedBy;
	
	@XStreamAlias("dependency")
	@XStreamConverter(URIConverter.class)
	private URI dependency;
	
	@XStreamAlias("comment")
	private String comment;
	
	@XStreamAlias("mappingType")
	private String mappingType;

	@XStreamAlias("mappingSource")
	private MappingSourceEnum mappingSource;

	@XStreamAlias("mappingSourceName")
	private String mappingSourceName;

	@XStreamAlias("mappingSourceContactInfo")
	private String mappingSourceContactInfo;

	@XStreamAlias("mappingSourceSite")
	@XStreamConverter(URIConverter.class)
	private URI mappingSourceSite;

	@XStreamAlias("mappingSourceAlgorithm")
	private String mappingSourceAlgorithm;

	/**
	 * Default no-arg constructor.
	 */
	public AbstractMappingBean() {
	}

	/**
	 * @return the id
	 */
	public URI getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(URI id) {
		this.id = id;
	}

	/**
	 * @return the dependency
	 */
	public URI getDependency() {
		return dependency;
	}

	/**
	 * @param dependency
	 *            the dependency to set
	 */
	public void setDependency(URI dependency) {
		this.dependency = dependency;
	}

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
	public MappingSourceEnum getMappingSource() {
		return mappingSource;
	}

	/**
	 * @param mappingSource
	 *            the mappingSource to set
	 */
	public void setMappingSource(MappingSourceEnum mappingSource) {
		this.mappingSource = mappingSource;
	}

	/**
	 * @return the mappingSourceContactInfo
	 */
	public String getMappingSourceContactInfo() {
		return mappingSourceContactInfo;
	}

	/**
	 * @param mappingSourceContactInfo
	 *            the mappingSourceContactInfo to set
	 */
	public void setMappingSourceContactInfo(String mappingSourcecontactInfo) {
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
