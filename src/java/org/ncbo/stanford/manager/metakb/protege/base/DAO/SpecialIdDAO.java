package org.ncbo.stanford.manager.metakb.protege.base.DAO;

import java.util.Collection;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDALayer;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDAO;
import org.ncbo.stanford.util.protege.PropertyUtils;

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * Modification of {@link AbstractDAO} that provides support for a legacy method of assigning IDs
 * to new objects in the KB.
 * <p>
 * This class is provided for backwards compatibility only; new DAO classes should inherit
 * from {@link AbstractDAO} directly and use its more robust approach to object creation and
 * ID assignment.
 * <p>
 * XXX TODO Discuss non-atomic aspects, inefficiencies.
 * <p>
 * XXX TODO Discuss how the bean has to have an Integer Id attribute, and this class takes
 * care of setting the redundant ID.
 * <p>
 * Implementing classes must provide a public, zero-argument constructor that calls this classes 
 * constructor.
 * 
 * @author Tony Loeser
 *
 * @param <BeanType>
 */
public abstract class SpecialIdDAO<BeanType extends MetadataBean>
		extends AbstractDAO<BeanType> {
	
	public static final Integer INVALID_ID = -1;
	
	private final Integer startId;
	
	private final String localInstanceNamePrefix;
	
	// In cases where there is a redundant ID attribute in the KB, keep it in sync.
	// Actually, this doesn't take much work.  The ID will never change on the
	// Java side... so all we have to do on the KB side is set the ID properly
	// when the object is created. 
	//
	// Optionally, we could also check that the two KB IDs are in sync when any 
	// object is returned.  I think we can skip that for now.
	private static final String PROPERTY_ID = PREFIX_METADATA + "id";
	private final boolean hasIdProperty;
	private OWLDatatypeProperty idProperty = null;
	
	/**
	 * Constructor sets the parameters for the type of DAO.  XXX reword.
	 * 
	 * @param qualifiedClassName
	 * @param localInstanceNamePrefix
	 * @param startId
	 * @param hasIdProperty
	 */
	protected SpecialIdDAO(AbstractDALayer daLayer,
						   String qualifiedClassName,
						   String localInstanceNamePrefix,
						   Integer startId,
						   boolean hasIdProperty) {
		super(daLayer, qualifiedClassName);
		this.localInstanceNamePrefix = localInstanceNamePrefix;
		// Override the instance name to use an intentional prefix rather than the KB store's
		// default.
		instanceNamePrefix = INSTANCE_NAMESPACE + localInstanceNamePrefix;
		this.hasIdProperty = hasIdProperty;
		this.startId = startId;
	}
	
	
	@Override
	protected void initialize() {
		super.initialize();
		if (hasIdProperty) {
			idProperty = (OWLDatatypeProperty)getMetadataKb().getOWLProperty(PROPERTY_ID);
		}
	}
	
	
	// =========================================================================
	// ID Assignment support
	
	/**
	 * @return
	 */
	public Integer getNextAvailableId() {
		try {
			Collection<?> instances = getKbClass().getInstances(false);
			int max = startId;
			for (Object inst : instances) {
				if (inst instanceof RDFResource) {
					int id = AbstractDAO.convertNameToId(((RDFResource)inst).getName()).intValue();
					if (id > max) {
						max = id;
					}
				}
			}
			Integer newId = new Integer(max+1);
			while (findIndividualWithId(newId) != null) {
				newId = ((newId + 1500) / 1000) * 1000;
			}
			return newId;
		} catch (Exception e) {
			e.printStackTrace();
			return INVALID_ID;
		}
	}
	
	// Return the corresponding individual if it exists in the metdataKB, otherwise return null
	private OWLIndividual findIndividualWithId(Integer id) {
		try {
			return getIndividualForId(id);
		} catch (MetadataObjectNotFoundException e) {
			return null;
		}
	}
	
	// If we are keeping the redundant ID property, set it to be in sync
	// with the primary ID property.
	private void maybeSetIdProperty(OWLIndividual ind, BeanType bean) {
		if (hasIdProperty) {
			PropertyUtils.setPropertyValue(ind, idProperty, bean.getId());
		}
	}
	

	// =========================================================================
	// Save/Update support
	//
	// SpecialIdDAO does this differently from AbstractDAO.  (See class javadocs.)
	// First knock out the existing methods, then provide an alternative.
	
	@Override
	protected void createObject(BeanType bean) 
			throws MetadataException {
		// Creation goes a little different, for this class.  We compute the
		// ID first, set it, and then create the object in the persistent store.
		// Yes, it is incorrect code, if only because it is not thread safe. 
		// But it is necessary for the backwards compatibility.
		bean.setId(getNextAvailableId());
		// Now create the object in the persistent store
		OWLIndividual ind = getKbClass().createOWLIndividual(localInstanceNamePrefix+bean.getId());
		maybeSetIdProperty(ind, bean);
		// Copy the property values into the persistent store
		copyBeanPropertiesToIndividual(bean, ind);
	}
}
