package org.ncbo.stanford.manager.retrieval.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.enumeration.ConceptTypeEnum;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
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
	public ClassBean findRootConcept(OntologyBean ontologyVersion, boolean light) {
		KnowledgeBase kb = getKnowledgeBase(ontologyVersion);
		Slot synonymSlot = getSynonymSlot(kb, ontologyVersion.getSynonymSlot());
		Slot definitionSlot = getDefinitionSlot(kb, ontologyVersion
				.getDocumentationSlot());
		Slot authorSlot = getAuthorSlot(kb, ontologyVersion.getAuthorSlot());

		ClassBean targetClass = null;

		// Get all root nodes associated with this ontology. Then iterate
		// through the collection, returning the first one.
		Cls oThing = kb.getRootCls();

		if (oThing != null) {
			if (light) {
				targetClass = buildConceptLight(oThing, synonymSlot,
						definitionSlot, authorSlot);
			} else {
				targetClass = createClassBean(oThing, true, synonymSlot,
						definitionSlot, authorSlot);
			}
		}

		return targetClass;
	}

	public ClassBean findConcept(OntologyBean ontologyVersion,
			String conceptId, boolean light, boolean noRelations,
			boolean isIncludeInstances) {
		KnowledgeBase kb = getKnowledgeBase(ontologyVersion);
		Slot synonymSlot = getSynonymSlot(kb, ontologyVersion.getSynonymSlot());
		Slot definitionSlot = getDefinitionSlot(kb, ontologyVersion
				.getDocumentationSlot());
		Slot authorSlot = getAuthorSlot(kb, ontologyVersion.getAuthorSlot());
		Frame owlClass = getFrame(conceptId, kb);
		ClassBean targetClass = null;

		if (owlClass != null) {
			if (!(owlClass instanceof Cls)) {
				targetClass = createBaseClassBean(owlClass);
			} else if (noRelations) {
				targetClass = buildConceptNoRelations((Cls) owlClass,
						synonymSlot, definitionSlot, authorSlot);
			} else if (light) {
				targetClass = buildConceptLight((Cls) owlClass, synonymSlot,
						definitionSlot, authorSlot);
			} else {
				targetClass = createClassBean((Cls) owlClass, true,
						synonymSlot, definitionSlot, authorSlot);
				if (isIncludeInstances) {
					// TODO: start
					Cls clsObj = (Cls) owlClass;

					// get instance from KnowledgeBase
					// TODO: need to verify about using getDirectInstances() or
					// getInstances()
					Collection<Instance> instances = clsObj
							.getDirectInstances();

					List<InstanceBean> resultInstance = new ArrayList<InstanceBean>();

					for (Instance instance : instances) {
						// prepare instance object.
						InstanceBean inst = new InstanceBean();
						inst.setId(getId(instance));
						inst.setFullId(instance.getName());
						inst.setLabel(getBrowserText(instance));

						inst.setInstanceTypes(instance.getDirectTypes());

						Collection<Slot> properties = instance.getOwnSlots();
						Map<String, List<String>> pairs = new HashMap<String, List<String>>();

						Iterator<Slot> p = properties.iterator();
						while (p.hasNext()) {
							Slot nextProperty = p.next();
							List<String> pairValues = new ArrayList<String>();
							Collection values = instance
									.getOwnSlotValues(nextProperty);
							if (values != null && !values.isEmpty()) {
								for (Object val : values)
									// generate the property-value pair in the
									// bean
									// where property name is nextProperty and
									// values are rendered in the list above
									pairValues.add((String) val.toString());

							}
							pairs.put(getBrowserText(nextProperty), pairValues);
						}

						inst.addRelations(pairs);
						resultInstance.add(inst);
					}
					targetClass.setInstances(resultInstance);
					/*targetClass.setInstanceCount(((Cls) owlClass)
							.getInstanceCount());*/
					// TODO: end
				}
			}
		}

		return targetClass;
	}

	public Iterator<ClassBean> listAllClasses(OntologyBean ob) throws Exception {
		KnowledgeBase kb = getKnowledgeBase(ob);
		final Slot synonymSlot = getSynonymSlot(kb, ob.getSynonymSlot());
		final Slot definitionSlot = getDefinitionSlot(kb, ob
				.getDocumentationSlot());
		final Slot authorSlot = getAuthorSlot(kb, ob.getAuthorSlot());
		ArrayList<Cls> allClasses = new ArrayList<Cls>();

		if (kb instanceof OWLModel) {
			// RDF/OWL format
			Iterator clsIt = ((OWLModel) kb).listOWLNamedClasses();
			for (; clsIt.hasNext();) {
				RDFSClass cls = (RDFSClass) clsIt.next();

				if (!cls.isSystem()) {
					allClasses.add(cls);
				}
			}
		} else {
			// Protege format
			Collection clses = kb.getClses();

			for (Iterator clsIt = clses.iterator(); clsIt.hasNext();) {
				Cls cls = (Cls) clsIt.next();

				if (!cls.isSystem()) {
					allClasses.add(cls);
				}
			}
		}
		// There could be very many classes in the results. Hopefully clients
		// to this method will use them one at a time. So inflate ClassBean
		// objects
		// one at a time.
		final Iterator<Cls> resultIt = allClasses.iterator();
		return new Iterator<ClassBean>() {
			public boolean hasNext() {
				return resultIt.hasNext();
			}

			public ClassBean next() {
				return createClassBean(resultIt.next(), true, synonymSlot,
						definitionSlot, authorSlot);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public InstanceBean findInstanceById(OntologyBean ontologyVersion,
			String instanceId) throws Exception {

		KnowledgeBase kb = getKnowledgeBase(ontologyVersion);
		Frame owlClass = getFrame(instanceId, kb);
		if (owlClass instanceof Cls) {
			throw new InvalidInputException(MessageUtils
					.getMessage("msg.error.invalidinstanceid"));
		}
		// populate classBean and return to caller.
		return createInstanceBean(owlClass);
	}

	private InstanceBean createInstanceBean(Frame frame) {

		InstanceBean instanceBean = new InstanceBean();
		instanceBean.setId(getId(frame));
		instanceBean.setFullId(frame.getName());
		instanceBean.setLabel(getBrowserText(frame));

		ConceptTypeEnum protegeType = ConceptTypeEnum.CONCEPT_TYPE_INDIVIDUAL;

		if (frame instanceof Slot) {
			protegeType = ConceptTypeEnum.CONCEPT_TYPE_PROPERTY;
		}

		if (frame instanceof Instance) {
			instanceBean.setInstanceTypes(((Instance) frame).getDirectTypes());
		}
		// create map to set relations
		HashMap<Object, Object> relations = new HashMap<Object, Object>();

		Collection<Slot> properties = frame.getOwnSlots();
		Iterator p = properties.iterator();
		while (p.hasNext()) {
			Slot nextProperty = (Slot) p.next();
			Collection values = frame.getOwnSlotValues(nextProperty);
			if (values != null && !values.isEmpty()) {
				// generate the property-value pair in the bean where property
				// name is nextProperty and values are rendered in the list
				// above
				relations.put(getBrowserText(nextProperty), values);
			}
		}
		instanceBean.addRelations(relations);
		instanceBean.setType(protegeType);

		return instanceBean;
	}

	public ClassBean findPathFromRoot(OntologyBean ontologyVersion,
			String conceptId, boolean light) {
		ClassBean rootPath = null;
		KnowledgeBase kb = getKnowledgeBase(ontologyVersion);
		Frame cls = getFrame(conceptId, kb);

		if (cls instanceof Cls) {
			Collection nodes = ModelUtilities.getPathToRoot((Cls) cls);
			rootPath = buildPath(nodes, light);
		}

		return rootPath;
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
	private String getId(Frame node) {
		if (node instanceof RDFResource) {
			RDFResource rdfNode = (RDFResource) node;
			return NamespaceUtil.getPrefixedName(rdfNode.getOWLModel(), rdfNode
					.getName());
		} else {
			return node.getName();
		}
	}

	private Frame getFrame(String conceptId, KnowledgeBase kb) {
		Frame frame = null;

		if (kb instanceof OWLModel) {
			frame = ((OWLModel) kb).getRDFResource(conceptId);
		} else {
			frame = kb.getFrame(conceptId);
		}

		return frame;
	}

	private ClassBean buildConceptLight(Cls cls, Slot synonymSlot,
			Slot definitionSlot, Slot authorSlot) {
		ClassBean targetClass = buildConceptNoRelations(cls, synonymSlot,
				definitionSlot, authorSlot);

		targetClass.addRelation(ApplicationConstants.CHILD_COUNT,
				getUniqueClasses(cls.getDirectSubclasses()).size());

		List<ClassBean> children = convertLightBeans(getUniqueClasses(cls
				.getDirectSubclasses()));
		targetClass.addRelation(ApplicationConstants.SUB_CLASS, children);

		return targetClass;
	}

	private ClassBean buildConceptNoRelations(Cls cls, Slot synonymSlot,
			Slot definitionSlot, Slot authorSlot) {
		ClassBean targetClass = createBaseClassBean(cls);
		addSynonyms(cls, synonymSlot, targetClass);
		addDefinitions(cls, definitionSlot, targetClass);
		addAuthors(cls, authorSlot, targetClass);

		return targetClass;
	}

	private ClassBean buildPath(Collection nodes, boolean light) {
		ClassBean rootBean = null;
		ClassBean currentBean = null;
		Cls previousNode = null;

		for (Object nodeObj : nodes) {
			Cls node = (Cls) nodeObj;
			ClassBean clsBean = createBaseClassBean(node);

			if (currentBean != null) {
				if (light) {
					Set beanSet = new HashSet();
					beanSet.add(clsBean);
					currentBean.addRelation(ApplicationConstants.SUB_CLASS,
							new ArrayList(beanSet));
					currentBean.addRelation(ApplicationConstants.CHILD_COUNT,
							node.getDirectSubclassCount());
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
					currentBean.addRelation(ApplicationConstants.CHILD_COUNT,
							siblings.size());
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
			if (cls.isVisible()) {
				ClassBean classBean = createBaseClassBean(cls);
				classBean.addRelation(ApplicationConstants.CHILD_COUNT,
						getUniqueClasses(cls.getDirectSubclasses()).size());
				beans.add(classBean);
			}
		}

		return beans;
	}

	private List<ClassBean> convertClasses(Collection<Cls> protegeClses,
			boolean recursive, Slot synonymSlot, Slot definitionSlot,
			Slot authorSlot, Map<Cls, ClassBean> recursionMap) {
		List<ClassBean> beans = new ArrayList<ClassBean>();

		for (Cls cls : protegeClses) {
			if (cls.isVisible()) {
				beans.add(createClassBean(cls, recursive, synonymSlot,
						definitionSlot, authorSlot, recursionMap));
			}
		}

		return beans;
	}

	private String getBrowserText(Frame frame) {
		return StringHelper.unSingleQuote(frame.getBrowserText());
	}

	private ClassBean createBaseClassBean(Frame frame) {
		ClassBean classBean = new ClassBean();
		classBean.setId(getId(frame));
		classBean.setFullId(frame.getName());
		classBean.setLabel(getBrowserText(frame));

		ConceptTypeEnum protegeType = ConceptTypeEnum.CONCEPT_TYPE_INDIVIDUAL;

		if (frame instanceof Cls) {
			protegeType = ConceptTypeEnum.CONCEPT_TYPE_CLASS;
		} else if (frame instanceof Slot) {
			protegeType = ConceptTypeEnum.CONCEPT_TYPE_PROPERTY;
		}

		classBean.setType(protegeType);

		return classBean;
	}

	private ClassBean createClassBean(Cls cls, boolean recursive,
			Slot synonymSlot, Slot definitionSlot, Slot authorSlot) {
		return createClassBean(cls, recursive, synonymSlot, definitionSlot,
				authorSlot, new HashMap<Cls, ClassBean>());
	}

	private ClassBean createClassBean(Cls cls, boolean recursive,
			Slot synonymSlot, Slot definitionSlot, Slot authorSlot,
			Map<Cls, ClassBean> recursionMap) {
		if (recursionMap.containsKey(cls)) {
			return recursionMap.get(cls);
		}

		boolean isOwl = cls.getKnowledgeBase() instanceof OWLModel;

		ClassBean classBean = createBaseClassBean(cls);
		addSynonyms(cls, synonymSlot, classBean);
		addDefinitions(cls, definitionSlot, classBean);
		addAuthors(cls, authorSlot, classBean);

		// TODO: need to check
		// set instance count
		// classBean.setInstanceCount(cls.getInstanceCount());

		recursionMap.put(cls, classBean);

		// add properties
		Collection<Slot> slots;

		if (isOwl && cls instanceof RDFSNamedClass) {
			slots = ((RDFSNamedClass) cls).getPossibleRDFProperties();
		} else {
			slots = cls.getOwnSlots();
		}

		// remove slots already defined as properties of the bean
		Set<Slot> reservedSlots = new HashSet<Slot>(3);
		reservedSlots.add(synonymSlot);
		reservedSlots.add(definitionSlot);
		reservedSlots.add(authorSlot);
		slots.removeAll(reservedSlots);

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
					// 2/23/09 Added subclass.getName().startsWith("@") to catch
					// a protege bug where non named classes got added
					// Using .startsWith because protege team suggested it as
					// the best way
					if (subclass.isSystem()
							|| subclass.getName().startsWith("@")) {
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
					convertClasses(subclasses, false, synonymSlot,
							definitionSlot, authorSlot, recursionMap));

			// add superclasses
			if (cls instanceof OWLNamedClass) {
				superclasses = getUniqueClasses(((OWLNamedClass) cls)
						.getNamedSuperclasses(false));
			} else {
				superclasses = getUniqueClasses(cls.getDirectSuperclasses());
			}

			classBean.addRelation(ApplicationConstants.SUPER_CLASS,
					convertClasses(superclasses, false, synonymSlot,
							definitionSlot, authorSlot, recursionMap));
		}

		// add RDF type
		if (cls instanceof OWLNamedClass) {
			classBean.addRelation(ApplicationConstants.RDF_TYPE,
					convertClasses(getUniqueClasses(((OWLNamedClass) cls)
							.getRDFTypes()), false, synonymSlot,
							definitionSlot, authorSlot, recursionMap));
		}

		return classBean;
	}

	private void addSynonyms(Cls cls, Slot synonymSlot, ClassBean classBean) {
		if (synonymSlot != null) {
			Collection<?> synonyms = cls.getOwnSlotValues(synonymSlot);

			for (Object synonym : synonyms) {
				String synonymStr;

				if (synonym instanceof Frame) {
					synonymStr = ((Frame) synonym).getBrowserText();
				} else {
					synonymStr = synonym.toString();
				}

				classBean.addSynonym(synonymStr);
			}
		}
	}

	private void addDefinitions(Cls cls, Slot definitionSlot,
			ClassBean classBean) {
		if (definitionSlot != null) {
			Collection<?> definitions = cls.getOwnSlotValues(definitionSlot);

			for (Object definition : definitions) {
				String definitionStr;

				if (definition instanceof Frame) {
					definitionStr = ((Frame) definition).getBrowserText();
				} else {
					definitionStr = definition.toString();
				}

				classBean.addDefinition(definitionStr);
			}
		}
	}

	private void addAuthors(Cls cls, Slot authorSlot, ClassBean classBean) {
		if (authorSlot != null) {
			Collection<?> authors = cls.getOwnSlotValues(authorSlot);

			for (Object author : authors) {
				String authorStr;

				if (author instanceof Frame) {
					authorStr = ((Frame) author).getBrowserText();
				} else {
					authorStr = author.toString();
				}

				classBean.addAuthor(authorStr);
			}
		}
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
			// Why not just call getOwnSlotValues?
			// In the RDF (OWL) case, the values may be RDFSLiteral objects, and
			// when you get those back
			// via getOwnSlotValues, they mix in the language tag, e.g. "~#en".
			// So instead fetch those values via getPropertyValues, and the
			// RDFSLiteral will do the right
			// thing later when you call toString on it.
			Collection classes = (isOwl && slot instanceof RDFProperty && concept instanceof RDFResource) ? ((RDFResource) concept)
					.getPropertyValues((RDFProperty) slot)
					: concept.getOwnSlotValues(slot);
			List vals = getUniqueClasses(classes);

			if (vals.isEmpty()) {
				continue;
			}

			for (Object val : vals) {
				if (val instanceof Instance) {
					String value = getBrowserText((Instance) val);

					if (value != null) {
						bpPropVals.add(value);
					}
				} else {
					// Tried to assume its a slot and failed, defaulting to
					// toString
					bpPropVals.add(val.toString());
				}
			}

			bpProps.put(getBrowserText(slot), bpPropVals);
			bpPropVals = new ArrayList<String>();
		}

		return bpProps;
	}

	public boolean hasParent(OntologyBean ontologyVersion,
			String childConceptId, String parentConceptId) throws Exception {
		KnowledgeBase kb = getKnowledgeBase(ontologyVersion);
		Frame clsChild = getFrame(childConceptId, kb);
		Frame clsParent = getFrame(parentConceptId, kb);

		if (clsChild != null && clsParent != null && clsChild instanceof Cls
				&& clsParent instanceof Cls
				&& ((Cls) clsChild).hasSuperclass((Cls) clsParent)) {
			return true;
		}

		return false;
	}
}
