package org.ncbo.stanford.domain.custom.entity.mapping;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.annotations.iri;

public abstract class Mapping implements Serializable {

	private static final long serialVersionUID = -237079713755255918L;

	@iri(ApplicationConstants.MAPPING_PREFIX + "id")
	private URI id;

	@iri(ApplicationConstants.MAPPING_PREFIX + "comment")
	private String comment;

	@iri(ApplicationConstants.MAPPING_PREFIX + "date")
	private Date date;

	@iri(ApplicationConstants.MAPPING_PREFIX + "submitted_by")
	private Integer submittedBy;

	@iri(ApplicationConstants.MAPPING_PREFIX + "dependency")
	private URI dependency;

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
	 * Generate an id for this mapping.
	 */
	private void generateId() {
		this.id = new URIImpl(ApplicationConstants.MAPPING_PREFIX
				+ UUID.randomUUID().toString());
	}

	/**
	 * Generate a list of Statements representing this object.
	 * 
	 * @return
	 */
	public ArrayList<Statement> toStatements() {
		Field[] fields = Mapping.class.getDeclaredFields();
		ArrayList<Statement> statements = new ArrayList<Statement>();
		URI objectId = this.getId();

		// Gather properties
		for (Field field : fields) {
			if (field.getAnnotation(iri.class) != null) {
				URI type = new URIImpl(field.getAnnotation(iri.class).value());

				Statement statement = null;
				try {
					statement = new StatementImpl(objectId, type, (Value) field
							.get(this));
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
		URI objectType = new URIImpl(Mapping.class.getAnnotation(iri.class)
				.value());
		statements.add(new StatementImpl((objectId), null, objectType));

		return statements;
	}

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

}
