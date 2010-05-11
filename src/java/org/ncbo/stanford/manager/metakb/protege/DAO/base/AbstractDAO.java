package org.ncbo.stanford.manager.metakb.protege.DAO.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.bean.TimedIdBean;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.query.QueryResults;


/**
 * Abstract version of a Data Access Object for a particular object, to move it between
 * Bean representation and Instance in the metadata kb.
 * <p>
 * The Bean plays the role of Transfer Object (in terms of the usual DAO pattern).  The 
 * metadata KB plays the role of the persistent store.
 * 
 * @author Tony Loeser
 */
public abstract class AbstractDAO<BeanType extends AbstractIdBean> {
	
	// This is set to be the same as BeanType. (Done in the constructor.)
	// Used for creating new beans.
	protected final Class<BeanType> beanType;
	
	// Property maps -- used to move property values to/from bean/owl
	protected List<PropertyMap> propMaps = new ArrayList<PropertyMap>();
	
	// Namespace prefixes from the metadata ontology
	protected static final String PREFIX_OMV = "OMV:";
	protected static final String PREFIX_METADATA = "metadata:"; 
	
	// Class name used to retrieve the kbClass and to name the instances
	// Should be set by the subclass via the constructor.
	protected final String qualifiedClassName;
	
	// Computed names
	static final String INSTANCE_NAMESPACE = "http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#";
	protected final String instanceNamePrefix;
	
	// Objects from the KB will be set in the constructor
	protected final OWLModel metadataKb;
	protected final OWLNamedClass kbClass;
	
	// =====================================
	// Members used when working with Timestamped beans
	
	// Standard slot names from the metadata ontology
	protected static final String PROP_NAME_DATE_CREATED = PREFIX_METADATA + "dateCreated";
	protected static final String PROP_NAME_DATE_MODIFIED = PREFIX_METADATA + "dateModified";
	
	// KB objects for timestamps initialized only if needed
	protected PropertyMap dateCreatedMap = null;
	protected PropertyMap dateModifiedMap = null;
	
	
	// =========================================================================
	// Initialization
	
