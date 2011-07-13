package org.ncbo.stanford.sparql.bean;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

public abstract class AbstractSPARQLBean implements Serializable {

	private static final long serialVersionUID = 6830819593933198443L;

	protected String prefix = ApplicationConstants.DEFAULT_RDF_PREFIX;

	protected URI id;

	protected URI type = ApplicationConstants.DEFAULT_RDF_TYPE;

	/**
	 * Parameter mappings
	 */
	public static class ParameterMap {
		public String variableName;
		public String URI;
		public ParameterMap() {};
	}

	/**
	 * Default no-arg constructor.
	 */
	public AbstractSPARQLBean() {
		generateId();
	}

	/**
	 * Constructor with a provided id prefix and/or type.
	 *
	 * @param id
	 */
	public AbstractSPARQLBean(String prefix, String type) {
		if (prefix != null)
			this.prefix = prefix;

		if (type != null)
			this.type = new URIImpl(type);

		generateId();
	}

	/**
	 * Generate an id for this mapping.
	 */
	private void generateId() {
		this.id = new URIImpl(this.prefix + UUID.randomUUID().toString());
	}

	public ArrayList<Statement> toStatements(ValueFactory vf) {
		ArrayList<Statement> statements = toStatements(this, this.getClass(),
				this.id, vf);

		// Adds the object's type
		statements.add(new StatementImpl(this.id,
				ApplicationConstants.RDF_TYPE_URI, this.type));

		return statements;
	}

	/**
	 * Generate a list of Statements representing this object. Also check for
	 * supers that have IRI annotations, which we assume to be SPARQL Beans.
	 *
	 * @return
	 */
	private static ArrayList<Statement> toStatements(Object object,
			Class<?> klass, URI objectId, ValueFactory vf) {
		Field[] thisFields = klass.getDeclaredFields();

		// Storage for statements
		ArrayList<Statement> statements = new ArrayList<Statement>();

		// Gather properties
		for (Field field : thisFields) {
			if (field.getAnnotation(IRI.class) != null) {
				URI type = new URIImpl(field.getAnnotation(IRI.class).value());

				Statement statement = null;
				try {
					// We need to convert primitives to proper RDF types
					// To do this, we look for known classes and convert
					// then to their RDF literal counterparts.
					Object value = field.get(object);

					if (value != null) {
						// source and target fields are lists, so we have to
						// check for those separately and handle them here
						if (value instanceof List<?>) {
							for (Object listItem : (List<?>) value) {
								statements.add(new StatementImpl(objectId,
										type, convertValueToLiteral(listItem,
												vf)));
							}
						} else {
							Value valueTyped = convertValueToLiteral(value,
									vf);

							statement = new StatementImpl(objectId, type,
									valueTyped);
							if (statement != null) {
								statements.add(statement);
							}
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		Class<?> superclass = klass.getSuperclass();
		if (superclass != null) {
			if (!AbstractSPARQLBean.class.isAssignableFrom(klass)
					&& klass != AbstractSPARQLBean.class) {
				ArrayList<Statement> emptyList = new ArrayList<Statement>(0);
				return emptyList;
			} else {
				statements
						.addAll(toStatements(object, superclass, objectId, vf));
			}
		}

		return statements;
	}

	private static Value convertValueToLiteral(Object value, ValueFactory vf) {
		Value valueTyped = null;

		if (value.getClass() == Integer.class) {
			valueTyped = vf.createLiteral((Integer) value);
		} else if (value.getClass() == String.class) {
			valueTyped = vf.createLiteral((String) value);
		} else if (value.getClass() == Date.class) {
			try {
				Date date = (Date) value;
				GregorianCalendar c = new GregorianCalendar();
				c.setTime(date);
				XMLGregorianCalendar XMLGregCal;
				XMLGregCal = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(c).normalize();
				valueTyped = vf.createLiteral(XMLGregCal);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		} else {
			valueTyped = (Value) value;
		}

		return valueTyped;
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

    public abstract Map<String, ParameterMap> getParameterMapping();
}
