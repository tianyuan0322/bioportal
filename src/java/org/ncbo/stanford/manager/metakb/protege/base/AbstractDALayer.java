package org.ncbo.stanford.manager.metakb.protege.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.query.QueryResults;

/**
 * Defines the main API for the Bean/Knowledgebase layer.  This Data Access
 * Layer (DAL) class acts as a container for the individual Data Access Object
 * instances (see {@link AbstracctDAO}), and provides generic CRUD methods
 * and other retrieval methods.  Use an instance of this class to translate
 * from Java Bean to KB Individual and vice-versa.
 * <p>
 * DAO instances (one for each type of object to be translated) are organized
 * here and accessed as needed; client code does not deal with specific DAOs.
 * DAOs are accessed by KB Class name (for translating from KB Individuals to
 * Java Beans), or by bean type (for translating from Beans to Individuals).
 * <p>
 * To build a specific DAL for an application, make a subclass of
 * AbstractDALayer, and define the method {@link AbstractDALayer#createDAOs} to
 * add all the specific DAO instances via {@link AbstractDALayer#addDAO}.
 * 
 * @author Tony Loeser
 */
public abstract class AbstractDALayer {
	
	// The metadata KB is used for creating DAO objects, and for some
	// direct retrieval operations.
	private OWLModel metadataKb = null;
	
	// Object that will produce the metadata kb when we really need it
	private MetadataKbProvider kbProvider = null;
	
	// Each DAO instance is stored as a value in both of these maps.
	private Map<Class<?>, AbstractDAO<?>> beanTypeDAOMap =
		new HashMap<Class<?>, AbstractDAO<?>>();
	private Map<String, AbstractDAO<?>> kbClassDAOMap =
		new HashMap<String, AbstractDAO<?>>();
	private boolean kbClassDAOMapIsInitialized = false;

	
	/**
	 * Constructor with the metadata kb that will be used to construct the DAO
	 * objects.
	 */
	public AbstractDALayer(OWLModel metadataKb) {
		this.metadataKb = metadataKb;
		createDAOs();
	}
	
	/**
	 * Object that promises to give us the metadata kb when asked.  Use with 
	 * the alternative constructor if desired.
	 */
	public interface MetadataKbProvider {
		public OWLModel getMetadataKb();
	}
	
	/**
	 * Alternative constructor that doesn't require the metadata KB to be
	 * created before the DAOGroup.  Creating and loading the metadata kb takes
	 * some time.  All that's really accomplished here is that the metadata kb
	 * loading happens just-in-time for DAO creation. This means we don't load
	 * the metadata KB eagerly every time we start the application -- which can
	 * be a pain for testing of unrelated functionality.
	 * 
	 * @param kbProvider will provide the metadata kb.
	 */
	public AbstractDALayer(MetadataKbProvider kbProvider) {
		this.kbProvider = kbProvider;
		createDAOs();
	}
	
	/**
	 * Return the KB that acts as the persistent store for this data access
	 * layer.
	 */
	protected OWLModel getMetadataKb() {
		if (metadataKb == null) {
			synchronized(this) {
				if (metadataKb == null) {
					if (kbProvider == null) {
						String msg = "DAOGroup has neither KB nor provider.";
						throw new BPRuntimeException(msg);
					} else {
						metadataKb = kbProvider.getMetadataKb();
					}
				}
			}
		}
		return metadataKb;
	}
	
	// =========================================================================
	// Initialization
	
	abstract protected void createDAOs();
	
	protected void addDAO(AbstractDAO<?> newDAO) {
		Class<?> beanType = newDAO.getBeanType();
		beanTypeDAOMap.put(beanType, newDAO);
	}
	
	private void ensureKbClassMapInitialized() {
		if (!kbClassDAOMapIsInitialized) {
			synchronized(this) {
				if (!kbClassDAOMapIsInitialized) {
					for (AbstractDAO<?> dao : beanTypeDAOMap.values()) {
						dao.ensureInitialization();
						kbClassDAOMap.put(dao.getKbClass().getName(), dao);
					}
					kbClassDAOMapIsInitialized = true;
				}
			}
		}
	}


	// =========================================================================
	// DAO Retrieval
	
	@SuppressWarnings("unchecked")
	private <BeanType extends MetadataBean>
			AbstractDAO<BeanType> getDAOForBean(BeanType bean) {
		Class<BeanType> beanType = (Class<BeanType>)bean.getClass();
		return getDAOForBeanType(beanType);
	}
	
	@SuppressWarnings("unchecked")
	private <BeanType extends MetadataBean>
			AbstractDAO<BeanType> getDAOForBeanType(Class<BeanType> beanType) {
		AbstractDAO<BeanType> dao = (AbstractDAO<BeanType>)
				beanTypeDAOMap.get(beanType);
		dao.ensureInitialization();
		return dao;
	}
	
	private AbstractDAO<?> getDAOForIndividual(OWLIndividual ind) {
		// Get the name of the direct type class
		String kbClassName = ind.getProtegeType().getName();
		return getDAOForKbClass(kbClassName);
	}
	
	private AbstractDAO<?> getDAOForKbClass(String kbClassName) {
		ensureKbClassMapInitialized();
		AbstractDAO<?> dao = kbClassDAOMap.get(kbClassName);
		dao.ensureInitialization();
		return dao;
	}
	
	
	// =========================================================================
	// Object access helpers
	
	// General object conversion, used to convert object property values
	protected MetadataBean convertIndividualToBean(OWLIndividual ind) {
		AbstractDAO<?> dao = getDAOForIndividual(ind);
		MetadataBean bean = dao.convertIndividualToBean(ind);
		return bean;
	}

