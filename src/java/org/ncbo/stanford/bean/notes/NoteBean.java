package org.ncbo.stanford.bean.notes;

import java.util.ArrayList;
import java.util.List;

public class NoteBean {
	private String id;
	private Integer ontologyId;
	private String type;
	private Integer author;
	private Long created;
	private Long updated;
	private String subject;
	private String body;
	private String status;
	private Boolean archived;
	private Integer createdInOntologyRevision;
	private Integer archivedInOntologyRevision;
	private List<AppliesToBean> appliesToList;
	private List<NoteBean> associated;
	private List<Object> values;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the author
	 */
	public Integer getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(Integer author) {
		this.author = author;
	}

	/**
	 * @return the created
	 */
	public Long getCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(Long created) {
		this.created = created;
	}

	/**
	 * @return the updated
	 */
	public Long getUpdated() {
		return updated;
	}

	/**
	 * @param updated
	 *            the updated to set
	 */
	public void setUpdated(Long updated) {
		this.updated = updated;
	}

	/**
	 * @return the appliesTo
	 */
	public List<AppliesToBean> getAppliesToList() {
		return appliesToList;
	}

	/**
	 * @param appliesTo
	 *            the appliesTo to set
	 */
	public void setAppliesToList(List<AppliesToBean> appliesToList) {
		this.appliesToList = appliesToList;
	}

	/**
	 * @param appliesTo
	 *            the appliesTo to add
	 */
	public void addAppliesToList(AppliesToBean appliesToList) {
		if (this.appliesToList == null) {
			this.appliesToList = new ArrayList<AppliesToBean>();
		}
		this.appliesToList.add(appliesToList);
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the values
	 */
	public List<Object> getValues() {
		return values;
	}

	/**
	 * @param values
	 *            the values to set
	 */
	public void setValues(List<Object> values) {
		this.values = values;
	}

	/**
	 * @param value
	 *            the value to add
	 */
	public void addValue(Object value) {
		this.values.add(value);
	}

	/**
	 * @return the archived
	 */
	public Boolean getArchived() {
		return archived;
	}

	/**
	 * @param archived
	 *            the archived to set
	 */
	public void setArchived(Boolean archived) {
		this.archived = archived;
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
	 * @return the createdInOntologyRevision
	 */
	public Integer getCreatedInOntologyRevision() {
		return createdInOntologyRevision;
	}

	/**
	 * @param createdInOntologyRevision
	 *            the createdInOntologyRevision to set
	 */
	public void setCreatedInOntologyRevision(Integer createdInOntologyRevision) {
		this.createdInOntologyRevision = createdInOntologyRevision;
	}

	/**
	 * @return the archivedInOntologyRevision
	 */
	public Integer getArchivedInOntologyRevision() {
		return archivedInOntologyRevision;
	}

	/**
	 * @param archivedInOntologyRevision
	 *            the archivedInOntologyRevision to set
	 */
	public void setArchivedInOntologyRevision(Integer archivedInOntologyRevision) {
		this.archivedInOntologyRevision = archivedInOntologyRevision;
	}

	/**
	 * @return the associated
	 */
	public List<NoteBean> getAssociated() {
		return associated;
	}

	/**
	 * @param associated
	 *            the associated to set
	 */
	public void setAssociated(List<NoteBean> associated) {
		this.associated = associated;
	}

	/**
	 * @param associated
	 *            the associated to add
	 */
	public void addAssociated(NoteBean associated) {
		if (this.associated == null) {
			this.associated = new ArrayList<NoteBean>();
		}
		this.associated.add(associated);
	}

	/**
	 * @return the ontologyId
	 */
	public Integer getOntologyId() {
		return ontologyId;
	}

	/**
	 * @param ontologyId
	 *            the ontologyId to set
	 */
	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

}