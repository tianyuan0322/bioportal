package org.ncbo.stanford.manager.retrieval.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import org.ncbo.stanford.bean.concept.ClassBeanResultListBean;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.bean.concept.InstanceBeanResultListBean;
import org.ncbo.stanford.bean.concept.InstanceTypesList;
import org.ncbo.stanford.bean.concept.PropertyBean;
import org.ncbo.stanford.enumeration.ConceptTypeEnum;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.paginator.Paginator;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.paginator.impl.PaginatorImpl;

import edu.stanford.smi.protege.model.BrowserSlotPattern;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.util.OWLBrowserSlotPattern;

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

	private Integer allConceptsMaxPageSize;

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
	public ClassBean findRootConcept(OntologyBean ontologyBean, boolean light) {
		KnowledgeBase kb = getKnowledgeBase(ontologyBean);
		Slot synonymSlot = getSynonymSlot(kb, ontologyBean.getSynonymSlot());
		Slot definitionSlot = getDefinitionSlot(kb, ontologyBean
				.getDocumentationSlot());
		Slot authorSlot = getAuthorSlot(kb, ontologyBean.getAuthorSlot());

		ClassBean targetClass = null;

		// Get all root nodes associated with this ontology. Then iterate
		// through the collection, returning the first one.
		Cls oThing = kb.getRootCls();

		if (oThing != null) {
			if (light) {
				targetClass = buildConceptLight(oThing, synonymSlot,
						definitionSlot, authorSlot, ontologyBean);
			} else {
				targetClass = createClassBean(oThing, true, synonymSlot,
						definitionSlot, authorSlot, ontologyBean);
			}
		}

		return targetClass;
	}

	public ClassBean findConcept(OntologyBean ontologyBean, String conceptId,
			boolean light, boolean noRelations) {
		KnowledgeBase kb = getKnowledgeBase(ontologyBean);
		Slot synonymSlot = getSynonymSlot(kb, ontologyBean.getSynonymSlot());
		Slot definitionSlot = getDefinitionSlot(kb, ontologyBean
				.getDocumentationSlot());
		Slot authorSlot = getAuthorSlot(kb, ontologyBean.getAuthorSlot());
		Frame owlClass = getFrame(conceptId, kb);
		ClassBean targetClass = null;

		if (owlClass != null) {
			if (!(owlClass instanceof Cls)) {
				targetClass = createBaseClassBean(owlClass, ontologyBean);
			} else if (noRelations) {
				targetClass = buildConceptNoRelations((Cls) owlClass,
						synonymSlot, definitionSlot, authorSlot, ontologyBean);
			} else if (light) {
				targetClass = buildConceptLight((Cls) owlClass, synonymSlot,
						definitionSlot, authorSlot, ontologyBean);
			} else {
				targetClass = createClassBean((Cls) owlClass, true,
						synonymSlot, definitionSlot, authorSlot, ontologyBean);
			}
		}

		return targetClass;
	}

	/**
	 * returns an ordered collection of classes; guarantees the order based on
	 * frame name
	 * 
	 */
	public Page<ClassBean> findAllConcepts(OntologyBean ontologyBean,
			Integer pageSize, Integer pageNum) throws Exception {
		KnowledgeBase kb = getKnowledgeBase(ontologyBean);
		final Slot synonymSlot = getSynonymSlot(kb, ontologyBean
				.getSynonymSlot());
		final Slot definitionSlot = getDefinitionSlot(kb, ontologyBean
				.getDocumentationSlot());
		final Slot authorSlot = getAuthorSlot(kb, ontologyBean.getAuthorSlot());
		List<Cls> allClasses = getAllClasses(kb);
		ClassBeanResultListBean pageConcepts = new ClassBeanResultListBean(0);
		int totalResults = allClasses.size();

		if (pageSize == null || pageSize <= 0) {
			pageSize = (totalResults <= allConceptsMaxPageSize) ? totalResults
					: allConceptsMaxPageSize;
		} else if (pageSize > allConceptsMaxPageSize) {
			pageSize = allConceptsMaxPageSize;
		}

		Paginator<ClassBean> p = new PaginatorImpl<ClassBean>(pageConcepts,
				pageSize, totalResults);

		if (pageNum == null || pageNum <= 1) {
			pageNum = 1;
		} else {
			int numPages = p.getNumPages();
			pageNum = (pageNum > numPages) ? numPages : pageNum;
		}

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;
		List<Cls> allConceptsLimited = allClasses.subList(offset, limit);

		for (Cls cls : allConceptsLimited) {
			pageConcepts.add(createClassBean(cls, true, synonymSlot,
					definitionSlot, authorSlot, ontologyBean));
		}

		return p.getCurrentPage(pageNum);
	}

	/**
	 * returns an ordered collection of classes; guarantees the order based on
	 * frame name
	 * 
	 */
	public Iterator<ClassBean> listAllClasses(final OntologyBean ontologyBean)
			throws Exception {
		KnowledgeBase kb = getKnowledgeBase(ontologyBean);
		final Slot synonymSlot = getSynonymSlot(kb, ontologyBean
				.getSynonymSlot());
		final Slot definitionSlot = getDefinitionSlot(kb, ontologyBean
				.getDocumentationSlot());
		final Slot authorSlot = getAuthorSlot(kb, ontologyBean.getAuthorSlot());
		ArrayList<Cls> allClasses = getAllClasses(kb);

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
				// return createBaseClassBean(resultIt.next());
				return createClassBean(resultIt.next(), true, synonymSlot,
						definitionSlot, authorSlot, ontologyBean);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	private ArrayList<Cls> getAllClasses(KnowledgeBase kb) {
		ArrayList<Cls> allClasses = new ArrayList<Cls>();

		if (kb instanceof OWLModel) {
			// RDF/OWL format
			allClasses.addAll(((OWLModel) kb).getUserDefinedRDFSNamedClasses());
		} else {
			// Protege format
			allClasses.addAll(kb.getClses());
			allClasses.removeAll(kb.getSystemFrames().getFrames());
		}

		Collections.sort(allClasses, new Comparator<Cls>() {
			public int compare(Cls o1, Cls o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		return allClasses;
	}

	public InstanceBean findInstanceById(OntologyBean ontologyBean,
			String instanceId) throws Exception {

		KnowledgeBase kb = getKnowledgeBase(ontologyBean);

		// get frame using instance Id
		Frame owlClass = getFrame(instanceId, kb);
		if (owlClass instanceof Cls) {
			throw new InvalidInputException(MessageUtils
					.getMessage("msg.error.invalidinstanceid"));
		}
		// populate classBean and return to caller.
		return createInstanceBean(owlClass, ontologyBean, true);
	}

	public Page<InstanceBean> findInstancesByConceptId(
			OntologyBean ontologyBean, String conceptId, Integer pageSize,
			Integer pageNum) throws Exception {

		KnowledgeBase kb = getKnowledgeBase(ontologyBean);
		// get frame using conceptId
		Frame owlClass = getFrame(conceptId, kb);

		InstanceBeanResultListBean allInstances = new InstanceBeanResultListBean(
				0);
		if (owlClass != null) {
			Cls clsObj = (Cls) owlClass;
			// get instance from KnowledgeBase
			Collection<Instance> instances = clsObj.getDirectInstances();
			InstanceBean instanceBean;
			for (Instance instance : instances) {
				instanceBean = createInstanceBean(instance, ontologyBean, true);
				allInstances.add(instanceBean);
			}
		}

		int resultsSize = allInstances.size();

		if (pageSize == null || pageSize <= 0) {
			pageSize = resultsSize;
		}

		Page<InstanceBean> page;
		Paginator<InstanceBean> p = new PaginatorImpl<InstanceBean>(
				allInstances, pageSize);

		if (pageNum == null || pageNum <= 1) {
			page = p.getFirstPage();
		} else {
			page = p.getNextPage(pageNum - 1);
		}

		return page;
	}

	private InstanceBean createInstanceBean(Frame frame,
			OntologyBean ontologyBean, Boolean includeRelations) {
		InstanceBean instanceBean = new InstanceBean();
		instanceBean.setId(getId(frame));
		instanceBean.setFullId(getFullId(frame, ontologyBean));
		instanceBean.setLabel(getBrowserText(frame, ontologyBean));

		if (frame instanceof Instance) {
			Collection instanceTypes = ((Instance) frame).getDirectTypes();
			ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();

			for (Object obj : instanceTypes) {
				DefaultOWLNamedClass defaultOWLNamedClass = (DefaultOWLNamedClass) obj;
				ClassBean classBean = createBaseClassBean(defaultOWLNamedClass,
						ontologyBean);
				classBeans.add(classBean);
			}

			InstanceTypesList list = new InstanceTypesList();
			list.setInstanceTypes(classBeans);
			instanceBean.setInstanceType(list);
		}

		if (includeRelations) {
			// create map to set relations
			HashMap<Object, Object> relations = new HashMap<Object, Object>();
			// get all properties
			Collection<Slot> properties = frame.getOwnSlots();
			Iterator p = properties.iterator();

			while (p.hasNext()) {
				Slot nextProperty = (Slot) p.next();
				Collection values = frame.getOwnSlotValues(nextProperty);

				if (values != null && !values.isEmpty()) {
					// to store all property values(for <list>)
					List<Object> entryList = new ArrayList<Object>();

					for (Object obj : values) {
						if (obj instanceof Instance) {
							InstanceBean inst = createInstanceBean(
									(Instance) obj, ontologyBean, false);
							entryList.add(inst);
						} else {
							entryList.add(obj);
						}
					}

					PropertyBean propertyBean = createBasePropertyBean(
							nextProperty, ontologyBean);
					relations.put(propertyBean, entryList);
				}
			}

			instanceBean.addRelations(relations);
		}

		return instanceBean;
	}

	private PropertyBean createBasePropertyBean(Slot frame,
			OntologyBean ontologyBean) {
		PropertyBean propertyBean = new PropertyBean();
		propertyBean.setId(getId(frame));
		propertyBean.setFullId(getFullId(frame, ontologyBean));
		propertyBean.setLabel(getBrowserText(frame, ontologyBean));
		return propertyBean;
	}

	public ClassBean findPathFromRoot(OntologyBean ontologyBean,
			String conceptId, boolean light) {
		ClassBean rootPath = null;
		KnowledgeBase kb = getKnowledgeBase(ontologyBean);
		Frame cls = getFrame(conceptId, kb);

		if (cls instanceof Cls) {
			Collection nodes = ModelUtilities.getPathToRoot((Cls) cls);
			rootPath = buildPath(nodes, ontologyBean, light);
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
			Slot definitionSlot, Slot authorSlot, OntologyBean ontologyBean) {
		ClassBean targetClass = buildConceptNoRelations(cls, synonymSlot,
				definitionSlot, authorSlot, ontologyBean);

		targetClass.addRelation(ApplicationConstants.CHILD_COUNT,
				getUniqueClasses(cls.getDirectSubclasses()).size());

		List<ClassBean> children = convertLightBeans(getUniqueClasses(cls
				.getDirectSubclasses()), ontologyBean);
		targetClass.addRelation(ApplicationConstants.SUB_CLASS, children);

		return targetClass;
	}

	private ClassBean buildConceptNoRelations(Cls cls, Slot synonymSlot,
			Slot definitionSlot, Slot authorSlot, OntologyBean ontologyBean) {
		ClassBean targetClass = createBaseClassBean(cls, ontologyBean);
		addSynonyms(cls, synonymSlot, targetClass);
		addDefinitions(cls, definitionSlot, targetClass);
		addAuthors(cls, authorSlot, targetClass);

		return targetClass;
	}

	private ClassBean buildPath(Collection nodes, OntologyBean ontologyBean,
			boolean light) {
		ClassBean rootBean = null;
		ClassBean currentBean = null;
		Cls previousNode = null;

		for (Object nodeObj : nodes) {
			Cls node = (Cls) nodeObj;
			ClassBean clsBean = createBaseClassBean(node, ontologyBean);

			if (currentBean != null) {
				if (light) {
					Set beanSet = new HashSet();
					beanSet.add(clsBean);
					currentBean.addRelation(ApplicationConstants.SUB_CLASS,
							new ArrayList(beanSet));
					currentBean.addRelation(ApplicationConstants.CHILD_COUNT,
							node.getDirectSubclassCount());
				} else {
					List<ClassBean> siblings = convertLightBeans(
							removeAnnonymousClasses(getUniqueClasses(previousNode
									.getDirectSubclasses())), ontologyBean);

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

	private List<ClassBean> convertLightBeans(Collection<Cls> protegeClses,
			OntologyBean ontologyBean) {
		List<ClassBean> beans = new ArrayList<ClassBean>();
		List<Cls> subclasses = null;
		for (Cls cls : protegeClses) {

			ClassBean classBean = createBaseClassBean(cls, ontologyBean);
			// This code is used for removing the Anonymous classes
			if (cls instanceof OWLNamedClass) {
				// Collecting the subclasses for Cls
				subclasses = getUniqueClasses(((OWLNamedClass) cls)
						.getNamedSubclasses(false));

				OWLModel owlModel = (OWLModel) cls.getKnowledgeBase();

				if (cls.equals(owlModel.getOWLThingClass())) {
					subclasses = removeAnnonymousClasses(subclasses);
				}
			} else {
				// //Collecting the subclasses for Cls
				subclasses = getUniqueClasses(cls.getDirectSubclasses());
			}
			// It's adding the childCount Property after counting their
			// subclases
			classBean.addRelation(ApplicationConstants.CHILD_COUNT, subclasses
					.size());
			beans.add(classBean);
		}

		return beans;
	}

	/**
	 * 2/23/09 Added subclass.getName().startsWith("@") to catch a protege bug
	 * where non named classes got added Using .startsWith because protege team
	 * suggested it as the best way
	 * 
	 * @param protegeClasses
	 * @return
	 */
	private List<Cls> removeAnnonymousClasses(List<Cls> protegeClasses) {
		Iterator<Cls> it = protegeClasses.iterator();

		while (it.hasNext()) {
			Cls subclass = it.next();

			if (subclass.isSystem() || subclass.getName().startsWith("@")) {
				it.remove();
			}
		}

		return protegeClasses;
	}

	private List<ClassBean> convertClasses(Collection<Cls> protegeClses,
			boolean recursive, Slot synonymSlot, Slot definitionSlot,
			Slot authorSlot, OntologyBean ontologyBean,
			Map<Cls, ClassBean> recursionMap) {
		List<ClassBean> beans = new ArrayList<ClassBean>();

		for (Cls cls : protegeClses) {
			if (cls.isVisible()) {
				beans
						.add(createClassBean(cls, recursive, synonymSlot,
								definitionSlot, authorSlot, ontologyBean,
								recursionMap));
			}
		}

		return beans;
	}

	private String getBrowserText(Frame frame, OntologyBean ob) {
		if (frame instanceof SimpleInstance) {
			KnowledgeBase kb = frame.getKnowledgeBase();
			Slot browserSlot = getPreferredNameSlot(kb, ob
					.getPreferredNameSlot());
			BrowserSlotPattern bsp = kb instanceof OWLModel ? new OWLBrowserSlotPattern(
					browserSlot)
					: new BrowserSlotPattern(browserSlot);
			return bsp.getBrowserText((Instance) frame);
		} else {
			return StringHelper.unSingleQuote(frame.getBrowserText());
		}
	}

	private ClassBean createBaseClassBean(Frame frame, OntologyBean ontologyBean) {
		ClassBean classBean = new ClassBean();
		classBean.setId(getId(frame));
		classBean.setFullId(getFullId(frame, ontologyBean));
		classBean.setLabel(getBrowserText(frame, ontologyBean));

		ConceptTypeEnum protegeType = ConceptTypeEnum.CONCEPT_TYPE_INDIVIDUAL;

		// TODO : used this code as reference to identify Instance
		if (frame instanceof Cls) {
			protegeType = ConceptTypeEnum.CONCEPT_TYPE_CLASS;
		} else if (frame instanceof Slot) {
			protegeType = ConceptTypeEnum.CONCEPT_TYPE_PROPERTY;
		}

		classBean.setType(protegeType);

		return classBean;
	}

	private ClassBean createClassBean(Cls cls, boolean recursive,
			Slot synonymSlot, Slot definitionSlot, Slot authorSlot,
			OntologyBean ontologyBean) {
		return createClassBean(cls, recursive, synonymSlot, definitionSlot,
				authorSlot, ontologyBean, new HashMap<Cls, ClassBean>());
	}

	private ClassBean createClassBean(Cls cls, boolean recursive,
			Slot synonymSlot, Slot definitionSlot, Slot authorSlot,
			OntologyBean ontologyBean, Map<Cls, ClassBean> recursionMap) {
		if (recursionMap.containsKey(cls)) {
			return recursionMap.get(cls);
		}

		boolean isOwl = cls.getKnowledgeBase() instanceof OWLModel;

		ClassBean classBean = createBaseClassBean(cls, ontologyBean);
		addSynonyms(cls, synonymSlot, classBean);
		addDefinitions(cls, definitionSlot, classBean);
		addAuthors(cls, authorSlot, classBean);

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

		classBean.addRelations(convertProperties(cls, slots, ontologyBean,
				isOwl));

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
				subclasses = removeAnnonymousClasses(subclasses);
			}
		} else {
			subclasses = getUniqueClasses(cls.getDirectSubclasses());
		}

		classBean.addRelation(ApplicationConstants.CHILD_COUNT, subclasses
				.size());
		classBean.addRelation(ApplicationConstants.INSTANCE_COUNT, cls
				.getDirectInstanceCount());

		if (recursive) {
			classBean.addRelation(ApplicationConstants.SUB_CLASS,
					convertClasses(subclasses, false, synonymSlot,
							definitionSlot, authorSlot, ontologyBean,
							recursionMap));

			// add superclasses
			if (cls instanceof OWLNamedClass) {
				superclasses = getUniqueClasses(((OWLNamedClass) cls)
						.getNamedSuperclasses(false));
			} else {
				superclasses = getUniqueClasses(cls.getDirectSuperclasses());
			}

			classBean.addRelation(ApplicationConstants.SUPER_CLASS,
					convertClasses(superclasses, false, synonymSlot,
							definitionSlot, authorSlot, ontologyBean,
							recursionMap));
		}

		// add RDF type
		if (cls instanceof OWLNamedClass) {
			classBean.addRelation(ApplicationConstants.RDF_TYPE,
					convertClasses(getUniqueClasses(((OWLNamedClass) cls)
							.getRDFTypes()), false, synonymSlot,
							definitionSlot, authorSlot, ontologyBean,
							recursionMap));
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
	private Map<String, List<String>> convertProperties(Cls concept,
			Collection<Slot> slots, OntologyBean ontologyBean, boolean isOwl) {
		Map<String, List<String>> bpProps = new HashMap<String, List<String>>();
		List<String> bpPropVals = new ArrayList<String>();

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
					String value = getBrowserText((Instance) val, ontologyBean);

					if (value != null) {
						bpPropVals.add(value);
					}
				} else {
					// Tried to assume its a slot and failed, defaulting to
					// toString
					bpPropVals.add(val.toString());
				}
			}

			bpProps.put(getBrowserText(slot, ontologyBean), bpPropVals);
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

	/**
	 * @param allConceptsMaxPageSize
	 *            the allConceptsMaxPageSize to set
	 */
	public void setAllConceptsMaxPageSize(Integer allConceptsMaxPageSize) {
		this.allConceptsMaxPageSize = allConceptsMaxPageSize;
	}
}
