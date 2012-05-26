package org.ncbo.stanford.bean.mapping;

import java.util.Date;
import java.util.List;

import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.service.xml.converters.URIConverter;
import org.ncbo.stanford.service.xml.converters.URIListConverter;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("mapping")
public class MappingBean {

	@XStreamAlias("id")
	@XStreamConverter(URIConverter.class)
	private URI id;

	@XStreamAlias("source")
	@XStreamConverter(URIListConverter.class)
	private List<URI> source;

	@XStreamAlias("target")
	@XStreamConverter(URIListConverter.class)
	private List<URI> target;

	@XStreamAlias("relation")
	@XStreamConverter(URIConverter.class)
	private URI relation;

	@XStreamAlias("sourceOntologyId")
	private Integer sourceOntologyId;

	@XStreamAlias("targetOntologyId")
	private Integer targetOntologyId;

	@XStreamAlias("createdInSourceOntologyVersion")
	private Integer createdInSourceOntologyVersion;

	@XStreamAlias("createdInTargetOntologyVersion")
	private Integer createdInTargetOntologyVersion;

	@XStreamAlias("submittedBy")
	private Integer submittedBy;

	@XStreamAlias("date")
	private Date date;

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
	public MappingBean() {
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
	 * @return the source
	 */
	public List<URI> getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(List<URI> source) {
		this.source = source;
	}

	/**
	 * @return the target
	 */
	public List<URI> getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(List<URI> target) {
		this.target = target;
	}

	/**
	 * @return the relation
	 */
	public URI getRelation() {
		return relation;
	}

	/**
	 * @param relation
	 *            the relation to set
	 */
	public void setRelation(URI relation) {
		this.relation = relation;
	}

	/**
	 * @return the sourceOntologyId
	 */
	public Integer getSourceOntologyId() {
		return sourceOntologyId;
	}
	
	public URI getSourceOntologyURI() {
		return new URIImpl(ApplicationConstants.BIOPORTAL_ONTOLOGY_URI_PREFIX + this.sourceOntologyId);
	}

	/**
	 * @param sourceOntologyId
	 *            the sourceOntologyId to set
	 */
	public void setSourceOntologyId(Integer sourceOntologyId) {
		this.sourceOntologyId = sourceOntologyId;
	}

	/**
	 * @return the targetOntologyId
	 */
	public Integer getTargetOntologyId() {
		return targetOntologyId;
	}

	public URI getTargetOntologyURI() {
		return new URIImpl(ApplicationConstants.BIOPORTAL_ONTOLOGY_URI_PREFIX + this.targetOntologyId);
	}
	
	/**
	 * @param targetOntologyId
	 *            the targetOntologyId to set
	 */
	public void setTargetOntologyId(Integer targetOntologyId) {
		this.targetOntologyId = targetOntologyId;
	}

	/**
	 * @return the createdInSourceOntologyVersion
	 */
	public Integer getCreatedInSourceOntologyVersion() {
		return createdInSourceOntologyVersion;
	}
	
	public URI getCreatedInSourceOntologyVersionURI() {
		return new URIImpl(ApplicationConstants.BIOPORTAL_ONTOLOGY_URI_PREFIX + this.createdInSourceOntologyVersion);
	}
	/**
	 * @param createdInSourceOntologyVersion
	 *            the createdInSourceOntologyVersion to set
	 */
	public void setCreatedInSourceOntologyVersion(
			Integer createdInSourceOntologyVersion) {
		this.createdInSourceOntologyVersion = createdInSourceOntologyVersion;
	}

	/**
	 * @return the createdInTargetOntologyVersion
	 */
	public Integer getCreatedInTargetOntologyVersion() {
		return createdInTargetOntologyVersion;
	}

	public URI getCreatedInTargetOntologyVersionURI() {
		return new URIImpl(ApplicationConstants.BIOPORTAL_ONTOLOGY_URI_PREFIX + this.createdInTargetOntologyVersion);
	}
	
	/**
	 * @param createdInTargetOntologyVersion
	 *            the createdInTargetOntologyVersion to set
	 */
	public void setCreatedInTargetOntologyVersion(
			Integer createdInTargetOntologyVersion) {
		this.createdInTargetOntologyVersion = createdInTargetOntologyVersion;
	}

	/**
	 * @return the submittedBy
	 */
	public Integer getSubmittedBy() {
		return submittedBy;
	}

	/**
	 * @param submittedBy
	 *            the submittedBy to set
	 */
	public void setSubmittedBy(Integer submittedBy) {
		this.submittedBy = submittedBy;
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
	public void setMappingSourceContactInfo(String mappingSourceContactInfo) {
		this.mappingSourceContactInfo = mappingSourceContactInfo;
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

}
