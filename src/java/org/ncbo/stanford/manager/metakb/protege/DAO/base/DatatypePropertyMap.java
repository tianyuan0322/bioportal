package org.ncbo.stanford.manager.metakb.protege.DAO.base;

import org.ncbo.stanford.exception.BPRuntimeException;

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;


/**
 * A property map that corresponds to an OWL Datatype Property in the KB.  The 
 * value(s) for this property will be instances of String, Integer, or Date
 * on the java side.
 * 
 * @author <a href="mailto:loeser@cs.stanford.edu">Tony Loeser</a>
 */
public class DatatypePropertyMap extends PropertyMap {
	
	/**
	 * Constructor. 
	 * 
	 * @param beanPropName - the name of the corresponding data member on the java bean.
	 * @param beanType - the java type of the bean class that has the data member.
	 * @param singleValueType - the java type of the data member.
	 * @param isMultivalued - if <code>true</code>, bean property is a collection 
	 * @param owlPropName - the fully qualified name of the Datatype Property in the OWL KB.
	 * @param metadataKb - the KB itself.
	 */
	public DatatypePropertyMap(String beanPropName,
							   Class<?> beanType,
							   Class<?> singleValueType,
							   boolean isMultivalued,
							   String owlPropName,
							   OWLModel metadataKb) {
		super(beanPropName, beanType, singleValueType, isMultivalued, owlPropName, metadataKb);
		if (!OWLDatatypeProperty.class.isInstance(owlProperty)) {
			throw new BPRuntimeException("Error defining datatype property map: "+owlPropName+" is not a datatype property");
		}
	}
}
