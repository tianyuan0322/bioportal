package org.ncbo.stanford.util.protege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

/**
 * A collection of utility functions for manipulating property values on objects
 * in a Protege OWL KB. Note that we cannot assume that the properties
 * accessible in an OWLModel are OWLProperty or that the objects on which
 * property values are asserted are OWLIndividual. A Protege OWL KB is welcome
 * to import RDFS files. The properties from these files become RDFProperties.
 * The methods in this class are intended to be general enough to work with
 * whatever one finds in an OWLModel knowledge base.
 * <p>
 * Types of property values accepted are the same as the types handled by the
 * utilities in {@link ProtegeValueUtils}.
 * 
 * @author Tony Loeser
 */
public class PropertyUtils {

	// =========================================================================
	// Getters and Setters
	
	/**
	 * Return the value for this object/property pair, or else <code>null</code>
	 * if there is no value.
	 * 
	 * @param  res Object of the triple in the kb.
	 * @param prop Property of the triple in the kb.
	 * 
	 * @return The value from a matching triple in the kb, or <code>null</code>.
	 */
	public static Object getPropertyValue(RDFResource res, RDFProperty prop) {
		Object owlValue = res.getPropertyValue(prop);
		if (owlValue == null) {
			return null;
		} else {
			return convertOWLValueToJava(owlValue);
		}
	}

	/**
	 * Return all values for this object/property pair.  If there are no values then an
	 * empty collection is returned.
	 * 
	 * @param res Object of the triples in the kb.
	 * @param prop Property of the triples in the kb.
	 */
	public static Collection<Object> getPropertyValues(RDFResource res,
			RDFProperty prop) {
		Collection<?> owlValues = res.getPropertyValues(prop);
		Collection<Object> javaValues = new ArrayList<Object>(owlValues.size());
		for (Object owlValue : owlValues) {
			javaValues.add(convertOWLValueToJava(owlValue));
		}
		return javaValues;
	}

	/**
	 * Assert this triple into the knowledge base, replacing any existing
	 * property values for the individual.
	 * 
	 * @param   res Object of the triple.
	 * @param  prop Property of the triple.
	 * @param value Value of the triple.
	 */
	public static void setPropertyValue(RDFResource res, RDFProperty prop,
			Object value) {
		Object owlValue = convertJavaValueToOWL(value, res
				.getOWLModel());
		res.setPropertyValue(prop, owlValue);
	}

	/**
	 * Assert a set of triples into the knowledge base, one for each value in
	 * <code>values</code>. All other triples for this object and property are
	 * discarded.
	 * 
	 * @param    res Object of each triple.
	 * @param   prop Property of each triple.
	 * @param values Each of these values will be the value of one triple.
	 */
	public static void setPropertyValues(RDFResource res, RDFProperty prop,
			Collection<?> values) {
		List<Object> owlValues = new ArrayList<Object>(values.size());
		OWLModel owlModel = res.getOWLModel();
		for (Object javaValue : values) {
			owlValues.add(convertJavaValueToOWL(javaValue,
					owlModel));
		}
		res.setPropertyValues(prop, owlValues);
	}
	
	
	// =========================================================================
	// Search methods

	// This is just for String values
	@SuppressWarnings("unchecked")
	public static Collection<RDFResource> getResourcesWithPropertyValue(RDFProperty prop, String value) {
		OWLModel owlModel = prop.getOWLModel();
		Collection<RDFResource> matches = (Collection<RDFResource>)owlModel.getMatchingResources(prop, value, -1);
		return matches;
	}
	
	// Add a filter to just get the instances of a particular type
	public static Collection<RDFResource> getResourcesWithPropertyValue(RDFSClass cls, RDFProperty prop, String value) {
		Collection<RDFResource> matches = getResourcesWithPropertyValue(prop, value);
		Collection<RDFResource> typedMatches = new ArrayList<RDFResource>(matches.size());
		for (Object match : matches) {
			if (match instanceof RDFResource) {
				if (((RDFResource)match).hasRDFType(cls)) {
					typedMatches.add((RDFResource)match);
				}
			}
		}
		return typedMatches;
	}
	

	// =========================================================================
	// Value conversion
	//
	// Handles: Boolean, Date, Integer, OWLIndividual, String
	
	public static Object convertOWLValueToJava(Object owlValue) {
		Object javaValue = null;
		if (owlValue instanceof String) {
			javaValue = owlValue;
		} else if (owlValue instanceof Integer) {
			javaValue = owlValue;
		} else if (owlValue instanceof OWLIndividual) {
			javaValue = owlValue;
		} else if (owlValue instanceof RDFSLiteral) {
			javaValue = convertRDFSLiteralToObject((RDFSLiteral)owlValue);
		} else {
			throw new IllegalArgumentException("OWL value with unrecognized type: "+owlValue);
		}
		return javaValue;
	}
	
	public static Object convertJavaValueToOWL(Object javaValue, OWLModel owlModel) {
		Object owlValue = null;
		if (javaValue instanceof String ||
			javaValue instanceof Integer ||
			javaValue instanceof OWLIndividual) {
			owlValue = javaValue;
		} else if (javaValue instanceof Date) {
			owlValue = convertDateToDateTimeLiteral((Date)javaValue, owlModel);
		} else if (javaValue instanceof Boolean) {
			owlValue = convertBooleanToLiteral((Boolean)javaValue, owlModel);
		} else if (javaValue == null) {
			owlValue = null;
		} else {
			throw new IllegalArgumentException("Tried to convert Java value with unrecognized type: "+javaValue);
		}
		return owlValue;
	}
	
	// =========================================================================
	// RDFSLiteral helpers
	//
	// When retrieving values of DatatypeProperties, some values are handled directly as
	// their corresponding Java type (so far String, Integer), and others are wrapped as
	// RDFSLiterals (so far date, time, dateTime).  These utilities convert between the 
	// plain java representation (e.g. Date) and the RDFSLiteral representation.
	
	protected static final String RDFSTYPE_NAME_DATE = "http://www.w3.org/2001/XMLSchema#dateTime";
	protected static final String RDFSTYPE_NAME_BOOL = "http://www.w3.org/2001/XMLSchema#boolean";
	
	// Convert RDFSLiterals to their corresponding Java types
	protected static Object convertRDFSLiteralToObject(RDFSLiteral rdfsLiteral) {
		String typeString = rdfsLiteral.getDatatype().getName();
		if (typeString == RDFSTYPE_NAME_BOOL) {
			return new Boolean(rdfsLiteral.getBoolean());
		} else if (typeString == RDFSTYPE_NAME_DATE) {
			return XMLSchemaDatatypes.getDate(rdfsLiteral.getString());
		} else {
			throw new IllegalArgumentException("RDFSLiteral with unrecognized type: "+typeString);
		}
	}
	
	// Convert java.util.Date ---> literal with type http://www.w3.org/2001/XMLSchema#dateTime
	protected static RDFSLiteral convertDateToDateTimeLiteral(Date date, OWLModel owlModel) {
		String dateString = XMLSchemaDatatypes.getDateTimeString(date);
		return owlModel.createRDFSLiteral(dateString, owlModel.getXSDdateTime());
	}

	// Convert java.Boolean ---> literal with type http://www.w3.org/2001/XMLSchema#boolean
	protected static RDFSLiteral convertBooleanToLiteral(Boolean bool, OWLModel owlModel) {
		String boolString = bool ? "true" : "false";
		return owlModel.createRDFSLiteral(boolString, owlModel.getXSDboolean());
	}
}