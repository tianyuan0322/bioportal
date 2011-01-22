package org.ncbo.stanford.domain.custom.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.ArrayUtils;
import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

@IRI(ApplicationConstants.MAPPING_PREFIX + "One_To_One_Mapping")
public class Mapping implements Serializable {

	private static final long serialVersionUID = 5668752344409465584L;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "id")
	protected URI id;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "source")
	private List<URI> source = new ArrayList<URI>();

	@IRI(ApplicationConstants.MAPPING_PREFIX + "target")
	private List<URI> target = new ArrayList<URI>();

	@IRI(ApplicationConstants.MAPPING_PREFIX + "relation")
	private URI relation;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "source_ontology_id")
	private Integer sourceOntologyId;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "target_ontology_id")
	private Integer targetOntologyId;

	@IRI(ApplicationConstants.MAPPING_PREFIX
			+ "created_in_source_ontology_version")
	private Integer createdInSourceOntologyVersion;

	@IRI(ApplicationConstants.MAPPING_PREFIX
			+ "created_in_target_ontology_version")
	private Integer createdInTargetOntologyVersion;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "date")
	protected Date date;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "submitted_by")
	protected Integer submittedBy;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_type")
	protected String mappingType;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "dependency")
	protected URI dependency;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "comment")
	protected String comment;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source")
	protected String mappingSource;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source_name")
	protected String mappingSourceName;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source_contact_info")
	protected String mappingSourcecontactInfo;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source_site")
	protected URI mappingSourceSite;

	@IRI(ApplicationConstants.MAPPING_PREFIX + "mapping_source_algorithm")
	protected String mappingSourceAlgorithm;

	/**
	 * Default no-arg constructor.
	 */
	public Mapping() {
		generateId();
	}

	/**
	 * Constructor with a provided id.
	 * 
	 * @param id
	 */
	public Mapping(URI id) {
		this.id = id;
	}

	/**
	 * Generate a list of Statements representing this object.
	 * 
	 * @return
	 */
	public ArrayList<Statement> toStatements(ValueFactory vf) {
		Field[] thisFields = this.getClass().getDeclaredFields();
		Field[] parentFields = this.getClass().getSuperclass()
				.getDeclaredFields();
		Field[] fields = (Field[]) ArrayUtils.addAll(thisFields, parentFields);

		ArrayList<Statement> statements = new ArrayList<Statement>();

		URI objectId = this.getId();

		// Gather properties
		for (Field field : fields) {
			if (field.getAnnotation(IRI.class) != null) {
				URI type = new URIImpl(field.getAnnotation(IRI.class).value());

				Statement statement = null;
				try {
					// We need to convert primitives to proper RDF types
					Object value = field.get(this);
					Value valueTyped = null;
					List<?> valueTypedList = null;
					if (value == null) {
						continue;
					} else if (value.getClass() == Integer.class) {
						valueTyped = vf.createLiteral((Integer) value);
					} else if (value.getClass() == String.class) {
						valueTyped = vf.createLiteral((String) value);
					} else if (value.getClass() == Date.class) {
						Date date = (Date) value;
						GregorianCalendar c = new GregorianCalendar();
						c.setTime(date);
						XMLGregorianCalendar XMLGregCal = DatatypeFactory
								.newInstance().newXMLGregorianCalendar(c)
								.normalize();
						valueTyped = vf.createLiteral(XMLGregCal);
					} else if (value instanceof List<?>) {
						valueTypedList = (List<?>) value;
					} else {
						valueTyped = (Value) value;
					}

					// source and target fields are lists, so we have to check
					// for those separately and handle them here as URIs
					if (valueTypedList != null) {
						for (Object listItem : valueTypedList) {
							statements.add(new StatementImpl(objectId, type,
									(Value) listItem));
						}
					} else {
						statement = new StatementImpl(objectId, type,
								valueTyped);
						if (statement != null) {
							statements.add(statement);
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (DatatypeConfigurationException e) {
					e.printStackTrace();
				}

			}
		}

		// Get the object type
		URI objectType = new URIImpl(Mapping.class.getAnnotation(IRI.class)
				.value());
		statements.add(new StatementImpl((objectId),
				ApplicationConstants.RDF_TYPE_URI, objectType));

		return statements;
	}

	/**
	 * Generate an id for this mapping.
	 */
	private void generateId() {
		this.id = new URIImpl(ApplicationConstants.MAPPING_ID_PREFIX
				+ UUID.randomUUID().toString());
	}

	/**
	 * 
	 * Default setters/getters
	 * 
	 */

	/**
	 * @return the id
	 */
	public URI getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(URI id) {
		this.id = id;
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

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the submittedBy
	 */
	public Integer getSubmittedBy() {
		return submittedBy;
	}

	/**
	 * @param submittedBy2
	 *            the submittedBy to set
	 */
	public void setSubmittedBy(Integer submittedBy2) {
		this.submittedBy = submittedBy2;
	}

	/**
	 * @return the mappingSource
	 */
	public String getMappingSource() {
		return mappingSource;
	}

	/**
	 * @param mappingSource
	 *            the mappingSource to set
	 */
	public void setMappingSource(String mappingSource) {
		this.mappingSource = mappingSource;
	}

	/**
	 * @return the mappingSourcecontactInfo
	 */
	public String getMappingSourcecontactInfo() {
		return mappingSourcecontactInfo;
	}

	/**
	 * @param mappingSourcecontactInfo
	 *            the mappingSourcecontactInfo to set
	 */
	public void setMappingSourcecontactInfo(String mappingSourcecontactInfo) {
		this.mappingSourcecontactInfo = mappingSourcecontactInfo;
	}

	/**
	 * @return the mappingSourceSite
	 */
	public URI getMappingSourceSite() {
		return mappingSourceSite;
	}

	/**
	 * @param mappingSourceSite
	 *            the mappingSourceSite to set
	 */
	public void setMappingSourceSite(URI mappingSourceSite) {
		this.mappingSourceSite = mappingSourceSite;
	}

	/**
	 * @return the mappingSourceAlgorithm
	 */
	public String getMappingSourceAlgorithm() {
		return mappingSourceAlgorithm;
	}

	/**
	 * @param mappingSourceAlgorithm
	 *            the mappingSourceAlgorithm to set
	 */
	public void setMappingSourceAlgorithm(String mappingSourceAlgorithm) {
		this.mappingSourceAlgorithm = mappingSourceAlgorithm;
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
	 * @return the mappingSourceName
	 */
	public String getMappingSourceName() {
		return mappingSourceName;
	}

	/**
	 * @param mappingSourceName
	 *            the mappingSourceName to set
	 */
	public void setMappingSourceName(String mappingSourceName) {
		this.mappingSourceName = mappingSourceName;
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

}