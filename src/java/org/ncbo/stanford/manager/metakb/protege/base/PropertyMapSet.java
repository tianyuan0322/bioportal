package org.ncbo.stanford.manager.metakb.protege.base;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.MetadataException;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;

/**
 * XXX Write this
 * -- currently no protection against badly written inheritance
 * -- includes a bunch of convenience methods to add properties to the set,
 *    to make that less verbose.
 * 
 * @author Tony Loeser
 *
 */
public class PropertyMapSet {
	
	private final Class<? extends MetadataBean> beanType;
	private final Set<AbstractPropertyMap> theMaps = new HashSet<AbstractPropertyMap>();
	private final AbstractDALayer daLayer;
	
	/**
	 * Basic constructor.
	 * 
	 * @param beanType - The type of java object that we will be mapping property
	 *     values to/from.
	 * @param metadataKb - The KB that will 
	 */
	public PropertyMapSet(Class<? extends MetadataBean> beanType,
						  AbstractDALayer daLayer) {
		this.beanType = beanType;
		this.daLayer = daLayer;
	}
	
	public void add(AbstractPropertyMap pMap) {
		pMap.initialize(beanType, daLayer);
		theMaps.add(pMap);
	}

	
	// =========================================================================
	// Value mapping helpers
	
	/**
	 * Copy the property values from the BeanType object to the metadata KB
	 * instance.
	 * 
	 * @param bean - The java side object; the source for the values.
	 * @param individual - The KB side object; the target for the values.
	 * @throws MetadataException when there is a problem copying the value
	 *     onto the individual.
	 */
	public void copyBeanPropertiesToIndividual(Object bean, OWLIndividual individual)
			throws MetadataException {
		for (Iterator<AbstractPropertyMap> pmIt = theMaps.iterator(); pmIt.hasNext(); ) {
			AbstractPropertyMap propertyMap = pmIt.next();
			propertyMap.copyValueToIndividual(bean, individual);
		}
	}

	/**
	 * Copy the property values from the metadata KB instance to the BeanType
	 * object.
	 * 
	 * @param individual - The KB side object; the source for the values.
	 * @param bean - The java side object; the target for the values.
	 */
	public void copyIndividualPropertiesToBean(OWLIndividual individual, Object bean) {
		for (Iterator<AbstractPropertyMap> pmIt = theMaps.iterator(); pmIt.hasNext(); ) {
			AbstractPropertyMap propertyMap = pmIt.next();
			propertyMap.copyValueToBean(individual, bean);
		}
	}
}
