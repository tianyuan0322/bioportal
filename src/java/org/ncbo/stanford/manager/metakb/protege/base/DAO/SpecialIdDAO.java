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
					int id = AbstractDAO.extractIdFromIndividualName(((RDFResource)inst).getName()).intValue();
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
	

	// =========================================================================
	// Save/Update support
	//
	// SpecialIdDAO does this differently from AbstractDAO.  (See class javadocs.)
	// First knock out the existing methods, then provide an alternative.
	
	/**
	 * Save the bean, either updating an existing object in the metadata KB or
	 * creating a new one.  If the object is new, it should have had its id
	 * set (or initially obtained) via getNextAvailableId.  XXX Can we move that
	 * into here?
	 * 
	 * @param bean the Java representation of the object to be saved to the KB.
	 */
	@Override
	public void saveObject(BeanType bean) throws MetadataException {
		// The code in here is essentially copied from the AbstractDAO methods
		// createObject and updateObject.
		// There are two cases; either this is a new object or it already exists in the KB.
		OWLIndividual inst = findIndividualWithId(bean.getId());
		if (inst == null) {
			// Need to create this object in KB
			inst = getKbClass().createOWLIndividual(localInstanceNamePrefix+bean.getId());
			if (hasIdProperty) {
				// If we are keeping the redundant ID property, this is the place
				// (instance creation) where it needs to be set.
				PropertyUtils.setPropertyValue(inst, idProperty, bean.getId());
			}
		}
		copyBeanPropertiesToIndividual(bean, inst);		
	}
}
