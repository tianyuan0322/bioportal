/**
 * 
 */
package org.ncbo.stanford.service.concept;

import java.util.List;
import java.util.Set;

import org.ncbo.stanford.bean.AbstractIdBean;
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
			boolean withClassProperties) throws Exception;

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

	public List<ClassBean> findRootPaths(AbstractIdBean id, String conceptId,
			Integer offset, Integer limit, String delim) throws Exception;

	public List<ClassBean> findSiblings(
			OntologyVersionIdBean ontologyVersionId, String conceptId,
			Integer level, Integer offset) throws Exception;

	public List<ClassBean> findSiblings(OntologyIdBean ontologyId,
			String conceptId, Integer level, Integer offset) throws Exception;

	public List<ClassBean> findLeaves(AbstractIdBean id, String conceptId,
			Integer offset, Integer limit, String delim) throws Exception;

	public Page<ClassBean> findAllConcepts(Integer ontologyVersionId,
			Integer pageSize, Integer pageNum) throws Exception;

	public InstanceBean findInstanceById(Integer ontologyVerId,
			String instanceId) throws Exception;

	public Page<InstanceBean> findInstancesByConceptId(Integer ontologyVerId,
			String instanceId, Integer pageSize, Integer pageNum)
			throws Exception;
}
