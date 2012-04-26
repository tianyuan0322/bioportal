package org.ncbo.stanford.util.sparql;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.annotation.SPARQLSubject;
import org.ncbo.stanford.annotation.SPARQLVariableName;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;

public class SPARQLFilterGenerator {

	/**
	 * DEFINING SPARQL FILTER FIELDS
	 * 
	 * Each field on a SPARQL parameter object is required to have the following annotation:
	 * IRI (Predicate)
	 * 
	 * Optionally:
	 * SPARQLSubject (Subject variable name)
	 * SPARQLVariableName (Value/Object variable name)
	 *
	 * Example:
	 * <code>
		@IRI(PREFIX + "mapping_source")
		@SPARQLSubject(SUBJECT)
		@SPARQLVariableName("mappingSource")
	 * </code>
	 * protected String mappingSource;
	 */
	
	// TODO: Split these parameters out into subclasses
	
	/*
	 * Mapping Parameters
	 */
	
	@IRI(ApplicationConstants.MAPPING_PREFIX + "submitted_by")
	@SPARQLSubject("procInfo")
	@SPARQLVariableName("submittedBy")
	private List<Integer> submittedBy;
	
	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_type")
	@SPARQLSubject("mappingId")
	@SPARQLVariableName("mappingType")
	private String mappingType;
	
	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source")
	@SPARQLSubject("procInfo")
	@SPARQLVariableName("mappingSource")
	private List<MappingSourceEnum> mappingSources;
	
	@IRI(ApplicationConstants.MAPPING_PREFIX + "relation")
	@SPARQLSubject("mappingId")
	@SPARQLVariableName("relation")
	private List<URI> relationshipTypes;
	
	@IRI(ApplicationConstants.MAPPING_PREFIX + "date")
	@SPARQLSubject("procInfo")
	@SPARQLVariableName("date")
	private Date startDate;
	
	@IRI(ApplicationConstants.MAPPING_PREFIX + "date")
	@SPARQLSubject("procInfo")
	@SPARQLVariableName("date")
	private Date endDate;
	
	/*
	 * Provisional Term Parameters
	 */
	
	@IRI(ApplicationConstants.PROVISIONAL_TERM_PREFIX + "created")
	@SPARQLSubject("id")
	@SPARQLVariableName("created")
	private Date createdStartDate;
	
	@IRI(ApplicationConstants.PROVISIONAL_TERM_PREFIX + "created")
	@SPARQLSubject("id")
	@SPARQLVariableName("created")
	private Date createdEndDate;
	
	@IRI(ApplicationConstants.PROVISIONAL_TERM_PREFIX + "updated")
	@SPARQLSubject("id")
	@SPARQLVariableName("updated")
	private Date updatedStartDate;
	
	@IRI(ApplicationConstants.PROVISIONAL_TERM_PREFIX + "updated")
	@SPARQLSubject("id")
	@SPARQLVariableName("updated")
	private Date updatedEndDate;
	
	@IRI(ApplicationConstants.PROVISIONAL_TERM_PREFIX + "ontology_id")
	@SPARQLSubject("id")
	@SPARQLVariableName("ontologyId")
	private List<Integer> ontologyIds;
	
	@IRI(ApplicationConstants.MAPPING_PREFIX + "permanent_id")
	@SPARQLSubject("id")
	@SPARQLVariableName("permanentId")
	private Boolean permanentIdExists;

	/**
	 * Creates proper syntax for use in a SPARQL filter using provided
	 * parameters.
	 *
	 * @return
	 */
	public String toFilter() {
		String filter = "";

		if (submittedBy != null) {
			filter += (filter.length() > 0) ? " && " : "";
			for (Integer submitter : submittedBy) {
				filter += "?submittedBy = " + submitter;
				filter += (submittedBy.indexOf(submitter) == submittedBy.size() - 1) ? ""
						: " || ";
			}
		}

		if (ontologyIds != null) {
			filter += (filter.length() > 0) ? " && " : "";
			filter += "?ontologyId IN (" + StringUtils.join(ontologyIds, ", ")
					+ ")";
		}

		if (mappingType != null) {
			filter += (filter.length() > 0) ? " && " : "";
			filter += "?mappingType = \"" + mappingType + "\"";
		}

		// Convert created/updated to regular date
		String dateVariableName = null;
		if (createdStartDate != null || createdEndDate != null) {
			startDate = createdStartDate;
			endDate = createdEndDate;
			dateVariableName = "?created";
		}

		if (updatedStartDate != null || updatedEndDate != null) {
			startDate = updatedStartDate;
			endDate = updatedEndDate;
			dateVariableName = "?updated";
		}

		if (startDate != null) {
			dateVariableName = (dateVariableName == null) ? "?date"
					: dateVariableName;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String startDateStr = sdf.format(startDate);

			String endDateStr;
			if (endDate != null) {
				endDateStr = sdf.format(endDate);
			} else {
				endDateStr = sdf.format(new Date());
			}

			filter += (filter.length() > 0) ? " && " : "";
			filter += "xsd:dateTime (" + dateVariableName
					+ ") >= xsd:dateTime (\"" + startDateStr
					+ ApplicationConstants.TIMEZONE_ID
					+ "\") && xsd:dateTime (" + dateVariableName + ")"
					+ " <= xsd:dateTime (\"" + endDateStr
					+ ApplicationConstants.TIMEZONE_ID + "\")";
		}

		if (relationshipTypes != null) {
			filter += (filter.length() > 0) ? " && " : "";
			for (URI relationshipType : relationshipTypes) {
				filter += "?relation = <" + relationshipType + ">";
				filter += (relationshipTypes.indexOf(relationshipType) == relationshipTypes
						.size() - 1) ? "" : " || ";
			}
		}

		if (mappingSources != null) {
			filter += (filter.length() > 0) ? " && " : "";
			for (MappingSourceEnum source : mappingSources) {
				filter += "?mappingSource = \"" + source.toString() + "\"";
				filter += (mappingSources.indexOf(source) == mappingSources
						.size() - 1) ? "" : " || ";
			}
		}

		if (permanentIdExists != null && permanentIdExists == true) {
			filter += (filter.length() > 0) ? " && " : "";
			filter += "bound(?permanentId)";
		}

		return filter;
	}

