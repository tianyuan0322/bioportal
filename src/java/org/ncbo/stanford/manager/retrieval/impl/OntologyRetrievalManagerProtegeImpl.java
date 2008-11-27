package org.ncbo.stanford.manager.retrieval.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * A default implementation to OntologyRetrievalManager interface designed to
 * provide an abstraction layer to ontology and concept retrieval operations.
 * The service layer will consume this interface instead of directly calling a
 * specific implementation (i.e. LexGrid, Protege etc.). Do not use this class
 * directly in upper layers.
 * 
 * 
 * @author Michael Dorf
 */
@SuppressWarnings("unchecked")
public class OntologyRetrievalManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologyRetrievalManager {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(OntologyRetrievalManagerProtegeImpl.class);

	/**
	 * Default Constructor
	 */
	public OntologyRetrievalManagerProtegeImpl() {
	}

	//
	// Concept methods
	//

	/**
	 * Get the root concept for the specified ontology.
	 */
	public ClassBean findRootConcept(VNcboOntology ontologyVersion) {
		KnowledgeBase kb = getKnowledgeBase(ontologyVersion);

		// Get all root nodes associated with this ontology. Then iterate
		// through the collection, returning the first one.
		Cls oThing = kb.getRootCls();

		if (oThing != null) {
			return createClassBean(oThing, true);
		}

		return null;
	}

	public ClassBean findConcept(VNcboOntology ontologyVersion, String conceptId) {
		KnowledgeBase kb = getKnowledgeBase(ontologyVersion);

		Cls owlClass = getCls(conceptId, kb);

		if (owlClass != null) {
			return createClassBean(owlClass, true);
		}

		return null;
	}

	public ClassBean findPathFromRoot(VNcboOntology ontologyVersion,
			String conceptId, boolean light) {
		KnowledgeBase kb = getKnowledgeBase(ontologyVersion);

		Cls cls = getCls(conceptId, kb);
		Collection nodes = ModelUtilities.getPathToRoot(cls);

		return buildPath(nodes, light);
	}

	public ClassBean findParent(String id, Integer ontologyVersionId) {
		throw new UnsupportedOperationException();
	}

	public ArrayList<ClassBean> findChildren(String id,
			Integer ontologyVersionId) {
		throw new UnsupportedOperationException();
	}

	//
	// Non interface methods
	//

	//
	// Private methods
	//

	// This is to remove the URI reference that is used by protege for IDs
	private String getId(Cls node) {
		if (node instanceof RDFResource) {
			RDFResource rdfNode = (RDFResource) node;
			return NamespaceUtil.getPrefixedName(rdfNode.getOWLModel(), rdfNode
					.getName());
		} else {
			return node.getName();
		}
	}

	private Cls getCls(String conceptId, KnowledgeBase kb) {
		Cls cls = null;

		if (kb instanceof OWLModel) {
			cls = ((OWLModel) kb).getRDFSNamedClass(conceptId);
		} else {
			cls = kb.getCls(conceptId);
		}

		return cls;
	}

	private ClassBean buildPath(Collection nodes, boolean light) {
		ClassBean rootBean = null;
		ClassBean currentBean = null;
		Cls previousNode = null;

		for (Object nodeObj : nodes) {
			ClassBean clsBean = new ClassBean();
			Cls node = (Cls) nodeObj;
			clsBean.setId(getId(node));

			if (currentBean != null) {
				if (light) {
					Set beanSet = new HashSet();
					beanSet.add(clsBean);
					currentBean.addRelation(ApplicationConstants.SUB_CLASS,
							new ArrayList(beanSet));
				} else {
					List<ClassBean> siblings = convertLightBeans(getUniqueClasses(previousNode
							.getDirectSubclasses()));

					for (ClassBean sibling : siblings) {
						if (sibling.getId().equals(clsBean.getId())) {
							clsBean = sibling;
						}
					}

					currentBean.addRelation(ApplicationConstants.SUB_CLASS,
							siblings);
				}
			} else {
				rootBean = clsBean;
			}

			previousNode = node;
			currentBean = clsBean;
		}

		return rootBean;
	}

	private List<ClassBean> convertLightBeans(Collection<Cls> protegeClses) {
		List<ClassBean> beans = new ArrayList<ClassBean>();

		for (Cls cls : protegeClses) {
			if (cls.isVisible())
				beans.add(createLightClassBean(cls));
		}

		return beans;
	}

	private List<ClassBean> convertClasses(Collection<Cls> protegeClses,
			boolean recursive) {
		List<ClassBean> beans = new ArrayList<ClassBean>();

		for (Cls cls : protegeClses) {
			if (cls.isVisible())
				beans.add(createClassBean(cls, recursive));
		}

		return beans;
	}

