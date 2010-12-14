package org.ncbo.stanford.domain.custom.entity.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;

@IRI("http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#Mapping")
public abstract class Mapping implements Serializable {

	private static final long serialVersionUID = -237079713755255918L;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "id")
	protected URI id;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "date")
	protected Date date;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "submitted_by")
	protected Integer submittedBy;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_type")
	protected String mappingType;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "dependency")
	protected URI dependency;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "comment")
	protected String comment;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source")
	protected String mappingSource;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source_name")
	protected String mappingSourceName;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source_contact_info")
	protected String mappingSourcecontactInfo;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source_site")
	protected URI mappingSourceSite;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source_algorithm")
	protected String mappingSourceAlgorithm;

	/**
	 * Default no-arg constructor.
	 */
	public Mapping() {
		generateId();
	}

	/**
	 * Constructor with a provided id.
	 * 
	 * @param id
	 */
	public Mapping(URI id) {
		this.id = id;
	}

	/**
	 * Generate an id for this mapping.
	 */
	private void generateId() {
		this.id = new URIImpl(ApplicationConstants.MAPPING_ID_PREFIX
				+ UUID.randomUUID().toString());
	}

	/**
	 * Generate a list of Statements representing this object.
	 * 
	 * @return
	 */
	public abstract ArrayList<Statement> toStatements(ValueFactory vf);

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
	 * @return the mappingSourcecontactInfo
	 */
	public String getMappingSourcecontactInfo() {
		return mappingSourcecontactInfo;
	}

	/**
	 * @param mappingSourcecontactInfo
	 *            the mappingSourcecontactInfo to set
	 */
	public void setMappingSourcecontactInfo(String mappingSourcecontactInfo) {
		this.mappingSourcecontactInfo = mappingSourcecontactInfo;
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
