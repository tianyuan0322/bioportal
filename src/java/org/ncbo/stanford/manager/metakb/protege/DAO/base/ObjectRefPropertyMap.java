package org.ncbo.stanford.manager.metakb.protege.DAO.base;

import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * A property map that corresponds to an OWL Object Property in the KB, but that stores the value
 * on the Java side by reference.  Rather than store the value's corresponding Java bean on the
 * Java side, the map provides/expects the integer id of that value.  As with the similar class
 * {@link ObjectPropertyMap}, this map has an instance of {@link AbstractDAO} to handle the 
 * conversion from KB instance to Java bean.
 * <p>
 * XXX Do we need to talk about what happens on a save, as we did for ObjectPropertyMap?
 * 
 * @author Tony Loeser
 *
 */
public class ObjectRefPropertyMap extends ObjectPropertyMap {

	
	/**
	 * Constructor.  Same as constructor for {@link ObjectPropertyMap}.
	 */
	public ObjectRefPropertyMap(String beanPropName,
							 Class<?> beanType,
							 String owlPropName,
							 boolean isMultivalued,
							 OWLModel metadataKb,
							 AbstractDAO<?> valueDAO) {
		super(beanPropName, beanType, Integer.class, owlPropName, isMultivalued, metadataKb, valueDAO);
	}


	// =========================================================================
	// OWL value conversion
	
	@Override
	public Object convertJavaToOWLValue(Object value) throws MetadataException {
		if (value instanceof Integer) {
			return valueDAO.getIndividualForId((Integer)value);
		} else {
			String msg = "Attempt to set non-integer value ("+value+") as a reference for an OWL object property ("+owlProperty.getName()+")";
			throw new BPRuntimeException(msg);
		}
	}

	@Override
	public Object convertOWLToJavaValue(Object value) {
		if (value == null) {
			return value;
		} else if (value instanceof OWLIndividual) {
			return valueDAO.getIdForIndividual((OWLIndividual)value);
		} else {
			String msg = "Unexpected value type ("+value.getClass().getName()+") on OWL object property "+owlProperty.getName();
			throw new BPRuntimeException(msg);
		}
	}
}
