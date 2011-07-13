package org.ncbo.stanford.sparql.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;

public class Mapping extends AbstractSPARQLBean {

	private static final long serialVersionUID = 5668752344409465584L;

	private static final String PREFIX = ApplicationConstants.MAPPING_PREFIX;
	private static final String ID_PREFIX = ApplicationConstants.MAPPING_ID_PREFIX;
	private static final String RDF_TYPE = PREFIX + "One_To_One_Mapping";

	/**
	 * This Map can be used to tie a parameter to a URI and variable name when generating triples.
	 */
	public static HashMap<String, ParameterMap> parameterMapping = new HashMap<String, ParameterMap>();
	static {
		Field[] fields = Mapping.class.getDeclaredFields();

		HashMap<String, Field> fieldMap = new HashMap<String, Field>();
		for (Field field : fields) {
			fieldMap.put(field.getName(), field);
		}

		ParameterMap submittedBy = new ParameterMap();
		submittedBy.URI = fieldMap.get("submittedBy").getAnnotation(IRI.class)
				.value();
		submittedBy.variableName = "submittedBy";
		parameterMapping.put("submittedBy", submittedBy);

		ParameterMap mappingType = new ParameterMap();
		mappingType.URI = fieldMap.get("mappingType").getAnnotation(IRI.class)
				.value();
		mappingType.variableName = "mappingType";
		parameterMapping.put("mappingType", mappingType);

		ParameterMap startDate = new ParameterMap();
		startDate.URI = fieldMap.get("date").getAnnotation(IRI.class)
				.value();
		startDate.variableName = "date";
		parameterMapping.put("startDate", startDate);

		ParameterMap endDate = new ParameterMap();
		endDate.URI = fieldMap.get("date").getAnnotation(IRI.class).value();
		endDate.variableName = "date";
		parameterMapping.put("endDate", endDate);

		ParameterMap relationshipTypes = new ParameterMap();
		relationshipTypes.URI = fieldMap.get("relation").getAnnotation(IRI.class)
				.value();
		relationshipTypes.variableName = "relation";
		parameterMapping.put("relationshipTypes", relationshipTypes);

		ParameterMap mappingSources = new ParameterMap();
		mappingSources.URI = fieldMap.get("mappingSource")
				.getAnnotation(IRI.class).value();
		mappingSources.variableName = "mappingSource";
		parameterMapping.put("mappingSources", mappingSources);
	};

	@IRI(PREFIX + "id")
	protected URI id;

	@IRI(PREFIX + "source")
	protected List<URI> source = new ArrayList<URI>();

	@IRI(PREFIX + "target")
	protected List<URI> target = new ArrayList<URI>();

	@IRI(PREFIX + "relation")
	protected URI relation;

	@IRI(PREFIX + "source_ontology_id")
	protected Integer sourceOntologyId;

	@IRI(PREFIX + "target_ontology_id")
	protected Integer targetOntologyId;

	@IRI(PREFIX + "created_in_source_ontology_version")
	protected Integer createdInSourceOntologyVersion;

	@IRI(PREFIX + "created_in_target_ontology_version")
	protected Integer createdInTargetOntologyVersion;

	@IRI(PREFIX + "date")
	protected Date date;

	@IRI(PREFIX + "submitted_by")
	protected Integer submittedBy;

	@IRI(PREFIX + "mapping_type")
	protected String mappingType;

	@IRI(PREFIX + "dependency")
	protected URI dependency;

	@IRI(PREFIX + "comment")
	protected String comment;

	@IRI(PREFIX + "mapping_source")
	protected String mappingSource;

	@IRI(PREFIX + "mapping_source_name")
	protected String mappingSourceName;

	@IRI(PREFIX + "mapping_source_contact_info")
	protected String mappingSourcecontactInfo;

	@IRI(PREFIX + "mapping_source_site")
	protected URI mappingSourceSite;

	@IRI(PREFIX + "mapping_source_algorithm")
	protected String mappingSourceAlgorithm;

	/**
	 * Default no-arg constructor.
	 */
	public Mapping() {
		super(ID_PREFIX, RDF_TYPE);
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
	 *
	 * @param source
	 *            the source to add
	 */
	public void addSource(URI source) {
		this.source.add(source);
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
	 *
	 * @param target
	 *            the target to add
	 */
	public void addTarget(URI target) {
		this.target.add(target);
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

	/**
	 * @param createdInTargetOntologyVersion
	 *            the createdInTargetOntologyVersion to set
	 */
	public void setCreatedInTargetOntologyVersion(
			Integer createdInTargetOntologyVersion) {
		this.createdInTargetOntologyVersion = createdInTargetOntologyVersion;
	}

    public Map<String, ParameterMap> getParameterMapping() {
        return Mapping.parameterMapping;
    }

}
