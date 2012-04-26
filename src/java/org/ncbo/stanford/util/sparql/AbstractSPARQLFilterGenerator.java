package org.ncbo.stanford.util.sparql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.annotation.SPARQLSubject;
import org.ncbo.stanford.annotation.SPARQLVariableName;

public abstract class AbstractSPARQLFilterGenerator {

	/**
	 * DEFINING SPARQL FILTER FIELDS
	 * 
	 * Each field on a SPARQL parameter object is required to have the following annotation:
	 * IRI (Predicate)
	 * 
	 * Optionally:
	 * SPARQLSubject (Subject variable name)
	 * SPARQLVariableName (Value/Object variable name)
	 *
	 * Example:
	 * <code>
		@IRI(PREFIX + "mapping_source")
		@SPARQLSubject(SUBJECT)
		@SPARQLVariableName("mappingSource")
	 * </code>
	 * protected String mappingSource;
	 */
	
	// TODO: Split these parameters out into subclasses
	
	/**
	 * This method generates triple patterns in the SPARQL syntax. It is used to
	 * generate triples for parameters that have been provided for a particular
	 * call.
	 *
	 * @param SPARQLBeanClass
	 * @return
	 */
	public List<String> generateTriplePatterns() {
		Field[] fields = this.getClass().getDeclaredFields();

		List<String> triples = new ArrayList<String>();

		for (Field field : fields) {
			try {
				if (field.get(this) != null
						&& field.get(this).toString().length() > 0) {

					String type = field.getAnnotation(IRI.class).value();
					String variableName = field.getAnnotation(SPARQLVariableName.class).value();
					String subjectName = field.getAnnotation(SPARQLSubject.class).value();

					String triple = "?" + subjectName + " <" + type + ">"
							+ " ?" + variableName;

					triples.add(triple);
				}
			} catch (IllegalArgumentException e) {
				// Do nothing
			} catch (IllegalAccessException e) {
				// Do nothing
			}
		}

		return triples;
	}

	public Boolean isEmpty() {
		Field[] fields = this.getClass().getDeclaredFields();

		for (Field field : fields) {
			try {
				if (field.get(this) != null
						&& field.get(this).toString().length() > 0) {
					return false;
				}
			} catch (IllegalArgumentException e) {
				// Do nothing
			} catch (IllegalAccessException e) {
				// Do nothing
			}
		}

		return true;
	}

	/**
	 * Creates proper syntax for use in a SPARQL filter using provided
	 * parameters.
	 *
	 * @return
	 */
	public abstract String toFilter();

}