	public Boolean isEmpty() {
		Field[] fields = this.getClass().getDeclaredFields();

		for (Field field : fields) {
			try {
				if (field.get(this) != null
						&& field.get(this).toString().length() > 0) {
					return false;
				}
			} catch (IllegalArgumentException e) {
				// Do nothing
			} catch (IllegalAccessException e) {
				// Do nothing
			}
		}

		return true;
	}

	/**
	 * This method generates triple patterns in the SPARQL syntax. It is used to
	 * generate triples for parameters that have been provided for a particular
	 * call.
	 *
	 * @param SPARQLBeanClass
	 * @return
	 */
	public List<String> generateTriplePatterns() {
		Field[] fields = this.getClass().getDeclaredFields();

		List<String> triples = new ArrayList<String>();

		for (Field field : fields) {
			try {
				if (field.get(this) != null
						&& field.get(this).toString().length() > 0) {

					String type = field.getAnnotation(IRI.class).value();
					String variableName = field.getAnnotation(SPARQLVariableName.class).value();
					String subjectName = field.getAnnotation(SPARQLSubject.class).value();

					String triple = "?" + subjectName + " <" + type + ">"
							+ " ?" + variableName;

					triples.add(triple);
				}
			} catch (IllegalArgumentException e) {
				// Do nothing
			} catch (IllegalAccessException e) {
				// Do nothing
			}
		}

		return triples;
	}

	/**
	 * @return the submitters
	 */
	public List<Integer> getSubmittedBy() {
		return submittedBy;
	}

	/**
	 * @param submitters
	 *            the submitters to set
	 */
	public void setSubmittedBy(List<Integer> submitters) {
		this.submittedBy = submitters;
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
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the relationshipTypes
	 */
	public List<URI> getRelationshipTypes() {
		return relationshipTypes;
	}

	/**
	 * @param relationshipTypes
	 *            the relationshipTypes to set
	 */
	public void setRelationshipTypes(List<URI> relationshipTypes) {
		this.relationshipTypes = relationshipTypes;
	}

	/**
	 * @return the mappingSources
	 */
	public List<MappingSourceEnum> getMappingSource() {
		return mappingSources;
	}

	/**
	 * @param mappingSources
	 *            the mappingSources to set
	 */
	public void setMappingSource(List<MappingSourceEnum> mappingSource) {
		this.mappingSources = mappingSource;
	}

	/**
	 * @return the createdStartDate
	 */
	public Date getCreatedStartDate() {
		return createdStartDate;
	}

	/**
	 * @param createdStartDate
	 *            the createdStartDate to set
	 */
	public void setCreatedStartDate(Date createdStartDate) {
		this.createdStartDate = createdStartDate;
	}

	/**
	 * @return the createdEndDate
	 */
	public Date getCreatedEndDate() {
		return createdEndDate;
	}

	/**
	 * @param createdEndDate
	 *            the createdEndDate to set
	 */
	public void setCreatedEndDate(Date createdEndDate) {
		this.createdEndDate = createdEndDate;
	}

	/**
	 * @return the updatedStartDate
	 */
	public Date getUpdatedStartDate() {
		return updatedStartDate;
	}

	/**
	 * @param updatedStartDate
	 *            the updatedStartDate to set
	 */
	public void setUpdatedStartDate(Date updatedStartDate) {
		this.updatedStartDate = updatedStartDate;
	}

	/**
	 * @return the updatedEndDate
	 */
	public Date getUpdatedEndDate() {
		return updatedEndDate;
	}

	/**
	 * @param updatedEndDate
	 *            the updatedEndDate to set
	 */
	public void setUpdatedEndDate(Date updatedEndDate) {
		this.updatedEndDate = updatedEndDate;
	}

	/**
	 * @return the ontologyIds
	 */
	public List<Integer> getOntologyIds() {
		return ontologyIds;
	}

	/**
	 * @param ontologyIds
	 *            the ontologyIds to set
	 */
	public void setOntologyIds(List<Integer> ontologyIds) {
		this.ontologyIds = ontologyIds;
	}

	/**
	 * @return the mappingSources
	 */
	public List<MappingSourceEnum> getMappingSources() {
		return mappingSources;
	}

	/**
	 * @param mappingSources
	 *            the mappingSources to set
	 */
	public void setMappingSources(List<MappingSourceEnum> mappingSources) {
		this.mappingSources = mappingSources;
	}

	/**
	 * @return the permanentIdExists
	 */
	public Boolean isPermanentIdExists() {
		return permanentIdExists;
	}

	/**
	 * @param permanentIdExists
	 *            the permanentIdExists to set
	 */
	public void setPermanentIdExists(Boolean permanentIdExists) {
		this.permanentIdExists = permanentIdExists;
	}

}
