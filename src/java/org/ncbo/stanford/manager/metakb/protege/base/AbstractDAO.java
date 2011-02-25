package org.ncbo.stanford.manager.metakb.protege.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * Abstract version of a Data Access Object for a particular object, to move it
 * between Bean representation and Instance in the metadata kb.
 * <p>
 * The Bean plays the role of Transfer Object (in terms of the usual DAO
 * pattern).  The metadata KB plays the role of the persistent store.
 * <p>
 * Implementing sub-classes must provide two things.  First, a no-argument
 * constructor that calls this class's constructor and sets the (kb) class name.
 * Second, an implementation of {@link #initializePropertyMaps()} that defines
 * the properties to be mapped back and forth across the layer, using calls to
 * {@link #addMap}.
 * 
 * @author Tony Loeser
 */
public abstract class AbstractDAO<BeanType extends MetadataBean> {
	
	// =========================================================================
	// Constants
	// For use in subclasses.

	// Namespace prefixes from the metadata ontology
	protected static final String PREFIX_OMV = "OMV:";
	protected static final String PREFIX_METADATA = "metadata:";
	protected static final String PREFIX_METRICS = "metrics:";
	protected static final String INSTANCE_NAMESPACE = "http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#";

	// Used to call constructors of PropertyMaps.  Not the best place
	// for these to be defined, huh.  Most parsimonious, though.
	protected static boolean COLLECTION = true;
	protected static boolean SINGLE_VALUE = false;
	
	// =========================================================================
	// Data members

	// This is set to be the same as BeanType. (Done in the constructor.)
	// Used for creating new beans.
	protected final Class<BeanType> beanType;
	
	// Property maps -- used to move property values to/from bean/owl
	private final List<AbstractPropertyMap> pMaps =
				new ArrayList<AbstractPropertyMap>();

	
	// Given name of the direct type for the instances
	protected final String qualifiedClassName;
	
	// Computed names
	protected String instanceNamePrefix;
	
	// Objects from the KB will be set during initialization, which is a
	// separate step from construction.
	private OWLModel metadataKb = null;
	private OWLNamedClass kbClass = null;
	
	// The data access layer of which this is a part
	private final AbstractDALayer daLayer;

	// =========================================================================
	// Initialization
	
	// Initialization is done in two steps.
	// First, in the creator method some basic data members are set.
	// Second, in initialize(), the KB is set up and objects from the KB are 
	// accessed.
	// This split allows the KB initialization -- which is time consuming -- to
	// be delayed until the data access layer is actually used.  This allows,
	// for example, test cases unrelated to metadata to skip the initialization.
	
	@SuppressWarnings("unchecked")
	protected AbstractDAO(AbstractDALayer daLayer, String qualifiedClassName) {
		// Initialize Java params
		this.beanType = (Class<BeanType>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		// Initialize String values
		this.qualifiedClassName = qualifiedClassName;
		int localNameStart = qualifiedClassName.lastIndexOf(':') + 1;
		String localClassName = qualifiedClassName.substring(localNameStart);
		instanceNamePrefix = INSTANCE_NAMESPACE + localClassName + "_";
		// Every DAO is part of a data access layer
		this.daLayer = daLayer;
	}
	
	protected void ensureInitialization() {
		if (metadataKb == null) {
			initialize();
		}
	}
	
	protected void initialize() {
		synchronized(this) {
			if (this.metadataKb == null) {
				try {
					this.metadataKb = daLayer.getMetadataKb();
					kbClass = metadataKb.getOWLNamedClass(qualifiedClassName);
				} catch(Exception e) {
					// XXX Do something better here.
					e.printStackTrace();
					throw new BPRuntimeException("Error encountered initializating metadata kb access layer.", e);
				}
				initializePropertyMaps();
			}
		}
	}
		
	// Subclasses must implement this with calls to the PropertyMapSet
	abstract protected void initializePropertyMaps();
	

	// =========================================================================
	// CRUD Operations
	// These methods are called from the DALayer object.
	
	// Create a corresponding object in the persistent store
	// Side effect: Bean is updated to match the new ID.
	protected void createObject(BeanType bean) 
			throws MetadataException {
		// Create the object in the persistent store
		OWLIndividual ind = kbClass.createOWLIndividual(null);
		// Update the bean with the id from the persistent store
		bean.setId(getIdForIndividual(ind));
		// Copy the property values into the persistent store
		copyBeanPropertiesToIndividual(bean, ind);
	}
	
	// Find the corresponding object, and copy the property values to it
	protected void updateObject(BeanType bean) 
			throws MetadataObjectNotFoundException, MetadataException {
		// Retrieve the individual from the persistent store
		OWLIndividual ind = getIndividualForId(bean.getId());
		// Copy the property values into the persistent store
		copyBeanPropertiesToIndividual(bean, ind);
	}
	
	// Find the corresponding object in the persistent store
	protected BeanType retrieveObject(Integer id) 
			throws MetadataObjectNotFoundException {
		OWLIndividual ind = getIndividualForId(id);
		return convertIndividualToBean(ind);
	}
	
	// Find the corresponding object in the persistent store, and delete it
	protected void deleteObject(Integer id) 
			throws MetadataObjectNotFoundException {
		OWLIndividual ind = getIndividualForId(id);
		ind.delete();
	}
	
	// =========================================================================
	// Additional retrieval
	
	protected Collection<BeanType> getAllObjects() {
		Collection<OWLIndividual> inds = getAllIndividuals();
		List<BeanType> result = new ArrayList<BeanType>();
		for (OWLIndividual ind: inds) {
			Integer id = convertNameToId(ind.getName());
			if (id != null) {
				BeanType bean = createNewBean(convertNameToId(ind.getName()));
				copyIndividualPropertiesToBean(ind, bean);
				result.add(bean);
			}
		}
		return result;
	}
	
	/**
	 * Given an individual of the appropriate type in the KB, construct the matching
	 * java bean.
	 */
	protected BeanType convertIndividualToBean(OWLIndividual ind) {
		BeanType bean = createNewBean(convertNameToId(ind.getName()));
		copyIndividualPropertiesToBean(ind, bean);
		return bean;
	}


	// =========================================================================
	// Object handling helpers
	
	// Create a fresh bean to hold the data (e.g. return new BeanType())
	protected BeanType createNewBean(Integer id) {
		try {
			Constructor<BeanType> con = beanType.getConstructor(Integer.class);
			return con.newInstance(id);
			//return (BeanType)beanType.newInstance(id);
		}
		catch (InstantiationException e) {
			String msg = "Misconfigured MetaKbDAO, beanType ("+
						 beanType.getName()+"does not match BeanType";
			throw new BPRuntimeException(msg, e);
		}
		catch (IllegalAccessException e) {
			String msg = "Constructor on bean class "+beanType.getName()+
						 " is not public";
			throw new BPRuntimeException(msg, e);
		}
		catch (InvocationTargetException e) {
			String msg = "Problem calling constructor on bean class "+
					     beanType.getName();
			throw new BPRuntimeException(msg, e);
		}
		catch (NoSuchMethodException e) {
			String msg = "Bean class "+beanType.getName()+
					" does not seem to have a custructor that takes an Integer";
			throw new BPRuntimeException(msg, e);
		}
	}
	
	// Retrieve an instance from the knowledge base
	protected OWLIndividual getIndividualForId(Integer id)
			throws MetadataObjectNotFoundException {
		String name = convertIdToName(id);
		OWLIndividual individual = metadataKb.getOWLIndividual(name);
		if (individual == null || individual.isDeleted() ||
				individual.isBeingDeleted()) {
			String msg = "Could not find "+qualifiedClassName+" instance (id="+
						 id+") in metadata knowledge base";
			throw new MetadataObjectNotFoundException(msg);
		}
		return individual;
	}
	
	private static boolean individualIsValid(OWLIndividual ind) {
		return !(ind.isBeingDeleted() || ind.isDeleted());
	}
	
	protected Collection<OWLIndividual> getAllIndividuals() {
		Collection<?> insts = kbClass.getInstances(false);
		List<OWLIndividual> result = new ArrayList<OWLIndividual>(insts.size());
		for (Object inst: insts) {
			OWLIndividual ind = (OWLIndividual)inst;
			if (individualIsValid(ind)) {
				result.add(ind);
			}
		}
		return result;
	}

	// Extract the id from an instance
	protected Integer getIdForIndividual(OWLIndividual inst) {
		return convertNameToId(inst.getName());
	}
	

	// =========================================================================
	// Name/ID conversion
	
	// Note: Returns null if there is a format error.
	protected static Integer convertNameToId(String name) {
		Integer id = null;
		int numStart = name.lastIndexOf('_') + 1;
		try {
			id = new Integer(name.substring(numStart));
		} catch (Exception e) {
			return null;
		}
		return id;
	}
	
	protected String convertIdToName(Integer id) {
		return instanceNamePrefix + id;
	}


	// =========================================================================
	// Value mapping helpers

	// Add a property map to this DAO
	protected void addMap(AbstractPropertyMap pMap) {
		pMap.initialize(beanType, daLayer);
		pMaps.add(pMap);
	}
	
	// Copy the property values from the BeanType object to the KB instance.
	protected void copyBeanPropertiesToIndividual(BeanType b, OWLIndividual ind) 
			throws MetadataException {
		for (AbstractPropertyMap propertyMap: pMaps) {
			propertyMap.copyValueToIndividual(b, ind);
		}
	}

	// Copy the property values from the KB instance to the BeanType object.
	protected void copyIndividualPropertiesToBean(OWLIndividual i, BeanType b) {
		for (AbstractPropertyMap propertyMap: pMaps) {
			propertyMap.copyValueToBean(i, b);
		}
	}
	
	
	// ============================================================
	// Accessors
	
	protected Class<BeanType> getBeanType() {
		return beanType;
	}
	
	protected AbstractDALayer getDaLayer() {
		return daLayer;
	}
	
	protected OWLNamedClass getKbClass() {
		return kbClass;
	}
	
	protected String getQualifiedClassName() {
		return qualifiedClassName;
	}
	
	protected OWLModel getMetadataKb() {
		return metadataKb;
	}
}
