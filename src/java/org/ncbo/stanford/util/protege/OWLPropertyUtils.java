package org.ncbo.stanford.util.protege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

/**
 * A collection of utility functions for manipulating property values on instances
 * in Protege OWL KBs.
 * <p>
 * In the single-valued get/set functions, the following java datatypes are supported:
 * <ul>
 *   <li>{@link OWLIndividual}</li>
 *   <li>{@link String}</li>
 *   <li>{@link Integer}</li>
 *   <li>{@link Date} (Note that Date values are mappeed to DateTime RDFS Literals.)</li>
 * </ul>
 * In the multi-valued get/set functions, the value should be a {@link Collection} of 
 * any of the above.
 * 
 * @author <a href="mailto:loeser@cs.stanford.edu">Tony Loeser</a>
 */
public class OWLPropertyUtils {

	/**
	 * Assert this triple into the knowledge base, replacing any existing property
	 * values for the individual.
	 * 
	 * @param ind Object of the triple.
	 * @param prop Property of the triple.
	 * @param value Value of the triple.
	 */
	public static void setPropertyValue(OWLIndividual ind, OWLProperty prop, Object value) {
		Object owlValue = convertJavaValueToOWL(value, ind.getOWLModel());
		ind.setPropertyValue(prop, owlValue);
	}
	
	/**
	 * Assert a set of triples into the knowledge base, one for each value in <code>values</code>.
	 * All other triples for this object and property are discarded.
	 * 
	 * @param ind Object of each triple.
	 * @param prop Property of each triple.
	 * @param values Each of these values will be the value of one triple.
	 */
	public static void setPropertyValues(OWLIndividual ind, OWLProperty prop, Collection<?> values) {
		List<Object> owlValues = new ArrayList<Object>();
		for (Iterator<?> valIt = values.iterator(); valIt.hasNext(); ) {
			Object val = valIt.next();
			owlValues.add(convertJavaValueToOWL(val, ind.getOWLModel()));
		}
		ind.setPropertyValues(prop, owlValues);
	}
	
	/**
	 * Return the value for this object/property pair, or else <code>null</code> if there is no value.
	 * <p>
	 * The exception conditions have not been tested yet.
	 * 
	 * @param ind Object of the triple in the kb.
	 * @param prop Property of the triple in the kb.
	 * 
	 * @return The value from a matching triple in the kb, or <code>null</code>.
	 */
	public static Object getPropertyValue(OWLIndividual ind, OWLProperty prop) {
		Object owlValue = ind.getPropertyValue(prop);
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
	 * @param ind Object of the triples in the kb.
	 * @param prop Property of the triples in the kb.
	 * 
	 * @return Collection of the values from all matching triples in the kb.
	 */
	public static Collection<Object> getPropertyValues(OWLIndividual ind, OWLProperty prop) {
		Collection<?> owlValues = ind.getPropertyValues(prop);
		Collection<Object> javaValues = new ArrayList<Object>();
		for (Iterator<?> valIt = owlValues.iterator(); valIt.hasNext(); ) {
			Object owlValue = valIt.next();
			javaValues.add(convertOWLValueToJava(owlValue));
		}
		return javaValues;
	}
		
	// =========================================================================
	// Value conversion
	//
	// Handles: String, Integer, Date, OWLIndividual
	
	private static Object convertOWLValueToJava(Object owlValue) {
		Object javaValue = null;
		if (owlValue instanceof String) {
			javaValue = owlValue;
		} else if (owlValue instanceof Integer) {
			javaValue = owlValue;
		} else if (owlValue instanceof OWLIndividual) {
			javaValue = owlValue;
		} else if (owlValue instanceof RDFSLiteral) {
			// So far we only handle datetime literals
			// XXX Ought to check what type of literal, here!
			javaValue = convertDateTimeLiteralToDate((RDFSLiteral)owlValue);
		} else {
			throw new IllegalArgumentException("OWL value with unrecognized type: "+owlValue);
		}
		return javaValue;
	}
	
	private static Object convertJavaValueToOWL(Object javaValue, OWLModel owlModel) {
		Object owlValue = null;
		if (javaValue instanceof String ||
			javaValue instanceof Integer ||
			javaValue instanceof OWLIndividual) {
				owlValue = javaValue;
			} else if (javaValue instanceof Date) {
				owlValue = convertDateToDateTimeLiteral((Date)javaValue, owlModel);
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
	
	// Convert literal with type http://www.w3.org/2001/XMLSchema#dateTime ---> java.util.Date
	protected static Date convertDateTimeLiteralToDate(RDFSLiteral dateTimeLiteral) {
		return XMLSchemaDatatypes.getDate(dateTimeLiteral.getString());
	}
	
	// Convert java.util.Date ---> literal with type http://www.w3.org/2001/XMLSchema#dateTime
	protected static RDFSLiteral convertDateToDateTimeLiteral(Date date, OWLModel owlModel) {
		String dateString = XMLSchemaDatatypes.getDateTimeString(date);
		return owlModel.createRDFSLiteral(dateString, owlModel.getXSDdateTime());
	}
}
