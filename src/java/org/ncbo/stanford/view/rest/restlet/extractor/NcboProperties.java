package org.ncbo.stanford.view.rest.restlet.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;

public class NcboProperties {
	private static transient Logger log = Logger
			.getLogger(NcboProperties.class);

	public static final String ONTOLOGY_ID_PROPERTY = "bioportal.ontology.ref";
	public static final String TOP_CONCEPT_ID_PROPERTY = "bioportal.top.concept.id";
	public static final String FILTERED_OUT_PROPERTIES = "bioportal.filter.relations";

	public static final String ONTOLOGY_FILE_PROPERTY = "target.ontology.file";
	public static final String ONTOLOGY_NAME_PROPERTY = "target.ontology.name";
	public static final String APPEND_PROPERTY = "target.append.existing.ontology";
	public static final String CLASS_PREFIX = "target.class.prefix";

	// TODO: should not be static
	private Collection<String> filteredProps;

	private Properties props;

	/*
	 * static { try { props.load(new FileInputStream(new
	 * File("local.properties"))); } catch (IOException ioe) {
	 * log.error("Could not load properties file", ioe); } }
	 */

	public String getBioportalOntologyId() {
		return props.getProperty(ONTOLOGY_ID_PROPERTY);
	}

	public String getBioportalTopConceptId() {
		return props.getProperty(TOP_CONCEPT_ID_PROPERTY);
	}

	public String getOntologyFileLocation() {
		return props.getProperty(ONTOLOGY_FILE_PROPERTY);
	}

	public String getOwlOntologyName() {
		return props.getProperty(ONTOLOGY_NAME_PROPERTY);
	}

	public boolean getAppendOntologyFile() {
		String appendPropertyValue = props.getProperty(APPEND_PROPERTY);
		return !(appendPropertyValue == null || !appendPropertyValue
				.toLowerCase().equals("true"));
	}

	public Collection<String> getFilteredOutProperties() {
		if (filteredProps == null) {
			String allProps = props.getProperty(FILTERED_OUT_PROPERTIES)
					.toLowerCase();
			if (allProps == null) {
				return new ArrayList<String>();
			}
			String[] allPropsArray = allProps.split(",");
			filteredProps = Arrays.asList(allPropsArray);
		}
		return filteredProps;
	}

	public String getClassPrefix() {
		String c = props.getProperty(CLASS_PREFIX);
		return c == null ? "" : c;
	}

	public void setProps(Properties props) {
		this.props = props;
	}
}
