package org.ncbo.stanford.util.sparql;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;

public class SPARQLFilterGenerator {

	private List<Integer> submittedBy;
	private String mappingType;
	private Date startDate;
	private Date endDate;
	private Date createdStartDate;
	private Date createdEndDate;
	private Date updatedStartDate;
	private Date updatedEndDate;
	private List<Integer> ontologyIds;
	private List<URI> relationshipTypes;
	private List<MappingSourceEnum> mappingSources;
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

		if (permanentIdExists == true) {
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
	 * @param permanentIdExists the permanentIdExists to set
	 */
	public void setPermanentIdExists(Boolean permanentIdExists) {
		this.permanentIdExists = permanentIdExists;
	}

}
