/**
 * 
 */
package org.ncbo.stanford.service.concept;

import java.util.List;
import java.util.Set;

import org.ncbo.stanford.bean.OntologyIdBean;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.util.paginator.impl.Page;

/**
 * @author nickgriffith
 * @author Michael Dorf
 * 
 */
public interface ConceptService {

	public ClassBean findRootConcept(Integer ontologyVersionId,
			Integer maxNumChildren, boolean light) throws Exception;

	public ClassBean findConcept(Integer ontologyVersionId, String conceptId,
			Integer maxNumChildren, boolean light, boolean noRelations,
			boolean isIncludeInstances) throws Exception;

	public ClassBean findPathFromRoot(Integer ontologyVersionId,
			String conceptId, boolean light, Integer maxNumChildren)
			throws Exception;

	public List<ClassBean> findParents(OntologyVersionIdBean ontologyVersionId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception;

	public List<ClassBean> findParents(OntologyIdBean ontologyId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception;

	public List<ClassBean> findChildren(
			OntologyVersionIdBean ontologyVersionId, String conceptId,
			Integer level, Integer offset, Integer limit) throws Exception;

	public List<ClassBean> findChildren(OntologyIdBean ontologyId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception;

	public Set<String> findChildrenConceptIds(OntologyIdBean ontologyId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception;

	public List<ClassBean> findRootPaths(
			OntologyVersionIdBean ontologyVersionId, String conceptId,
			Integer offset, Integer limit) throws Exception;

	public List<ClassBean> findRootPaths(OntologyIdBean ontologyVersionId,
			String conceptId, Integer offset, Integer limit) throws Exception;

	public List<ClassBean> findSiblings(
			OntologyVersionIdBean ontologyVersionId, String conceptId,
			Integer level, Integer offset) throws Exception;

	public List<ClassBean> findSiblings(OntologyIdBean ontologyId,
			String conceptId, Integer level, Integer offset) throws Exception;

	public List<ClassBean> findLeaves(OntologyVersionIdBean ontologyVersionId,
			String conceptId, Integer offset, Integer limit) throws Exception;

	public List<ClassBean> findLeaves(OntologyIdBean ontologyVersionId,
			String conceptId, Integer offset, Integer limit) throws Exception;

	public Page<ClassBean> findAllConcepts(
			OntologyVersionIdBean ontologyVersionId, Integer offset,
			Integer limit) throws Exception;

	public List<ClassBean> findAllConcepts(OntologyIdBean ontologyId,
			Integer offset, Integer limit) throws Exception;

	public InstanceBean findInstanceById(Integer ontologyVerId,
			String instanceId) throws Exception;
}
