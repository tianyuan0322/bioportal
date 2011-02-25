package org.ncbo.stanford.manager.metakb.protege.base.prop;

import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;

/**
 * A property map that corresponds to an object property in the KB, but that
 * stores the value on the Java side as the local (not qualified) name of the KB
 * instance.
 * <p>
 * The types are as follows:
 * <ul>
 * <li>On the Java side, the value is a {@link String}.</li>
 * <li>On the KB side, the value is an {@link OWLIndividual}.</li>
 * </ul>
 * 
 * @author Tony Loeser
 *
 */
public class InstanceNamePropertyMap extends ObjectPropertyMap {
	
	private final String instancePrefix;
	
	public InstanceNamePropertyMap(String beanPropName,
								   boolean isMultivalued,
								   String owlPropName,
								   String instancePrefix) {
		super(beanPropName,
			  String.class, // Because Java values are Strings by definition
			  isMultivalued,
			  owlPropName);
		this.instancePrefix = instancePrefix;
	}

	@Override
	protected Object prepareValueForOWL(Object value) throws MetadataException {
		if (value instanceof String) {
			String qualifiedInstanceName = instancePrefix + (String)value;
			return retrieveIndividualForName(qualifiedInstanceName);
		} else {
			String msg = "Should not reach here: instance name not a String";
			throw new BPRuntimeException(msg);
		}
	}
	
	@Override
	protected Object handleValueFromOWL(Object value) {
		if (value instanceof OWLIndividual) {
			String qualifiedName = ((OWLIndividual)value).getName();
			int nameStart = qualifiedName.lastIndexOf('#') + 1;
			if (nameStart == 0) {
				throw new BPRuntimeException("Instance name not qualified: " + qualifiedName);
			}
			String localName = qualifiedName.substring(nameStart);
			return localName;
		} else {
			throw new BPRuntimeException("OWL instance value was not OWLInstance: " + value);
		}
	}

}
