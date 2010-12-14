package org.ncbo.stanford.bean.mapping;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;

public class MappingParametersBean {

	private List<Integer> submittedBy;
	private String mappingType;
	private Date startDate;
	private Date endDate;
	private List<URI> relationshipTypes;
	private List<MappingSourceEnum> mappingSources;

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

		if (mappingType != null) {
			filter += (filter.length() > 0) ? " && " : "";
			filter += "?mappingType = \"" + mappingType + "\"";
		}

		if (startDate != null && endDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String startDateStr = sdf.format(startDate);
			String endDateStr = sdf.format(endDate);

			filter += (filter.length() > 0) ? " && " : "";
			filter += "xsd:dateTime (?date) >= xsd:dateTime (\"" + startDateStr
					+ ApplicationConstants.TIMEZONE_ID
					+ "\") && xsd:dateTime (?date) <= xsd:dateTime (\""
					+ endDateStr + ApplicationConstants.TIMEZONE_ID + "\")";
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

}
