package org.ncbo.stanford.sparql.bean;

import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class Mapping extends AbstractSPARQLBean {

	// Name to use for the "subject" position in SPARQL queries for fields in this object
	public static final String SPARQL_SUBJECT_NAME = "mappingId";

	private static final long serialVersionUID = 5668752344409465584L;

	private static final String PREFIX = ApplicationConstants.MAPPING_PREFIX;
	private static final String ID_PREFIX = ApplicationConstants.MAPPING_ID_PREFIX;
	private static final String RDF_TYPE = PREFIX + "One_To_One_Mapping";

	/**
	 * Name to use for the "subject" position in SPARQL queries for fields in this object 
	 * @return
	 */
	public static String getSPARQLSubjectName() {
		return "mappingId";
	}
	
	@IRI(PREFIX + "id")
	protected URI id;

	@IRI(PREFIX + "source")
	protected List<URI> source = new ArrayList<URI>();

	@IRI(PREFIX + "target")
	protected List<URI> target = new ArrayList<URI>();

	@IRI(PREFIX + "relation")
	protected URI relation;

	@IRI(PREFIX + "source_ontology")
	protected URI sourceOntology;

	@IRI(PREFIX + "target_ontology")
	protected URI targetOntology;

	@IRI(PREFIX + "created_in_source_ontology_version")
	protected URI createdInSourceOntologyVersion;

	@IRI(PREFIX + "created_in_target_ontology_version")
	protected URI createdInTargetOntologyVersion;

	@IRI(PREFIX + "has_process_info")
	protected ProcessInfo processInfo;

	@IRI(PREFIX + "dependency")
	protected URI dependency;

	@IRI(PREFIX + "comment")
	protected String comment;
	
	public static Integer ontologyURI2Id(URI uri) {
		String parts[] = uri.toString().split("/");
		return Integer.parseInt(parts[parts.length-1]);
	}
	
	public static URI ontologyURIFromOntologyID(Integer id) {
		return new URIImpl(ApplicationConstants.BIOPORTAL_ONTOLOGY_URI_PREFIX + id);
	}
	
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
	public URI getSourceOntology() {
		return sourceOntology;
	}
	public Integer getSourceOntologyAsID() {
		return ontologyURI2Id(this.sourceOntology);
	}

	/**
	 * @param sourceOntologyId
	 *            the sourceOntologyId to set
	 */
	public void setSourceOntology(URI sourceOntology) {
		this.sourceOntology = sourceOntology;
	}

	/**
	 * @return the targetOntologyId
	 */
	public URI getTargetOntology() {
		return targetOntology;
	}
	public Integer getTargetOntologyAsID() {
		return ontologyURI2Id(this.targetOntology);
	}

	/**
	 * @param targetOntologyId
	 *            the targetOntologyId to set
	 */
	public void setTargetOntology(URI targetOntology) {
		this.targetOntology = targetOntology;
	}

	/**
	 * @return the createdInSourceOntologyVersion
	 */
	public URI getCreatedInSourceOntologyVersion() {
		return createdInSourceOntologyVersion;
	}
	public Integer getCreatedInSourceOntologyVersionAsID() {
		return ontologyURI2Id(this.createdInSourceOntologyVersion);
	}
	
	/**
	 * @param createdInSourceOntologyVersion
	 *            the createdInSourceOntologyVersion to set
	 */
	public void setCreatedInSourceOntologyVersion(
			URI createdInSourceOntologyVersion) {
		this.createdInSourceOntologyVersion = createdInSourceOntologyVersion;
	}

	/**
	 * @return the createdInTargetOntologyVersion
	 */
	public URI getCreatedInTargetOntologyVersion() {
		return createdInTargetOntologyVersion;
	}
	public Integer getCreatedInTargetOntologyVersionAsID() {
		return ontologyURI2Id(this.createdInTargetOntologyVersion);
	}

	/**
	 * @param createdInTargetOntologyVersion
	 *            the createdInTargetOntologyVersion to set
	 */
	public void setCreatedInTargetOntologyVersion(
			URI createdInTargetOntologyVersion) {
		this.createdInTargetOntologyVersion = createdInTargetOntologyVersion;
	}

	public ProcessInfo getProcessInfo() {
		return processInfo;
	}

	public void setProcessInfo(ProcessInfo processInfo) {
		this.processInfo = processInfo;
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
}
