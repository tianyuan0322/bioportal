package org.ncbo.stanford.manager.metakb;

import java.util.Collection;

import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;

/**
 * Persistence manager for simple java bean objects.  Includes the basic 
 * CRUD functions.
 * 
 * @author Tony Loeser
 *
 * @param <BeanType> Is the java bean representation of the type of object being managed.
 */
public interface SimpleObjectManager<BeanType extends AbstractIdBean> {

	/**
	 * Creates a new instance of the object in the persistent store, and returns
	 * the corresponding, fresh java bean.
	 * 
	 * @return a bean representation of the new object
	 * @throws MetadataException when there is a problem creating the object in the store
	 */
	public BeanType createObject() throws MetadataException;
	
	/**
	 * Retrieves an object, uniquely identified by its id.
	 * 
	 * @param id the unique id of the object to retrieve.
	 * @throws MetadataObjectNotFoundException when there is no such object (with the
	 *    indicated id) in the persistent store.
	 */
	public BeanType retrieveObject(Integer id) throws MetadataObjectNotFoundException;
	
	/**
	 * Retrieves a Collection of all objects of type corresponding to <code>BeanType</code>
	 * found in the persistent store.
	 */
	public Collection<BeanType> retrieveAllObjects();
	
	/**
	 * Update the values in the object, to match those in the java bean.  The object
	 * is located in the persistent store using the bean's id value, and then all values
	 * are copied from the bean to the stored version of the object.
	 * 
	 * @param bean the java bean representation of the object to be updated
	 * @throws MetadataObjectNotFoundException when the corresponding object is not present
	 *     in the persistent store.
	 * @throws MetadataException when there is a problem copying the values from the java
	 *     bean to the persistent store.
	 */
	public void updateObject(BeanType bean) throws MetadataObjectNotFoundException, MetadataException;
	
	/**
	 * Remove the corresponding object from the persistent store.
	 * 
	 * @param id the unique id of the object to be deleted.
	 * @throws MetadataObjectNotFoundException when the corresponding object is not present
	 *     in the persistent store.
	 */
	public void deleteObject(Integer id) throws MetadataObjectNotFoundException;
}