	/*
	 * protected String getLabel(Frame node) { String label = null;
	 * 
	 * if (node instanceof RDFResource) { RDFResource rs = (RDFResource) node;
	 * Collection labels = rs.getLabels();
	 * 
	 * if (labels == null || labels.isEmpty()) { label = node.getName(); } else {
	 * label = CollectionUtilities.getFirstItem(labels).toString(); } } else {
	 * label = node.getName(); }
	 * 
	 * return label; }
	 */

	private ClassBean createLightClassBean(Cls cls) {
		ClassBean classBean = new ClassBean();
		classBean.setId(getId(cls));

		classBean.setLabel(cls.getBrowserText());
		classBean.addRelation(ApplicationConstants.CHILD_COUNT,
				getUniqueClasses(cls.getDirectSubclasses()).size());

		return classBean;
	}

	private ClassBean createClassBean(Cls cls, boolean recursive) {
		boolean isOwl = cls.getKnowledgeBase() instanceof OWLModel;

		ClassBean classBean = new ClassBean();
		classBean.setId(getId(cls));

		classBean.setLabel(cls.getBrowserText());

		// add properties
		Collection<Slot> slots;

		if (isOwl && cls instanceof RDFSNamedClass) {
			slots = ((RDFSNamedClass) cls).getPossibleRDFProperties();
		} else {
			slots = cls.getOwnSlots();
		}

		classBean.addRelations(convertProperties(cls, slots, isOwl));

		// add subclasses
		// if OWLNamedClass, then use getNamedSubclasses/Superclasses,
		// else use getDirectSubclasses/Superclasses (cast to
		// Collection<Cls>)
		List<Cls> subclasses = null;
		List<Cls> superclasses = null;

		if (cls instanceof OWLNamedClass) {
			subclasses = getUniqueClasses(((OWLNamedClass) cls)
					.getNamedSubclasses(false));

			OWLModel owlModel = (OWLModel) cls.getKnowledgeBase();

			if (cls.equals(owlModel.getOWLThingClass())) {
				Iterator<Cls> it = subclasses.iterator();

				while (it.hasNext()) {
					Cls subclass = it.next();

					if (subclass.isSystem()) {
						it.remove();
					}
				}
			}
		} else {
			subclasses = getUniqueClasses(cls.getDirectSubclasses());
		}

		classBean.addRelation(ApplicationConstants.CHILD_COUNT, subclasses
				.size());

		if (recursive) {
			classBean.addRelation(ApplicationConstants.SUB_CLASS,
					convertClasses(subclasses, false));

			// add superclasses
			if (cls instanceof OWLNamedClass) {
				superclasses = getUniqueClasses(((OWLNamedClass) cls)
						.getNamedSuperclasses(false));
			} else {
				superclasses = getUniqueClasses(cls.getDirectSuperclasses());
			}

			classBean.addRelation(ApplicationConstants.SUPER_CLASS,
					convertClasses(superclasses, false));
		}

		// add RDF type
		if (cls instanceof OWLNamedClass) {
			classBean.addRelation(ApplicationConstants.RDF_TYPE,
					convertClasses(getUniqueClasses(((OWLNamedClass) cls)
							.getRDFTypes()), false));
		}

		return classBean;
	}

	private List getUniqueClasses(Collection classes) {
		if (classes != null) {
			Set c = new HashSet(classes);
			return new ArrayList(c);
		}

		return Collections.emptyList();
	}

	/**
	 * Converts collection of slots into a string representation of values
	 * 
	 * @param slots
	 * @return
	 */
	private HashMap<String, List<String>> convertProperties(Cls concept,
			Collection<Slot> slots, boolean isOwl) {
		HashMap<String, List<String>> bpProps = new HashMap<String, List<String>>();
		ArrayList<String> bpPropVals = new ArrayList<String>();

		// add properties
		for (Slot slot : slots) {
			Collection classes = (isOwl && slot instanceof RDFProperty) ? ((OWLNamedClass) concept)
					.getPropertyValues((RDFProperty) slot)
					: concept.getOwnSlotValues(slot);
			List vals = getUniqueClasses(classes);

			if (vals.isEmpty()) {
				continue;
			}

			for (Object val : vals) {
				if (val instanceof Instance) {
					String value = ((Instance) val).getBrowserText();

					if (value != null) {
						bpPropVals.add(value);
					}
				} else {
					// Tried to assume its a slot and failed, defaulting to
					// toString
					bpPropVals.add(val.toString());
				}
			}

			bpProps.put(slot.getBrowserText(), bpPropVals);
			bpPropVals = new ArrayList<String>();
		}

		return bpProps;
	}
}