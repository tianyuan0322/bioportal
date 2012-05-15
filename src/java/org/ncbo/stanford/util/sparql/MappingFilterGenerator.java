package org.ncbo.stanford.util.sparql;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.annotation.SPARQLSubject;
import org.ncbo.stanford.annotation.SPARQLVariableName;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;

public class MappingFilterGenerator extends AbstractSPARQLFilterGenerator {

	private static final String PREFIX = ApplicationConstants.MAPPING_PREFIX;

	/*
	 * Mapping Parameters
	 */

	@IRI(PREFIX + "submitted_by")
	@SPARQLSubject("procInf")
	@SPARQLVariableName("submittedBy")
	protected List<Integer> submittedBy;

	@IRI(PREFIX + "mapping_type")
	@SPARQLSubject("mappingId")
	@SPARQLVariableName("mappingType")
	protected String mappingType;

	@IRI(PREFIX + "mapping_source")
	@SPARQLSubject("procInf")
	@SPARQLVariableName("mappingSource")
	protected List<MappingSourceEnum> mappingSources;

	@IRI(PREFIX + "relation")
	@SPARQLSubject("mappingId")
	@SPARQLVariableName("relation")
	protected List<URI> relationshipTypes;

	@IRI(PREFIX + "date")
	@SPARQLSubject("procInf")
	@SPARQLVariableName("date")
	protected Date startDate;

	@IRI(PREFIX + "date")
	@SPARQLSubject("procInf")
	@SPARQLVariableName("date")
	protected Date endDate;

	public Integer getSourceOntologyId() {
		return sourceOntologyId;
	}

	public void setSourceOntologyId(Integer sourceOntologyId) {
		this.sourceOntologyId = sourceOntologyId;
	}

	public Integer getTargetOntologyId() {
		return targetOntologyId;
	}

	public void setTargetOntologyId(Integer targetOntologyId) {
		this.targetOntologyId = targetOntologyId;
	}

	protected Integer sourceOntologyId = null;
	protected Integer targetOntologyId = null;
	
	public List<String> generateTriplePatterns() {
		Field[] fields = this.getClass().getDeclaredFields();

		List<String> triples = new ArrayList<String>();

		// Bind proccessInfo to mapping
		triples.add(" ?mappingId <" + ApplicationConstants.MAPPING_PREFIX + "has_process_info> ?procInf");

		for (Field field : fields) {
			try {
				if (field.get(this) != null
						&& field.get(this).toString().length() > 0) {

					// If this field doesn't have an IRI defined we can't serialize to triple
					IRI typeAnnotation = field.getAnnotation(IRI.class);
					if (typeAnnotation == null)
						continue;
					
					String type = field.getAnnotation(IRI.class).value();
					String variableName = field.getAnnotation(
							SPARQLVariableName.class).value();
					String subjectName = field.getAnnotation(
							SPARQLSubject.class).value();

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

		if (mappingType != null) {
			filter += (filter.length() > 0) ? " && " : "";
			filter += "?mappingType = \"" + mappingType + "\"";
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

		if (startDate != null) {
			String dateVariableName = "?date";
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

		return filter;
	}

	public List<Integer> getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(List<Integer> submittedBy) {
		this.submittedBy = submittedBy;
	}

	public String getMappingType() {
		return mappingType;
	}

	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}

	public List<MappingSourceEnum> getMappingSources() {
		return mappingSources;
	}

	public void setMappingSources(List<MappingSourceEnum> mappingSources) {
		this.mappingSources = mappingSources;
	}

	public List<URI> getRelationshipTypes() {
		return relationshipTypes;
	}

	public void setRelationshipTypes(List<URI> relationshipTypes) {
		this.relationshipTypes = relationshipTypes;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
