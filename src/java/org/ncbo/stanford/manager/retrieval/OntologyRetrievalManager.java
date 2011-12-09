package org.ncbo.stanford.manager.retrieval;

import java.util.Iterator;
import java.util.List;

import org.ncbo.stanford.bean.NamespaceBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.bean.concept.PropertyBean;
import org.ncbo.stanford.util.paginator.impl.Page;

/**
 * An interface designed to provide an abstraction layer to ontology and concept
 * operations. The service layer will consume this interface instead of directly
 * calling a specific implementation (i.e. LexGrid, Protege etc.). This
 * interface should be used by internal services and should not be exposed to
 * upper layers.
 * 
 * @author Michael Dorf
 */
public interface OntologyRetrievalManager {
	public ClassBean findConcept(OntologyBean ob, String conceptId,
			Integer maxNumChildren, boolean light, boolean noRelations,
			boolean withClassProperties) throws Exception;

	public ClassBean findRootConcept(OntologyBean ob, Integer maxNumChildren,
			boolean light) throws Exception;

	public ClassBean findPathFromRoot(OntologyBean ob, String conceptId,
			boolean light) throws Exception;

	public boolean hasParent(OntologyBean ob, String childConceptId,
			String parentConceptId) throws Exception;

	/**
	 * Retrieve all of the classes (concepts, in LexGrid) in the ontology.
	 * Returning as an Iterator allows the implementations to do the conversion
	 * to ClassBean one at a time, potentially reducing the space required.
	 * (Provided the client of this manager doesn't accumulate the ClassBean
	 * objects.)
	 * <p>
	 * Note that the iterator's <code>next()</code> and <code>hasNext()</code>
	 * methods may end up throwing a
	 * {@link org.ncbo.stanford.exception.BPRuntimeException} if something goes
	 * wrong with retrieving the back end class representation, or with
	 * converting it to a ClassBean.
	 * 
	 * @throws Exception
	 */
	public Iterator<ClassBean> listAllClasses(OntologyBean ob) throws Exception;

	public InstanceBean findInstanceById(OntologyBean ontologyBean,
			String instanceId) throws Exception;

	public Page<ClassBean> findAllConcepts(OntologyBean ob,
			Integer maxNumChildren, Integer pageSize, Integer pageNum)
			throws Exception;

	public Page<InstanceBean> findInstancesByConceptId(
			OntologyBean ontologyBean, String instanceId, Integer pageSize,
			Integer pageNum) throws Exception;

	/**
	 * Retrieve all available properties with their associate metadata for a
	 * given ontology
	 * 
	 * @param ob
	 * @return
	 * 
	 * @throws Exception
	 */
	public List<PropertyBean> findProperties(OntologyBean ob) throws Exception;

	/**
	 * Retrieve all available namespaces with prefixes and uris for a given
	 * ontology
	 * 
	 * @param ob
	 * @return List<NamespaceBean>
	 * 
	 * @throws Exception
	 */
	public List<NamespaceBean> findNamespaces(OntologyBean ob) throws Exception;
}
