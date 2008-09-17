package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.OntologyRetrievalManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.query.querytypes.LuceneOwnSlotValueQuery;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
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

	@SuppressWarnings("deprecation")
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
		return new ClassBean();
	}

	public ArrayList<ClassBean> findChildren(String id,
			Integer ontologyVersionId) {
		return new ArrayList();
	}

	public List<SearchResultBean> findConceptNameExact(
			List<VNcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (VNcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean srb = new SearchResultBean();
			srb.setOntologyVersionId(ontologyVersion.getId());
			KnowledgeBase kb = getKnowledgeBase(ontologyVersion);
			Collection<Frame> frames = new ArrayList<Frame>();

			if (kb instanceof OWLModel) {
				Collection<Frame> allFrames = kb
						.executeQuery(new LuceneOwnSlotValueQuery(kb
								.getNameSlot(), "*" + query));

				for (Frame frame : allFrames) {
					if (frame instanceof RDFSNamedClass && frame.isVisible()
							&& !frame.isSystem()) {
						RDFResource resource = (RDFResource) frame;

						if (resource.getLocalName().equals(query)) {
							frames.add(frame);
						}
					}
				}
			} else {
				frames = kb.getFramesWithValue(kb.getNameSlot(), null, false,
						query);
			}

			int i = 0;

			for (Frame frame : frames) {
				if (i >= maxToReturn) {
					break;
				}

				if (frame instanceof Cls) {
					Cls cls = (Cls) frame;
					srb.addName(createLightBean(cls));
				}

				i++;
			}

			results.add(srb);
		}

		return results;
	}

	public List<SearchResultBean> findConceptNameStartsWith(
			List<VNcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (VNcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean srb = new SearchResultBean();
			srb.setOntologyVersionId(ontologyVersion.getId());
			KnowledgeBase kb = getKnowledgeBase(ontologyVersion);
			Collection<Frame> frames = new HashSet<Frame>();

			if (kb instanceof OWLModel) {
				Collection<Frame> allFrames = kb
						.executeQuery(new LuceneOwnSlotValueQuery(kb
								.getNameSlot(), "*" + query));
				for (Frame frame : allFrames) {
					if (frame instanceof RDFSNamedClass && frame.isVisible()
							&& !frame.isSystem()) {
						RDFResource resource = (RDFResource) frame;
						if (resource.getLocalName().startsWith(query)) {
							frames.add(frame);
						}
					}
				}
			} else {
				frames.addAll(kb.executeQuery(new LuceneOwnSlotValueQuery(kb
						.getNameSlot(), query + "*")));
			}

			int i = 0;

			for (Frame frame : frames) {
				if (i >= maxToReturn) {
					break;
				}

				if (frame instanceof Cls) {
					Cls cls = (Cls) frame;
					srb.addName(createLightBean(cls));
				}

				i++;
			}

			results.add(srb);
		}

		return results;
	}

	public List<SearchResultBean> findConceptNameContains(
			List<VNcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (VNcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean srb = new SearchResultBean();
			srb.setOntologyVersionId(ontologyVersion.getId());
			KnowledgeBase kb = getKnowledgeBase(ontologyVersion);

			// TODO: This should use the browser slot (when there's a browser
			// slot, don't use the name slot).
			// This can be obtained from the pprj file.
			// (At load time, we should store the browser slot as a metadata
			// attribute).

			Collection<Frame> frames = kb.executeQuery(new LuceneOwnSlotValueQuery(kb
					.getNameSlot(), query));
			int i = 0;

			for (Frame frame : frames) {
				if (i >= maxToReturn) {
					break;
				}

				if (frame instanceof Cls && frame.isVisible()
						&& !frame.isSystem()) {
					Cls cls = (Cls) frame;
					srb.addName(createLightBean(cls));
				}

				i++;
			}

			results.add(srb);
		}

		return results;
	}

	public ArrayList<SearchResultBean> findConceptPropertyContains(
			List<VNcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (VNcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean srb = new SearchResultBean();
			srb.setOntologyVersionId(ontologyVersion.getId());

			KnowledgeBase kb = getKnowledgeBase(ontologyVersion);
			Collection<Frame> frames = kb.executeQuery(new LuceneOwnSlotValueQuery(
					null, "*" + query + "*"));
			int i = 0;

			for (Frame frame : frames) {
				if (i >= maxToReturn) {
					break;
				}

				if (frame instanceof Cls && frame.isVisible()
						&& !frame.isSystem()) {
					Cls cls = (Cls) frame;
					srb.addProperty(createLightBean(cls));
				}

				i++;
			}

			results.add(srb);
		}

		return results;
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
		Cls owlClass = null;

		if (kb instanceof OWLModel) {
			owlClass = ((OWLModel) kb).getOWLNamedClass(conceptId);
		} else {
			owlClass = kb.getCls(conceptId);
		}

		return owlClass;
	}

	private ClassBean buildPath(Collection nodes, boolean light) {
		ClassBean rootBean = null;
		ClassBean currentBean = null;
		Cls previousNode = null;

		for (Object nodeObj : nodes) {
			ClassBean clsBean = new ClassBean();
			Cls node = (Cls) nodeObj;
			clsBean.setId(getId(node));
			clsBean.setLabel(node.getBrowserText());

			if (currentBean != null) {
				if (light) {
					List beanList = new ArrayList();
					beanList.add(clsBean);
					currentBean.addRelation(ApplicationConstants.SUB_CLASS,
							beanList);
				} else {
					Collection<ClassBean> siblings = convertLightBeans(previousNode
							.getDirectSubclasses());

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

	private Collection<ClassBean> convertLightBeans(Collection<Cls> protegeClses) {
		Collection<ClassBean> beans = new ArrayList<ClassBean>();

		for (Cls cls : protegeClses) {
			if (cls.isVisible())
				beans.add(createLightBean(cls));
		}

		return beans;
	}

	private ClassBean createLightBean(Cls cls) {
		ClassBean bean = new ClassBean();
		bean.setId(getId(cls));
		bean.setLabel(cls.getBrowserText());
		bean.addRelation(ApplicationConstants.CHILD_COUNT, cls
				.getDirectSubclasses().size());

		return bean;
	}

	private Collection<ClassBean> convertClasses(Collection<Cls> protegeClses,
			boolean recursive) {
		Collection<ClassBean> beans = new ArrayList<ClassBean>();

		for (Cls cls : protegeClses) {
			if (cls.isVisible())
				beans.add(createClassBean(cls, recursive));
		}

		return beans;
	}

	private ClassBean createClassBean(Cls pConcept, boolean recursive) {
		boolean isOwl = pConcept.getKnowledgeBase() instanceof OWLModel;

		ClassBean classBean = new ClassBean();
		classBean.setId(getId(pConcept));
		classBean.setLabel(pConcept.getBrowserText());

		// add properties
		Collection<Slot> slots;

		if (isOwl && pConcept instanceof RDFSNamedClass) {
			slots = ((RDFSNamedClass) pConcept).getPossibleRDFProperties();
		} else {
			slots = pConcept.getOwnSlots();
		}

		classBean.addRelations(convertProperties(pConcept, slots));

		// add subclasses
		// if OWLNamedClass, then use getNamedSubclasses/Superclasses,
		// else use getDirectSubclasses/Superclasses (cast to
		// Collection<Cls>)
		Collection<Cls> subclasses = null;
		Collection<Cls> superclasses = new ArrayList<Cls>();

		if (pConcept instanceof OWLNamedClass) {
			subclasses = ((OWLNamedClass) pConcept).getNamedSubclasses(false);
			OWLModel owlModel = (OWLModel) pConcept.getKnowledgeBase();

			if (pConcept.equals(owlModel.getOWLThingClass())) {
				Iterator<Cls> it = subclasses.iterator();

				while (it.hasNext()) {
					Cls subclass = it.next();

					if (subclass.isSystem()) {
						it.remove();
					}
				}
			}
		} else {
			subclasses = pConcept.getDirectSubclasses();
		}

		classBean.addRelation(ApplicationConstants.CHILD_COUNT, subclasses
				.size());

		if (recursive) {
			classBean.addRelation(ApplicationConstants.SUB_CLASS,
					convertClasses(subclasses, false));

			// add superclasses
			if (pConcept instanceof OWLNamedClass) {
				superclasses = ((OWLNamedClass) pConcept)
						.getNamedSuperclasses(false);
			} else {
				superclasses = pConcept.getDirectSuperclasses();
			}

			classBean.addRelation(ApplicationConstants.SUPER_CLASS,
					convertClasses(superclasses, false));
		}

		// add RDF type
		if (pConcept instanceof OWLNamedClass) {
			classBean.addRelation(ApplicationConstants.RDF_TYPE,
					convertClasses(((OWLNamedClass) pConcept).getRDFTypes(),
							false));
		}

		return classBean;
	}

	/**
	 * Converts collection of slots into a string representation of values
	 * 
	 * @param slots
	 * @return
	 */
	private HashMap<String, List<String>> convertProperties(Cls concept,
			Collection<Slot> slots) {
		HashMap<String, List<String>> bpProps = new HashMap<String, List<String>>();
		ArrayList<String> bpPropVals = new ArrayList<String>();

		// add properties
		for (Slot slot : slots) {
			Collection<Object> vals = concept.getOwnSlotValues(slot);

			if (vals.isEmpty()) {
				continue;
			}

			for (Object val : vals) {
				if (val instanceof Instance) {
					String value = ((Instance) val).getBrowserText();

					if (value != null) {
						if (value.indexOf("~#") > -1) {
							value = value.substring(value.indexOf(" "));
						}

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
