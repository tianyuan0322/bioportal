package org.ncbo.stanford.sparql.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;

public class Mapping extends AbstractSPARQLBean {

	private static final long serialVersionUID = 5668752344409465584L;

	private static final String PREFIX = ApplicationConstants.MAPPING_PREFIX;
	private static final String ID_PREFIX = ApplicationConstants.MAPPING_ID_PREFIX;
	private static final String RDF_TYPE = PREFIX + "One_To_One_Mapping";

	/**
	 * This Map can be used to tie a parameter to a URI and variable name when generating triples.
	 */
	public static HashMap<String, ParameterMap> parameterMapping = new HashMap<String, ParameterMap>();
	static {
		Field[] fields = Mapping.class.getDeclaredFields();

		HashMap<String, Field> fieldMap = new HashMap<String, Field>();
		for (Field field : fields) {
			fieldMap.put(field.getName(), field);
		}


		ParameterMap relationshipTypes = new ParameterMap();
		relationshipTypes.URI = fieldMap.get("relation").getAnnotation(IRI.class)
				.value();
		relationshipTypes.variableName = "relation";
		parameterMapping.put("relationshipTypes", relationshipTypes);

	};

	@IRI(PREFIX + "id")
	protected URI id;

	@IRI(PREFIX + "source")
	protected List<URI> source = new ArrayList<URI>();

	@IRI(PREFIX + "target")
	protected List<URI> target = new ArrayList<URI>();

	@IRI(PREFIX + "relation")
	protected URI relation;

	@IRI(PREFIX + "source_ontology_id")
	protected Integer sourceOntologyId;

	@IRI(PREFIX + "target_ontology_id")
	protected Integer targetOntologyId;

	@IRI(PREFIX + "created_in_source_ontology_version")
	protected Integer createdInSourceOntologyVersion;

	@IRI(PREFIX + "created_in_target_ontology_version")
	protected Integer createdInTargetOntologyVersion;

	@IRI(PREFIX + "has_process_info")
	protected ProcessInfo processInfo;



	@IRI(PREFIX + "dependency")
	protected URI dependency;


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
	public Integer getSourceOntologyId() {
		return sourceOntologyId;
	}

	/**
	 * @param sourceOntologyId
	 *            the sourceOntologyId to set
	 */
	public void setSourceOntologyId(Integer sourceOntologyId) {
		this.sourceOntologyId = sourceOntologyId;
	}

	/**
	 * @return the targetOntologyId
	 */
	public Integer getTargetOntologyId() {
		return targetOntologyId;
	}

	/**
	 * @param targetOntologyId
	 *            the targetOntologyId to set
	 */
	public void setTargetOntologyId(Integer targetOntologyId) {
		this.targetOntologyId = targetOntologyId;
	}

	/**
	 * @return the createdInSourceOntologyVersion
	 */
	public Integer getCreatedInSourceOntologyVersion() {
		return createdInSourceOntologyVersion;
	}

	/**
	 * @param createdInSourceOntologyVersion
	 *            the createdInSourceOntologyVersion to set
	 */
	public void setCreatedInSourceOntologyVersion(
			Integer createdInSourceOntologyVersion) {
		this.createdInSourceOntologyVersion = createdInSourceOntologyVersion;
	}

	/**
	 * @return the createdInTargetOntologyVersion
	 */
	public Integer getCreatedInTargetOntologyVersion() {
		return createdInTargetOntologyVersion;
	}

	/**
	 * @param createdInTargetOntologyVersion
	 *            the createdInTargetOntologyVersion to set
	 */
	public void setCreatedInTargetOntologyVersion(
			Integer createdInTargetOntologyVersion) {
		this.createdInTargetOntologyVersion = createdInTargetOntologyVersion;
	}

    public Map<String, ParameterMap> getParameterMapping() {
        return Mapping.parameterMapping;
    }

	public ProcessInfo getProcessInfo() {
		return processInfo;
	}

	public void setProcessInfo(ProcessInfo processInfo) {
		this.processInfo = processInfo;
	}
}
