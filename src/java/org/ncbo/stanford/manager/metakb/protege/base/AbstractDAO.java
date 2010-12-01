package org.ncbo.stanford.manager.metakb.protege.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDALayer;
import org.ncbo.stanford.manager.metakb.protege.base.PropertyMapSet;

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
 * <p>
 * Implementing classes must provide a no-argument constructor that calls this classes
 * constructor and sets the (kb) class name.
 * 
 * @author Tony Loeser
 */
public abstract class AbstractDAO<BeanType extends MetadataBean> {
	
	// This is set to be the same as BeanType. (Done in the constructor.)
	// Used for creating new beans.
	protected final Class<BeanType> beanType;
	
	// Property maps -- used to move property values to/from bean/owl
	private PropertyMapSet propMapSet = null;
	
	// Namespace prefixes from the metadata ontology
	protected static final String PREFIX_OMV = "OMV:";
	protected static final String PREFIX_METADATA = "metadata:";
	protected static final String PREFIX_METRICS = "metrics:";
	
	// Given names
	protected final String qualifiedClassName;
	
	// Computed names
	protected static final String INSTANCE_NAMESPACE = "http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#";
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
	// Second, in initialize, the KB is set up and objects from the KB are 
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
	
	/**
	 * 
	 */
	public void ensureInitialization() {
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
				propMapSet = new PropertyMapSet(beanType, daLayer);
				initializePropertyMaps(propMapSet);
			}
		}
	}
		
	protected static boolean COLLECTION = true;
	protected static boolean SINGLE_VALUE = false;
	
	// Subclasses must implement this with calls to the PropertyMapSet
	abstract protected void initializePropertyMaps(PropertyMapSet maps);
	

	// =========================================================================
	// Public API
	
	// ========== CRUD Operations ==========
	
	/**
	 * Saves the bean to the knowledge base (persistent store).  Update the 
	 * corresponding Individual in the KB if there is one, otherwise create
	 * a corresponding Individual.  Combines the Create and Update in CRUD.
	 * 
	 * @param bean The bean to be saved to persistent store.
	 * 
	 * @throws MetadataObjectNotFoundException when the bean is not new, but the
	 * 		corresponding Individual cannot be found in the KB.
	 * @throws MetadataException when there is an error transferring the 
	 * 		property values to the Individual.
	 */
	public void saveObject(BeanType bean)
			throws MetadataObjectNotFoundException, MetadataException {
		OWLIndividual ind; // The corresponding object in the kb.
		if (bean.getId() == null) {
			// Object does not exist in kb.  Start by creating it.
			ind = kbClass.createOWLIndividual(null);
			// Update the bean with the id from the persistent store
			bean.setId(extractIdFromIndividualName(ind.getName()));
		} else {
			// Object exists in kb; retrieve it by id.
			ind = getIndividualForId(bean.getId());
		}
		// Copy property values from java bean to kb individual
		copyBeanPropertiesToIndividual(bean, ind);
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
	public BeanType retrieveObject(Integer id)
			throws MetadataObjectNotFoundException {
		OWLIndividual instance = getIndividualForId(id);
		return convertIndividualToBean(instance);
	}
	
	/**
	 * Removes the object from the metadata kb.
	 * 
	 * @param id Identifies which object is removed.
	 * 
	 * @throws MetadataObjectNotFoundException when there is no matching object in the metadata kb.
	 */
	public void deleteObject(Integer id) throws MetadataObjectNotFoundException {
		OWLIndividual instance = getIndividualForId(id);
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
				Integer id = extractIdFromIndividualName(instance.getName());
				if (id != null) {
					BeanType bean = newBean(extractIdFromIndividualName(instance.getName()));
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
					BeanType resultBean = newBean(extractIdFromIndividualName(instance.getName()));
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
		BeanType bean = newBean(extractIdFromIndividualName(ind.getName()));
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
	public OWLIndividual getIndividualForId(Integer id) throws MetadataObjectNotFoundException {
		String name = convertIdToName(id);
		OWLIndividual individual = metadataKb.getOWLIndividual(name);
		if (individual == null || individual.isDeleted() || individual.isBeingDeleted()) {
			String msg = "Could not find "+qualifiedClassName+" instance (id="+id+
			  			 ") in metadata knowledge base";
			throw new MetadataObjectNotFoundException(msg);
		}
		return individual;
	}

	// Extract the id from an instance
	protected Integer getIdForIndividual(OWLIndividual inst) {
		return extractIdFromIndividualName(inst.getName());
	}
	

	// =========================================================================
	// Access to related DAO objects
	
	public <DAOType extends AbstractDAO<?>> DAOType
	  getSiblingDAO(Class<DAOType> daoType) {
		return daLayer.getDAO(daoType);
	}


	// =========================================================================
	// Name/ID conversion
	
	// 
	// Note: Returns null if there is a format error.
	public static Integer extractIdFromIndividualName(String name) {
		Integer id = null;
		int numStart = name.lastIndexOf('_') + 1;
		try {
			id = new Integer(name.substring(numStart));
		} catch (Exception e) {
			return null;
		}
		return id;
	}
	
	public static String extractClassNameFromIndividualName(String name) {
		int nameEnd = name.lastIndexOf('_');
		return name.substring(0, nameEnd);
	}
	
	protected String convertIdToName(Integer id) {
		return instanceNamePrefix + id;
	}


	// =========================================================================
	// Value mapping helpers

	
	// Copy the property values from the BeanType object to the metadata KB instance.
	protected void copyBeanPropertiesToIndividual(BeanType bean, OWLIndividual individual) throws MetadataException {
		propMapSet.copyBeanPropertiesToIndividual(bean, individual);
	}

	// Copy the property values from the metadata KB instance to the BeanType object.
	protected void copyIndividualPropertiesToBean(OWLIndividual individual, BeanType bean) {
		propMapSet.copyIndividualPropertiesToBean(individual, bean);
	}

	
	
	// ============================================================
	// Accessors
	
	public Class<BeanType> getBeanType() {
		return beanType;
	}
	
	public AbstractDALayer getDaLayer() {
		return daLayer;
	}
	
	public OWLNamedClass getKbClass() {
		return kbClass;
	}
	
	public String getQualifiedClassName() {
		return qualifiedClassName;
	}
	
	public OWLModel getMetadataKb() {
		return metadataKb;
	}
}