	// General object conversion, used to convert object property values
	protected OWLIndividual retrieveIndividualForBean(MetadataBean bean) 
			throws MetadataObjectNotFoundException {
		AbstractDAO<?> dao = getDAOForBean(bean);
		return dao.getIndividualForId(bean.getId());
	}

	// Returns the Individual, given the fully qualified and expanded name of
	// the object's direct type class in the KB.
	protected OWLIndividual retrieveIndividualForId(String typeName, Integer id) 
			throws MetadataObjectNotFoundException {
		AbstractDAO<?> dao = getDAOForKbClass(typeName);
		return dao.getIndividualForId(id);
	}

	
	// =========================================================================
	// Object API
	
	// ========== CRUD methods ==========
	
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
	public <BeanType extends MetadataBean>
			void saveObject(BeanType bean)
			throws MetadataObjectNotFoundException, MetadataException {
		AbstractDAO<BeanType> dao = getDAOForBean(bean);
		if (bean.getId() == null) {
			// Object does not yet exist in kb.
			dao.createObject(bean);
		} else {
			// Object exists in kb.
			dao.updateObject(bean);
		}
	}
	
	/**
	 * Loads the object specified by <code>id</code> from the metadata kb.  
	 * 
	 * @return a fresh <code>BeanType</code> object with the parameter values
	 *         copied from the corresponding instance in the kb.
	 * 
	 * @throws MetadataObjectNotFoundException when there is no instance with
	 *         that <code>id</code>.
	 * 
	 * @throws Exception when there is a problem copying the property values.
	 */
	public <BeanType extends MetadataBean>
			BeanType retrieveObject(Class<BeanType> beanType, Integer id)
			throws MetadataObjectNotFoundException {
		AbstractDAO<BeanType> dao = getDAOForBeanType(beanType);
		return dao.retrieveObject(id);
	}
	
	/**
	 * Removes the object from the metadata kb.
	 * 
	 * @param id Identifies which object is removed.
	 * 
	 * @throws MetadataObjectNotFoundException when there is no matching object 
	 *         in the metadata kb.
	 */
	public <BeanType extends MetadataBean>
			void deleteObject(Class<BeanType> beanType, Integer id)
			throws MetadataObjectNotFoundException {
		AbstractDAO<BeanType> dao = getDAOForBeanType(beanType);
		dao.deleteObject(id);
	}

	// ========== Other retrieval ==========
	
	/**
	 * Retrieve all instances of the associated class in the OWL KB.  (Actually,
	 * it retrieves all instances that follow the name/id conventions used in
	 * this DAL code.  We don't necessarily know what to do with any other
	 * random instances that may be lying around in the KB, so they are ignored. 
	 */
	public <BeanType extends MetadataBean>
			Collection<BeanType> getAllObjects(Class<BeanType> beanType) {
		AbstractDAO<BeanType> dao = getDAOForBeanType(beanType);
		return dao.getAllObjects();
	}

	/**
	 * Execute a SPARQL query and return the results that convert to instances
	 * of the indicated Bean type.
	 * <ul>
	 *   <li>The results variable in the SPARQL query must be '?obj'.  This 
	 * method will return all bindings of '?obj'.</li>
	 *   <li>Query results that are not OWLIndividuals will be ignored.</li>
	 *   <li>Query results that are OWLIndividuals but are not instances of the 
	 * indicated Bean type are ignored.</li>
	 * </ul>
	 * 
	 * @throws MetadataException when something goes wrong running the query
	 */
	@SuppressWarnings("unchecked")
	public <BeanType extends MetadataBean>
			Collection<BeanType> 
			getObjectsForSPARQLQuery(Class<BeanType> beanType, String query) 
			throws MetadataException {
		// This is the class in the KB that we use to filter which individuals
		// we convert and which ones we ignore.
		OWLNamedClass filterClass = getDAOForBeanType(beanType).getKbClass();
		QueryResults resultSets = null;
		try {
			resultSets = metadataKb.executeSPARQLQuery(query);
		} catch (Exception e) {
			String msg = "Exception encountered running SPARQL query";
			throw new MetadataException(msg, e);
		}
		// Result accumulator
		List<BeanType> resultBeans = new ArrayList<BeanType>();
		// Iterate through the query results, converting the matching objects.
		for ( ; resultSets.hasNext(); ) {
			Map<?,?> resultSet = resultSets.next();
			Object resultObject = resultSet.get("obj");
			// We ignore anything that isn't an individual
			if (resultObject instanceof OWLIndividual) {
				OWLIndividual instance = (OWLIndividual)resultObject;
				// Before doing any (possibly expensive) conversion from OWL
				// Individual to Java bean, check that the individual has the
				// proper type in the KB.
				if (instance.hasProtegeType(filterClass)) {
					MetadataBean resultBean = convertIndividualToBean(instance);
					// At this point, the bean had better have the correct 
					// type (we checked the type on the OWL side).  Double check
					// all the same, but we shouldn't reach the exception.
					if (beanType.isInstance(resultBean)) {
						resultBeans.add((BeanType)resultBean);
					} else {
						String msg = "Conversion from Individual to Bean does"+
									 " not make sense.  Type on OWL side"+
									 "matched "+filterClass.getLocalName()+", "+
									 "but Java Bean didn't match "+
									 beanType.getName()+". Java Bean type is "+
									 resultBean.getClass().getName()+".";
						throw new BPRuntimeException(msg);
					}
				}
			}
		}
		return resultBeans;
	}
}
