package org.ncbo.stanford.manager.metakb.protege.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * XXX
 * -- This is the main layer of translation.  Used to translate from Java to KB
 *    and vice-versa.
 * -- Stores a collection of DAO objects; one for each object that gets mapped
 * -- DAO objects can be retrieved using the type of the object, on either 
 *    side.  Either the Java bean, or the name of the direct class in the KB.
 * 
 * Container for a group of DAO objects that are associated with a single
 * instance of metadata KB. For each type of DAO, the DAOGroup contains a
 * single instance.
 * 
 * ... subclass this object and put all your DAOs in it.
 * 
 * @author Tony Loeser
 */
public abstract class AbstractDALayer {
	
	// The metadata KB is used for creating DAO objects
	private OWLModel metadataKb = null;
	
	// Object that will produce the metadata kb when we really need it
	private MetadataKbProvider kbProvider = null;
	
	// The various DAO instances held in this group.
	// Map is from DAO type to DAO instance
	private Map<Class<?>, AbstractDAO<?>> daoTypeDAOMap = new HashMap<Class<?>, AbstractDAO<?>>();
	
	private Map<Class<?>, AbstractDAO<?>> beanTypeDAOMap = new HashMap<Class<?>, AbstractDAO<?>>();
	
	private Map<String, AbstractDAO<?>> kbClassDAOMap = new HashMap<String, AbstractDAO<?>>();
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
	 * be a pain for testing.
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
	public OWLModel getMetadataKb() {
		if (metadataKb == null) {
			synchronized(this) {
				if (metadataKb == null) {
					if (kbProvider == null) {
						throw new BPRuntimeException("DAOGroup has neither KB nor provider.");
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
		String kbClassName = newDAO.getQualifiedClassName();
		kbClassDAOMap.put(kbClassName, newDAO);
		Class<?> daoType = newDAO.getClass();
		daoTypeDAOMap.put(daoType, newDAO);
	}
	
	private void ensureKbClassMapInitialized() {
		if (!kbClassDAOMapIsInitialized) {
			synchronized(this) {
				if (!kbClassDAOMapIsInitialized) {
					for (AbstractDAO<?> dao : beanTypeDAOMap.values()) {
						 
					}
				}
			}
		}
	}
	
	// =========================================================================
	// DAO Retrieval
	
	@SuppressWarnings("unchecked")
	public <BeanType extends MetadataBean>
			AbstractDAO<BeanType> getDAOForBeanType(Class<BeanType> beanType) {
		AbstractDAO<BeanType> dao = (AbstractDAO<BeanType>)beanTypeDAOMap.get(beanType);
		dao.ensureInitialization();
		return dao;
	}
	
	@SuppressWarnings("unchecked")
	public <BeanType extends MetadataBean>
			AbstractDAO<BeanType> getDAOForBean(BeanType bean) {
		Class<BeanType> beanType = (Class<BeanType>)bean.getClass();
		return getDAOForBeanType(beanType);
	}
	
	public AbstractDAO<?> getDAOForKbClass(String kbClassName) {
		AbstractDAO<?> dao = kbClassDAOMap.get(kbClassName);
		dao.ensureInitialization();
		return dao;
	}
	
	@SuppressWarnings("unchecked")
	public <DAOType extends AbstractDAO<?>>
			DAOType getDAOByType(Class<DAOType> daoType) {
		DAOType dao = (DAOType)daoTypeDAOMap.get(daoType);
		dao.ensureInitialization();
		return dao;
	}
	
	@SuppressWarnings("unchecked")
	public <DAOType extends AbstractDAO<?>> DAOType
			  getDAO(Class<DAOType> daoType) {
		DAOType theDAO = (DAOType)daoTypeDAOMap.get(daoType);
		if (theDAO == null) {
			theDAO = getOrCreateDAO(daoType);
		}
		return theDAO;
	}
	
	// Retrieve a DAO from the map, or create one if it is missing.
	@SuppressWarnings("unchecked")
	private <DAOType extends AbstractDAO<?>> DAOType
			  getOrCreateDAO(Class<DAOType> daoType) {
		DAOType theDAO;
		synchronized(daoTypeDAOMap) {
			theDAO = (DAOType)daoTypeDAOMap.get(daoType);
			if (theDAO == null) {
				theDAO = (DAOType)instantiateDAO(daoType);
				daoTypeDAOMap.put(daoType, theDAO);
			}
		}
		return theDAO;
	}
	
	// Create a new DAO, using the single-argument (OWLModel) constructor
	private AbstractDAO<?> instantiateDAO(Class<?> daoType) {
		if (AbstractDAO.class.isAssignableFrom(daoType)) {
			try {
				Constructor<?> con = daoType.getConstructor();
				AbstractDAO<?> theDAO = (AbstractDAO<?>)con.newInstance();
				theDAO.ensureInitialization();
				return theDAO;
			} catch (SecurityException e) {
				throw new BPRuntimeException("Reflection error instantiating an AbstractDAO", e);
			} catch (IllegalArgumentException e) {
				throw new BPRuntimeException("Reflection error instantiating an AbstractDAO", e);
			} catch (NoSuchMethodException e) {
				throw new BPRuntimeException("Reflection error instantiating an AbstractDAO", e);
			} catch (InstantiationException e) {
				throw new BPRuntimeException("Reflection error instantiating an AbstractDAO", e);
			} catch (IllegalAccessException e) {
				throw new BPRuntimeException("Reflection error instantiating an AbstractDAO", e);
			} catch (InvocationTargetException e) {
				throw new BPRuntimeException("Reflection error instantiating an AbstractDAO", e);
			}
		} else {
			throw new BPRuntimeException("This should have been an AbstractDAO subtype: " +
										 daoType.getName());
		}
	}

	
	// =========================================================================
	// Object Access
	
	public MetadataBean convertIndividualToBean(OWLIndividual ind) {
		String kbClassName = AbstractDAO.extractClassNameFromIndividualName(ind.getName());
		AbstractDAO<?> dao = getDAOForKbClass(kbClassName);
		MetadataBean bean = dao.convertIndividualToBean(ind);
		return bean;
	}

	public Integer getIdFromIndividual(OWLIndividual ind) {
		return AbstractDAO.extractIdFromIndividualName(ind.getName());
	}

	public OWLIndividual retrieveIndividualForBean(MetadataBean bean) 
			throws MetadataObjectNotFoundException {
		AbstractDAO<?> dao = getDAOForBean(bean);
		return dao.getIndividualForId(bean.getId());
	}

	public OWLIndividual retrieveIndividualForId(String className, Integer id) 
			throws MetadataObjectNotFoundException {
		AbstractDAO<?> dao = getDAOForKbClass(className);
		return dao.getIndividualForId(id);
	}

	
	// =========================================================================
	// Object Access
	
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
		dao.saveObject(bean);
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
	 * @throws MetadataObjectNotFoundException when there is no matching object in the metadata kb.
	 */
	public <BeanType extends MetadataBean>
			void deleteObject(Class<BeanType> beanType, Integer id)
			throws MetadataObjectNotFoundException {
		AbstractDAO<BeanType> dao = getDAOForBeanType(beanType);
		OWLIndividual instance = dao.getIndividualForId(id);
		instance.delete();
	}

	// ========== Other retrieval ==========
	
	/**
	 * Retrieve all instances of the associated class in the OWL KB.  (Actually,
	 * it retrieves all instances that follow the name/id conventions used in this
	 * DAL code.  We don't necessarily know what to do with any other random 
	 * instances that may be lying around in the KB, so they are ignored. 
	 */
	public <BeanType extends MetadataBean>
			List<BeanType> getAllObjects(Class<BeanType> beanType) {
		AbstractDAO<BeanType> dao = getDAOForBeanType(beanType);
		return dao.getAllObjects();
	}

}
