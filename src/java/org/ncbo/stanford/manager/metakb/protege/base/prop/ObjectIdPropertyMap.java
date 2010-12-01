package org.ncbo.stanford.manager.metakb.protege.base.prop;

import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDAO;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;

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
public class ObjectIdPropertyMap extends ObjectPropertyMap {

	private String owlClassName;
	
	/**
	 * Constructor.  Same as constructor for {@link ObjectPropertyMap}.
	 */
	public ObjectIdPropertyMap(String beanPropName,
							   boolean isMultivalued,
							   String owlPropName,
							   String owlClassName) {
		super(beanPropName, Integer.class, isMultivalued, owlPropName);
		this.owlClassName = owlClassName;
	}


	// =========================================================================
	// OWL value conversion
	
	@Override
	public Object prepareValueForOWL(Object value) throws MetadataException {
		if (value instanceof Integer) {
			return getDaLayer().retrieveIndividualForId(owlClassName, (Integer)value);
		} else {
			String msg = "Attempt to set non-integer value ("+
			             value+") as a reference for an OWL object property ("+
			             getOwlProperty().getName()+")";
			throw new BPRuntimeException(msg);
		}
	}

	@Override
	public Object handleValueFromOWL(Object value) {
		if (value == null) {
			return value;
		} else if (value instanceof OWLIndividual) {
			return getDaLayer().getIdFromIndividual((OWLIndividual)value);
		} else {
			String msg = "Unexpected value type ("+
			             value.getClass().getName()+") on OWL object property "+
			             getOwlProperty().getName();
			throw new BPRuntimeException(msg);
		}
	}
}
