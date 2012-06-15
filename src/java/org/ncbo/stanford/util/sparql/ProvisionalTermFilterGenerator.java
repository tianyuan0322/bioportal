package org.ncbo.stanford.util.sparql;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.annotation.SPARQLSubject;
import org.ncbo.stanford.annotation.SPARQLVariableName;
import org.ncbo.stanford.util.constants.ApplicationConstants;

public class ProvisionalTermFilterGenerator extends
		AbstractSPARQLFilterGenerator {

	private static final String PREFIX = ApplicationConstants.PROVISIONAL_TERM_PREFIX;

	/*
	 * Provisional Term Parameters
	 */

	@IRI(PREFIX + "submitted_by")
	@SPARQLSubject("id")
	@SPARQLVariableName("submittedBy")
	protected List<Integer> submittedBy;

	@IRI(PREFIX + "created")
	@SPARQLSubject("id")
	@SPARQLVariableName("created")
	protected Date createdStartDate;

	@IRI(PREFIX + "created")
	@SPARQLSubject("id")
	@SPARQLVariableName("created")
	protected Date createdEndDate;

	@IRI(PREFIX + "updated")
	@SPARQLSubject("id")
	@SPARQLVariableName("updated")
	protected Date updatedStartDate;

	@IRI(PREFIX + "updated")
	@SPARQLSubject("id")
	@SPARQLVariableName("updated")
	protected Date updatedEndDate;

	@IRI(PREFIX + "ontology_id")
	@SPARQLSubject("id")
	@SPARQLVariableName("ontologyId")
	protected List<Integer> ontologyIds;

	@IRI(PREFIX + "permanent_id")
	@SPARQLSubject("id")
	@SPARQLVariableName("permanentId")
	protected Boolean permanentIdExists;

	protected Date startDate;
	protected Date endDate;

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

		if (permanentIdExists != null && permanentIdExists == true) {
			filter += (filter.length() > 0) ? " && " : "";
			filter += "bound(?permanentId)";
		}

		return filter;
	}

	public Date getCreatedStartDate() {
		return createdStartDate;
	}

	public void setCreatedStartDate(Date createdStartDate) {
		this.createdStartDate = createdStartDate;
	}

	public Date getCreatedEndDate() {
		return createdEndDate;
	}

	public void setCreatedEndDate(Date createdEndDate) {
		this.createdEndDate = createdEndDate;
	}

	public Date getUpdatedStartDate() {
		return updatedStartDate;
	}

	public void setUpdatedStartDate(Date updatedStartDate) {
		this.updatedStartDate = updatedStartDate;
	}

	public Date getUpdatedEndDate() {
		return updatedEndDate;
	}

	public void setUpdatedEndDate(Date updatedEndDate) {
		this.updatedEndDate = updatedEndDate;
	}

	public List<Integer> getOntologyIds() {
		return ontologyIds;
	}

	public void setOntologyIds(List<Integer> ontologyIds) {
		this.ontologyIds = ontologyIds;
	}

	public Boolean getPermanentIdExists() {
		return permanentIdExists;
	}

	public void setPermanentIdExists(Boolean permanentIdExists) {
		this.permanentIdExists = permanentIdExists;
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

	public List<Integer> getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(List<Integer> submittedBy) {
		this.submittedBy = submittedBy;
	}

}
