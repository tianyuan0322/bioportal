package org.ncbo.stanford.manager.metakb.protege.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.util.protege.PropertyUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLProperty;

/**
 * Encapsulates a mapping of a single property between a java bean and an instance in
 * a Protege OWL knowledge base.  The property is set on initialization -- a data member
 * (with getter and setter) on the java bean, and a property for the owl class.  The
 * methods on this class deal with getting / setting the value of that property from
 * the corresponding beans and OWL individuals.
 * 
 * @author Tony Loeser
 */
public abstract class AbstractPropertyMap {
	
	// Java side
	private final String beanPropName;
	private Method beanValueGetter;
	private Method beanValueSetter;
	
	// OWL side
	private final String owlPropName;
	private OWLProperty owlProperty;
	
	// Overall layer that will contain this property map
	private AbstractDALayer daLayer = null;
	
	// The type of value that will be transferred to/from Java/OWL
	// If isMultivalued == true, then these values will be gathered into a collection,
	//   i.e. the java bean data member's type should be Collection<valueType> 
	private final Class<?> singleValueType; // The java type of a single property value
	private final boolean isMultivalued;    // true iff valueType is a Collection
	
	// =========================================================================
	// Initialization
	
	protected AbstractPropertyMap(String beanPropName,      // Name of the data member on the java bean
			                      Class<?> singleValueType, // Java type of a single property value
			                      boolean isMultivalued,    // true iff bean property values are Collection<valueType>
			                      String owlPropName) {     // Name of the property in the OWL KB
		this.beanPropName = beanPropName;
		this.owlPropName = owlPropName;
		this.singleValueType = singleValueType;
		this.isMultivalued = isMultivalued;
	}
	
	public void initialize(Class<?> beanType, AbstractDALayer daLayer) {
		this.daLayer = daLayer;
		// Set up the Java side -- accessor methods
		String capName = capitalize(beanPropName);
		beanValueGetter = myGetMethod(beanType, "get"+capName, (Class<?>[])null);
		beanValueSetter = myGetMethod(beanType, "set"+capName, beanValueGetter.getReturnType());
		// Set up the OWL side -- OWL Property
		owlProperty = daLayer.getMetadataKb().getOWLProperty(owlPropName);
		checkOWLProperty(owlProperty);
	}
	
	public boolean isInitialized() {
		return (daLayer == null);
	}
	
	protected abstract void checkOWLProperty(OWLProperty prop);
	
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
		try {
			char first = name.charAt(0);
			return Character.toUpperCase(first) + name.substring(1);
		} catch (StringIndexOutOfBoundsException e) {
			String msg = "Empty java property name";
			throw new BPRuntimeException(msg, e);
		}
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
			if (value == null) {
				Collection<?> owlValues = Collections.EMPTY_LIST;
				PropertyUtils.setPropertyValues(ind, owlProperty, owlValues);
			} else if (value instanceof Collection<?>) {
				Collection<?> owlValues = prepareValuesForOWL((Collection<?>)value);
				PropertyUtils.setPropertyValues(ind, owlProperty, owlValues);
			} else {
				String msg = "Attempt to assert single value ("+value+") on a multivalued property ("+owlProperty.getName()+")";
				throw new BPRuntimeException(msg);
			}
		} else {
			// This is a single value
			Object owlValue = prepareValueForOWL(value);
			PropertyUtils.setPropertyValue(ind, owlProperty, owlValue);
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
			Collection<?> owlValues = PropertyUtils.getPropertyValues(ind, owlProperty);
			return handleValuesFromOWL(owlValues);
		} else {
			// This is a single value
			Object owlValue = PropertyUtils.getPropertyValue(ind, owlProperty);
			return handleValueFromOWL(owlValue);
		}
	}
	
	// =========================================================================
	// OWL value conversion
	
	protected Object prepareValueForOWL(Object value) throws MetadataException {
		if (value instanceof MetadataBean) {
			return daLayer.retrieveIndividualForBean((MetadataBean)value);
		} else {
			return value;
		}
	}
	
	private Collection<?> prepareValuesForOWL(Collection<?> values)
			throws MetadataException {
		List<Object> owlValues = new ArrayList<Object>(values.size());
		for (Iterator<?> valIt = values.iterator(); valIt.hasNext(); ) {
			owlValues.add(prepareValueForOWL((Object)valIt.next()));
		}
		return owlValues;
	}
	
	protected Object handleValueFromOWL(Object value) {
		if (value instanceof OWLIndividual) {
			return daLayer.convertIndividualToBean((OWLIndividual)value);
		} else {
			return value;
		}
	}
		
	private Collection<?> handleValuesFromOWL(Collection<?> values) {
		List<Object> beanValues = new ArrayList<Object>(values.size());
		for (Iterator<?> valIt = values.iterator(); valIt.hasNext(); ) {
			beanValues.add(handleValueFromOWL((Object)valIt.next()));
		}
		return beanValues;
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
			} else if (value == null) {
				return true;
			} else {
				return false;
			}
		} else {
			// value should be singleValueType
			return value == null || singleValueType.isInstance(value);
		}
	}

	// =========================================================================
	// Accessors
	
	public AbstractDALayer getDaLayer() {
		return daLayer;
	}
	
	public OWLProperty getOwlProperty() {
		return owlProperty;
	}
}
