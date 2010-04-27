package org.ncbo.stanford.manager.metakb.protege.DAO.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.util.protege.OWLPropertyUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;

/**
 * Encapsulates a mapping of a single property between a java bean and an instance in
 * a Protege OWL knowledge base.  The property is set on initialization -- a data member
 * (with getter and setter) on the java bean, and a property for the owl class.  The
 * methods on this class deal with getting / setting the value of that property from
 * the corresponding beans and OWL individuals.
 * 
 * @author <a href="mailto:loeser@cs.stanford.edu">Tony Loeser</a>
 */
public class PropertyMap {
	
	// Java side
	protected final Method beanValueGetter;
	protected final Method beanValueSetter;
	
	// OWL side
	protected final OWLProperty owlProperty;
	
	// The type of value that will be transferred to/from Java/OWL
	// If isMultivalued == true, then these values will be gathered into a collection,
	//   i.e. the java bean data member should be Collection<valueType> 
	protected final Class<?> singleValueType; // The java type of a single property value
	protected final boolean isMultivalued;    // true iff valueType is a Collection
	
	// =========================================================================
	// Initialization
	
	protected PropertyMap(String beanPropName,      // Name of the data member on the java bean
						  Class<?> beanType,        // Type of the java bean
						  Class<?> singleValueType, // Java type of a single property value
						  boolean isMultivalued,    // true iff bean property values are Collection<valueType>
						  String owlPropName,       // Name of the property in the OWL KB
						  OWLModel metadataKb) {
		try {
			// Set types
			this.singleValueType = singleValueType;
			this.isMultivalued = isMultivalued;
			// Set up the Java side == accessor methods
			String capName = capitalize(beanPropName);
			beanValueGetter = myGetMethod(beanType, "get"+capName, (Class<?>[])null);
			beanValueSetter = myGetMethod(beanType, "set"+capName, beanValueGetter.getReturnType());
			// Set up the OWL side -- OWL Property
			owlProperty = metadataKb.getOWLProperty(owlPropName);
		} catch (StringIndexOutOfBoundsException e) {
			String msg = "Tried to set up bean-to-metakb map with empty prop name for bean: " + beanType;
			throw new BPRuntimeException(msg, e);
		}
	}
	
	private static Method myGetMethod(Class<?> inClass, String methodName, Class<?>...argTypes) {
		try {
			return inClass.getMethod(methodName, argTypes);
		} catch (SecurityException e) {
			String msg = "Bad bean configuration ("+inClass+") -- private accessor: "+methodName;
			throw new BPRuntimeException(msg, e);
		} catch (NoSuchMethodException e) {
			String msg = "Mismatch between spec and bean ("+inClass+") -- missing accessor: "+methodName;
			throw new BPRuntimeException(msg, e);
		}
	}

	private static String capitalize(String name) {
		char first = name.charAt(0);
		return Character.toUpperCase(first) + name.substring(1);
	}
	
	// =========================================================================
	// Value manipulation helpers
	
	/**
	 * Copy the value for this PropertyMap's property from the {@link OWLIndividual} to
	 * the java bean.
	 */
	public void copyValueToBean(OWLIndividual ind, Object bean) {
		Object value = getOWLValue(ind);
		setBeanValue(bean, value);
	}
	
	/**
	 * Copy the value for this PropertyMap's property from the java bean to the
	 * {@link OWLIndiivdual}. 
	 * 
	 * @throws MetadataException when there is a problem asserting the value in
	 * the OWL KB.
	 */
	public void copyValueToIndividual(Object bean, OWLIndividual ind) throws MetadataException {
		Object value = getBeanValue(bean);
		setOWLValue(ind, value);
	}
	
	/**
	 * Assert the supplied value on both the java bean and the {@link OWLIndividual}.
	 * 
	 * @throws MetadataException when there is a problem asserting the value in
	 * the OWL KB.
	 */
	public void setValueOnBoth(Object bean, OWLIndividual ind, Object value) throws MetadataException{
		setOWLValue(ind, value);
		setBeanValue(bean, value);
	}
	
	
	// =========================================================================
	// OWL value accessors
	