	@SuppressWarnings("unchecked")
	protected AbstractDAO(String qualifiedClassName, OWLModel metadataKb) {
		this.beanType = (Class<BeanType>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		// Initialize String values
		this.qualifiedClassName = qualifiedClassName;
		int unqualifiedNameStart = qualifiedClassName.lastIndexOf(':') + 1;
		String unqualifiedClassName = qualifiedClassName.substring(unqualifiedNameStart);
		instanceNamePrefix = INSTANCE_NAMESPACE + unqualifiedClassName + "_";
		try {
			this.metadataKb = metadataKb;
			kbClass = metadataKb.getOWLNamedClass(qualifiedClassName);
		} catch(Exception e) {
			// XXX Do something better here.
			e.printStackTrace();
			throw new BPRuntimeException("Error encountered initializating metadata kb access layer.", e);
		}
		initializePropertyMaps();
	}
	
	// Subclass initializes the property mappings
	protected abstract void initializePropertyMaps();

	
	// =========================================================================
	// Public API
	
	// ========== CRUD Operations ==========
	
	/**
	 * Creates a new object in the metadata kb, and returns the corresponding java bean.  The
	 * bean's <code>id</code> attribute matches the new individual, and <code>dateCreated</code>
	 * if applicable.
	 *             
	 * @return The new bean corresponding to the object in the metadata kb.
	 * 
	 * @throws MetadataException when there is an error setting property values.
	 */
	public BeanType createObject() throws MetadataException {
		// Create the individual in the MetaKB
		OWLIndividual instance = kbClass.createOWLIndividual(null);
		// Extract the ID
		Integer id = convertNameToId(instance.getName());
		// Create the bean using the new id
		BeanType bean = newBean(id);
		// Handle timestamps, if appropriate
		maybeSetDateCreatedAndModified(instance, bean);		
		
		return bean;
	}
	
	/**
	 * Loads the object specified by <code>id</code> from the metadata kb.  
	 * 
	 * @return a fresh <code>BeanType</code> object with the parameter values copied from the corresponding
	 * instance in the kb.
	 * 
	 * @throws MetadataObjectNotFoundException when there is no instance with that <code>id</code>.
	 * 
	 * @throws Exception when there is a problem copying the property values.
	 */
	public BeanType retreiveObject(Integer id)
			throws MetadataObjectNotFoundException {
		OWLIndividual instance = getInstance(id);
		return convertIndividualToBean(instance);
	}
	
	/**
	 * Updates the object stored in the metadata kb to match <code>bean</code>.  Assumes that
	 * <code>bean</code> has been previously saved in the kb.
	 * 
	 * @throws MetadataObjectNotFoundException when the object has not been previously saved
	 * to the kb.
	 *         
	 * @throws MetadataException when there is a problem copying the values into the kb.
	 */
	public void updateObject(BeanType bean)
			throws MetadataObjectNotFoundException, MetadataException {
		// Retrieve the matching instance
		OWLIndividual instance = getInstance(bean.getId());
		// Set the properties -- no need for a save step
		copyBeanPropertiesToIndividual(bean, instance);
		maybeSetDateModified(instance, bean);
	}
	
	/**
	 * Removes the object from the metadata kb.
	 * 
	 * @param id Identifies which object is removed.
	 * 
	 * @throws MetadataObjectNotFoundException when there is no matching object in the metadata kb.
	 */
	public void deleteObject(Integer id) throws MetadataObjectNotFoundException {
		OWLIndividual instance = getInstance(id);
		instance.delete();
	}

	// ========== Other retrieval ==========
	
	/**
	 * Retrieve all instances of the associated class in the OWL KB.  (Actually,
	 * it retrieves all instances that follow the name/id conventions used in this
	 * DAO code.  We don't necessarily know what to do with any other random 
	 * instances that may be lying around in the KB, so they are ignored. 
	 */
	public List<BeanType> getAllObjects() {
		Collection<?> allInstances = kbClass.getInstances(false);
		List<BeanType> result = new ArrayList<BeanType>();
		for (Iterator<?> allInstIt = allInstances.iterator(); allInstIt.hasNext(); ) {
			OWLIndividual instance = (OWLIndividual)allInstIt.next();
			if (!instance.isBeingDeleted() && !instance.isDeleted()) {
				Integer id = convertNameToId(instance.getName());
				if (id != null) {
					BeanType bean = newBean(convertNameToId(instance.getName()));
					copyIndividualPropertiesToBean(instance, bean);
					result.add(bean);
				}
			}
		}
		return result;
	}
	
	/**
	 * Execute a SPARQL query and return the results that are instances of this 
	 * DAO's class in the KB.  The results are converted from individuals in the
	 * OWL KB to this DAO's corresponding java bean type.
	 * <ul>
	 *   <li>The results variable in the SPARQL query must be '?obj'.  This method
	 * will return all bindings of '?obj'.</li>
	 *   <li>Query results that are not OWL individuals will be ignored.</li>
	 *   <li>Query results that are OWL individuals but are not instances of this
	 * DAO's KB class are ignored.</li>
	 * </ul>
	 * 
	 * @throws MetadataException when something goes wrong running the SPARQL query
	 */
	public List<BeanType> getInstancesForSPARQLQuery(String query) throws MetadataException {
		QueryResults resultSets = null;
		try {
			resultSets = metadataKb.executeSPARQLQuery(query);
		} catch (Exception e) {
			throw new MetadataException("Exception encountered running SPARQL query", e);
		}
		List<BeanType> resultBeans = new ArrayList<BeanType>();
		for ( ; resultSets.hasNext(); ) {
			Map<?,?> resultSet = resultSets.next();
			Object resultObject = resultSet.get("obj");
			if (resultObject instanceof OWLIndividual) { // assume anything else is just chaff
				OWLIndividual instance = (OWLIndividual)resultObject;
				if (instance.hasProtegeType(kbClass)) {
					BeanType resultBean = newBean(convertNameToId(instance.getName()));
					copyIndividualPropertiesToBean(instance, resultBean);
					resultBeans.add(resultBean);
				}
			}
		}
		return resultBeans;
	}
	
	/**
	 * Given an individual of the appropriate type in the KB, construct the matching
	 * java bean.
	 */
	public BeanType convertIndividualToBean(OWLIndividual ind) {
		BeanType bean = newBean(convertNameToId(ind.getName()));
		copyIndividualPropertiesToBean(ind, bean);
		return bean;
	}


	// =========================================================================
	// Object handling helpers
	
	// Create a fresh bean to hold the data (e.g. return new BeanType())
	protected BeanType newBean(Integer id) {
		try {
			Constructor<BeanType> con = beanType.getConstructor(Integer.class);
			return con.newInstance(id);
			//return (BeanType)beanType.newInstance(id);
		}
		catch (InstantiationException e) {
			String msg = "Misconfigured MetaKbDAO, beanType ("+beanType.getName()+"does not match BeanType";
			throw new BPRuntimeException(msg, e);
		}
		catch (IllegalAccessException e) {
			String msg = "Constructor on bean class "+beanType.getName()+" is not public";
			throw new BPRuntimeException(msg, e);
		}
		catch (InvocationTargetException e) {
			String msg = "Problem calling constructor on bean class "+beanType.getName();
			throw new BPRuntimeException(msg, e);
		}
		catch (NoSuchMethodException e) {
			String msg = "Bean class "+beanType.getName()+" does not seem to have a custructor that takes an Integer";
			throw new BPRuntimeException(msg, e);
		}
	}
	
	// Retrieve an instance from the knowledge base
	protected OWLIndividual getInstance(Integer id) throws MetadataObjectNotFoundException {
		String name = convertIdToName(id);
		OWLIndividual individual = metadataKb.getOWLIndividual(name);
		if (individual == null || individual.isDeleted() || individual.isBeingDeleted()) {
			throw new MetadataObjectNotFoundException("Could not find "+qualifiedClassName+" instance (id="+id+") in metadata knowledge base");
		}
		return individual;
	}
	

	// =========================================================================
	// Name/ID conversion
	
	// 
	// Note: Returns null if there is a format error.
	private Integer convertNameToId(String name) {
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
	protected void addDatatypePropertyMap(String beanPropName,      // Name of the data member on the bean
										  Class<?> singleValueType, // Java type of a single property value
										  boolean isMultivalued,    // If true, bean data member type is
										  							//   Collection<singleValueType>
										  String owlPropName) {     // Name of the property object in the OWL KB
		PropertyMap pMap = new DatatypePropertyMap(beanPropName, beanType, singleValueType,
												   isMultivalued, owlPropName, metadataKb);
		propMaps.add(pMap);
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
	protected void addObjectPropertyMap(String beanPropName,
										boolean isMultivalued,
										String owlPropName,
										AbstractDAO<?> valueDAO) {
		PropertyMap pMap = new ObjectPropertyMap(beanPropName, beanType, owlPropName,
												 isMultivalued, metadataKb, valueDAO);
		propMaps.add(pMap);
	}
	
	// Copy the property values from the BeanType object to the metadata KB instance.
	// Does not copy the timestamps, as this operation represents a "create" or "update",
	// and timestamps need to be handled accordingly.
	protected void copyBeanPropertiesToIndividual(BeanType bean, OWLIndividual individual) throws MetadataException {
		for (Iterator<PropertyMap> pmIt = propMaps.iterator(); pmIt.hasNext(); ) {
			PropertyMap propertyMap = pmIt.next();
			propertyMap.copyValueToIndividual(bean, individual);
		}
	}

	// Copy the property values from the metadata KB instance to the BeanType object.
	// Copies timestamp values as well.
	protected void copyIndividualPropertiesToBean(OWLIndividual individual, BeanType bean) {
		for (Iterator<PropertyMap> pmIt = propMaps.iterator(); pmIt.hasNext(); ) {
			PropertyMap propertyMap = pmIt.next();
			propertyMap.copyValueToBean(individual, bean);
		}
		if (bean instanceof TimedIdBean) {
			copyTimestampsToBean(individual, (TimedIdBean)bean);
		}
	}

	
	// =========================================================================
	// Timestamp helpers

	// Call this method in the case when the bean is first created in the persistent
	// store. If appropriate, the timestamp properties will be updated.
	//
	// If the bean has dateCreated already, then we assume it was previously created
	// in a different persistent store, and we keep that timestamp value.
	//
	private void maybeSetDateCreatedAndModified(OWLIndividual inst, BeanType bean) throws MetadataException {
		if (bean instanceof TimedIdBean) {
			ensureTimestampMapInitialization();
			TimedIdBean tBean = (TimedIdBean)bean;
			Date now = new Date();
			if (tBean.getDateCreated() == null) {
				// It is a new object
				dateCreatedMap.setValueOnBoth(bean, inst, now);
				dateModifiedMap.setValueOnBoth(bean, inst, now);
			} else {
				// It was previously stored somewhere else
				dateCreatedMap.setOWLValue(inst, tBean.getDateCreated());
				if (tBean.getDateModified() == null) {
					tBean.setDateModified(now);
				}
				dateModifiedMap.setOWLValue(inst, tBean.getDateModified());
			}
		}
	}
	
	// Call this method in the case when the bean is being updated in the persistent
	// store.  If appropriate, the dateModified property will be updated.
	private void maybeSetDateModified(OWLIndividual inst, BeanType bean) throws MetadataException {
		if (bean instanceof TimedIdBean) {
			ensureTimestampMapInitialization();
			Date now = new Date();
			dateModifiedMap.setValueOnBoth(bean, inst, now);
		}
	}
	
	// Subclasses may need to use this method to set the timestamp properties when
	// retrieving a bean from the persistent store
	protected void copyTimestampsToBean(OWLIndividual inst, TimedIdBean tBean) {
		ensureTimestampMapInitialization();
		dateCreatedMap.copyValueToBean(inst, tBean);
		dateModifiedMap.copyValueToBean(inst, tBean);
	}
	
	private void ensureTimestampMapInitialization() {
		if (dateCreatedMap == null) {
			dateCreatedMap = new DatatypePropertyMap("dateCreated", beanType, Date.class, false, PROP_NAME_DATE_CREATED, metadataKb);
			dateModifiedMap  = new DatatypePropertyMap("dateModified", beanType, Date.class, false, PROP_NAME_DATE_MODIFIED, metadataKb);
		}
	}
	
	
	// ============================================================
	// Accessors
	
	Class<BeanType> getBeanType() {
		return beanType;
	}
}
