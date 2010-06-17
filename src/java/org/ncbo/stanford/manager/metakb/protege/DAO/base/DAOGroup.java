package org.ncbo.stanford.manager.metakb.protege.DAO.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.ncbo.stanford.exception.BPRuntimeException;

import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * Container for a group of DAO objects that are associated with a single instance of metadata KB.
 * For each type of DAO, the DAOGroup contains a single instance.
 * 
 * @author Tony Loeser
 */
public class DAOGroup {
	
	// The metadata KB is used for creating DAO objects
	private OWLModel metadataKb = null;
	
	// The various DAO instances held in this group.
	// Map is from DAO type to DAO instance
	private Map<Class<?>, AbstractDAO<?>> daoMap = new HashMap<Class<?>, AbstractDAO<?>>();
	
	// Object that will produce the metadata kb when we really need it
	private MetadataKbProvider kbProvider;
	
	/**
	 * Constructor with the metadata kb that will be used to construct the DAO objects.
	 */
	public DAOGroup(OWLModel metadataKb) {
		this.metadataKb = metadataKb;
	}
	
	/**
	 * Object that promises to give us the metadata kb when asked.
	 * 
	 * @author Tony Loeser
	 */
	public interface MetadataKbProvider {
		public OWLModel getMetadataKb();
	}
	
	
	/**
	 * Alternative constructor that doesn't require the metadata KB to be created before the
	 * DAOGroup.  Creating and loading the metadata kb takes some time.  All that's really 
	 * accomplished here is that the metadata kb loading happens just-in-time for DAO creation.
	 * This means we don't load the metadata KB eagerly every time we start the application --
	 * which can be a pain for testing.
	 * 
	 * @param kbProvider will provide the metadata kb.
	 */
	public DAOGroup(MetadataKbProvider kbProvider) {
		this.kbProvider = kbProvider;
	}
	
	// Obtain that metadata kb, whether we have to ask the provider for it or not
	private OWLModel getMetadataKb() {
		// Not a particularly fast operation because of synchro, but it doesn't get called often
		synchronized(this) {
			if (metadataKb == null) {
				if (kbProvider == null) {
					throw new BPRuntimeException("DAOGroup has neither KB nor provider.");
				} else {
					metadataKb = kbProvider.getMetadataKb();
				}
			}
			return metadataKb;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <DAOType extends AbstractDAO<?>> DAOType
			  getDAO(Class<DAOType> daoType) {
		DAOType theDAO = (DAOType)daoMap.get(daoType);
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
		synchronized(daoMap) {
			theDAO = (DAOType)daoMap.get(daoType);
			if (theDAO == null) {
				theDAO = (DAOType)instantiateDAO(daoType);
				daoMap.put(daoType, theDAO);
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
				theDAO.initialize(getMetadataKb(), this);
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
}
