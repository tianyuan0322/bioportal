package org.ncbo.stanford.sparql.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;

public class ProvisionalTerm extends AbstractSPARQLBean {

	private static final long serialVersionUID = -6653109316901121381L;

	private static final String PREFIX = ApplicationConstants.PROVISIONAL_TERM_PREFIX;
	private static final String ID_PREFIX = ApplicationConstants.PROVISIONAL_TERM_ID_PREFIX;
	private static final String RDF_TYPE = PREFIX + "Provisional_Term";

	@IRI(PREFIX + "ontology_id")
	protected List<Integer> ontologyIds = new ArrayList<Integer>();

	@IRI("http://www.w3.org/2000/01/rdf-schema#label")
	protected String label;

	@IRI(PREFIX + "synonym")
	protected List<String> synonyms = new ArrayList<String>();

	@IRI(PREFIX + "definition")
	protected String definition;

	@IRI(PREFIX + "provisional_subclass_of")
	protected URI provisionalSubclassOf;

	@IRI(PREFIX + "created")
	protected Date created;

	@IRI(PREFIX + "updated")
	protected Date updated;

	@IRI(PREFIX + "submitted_by")
	protected Integer submittedBy;

	@IRI(PREFIX + "note_id")
	protected String noteId;
	
	@IRI(PREFIX + "status")
	protected String status;
	
	@IRI(PREFIX + "permanent_id")
	protected URI permanentId;

	/**
	 * Default no-arg constructor.
	 */
	public ProvisionalTerm() {
		super(ID_PREFIX, RDF_TYPE);
	}

	/**
	 * @return the id
	 */
	public URI getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(URI id) {
		this.id = id;
	}

	/**
	 * @return the ontologyIds
	 */
	public List<Integer> getOntologyIds() {
		return ontologyIds;
	}

	/**
	 * @param ontologyIds the ontologyIds to set
	 */
	public void setOntologyIds(List<Integer> ontologyIds) {
		this.ontologyIds = ontologyIds;
	}
	
	public void addOntologyIds(Integer ontologyId) {
		this.ontologyIds.add(ontologyId);
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
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
	 * @param synonyms the synonyms to set
	 */
	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}
	
	public void addSynonyms(String synonym) {
		this.synonyms.add(synonym);
	}

	/**
	 * @return the definition
	 */
	public String getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
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
	 * @param provisionalSubclassOf the provisionalSubclassOf to set
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
	 * @param created the created to set
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
	 * @param updated the updated to set
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
	 * @param submittedBy the submittedBy to set
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
	 * @param noteId the noteId to set
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
	 * @param status the status to set
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
	 * @param permanentId the permanentId to set
	 */
	public void setPermanentId(URI permanentId) {
		this.permanentId = permanentId;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the prefix
	 */
	public static String getPrefix() {
		return PREFIX;
	}

	/**
	 * @return the idPrefix
	 */
	public static String getIdPrefix() {
		return ID_PREFIX;
	}

	/**
	 * @return the rdfType
	 */
	public static String getRdfType() {
		return RDF_TYPE;
	}
    
}
