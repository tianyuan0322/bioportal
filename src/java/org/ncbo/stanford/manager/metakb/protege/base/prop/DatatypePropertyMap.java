package org.ncbo.stanford.manager.metakb.protege.base.prop;

import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractPropertyMap;

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;


/**
 * A property map that corresponds to an OWL Datatype Property in the KB.  The 
 * value(s) for this property will be instances of String, Integer, or Date
 * on the java side.
 * 
 * @author Tony Loeser
 */
public class DatatypePropertyMap extends AbstractPropertyMap {
	
	/**
	 * Constructor. 
	 * 
	 * @param beanPropName - the name of the corresponding data member on the java bean.
	 * @param singleValueType - the java type of the data member.
	 * @param isMultivalued - if <code>true</code>, bean property is a collection 
	 * @param owlPropName - the fully qualified name of the Datatype Property in the OWL KB.
	 */
	public DatatypePropertyMap(String beanPropName,
							   Class<?> singleValueType,
							   boolean isMultivalued,
							   String owlPropName) {
		super(beanPropName, singleValueType, isMultivalued, owlPropName);
	}
	
	@Override
	protected void checkOWLProperty(OWLProperty prop) {
		if (!(prop instanceof OWLDatatypeProperty)) {
			String msg = "Map requires a datatype property, but '"+
			             prop.getName()+"' is a "+
						 prop.getClass().getName();
			throw new BPRuntimeException(msg);
		}
	}
}
