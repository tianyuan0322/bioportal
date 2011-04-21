package org.ncbo.stanford.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openrdf.model.URI;

public class ProvisionalTermBean implements Serializable {

	private static final long serialVersionUID = 7558381180705785552L;

	private List<Integer> ontologyIds = new ArrayList<Integer>();
	private String label;
	private List<String> synonyms = new ArrayList<String>();
	private String definition;
	private URI provisionalSubclassOf;
	private Date created;
	private Date updated;
	private Integer submittedBy;
	private String noteId;
	private String status;
	private URI permanentId;

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
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the synonyms
	 */
	public List<String> getSynonyms() {
		return synonyms;
	}

	/**
	 * @param synonyms
	 *            the synonyms to set
	 */
	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	/**
	 * @return the definition
	 */
	public String getDefinition() {
		return definition;
	}

	/**
	 * @param definition
	 *            the definition to set
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
	}

	/**
	 * @return the provisionalSubclassOf
	 */
	public URI getProvisionalSubclassOf() {
		return provisionalSubclassOf;
	}

	/**
	 * @param provisionalSubclassOf
	 *            the provisionalSubclassOf to set
	 */
	public void setProvisionalSubclassOf(URI provisionalSubclassOf) {
		this.provisionalSubclassOf = provisionalSubclassOf;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the updated
	 */
	public Date getUpdated() {
		return updated;
	}

	/**
	 * @param updated
	 *            the updated to set
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
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
	 * @return the noteId
	 */
	public String getNoteId() {
		return noteId;
	}

	/**
	 * @param noteId
	 *            the noteId to set
	 */
	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the permanentId
	 */
	public URI getPermanentId() {
		return permanentId;
	}

	/**
	 * @param permanentId
	 *            the permanentId to set
	 */
	public void setPermanentId(URI permanentId) {
		this.permanentId = permanentId;
	}
}
