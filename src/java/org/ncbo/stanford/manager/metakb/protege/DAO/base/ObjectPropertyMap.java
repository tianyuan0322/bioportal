package org.ncbo.stanford.manager.metakb.protege.DAO.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
	
	private AbstractDAO<?> valueDAO;
	
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
		super(beanPropName, beanType, valueDAO.beanType, isMultivalued, owlPropName, metadataKb);
		if (!OWLObjectProperty.class.isInstance(owlProperty)) {
			throw new BPRuntimeException("Error defining datatype property map: "+owlPropName+" is not a datatype property");
		}
		this.valueDAO = valueDAO;
	}


	// =========================================================================
	// OWL value conversion
	
	// Override
	public Object convertJavaToOWLValue(Object value) throws MetadataException {
		if (value instanceof AbstractIdBean) {
			AbstractIdBean bean = (AbstractIdBean)value;
			return valueDAO.getInstance(bean.getId());
		} else {
			String msg = "Attempt to set non-bean value ("+value+") on OWL Object Property ("+owlProperty.getName()+")";
			return new BPRuntimeException(msg);
		}
	}

	public Collection<?> convertJavaToOWLValues(Collection<?> values)
			throws MetadataException {
		List<Object> owlValues = new ArrayList<Object>(values.size());
		for (Iterator<?> valIt = values.iterator(); valIt.hasNext(); ) {
			owlValues.add(convertJavaToOWLValue((Object)valIt.next()));
		}
		return owlValues;
	}

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

	public Collection<?> convertOWLToJavaValues(Collection<?> values) {
		List<Object> beanValues = new ArrayList<Object>(values.size());
		for (Iterator<?> valIt = values.iterator(); valIt.hasNext(); ) {
			beanValues.add(convertOWLToJavaValue((Object)valIt.next()));
		}
		return beanValues;
	}
}