	/**
	 * Assert the given value on the {@link OWLIndividual}.
	 * 
	 * @throws MetadataException when there is a problem asserting the value in
	 * the OWL KB.
	 */
	public void setOWLValue(OWLIndividual ind, Object value) throws MetadataException {
		if (!checkValueType(value)) {
			String msg = "Attempt to set value of type "+value.getClass().getName()+" on "+owlProperty.getName();
			throw new BPRuntimeException(msg);
		}
		if (isMultivalued) {
			// This is a collection of values
			if (Collection.class.isInstance(value)) {
				Collection<?> owlValues = convertJavaToOWLValues((Collection<?>)value);
				OWLPropertyUtils.setPropertyValues(ind, owlProperty, owlValues);
			} else {
				String msg = "Attempt to assert single value ("+value+") on a multivalued property ("+owlProperty.getName()+")";
				throw new BPRuntimeException(msg);
			}
		} else {
			// This is a single value
			Object owlValue = convertJavaToOWLValue(value);
			OWLPropertyUtils.setPropertyValue(ind, owlProperty, owlValue);
		}
	}
	
	/**
	 * Retrieve this PropertyMap's property's value, if any, asserted on the given
	 * {@link OWLIndividual}.
	 * 
	 * @return The value, or null if there is no value in the KB.
	 */
	public Object getOWLValue(OWLIndividual ind) {
		if (isMultivalued) {
			// This is a collection of values
			Collection<?> owlValues = OWLPropertyUtils.getPropertyValues(ind, owlProperty);
			return convertOWLToJavaValues(owlValues);
		} else {
			// This is a single value
			Object owlValue = OWLPropertyUtils.getPropertyValue(ind, owlProperty);
			return convertOWLToJavaValue(owlValue);
		}
	}
	
	// =========================================================================
	// OWL value conversion
	
	public Object convertJavaToOWLValue(Object value) throws MetadataException {
		return value;
	}
	
	public Collection<?> convertJavaToOWLValues(Collection<?> values) throws MetadataException {
		return values;
	}
	
	public Object convertOWLToJavaValue(Object value) {
		return value;
	}
	
	public Collection<?> convertOWLToJavaValues(Collection<?> values) {
		return values;
	}
	
	// =========================================================================
	// Bean value accessors

	/**
	 * Set the given value on the java bean object.
	 */
	public void setBeanValue(Object bean, Object value) {
		if (!checkValueType(value)) {
			String msg;
			if (value == null) {
				msg = "Attempt to set null value on multivalued property "+beanValueSetter.getName();
			} else {
				msg = "Attempt to set value of type "+value.getClass().getName()+" on "+beanValueSetter.getName();
			}
			throw new BPRuntimeException(msg);
		}
		try {
			beanValueSetter.invoke(bean, value);
		} catch (IllegalArgumentException e) {
			String msg = "Setter "+beanValueSetter.getName()+" on type "+bean.getClass().getName()+" has incorrect arguments.";
			throw new BPRuntimeException(msg, e);
		} catch (IllegalAccessException e) {
			String msg = "Setter "+beanValueSetter.getName()+" on type "+bean.getClass().getName()+" not public.";
			throw new BPRuntimeException(msg, e);
		} catch (InvocationTargetException e) {
			String msg = "Setter "+beanValueSetter.getName()+" on type "+bean.getClass().getName()+" threw an exception.";
			throw new BPRuntimeException(msg, e);
		}
	}
	
	/**
	 * Retrieve the value for this PropertyMap's property from the java bean
	 * object.
	 * 
	 * @return The value, which may be null.
	 */
	public Object getBeanValue(Object bean) {
		try {
			return beanValueGetter.invoke(bean);
		} catch (IllegalArgumentException e) {
			String msg = "Getter "+beanValueGetter.getName()+" on type "+bean.getClass().getName()+" has arguments.";
			throw new BPRuntimeException(msg, e);
		} catch (IllegalAccessException e) {
			String msg = "Getter "+beanValueGetter.getName()+" on type "+bean.getClass().getName()+" not public.";
			throw new BPRuntimeException(msg, e);
		} catch (InvocationTargetException e) {
			String msg = "Getter "+beanValueGetter.getName()+" on type "+bean.getClass().getName()+" threw an exception.";
			throw new BPRuntimeException(msg, e);
		}
	}
	
	// =========================================================================
	// Type check helper
	
	private boolean checkValueType(Object value) {
		if (isMultivalued) {
			// value should be Collection<singleValueType>
			if (value instanceof Collection) {
				Collection<?> colValue = (Collection<?>)value;
				return colValue.isEmpty() ||
					   singleValueType.isInstance(colValue.iterator().next());
			} else {
				return false;
			}
		} else {
			// value should be singleValueType
			return value == null || singleValueType.isInstance(value);
		}
	}

}
