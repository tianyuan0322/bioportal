package org.ncbo.stanford.manager.metakb;

import java.util.Collection;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;

/**
 * Persistence manager for simple java bean objects.  Includes the basic 
 * CRUD functions.
 * 
 * @author Tony Loeser
 *
 * @param <BeanType> Is the java bean representation of the type of object being
 *     managed.
 */
public interface SimpleObjectManager<BeanType extends MetadataBean> {

	/**
	 * Save the object to the persistent store, either creating it fresh or
	 * updating the property values if the object already exists in the store.
	 * Covers both the Create and Update operations in CRUD.
	 * 
	 * @param bean the java bean representation of the object to be saved
	 * @throws MetadataObjectNotFoundException when the object is not new, but
	 *     the corresponding object is not present in the persistent store.
	 * @throws MetadataException when there is a problem copying the values from
	 *     the java bean to the persistent store.
	 */
	public void saveObject(BeanType bean)
			throws MetadataObjectNotFoundException, MetadataException;
	
	/**
	 * Retrieves an object, uniquely identified by its id.
	 * 
	 * @param id the unique id of the object to retrieve.
	 * @throws MetadataObjectNotFoundException when there is no such object
	 *    (with the indicated id) in the persistent store.
	 */
	public BeanType retrieveObject(Integer id) 
			throws MetadataObjectNotFoundException;
	
	/**
	 * Retrieves a Collection of all objects of type corresponding to
	 * <code>BeanType</code> found in the persistent store.
	 */
	public Collection<BeanType> retrieveAllObjects();
	
	/**
	 * Remove the corresponding object from the persistent store.
	 * 
	 * @param id the unique id of the object to be deleted.
	 * @throws MetadataObjectNotFoundException when the corresponding object is
	 *     not present in the persistent store.
	 */
	public void deleteObject(Integer id) throws MetadataObjectNotFoundException;
}
