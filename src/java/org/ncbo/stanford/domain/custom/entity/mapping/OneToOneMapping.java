package org.ncbo.stanford.domain.custom.entity.mapping;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.annotations.iri;

@iri(ApplicationConstants.MAPPING_PREFIX + "One_To_One_Mapping")
public class OneToOneMapping extends Mapping implements Serializable {

	private static final long serialVersionUID = 5668752344409465584L;

	@iri(ApplicationConstants.MAPPING_PREFIX + "source")
	private URI source;

	@iri(ApplicationConstants.MAPPING_PREFIX + "target")
	private URI target;

	@iri(ApplicationConstants.MAPPING_PREFIX + "relation")
	private URI relation;

	@iri(ApplicationConstants.MAPPING_PREFIX + "source_ontology_id")
	private Integer sourceOntologyId;

	@iri(ApplicationConstants.MAPPING_PREFIX + "target_ontology_id")
	private Integer targetOntologyId;

	@iri(ApplicationConstants.MAPPING_PREFIX
			+ "created_in_source_ontology_version")
	private Integer createdInSourceOntologyVersion;

	@iri(ApplicationConstants.MAPPING_PREFIX
			+ "created_in_target_ontology_version")
	private Integer createdInTargetOntologyVersion;

	/**
	 * Default no-arg constructor.
	 * */
	public OneToOneMapping() {
		super();
	}

	/**
	 * Constructor with a provided id.
	 * 
	 * @param id
	 */
	public OneToOneMapping(URI id) {
		super(id);
	}

	/**
	 * Generate a list of Statements representing this object.
	 * 
	 * @return
	 */
	public ArrayList<Statement> toStatements(ValueFactory vf) {
		Field[] fields = OneToOneMapping.class.getDeclaredFields();
		ArrayList<Statement> statements = new ArrayList<Statement>();
		URI objectId = this.getId();

		// Gather properties
		for (Field field : fields) {
			if (field.getAnnotation(iri.class) != null) {
				URI type = new URIImpl(field.getAnnotation(iri.class).value());

				Statement statement = null;
				try {
					// We need to convert primitives to proper RDF store types
					Object value = field.get(this);
					Value valueTyped;
					if (value.getClass() == Integer.class) {
						valueTyped = vf.createLiteral((Integer) value);
					} else if (value.getClass() == String.class) {
						valueTyped = vf.createLiteral((String) value);
					} else {
						valueTyped = (Value) value;
					}
					
					statement = new StatementImpl(objectId, type, valueTyped);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (statement != null) {
					statements.add(statement);
				}
			}
		}

		// Get the object type
		URI objectType = new URIImpl(OneToOneMapping.class.getAnnotation(
				iri.class).value());
		statements.add(new StatementImpl((objectId), null, objectType));

		return statements;
	}

	/**
	 * @return the source
	 */
	public URI getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(URI source) {
		this.source = source;
	}

	/**
	 * @return the target
	 */
	public URI getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(URI target) {
		this.target = target;
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

}