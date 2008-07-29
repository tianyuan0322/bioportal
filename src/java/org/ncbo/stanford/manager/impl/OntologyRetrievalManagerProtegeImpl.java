package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.OntologyRetrievalManager;
import org.ncbo.stanford.util.cache.CacheMap;
import org.ncbo.stanford.util.constants.ApplicationConstants;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLHasValue;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLSomeValuesFrom;

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

	// Expire cache based on timeout
	private CacheMap<Integer, KnowledgeBase> protegeKnowledgeBases = null;

	/**
	 * Default Constructor
	 */
	public OntologyRetrievalManagerProtegeImpl() {
	}

	/**
	 * Returns the specified ontology.
	 * 
	 * @param id
	 * @return
	 */

	public OntologyBean findOntology(Integer ontologyVersionId) {
		return null;
	}

	public OntologyBean findOntology(Integer ontologyVersionId, String version) {
		return null;
	}

	public List<OntologyBean> findOntologyVersions(Integer ontologyId) {
		return new ArrayList();
	}

	public List<String> findProperties(Integer ontologyVersionId) {
		return new ArrayList();
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

		Cls owlClass = kb.getCls(conceptId);

		if (owlClass != null) {
			return createClassBean(owlClass, true);
		}

		return null;
	}

	public ClassBean findPathToRoot(VNcboOntology ontologyVersion,
			String conceptId, boolean light) {
		KnowledgeBase kb = getKnowledgeBase(ontologyVersion);

		Cls cls = kb.getCls(conceptId);
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
				frames = kb.getFramesWithValue(((OWLModel) kb)
						.getRDFSLabelProperty(), null, false, query);
				if (frames.size() < 1) {
					frames = kb.getFramesWithValue(((OWLModel) kb)
							.getRDFSLabelProperty(), null, false, "@en "
							+ query);
				}
			} else {
				frames = kb.getFramesWithValue(kb.getNameSlot(), null, false,
						query);
			}

			if (frames != null) {
				for (Frame frame : frames) {
					if (frame instanceof Cls) {
						Cls owlClass = (Cls) frame;
						srb.getNames().add(createLightBean(owlClass));
					}
				}
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
			Collection<Frame> frames = null;
			if (kb instanceof OWLModel) {
				frames = kb.getMatchingFrames(((OWLModel) kb)
						.getRDFSLabelProperty(), null, false, "@en " + query
						+ "*", -1);
			} else {
				frames = kb.getMatchingFrames(kb.getNameSlot(), null, false,
						query + "*", -1);
			}

			if (frames != null) {
				for (Frame frame : frames) {
					if (frame instanceof Cls) {
						Cls owlClass = (Cls) frame;
						srb.getNames().add(createLightBean(owlClass));
					}
				}
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
			Collection<Frame> frames = new HashSet<Frame>();

			if (kb instanceof OWLModel) {

				frames.addAll(kb.getMatchingFrames(((OWLModel) kb)
						.getRDFSLabelProperty(), null, false,
						"*" + query + "*", -1));
				frames.addAll(kb.getMatchingFrames(kb.getNameSlot(), null,
						false, "*" + query + "*", -1));

			} else {
				frames = kb.getMatchingFrames(kb.getNameSlot(), null, false,
						"*" + query + "*", -1);
			}

			if (frames != null) {
				for (Frame frame : frames) {
					if (frame instanceof Cls && frame.isVisible()) {
						Cls owlClass = (Cls) frame;
						srb.getNames().add(createLightBean(owlClass));
					}
				}
			}

			results.add(srb);
		}

		return results;
	}

	public ArrayList<ClassBean> findConceptPropertyExact(String property,
			String query, ArrayList<Integer> ontologyVersionIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptPropertyStartsWith(String property,
			String query, ArrayList<Integer> ontologyVersionIds) {
		return new ArrayList();
	}

	public ArrayList<SearchResultBean> findConceptPropertyContains(
			List<VNcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (VNcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean srb = new SearchResultBean();
			srb.setOntologyVersionId(ontologyVersion.getId());
			KnowledgeBase kb = getKnowledgeBase(ontologyVersion);
			Collection<Frame> frames = new HashSet<Frame>();
			Collection<Reference> ref = new HashSet<Reference>();

			if (kb instanceof OWLModel) {

				ref.addAll(kb.getMatchingReferences("*" + query + "*", -1));
				for (Reference reference : ref) {
					if (!reference.getSlot().equals(
							((OWLModel) kb).getRDFSLabelProperty())
							&& !reference.getSlot().equals(kb.getNameSlot())
							&& reference.getSlot().isVisible()) {
						frames.add(reference.getFrame());
					}
				}

			} else {
				ref.addAll(kb.getMatchingReferences("*" + query + "*", -1));
				for (Reference reference : ref) {
					if (!reference.getSlot().equals(kb.getNameSlot())
							&& reference.getSlot().isVisible()) {
						frames.add(reference.getFrame());
					}
				}
			}

			if (frames != null) {
				for (Frame frame : frames) {
					if (frame instanceof Cls) {
						Cls owlClass = (Cls) frame;
						srb.getProperties().add(createLightBean(owlClass));
					}
				}
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

	/**
	 * Gets the Protege ontology associated with the specified ontology id.
	 */
	private KnowledgeBase createKnowledgeBaseInstance(
			VNcboOntology ontologyVersion) {
		DatabaseKnowledgeBaseFactory factory = null;

		if (ontologyVersion.getFormat().contains("OWL")) {
			factory = new OWLDatabaseKnowledgeBaseFactory();
		} else {
			factory = new DatabaseKnowledgeBaseFactory();
		}

		List errors = new ArrayList();

		Project prj = Project.createNewProject(factory, errors);
		DatabaseKnowledgeBaseFactory.setSources(prj.getSources(),
				protegeJdbcDriver, protegeJdbcUrl, getTableName(ontologyVersion
						.getId()), protegeJdbcUsername, protegeJdbcPassword);
		prj.createDomainKnowledgeBase(factory, errors, true);

		return prj.getKnowledgeBase();
	}

	/**
	 * Returns a singleton KnowledgeBase instance for given ontologyVersion.
	 */
	public KnowledgeBase getKnowledgeBase(VNcboOntology ontologyVersion) {
		KnowledgeBase knowledgeBase = (KnowledgeBase) protegeKnowledgeBases
				.get(ontologyVersion.getId());

		if (knowledgeBase == null) {
			knowledgeBase = createKnowledgeBaseInstance(ontologyVersion);
			protegeKnowledgeBases.put(ontologyVersion.getId(), knowledgeBase);
		}

		return knowledgeBase;
	}

	private ClassBean buildPath(Collection nodes, boolean light) {

		ClassBean rootBean = null;
		ClassBean currentBean = null;
		Cls previousNode = null;
		for (Object nodeObj : nodes) {
			ClassBean clsBean = new ClassBean();
			Cls node = (Cls) nodeObj;
			clsBean.setId(node.getName());
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
		bean.setId(cls.getName());
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
		classBean.setId(pConcept.getName());
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
		Collection<Cls> subclasses;
		Collection<Cls> superclasses;

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
				if (val instanceof Slot) {

					bpPropVals.add(((Slot) val).getBrowserText());

				} else if (val instanceof DefaultOWLNamedClass) {
					bpPropVals.add(((DefaultOWLNamedClass) val)
							.getBrowserText());
				} else if (val instanceof DefaultOWLHasValue) {
					bpPropVals.add(((DefaultOWLHasValue) val).getBrowserText());
				} else if (val instanceof DefaultOWLSomeValuesFrom) {
					bpPropVals.add(((DefaultOWLSomeValuesFrom) val)
							.getBrowserText());
				} else if (val instanceof DefaultOWLIntersectionClass) {
					bpPropVals.add(((DefaultOWLIntersectionClass) val)
							.getBrowserText());
				} else {
					try {
						bpPropVals.add(((Slot) val).getBrowserText());
					} catch (Exception e) {
						// Tried to assume its a slot and failed, defaulting to
						// toString
						bpPropVals.add(val.toString());
					}

				}
			}

			bpProps.put(slot.getName(), bpPropVals);
			bpPropVals = new ArrayList<String>();
		}

		return bpProps;
	}

	/**
	 * @return the protegeKnowledgeBases
	 */
	public CacheMap getProtegeKnowledgeBases() {
		return protegeKnowledgeBases;
	}

	/**
	 * @param protegeKnowledgeBases
	 *            the protegeKnowledgeBases to set
	 */
	public void setProtegeKnowledgeBases(CacheMap protegeKnowledgeBases) {
		this.protegeKnowledgeBases = protegeKnowledgeBases;
	}
}
