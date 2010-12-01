package org.ncbo.stanford.manager.metakb.protege.base.prop;

import java.util.Date;

import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;

import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

/**
 * Property map that copes with the case in which we use a legacy ontology to
 * define the metadata kb object, and that ontology represents dates as strings.
 * Use of this map is discouraged, as dates ought to be represented in the kb as 
 * RDFSLiterals.  Apparently we don't always have a choice.
 * <p>
 * The types are as follows:
 * <ul>
 * <li>On the Java side, the value is a {@link Date}.</li>
 * <li>On the KB side, the value is a (specifically formatted) string.</li>
 * </ul>
 * 
 * @author Tony Loeser
 */
public class DateStringPropertyMap extends DatatypePropertyMap {

	
	/**
	 * Constructor.
	 */
	public DateStringPropertyMap(String beanPropName,
								 boolean isMultivalued,
							 	 String owlPropName) {
		super(beanPropName, Date.class, isMultivalued, owlPropName);
	}

	
	// =========================================================================
	// OWL value conversion
	
	@Override
	public Object prepareValueForOWL(Object value) throws MetadataException {
		if (value instanceof Date) {
			return convertDateToDateTimeString((Date)value);
		} else {
			String msg = "DateStringPropertyMap requires Date value. Given value: "+value;
			throw new BPRuntimeException(msg);
		}
	}

	@Override
	public Object handleValueFromOWL(Object value) {
		if (value == null) {
			return value;
		} else if (value instanceof String) {
			return convertDateStringToDate((String)value);
		} else {
			String msg = "Unexpected value type("+value.getClass().getName()+
						 ") on OWL date property "+getOwlProperty().getName();
			throw new BPRuntimeException(msg);
		}
	}
	
	private static String convertDateToDateTimeString(Date date) {
		return XMLSchemaDatatypes.getDateTimeString(date);
	}
	
	private static Date convertDateStringToDate(String dateString) {
		if (dateString != null) {
			return XMLSchemaDatatypes.getDate(dateString);
		} else {
			return null;
		}
	}
}