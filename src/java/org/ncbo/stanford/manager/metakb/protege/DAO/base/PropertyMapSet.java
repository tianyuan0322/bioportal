package org.ncbo.stanford.manager.metakb.protege.DAO.base;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.exception.MetadataException;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class PropertyMapSet {
	
	private final Class<? extends AbstractIdBean> beanType;
	private final OWLModel metadataKb;
	private Set<PropertyMap> theMaps = new HashSet<PropertyMap>();
	
	/**
	 * XXX Write this
	 * -- currently no protection against badly written inheritance
	 * 
	 * @param beanType 
	 * @param metadataKb
	 */
	public PropertyMapSet(Class<? extends AbstractIdBean> beanType, OWLModel metadataKb) {
		this.beanType = beanType;
		this.metadataKb = metadataKb;
	}

	// =========================================================================
	// Value mapping helpers
	
	/**
	 * Add an OWL Datatype Property to the list of properties that are copied between
	 * the OWL KB and the java bean.  To be called by subclasses inside their implementation
	 * of {@link AbstractDAO#initializePropertyMaps}.
	 * 
	 * @param beanPropName - Name of the data member on the bean.
	 * @param singleValueType - Java type of a single property value.  e.g. <code>String.class</code>.
	 * @param isMultivalued - If <code>true</code>, the bean data member's type is 
	 *     <code>Collection&lt;singleValueType&gt;</code>.  (Otherwise the bean data 
	 *     member's type is simply <code>singleValueType</code>.)
	 * @param owlPropName - The qualified name of the OWL Datatype Property object.
	 */
	public void addDatatypePropertyMap(String beanPropName,
									   Class<?> singleValueType,
									   boolean isMultivalued,
									   String owlPropName) {
		PropertyMap pMap = new DatatypePropertyMap(beanPropName, beanType, singleValueType,
												   isMultivalued, owlPropName, metadataKb);
		theMaps.add(pMap);
	}

	/**
	 * Add an OWL Object Property to the list of properties that are copied between
	 * the OWL KB and the java bean.  To be called by subclasses inside their implementation
	 * of {@link AbstractDAO#initializePropertyMaps}.
	 * 
	 * @param beanPropName - Name of the data member on the bean.
	 * @param isMultivalued - If <code>true</code>, the bean data member's type is 
	 *     <code>Collection&lt;singleValueType&gt;</code>.  (Otherwise the bean data 
	 *     member's type is simply <code>singleValueType</code>.)
	 * @param owlPropName - The qualified name of the OWL Datatype Property object.
	 * @param valueDAO - The {@link AbstractDAO} implementation that is used to convert
	 *     this property's values between the java bean world and the OWL KB world.
	 */
	public void addObjectPropertyMap(String beanPropName,
									 boolean isMultivalued,
									 String owlPropName,
									 AbstractDAO<?> valueDAO) {
		PropertyMap pMap = new ObjectPropertyMap(beanPropName, beanType, owlPropName,
												 isMultivalued, metadataKb, valueDAO);
		theMaps.add(pMap);
	}
	
	// XXX Document this!
	public void addObjectRefPropertyMap(String beanPropName,
										boolean isMultivalued,
										String owlPropName,
										AbstractDAO<?> valueDAO) {
		PropertyMap pMap = new ObjectRefPropertyMap(beanPropName, beanType, owlPropName,
													isMultivalued, metadataKb, valueDAO);
		theMaps.add(pMap);
	}
	
	// XXX Document this!
	public void addDateStringPropertyMap(String beanPropName,
										 boolean isMultivalued,
										 String owlPropName) {
		PropertyMap pMap = new DateStringPropertyMap(beanPropName, beanType, isMultivalued,
													 owlPropName, metadataKb);
		theMaps.add(pMap);
	}
	
	// Copy the property values from the BeanType object to the metadata KB instance.
	// Does not copy the timestamps, as this operation represents a "create" or "update",
	// and timestamps need to be handled accordingly.
	protected void copyBeanPropertiesToIndividual(Object bean, OWLIndividual individual) throws MetadataException {
		for (Iterator<PropertyMap> pmIt = theMaps.iterator(); pmIt.hasNext(); ) {
			PropertyMap propertyMap = pmIt.next();
			propertyMap.copyValueToIndividual(bean, individual);
		}
	}

	// Copy the property values from the metadata KB instance to the BeanType object.
	// Copies timestamp values as well.
	protected void copyIndividualPropertiesToBean(OWLIndividual individual, Object bean) {
		for (Iterator<PropertyMap> pmIt = theMaps.iterator(); pmIt.hasNext(); ) {
			PropertyMap propertyMap = pmIt.next();
			propertyMap.copyValueToBean(individual, bean);
		}
// XXX Move this back to AbstractDAO
//		if (bean instanceof TimedIdBean) {
//			copyTimestampsToBean(individual, (TimedIdBean)bean);
//		}
	}

}
