package org.ncbo.stanford.sparql.bean;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;

public class ProcessInfo extends AbstractSPARQLBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4920943656148334898L;
	private static final String PREFIX = ApplicationConstants.MAPPING_PREFIX;
	private static final String ID_PREFIX = ApplicationConstants.MAPPING_ID_PREFIX;

	
	/**
	 * This Map can be used to tie a parameter to a URI and variable name when generating triples.
	 */
	public static HashMap<String, ParameterMap> parameterMapping = new HashMap<String, ParameterMap>();
	static {
		Field[] fields = ProcessInfo.class.getDeclaredFields();

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

		ParameterMap mappingSources = new ParameterMap();
		mappingSources.URI = fieldMap.get("mappingSource")
				.getAnnotation(IRI.class).value();
		mappingSources.variableName = "mappingSource";
		parameterMapping.put("mappingSources", mappingSources);
	};
	
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
	protected String mappingSourcecontactInfo;

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

	@Override
	public Map<String, ParameterMap> getParameterMapping() {
		// TODO Auto-generated method stub
		return parameterMapping;
	}
}
