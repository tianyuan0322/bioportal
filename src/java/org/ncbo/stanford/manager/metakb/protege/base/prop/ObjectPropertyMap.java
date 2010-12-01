package org.ncbo.stanford.manager.metakb.protege.base.prop;

import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDAO;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractPropertyMap;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;

/**
 * A property map that corresponds to an OWL Object Property in the KB.  The 
 * value(s) for this property will be other {@link OWLIndividual} objects, which will
 * need to be converted in turn to and from the corresponding java beans.  So, this
 * {@link PropertyMap} also has an instance of {@link AbstractDAO} to handle that
 * conversion.
 * <p>
 * Note that copying an object value into the Protege OWL KB does not save that object
 * and its values into the KB.  It merely matches the id attribute of the java object
 * to an individual in the KB, and asserts that individual as the object property value.
 * Use the value's DAO to initial create it in the KB ({@link AbstractDAO#createObject})
 * or to update its property values in the KB ({@link AbstractDAO#updateObject}).
 * 
 * @author Tony Loeser
 */
public class ObjectPropertyMap extends AbstractPropertyMap {
	
	/**
	 * Constructor. 
	 * 
	 * @param beanPropName - the name of the corresponding data member on the java bean.
	 * @param beanType - the java type of the bean class that has the data member.
	 * @param owlPropName - the fully qualified name of the Object Property in the OWL KB.
	 * @param metadataKb - the KB itself.
	 * @param daLayer - the layer that is used to retrieve and convert the value objects
	 * and forth between the java bean data member and the OWL property value.
	 */
	public ObjectPropertyMap(String beanPropName,
					 		 Class<?> valueType, // What values are mapped to on the Java side
					 		 boolean isMultivalued,
					 		 String owlPropName) {
		super(beanPropName, valueType, isMultivalued, owlPropName);
	}
	
	@Override
	protected void checkOWLProperty(OWLProperty prop) {
		if (!(prop instanceof OWLObjectProperty)) {
			String msg = "Map requires an object property, but '"+
			             prop.getName()+"' is a "+
						 prop.getClass().getName();
			throw new BPRuntimeException(msg);
		}
	}
}
