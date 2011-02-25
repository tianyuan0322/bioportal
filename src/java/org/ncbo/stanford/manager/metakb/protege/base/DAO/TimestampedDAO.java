package org.ncbo.stanford.manager.metakb.protege.base.DAO;

import java.util.Date;

import org.ncbo.stanford.bean.metadata.TimedIdBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDALayer;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDAO;
import org.ncbo.stanford.manager.metakb.protege.base.prop.DatatypePropertyMap;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;

/**
 * Abstract supertype adds timestamp handling to the basic {@link AbstractDAO}.
 * Timestamp upkeep is added to the various CRUD operations.
 * <p>
 * On the Java side, the bean supertype is upgraded to a {@link TimedIdBean},
 * which defines the created and modified data members.
 * <p>
 * On the KB side, the Individual will use the datatype properties from the
 * metadata kb, "dateCreated", and "dateModified".
 * 
 * @author Tony Loeser
 *
 * @param <BeanType>
 */
public abstract class TimestampedDAO<BeanType extends TimedIdBean>
		extends AbstractDAO<BeanType> {
	
	// KB objects for timestamps initialized only if needed
	private final DatatypePropertyMap dateCreatedMap;
	private final DatatypePropertyMap dateModifiedMap;
	
	// Standard slot names from the metadata ontology
	protected static final String PROP_NAME_DATE_CREATED = PREFIX_METADATA + 
														   "dateCreated";
	protected static final String PROP_NAME_DATE_MODIFIED = PREFIX_METADATA + 
															"dateModified";
	

	// =========================================================================
	// Initialization

	protected TimestampedDAO(AbstractDALayer daLayer, String qualifiedClassName) {
		super(daLayer, qualifiedClassName);
		dateCreatedMap = new DatatypePropertyMap("dateCreated",
												 Date.class,
												 SINGLE_VALUE,
												 PROP_NAME_DATE_CREATED);
		dateModifiedMap  = new DatatypePropertyMap("dateModified",
												   Date.class,
												   SINGLE_VALUE,
												   PROP_NAME_DATE_MODIFIED);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		synchronized(this) {
			if (!dateCreatedMap.isInitialized()) {
				dateCreatedMap.initialize(beanType, getDaLayer());
				dateModifiedMap.initialize(beanType, getDaLayer());
			}
		}
	}

	
	
	// =========================================================================
	// Timestamp insertion
	//
	// Override the various methods where we need to assign timestamp values.
	
	@Override
	protected void createObject(BeanType bean) 
			throws MetadataException {
		// Create the object in the persistent store
		OWLIndividual ind = getKbClass().createOWLIndividual(null);
		// Update the bean with the id from the persistent store
		bean.setId(getIdForIndividual(ind));
		// Copy the property values into the persistent store
		copyBeanPropertiesToIndividual(bean, ind);
		// This part is different: set the date properties
		setDateCreatedAndModified(ind, bean);
	}
	
	@Override
	protected void updateObject(BeanType bean) 
			throws MetadataObjectNotFoundException, MetadataException {
		// Retrieve the individual from the persistent store
		OWLIndividual ind = getIndividualForId(bean.getId());
		// Copy the property values into the persistent store
		copyBeanPropertiesToIndividual(bean, ind);
		// This part is different: set the date properties
		setDateModified(ind, bean);
	}

	@Override
	protected void copyIndividualPropertiesToBean(OWLIndividual individual,
												  BeanType bean) {
		super.copyIndividualPropertiesToBean(individual, bean);
		copyTimestampsToBean(individual, bean);
	}

	
	// =========================================================================
	// Timestamp helpers

	// Call this method in the case when the bean is first created in the
	// persistent store. If appropriate, the timestamp properties will be
	// updated.
	//
	// If the bean has dateCreated already, then we assume it was previously
	// created in a different persistent store, and we keep that timestamp
	// value.
	protected void setDateCreatedAndModified(OWLIndividual inst, BeanType bean)
			throws MetadataException {
		TimedIdBean tBean = (TimedIdBean)bean;
		Date now = new Date();
		if (tBean.getDateCreated() == null) {
			// It is a new object
			dateCreatedMap.setValueOnBoth(bean, inst, now);
			dateModifiedMap.setValueOnBoth(bean, inst, now);
		} else {
			// It was previously stored somewhere else
			dateCreatedMap.copyValueToIndividual(tBean, inst);
			if (tBean.getDateModified() == null) {
				tBean.setDateModified(now);
			}
			dateModifiedMap.copyValueToIndividual(tBean, inst);
		}
	}
	
	// Call this method in the case when the bean is being updated in the
	// persistent store.  If appropriate, the dateModified property will be
	// updated.
	protected void setDateModified(OWLIndividual inst, BeanType bean)
			throws MetadataException {
		Date now = new Date();
		dateModifiedMap.setValueOnBoth(bean, inst, now);
	}
	
	// Subclasses may need to use this method to set the timestamp properties
	// when retrieving a bean from the persistent store
	protected void copyTimestampsToBean(OWLIndividual inst, TimedIdBean tBean) {
		dateCreatedMap.copyValueToBean(inst, tBean);
		dateModifiedMap.copyValueToBean(inst, tBean);
	}
}
