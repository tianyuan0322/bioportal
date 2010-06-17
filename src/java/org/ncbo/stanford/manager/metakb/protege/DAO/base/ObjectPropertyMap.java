package org.ncbo.stanford.manager.metakb.protege.DAO.base;

import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;

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
public class ObjectPropertyMap extends PropertyMap {
	
	protected AbstractDAO<?> valueDAO;
	
	/**
	 * Constructor. 
	 * 
	 * @param beanPropName - the name of the corresponding data member on the java bean.
	 * @param beanType - the java type of the bean class that has the data member.
	 * @param owlPropName - the fully qualified name of the Object Property in the OWL KB.
	 * @param metadataKb - the KB itself.
	 * @param valueDAO - the DAO that is used to convert the values as they are moved back
	 * and forth between the java bean data member and the OWL property value.
	 */
	public ObjectPropertyMap(String beanPropName,
							 Class<?> beanType,
							 String owlPropName,
							 boolean isMultivalued,
							 OWLModel metadataKb,
							 AbstractDAO<?> valueDAO) {
		this(beanPropName,
			 beanType,
			 valueDAO.getBeanType(), // Values will be what the DAO creates on Java side.
			 owlPropName,
			 isMultivalued,
			 metadataKb,
			 valueDAO);
	}
	
	// Same as public constructor, but explicit valueType allows subclass implementations where
	// values are stored on the Java side as something other than the corresponding Java bean.
	ObjectPropertyMap(String beanPropName,
					  Class<?> beanType,
					  Class<?> valueType, // What values are mapped to on the Java side
					  String owlPropName,
					  boolean isMultivalued,
					  OWLModel metadataKb,
					  AbstractDAO<?> valueDAO) {
		super(beanPropName, beanType, valueType, isMultivalued, owlPropName, metadataKb);
		if (!OWLObjectProperty.class.isInstance(owlProperty)) {
			throw new BPRuntimeException("Error defining object property map: "+owlPropName+
										 " is not a object property");
		}
		this.valueDAO = valueDAO;
	}


	// =========================================================================
	// OWL value conversion
	
	@Override
	public Object convertJavaToOWLValue(Object value) throws MetadataException {
		if (value instanceof AbstractIdBean) {
			AbstractIdBean bean = (AbstractIdBean)value;
			return valueDAO.getIndividualForId(bean.getId());
		} else {
			String msg = "Attempt to set non-bean value ("+value+") on OWL Object Property ("+owlProperty.getName()+")";
			return new BPRuntimeException(msg);
		}
	}

	@Override
	public Object convertOWLToJavaValue(Object value) {
		if (value == null) {
			return value;
		} else if (value instanceof OWLIndividual) {
			return valueDAO.convertIndividualToBean((OWLIndividual)value);
		} else {
			String msg = "Unexpected value type ("+value.getClass().getName()+") on OWL object property "+owlProperty.getName();
			throw new BPRuntimeException(msg);
		}
	}
}
