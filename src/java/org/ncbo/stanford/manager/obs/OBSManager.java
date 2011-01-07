package org.ncbo.stanford.manager.obs;

import java.util.List;
import java.util.Set;

import org.ncbo.stanford.bean.concept.ClassBean;

public interface OBSManager {

	public Integer findLatestOntologyVersion(Integer ontologyId)
			throws Exception;

	public List<ClassBean> findParents(Integer ontologyVersionId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception;

	public List<ClassBean> findChildren(Integer ontologyVersionId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception;

	public Set<String> findChildrenConceptIds(Integer ontologyVersionId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception;

	public List<ClassBean> findRootPaths(Integer ontologyVersionId,
			String conceptId, Integer offset, Integer limit, String delim)
			throws Exception;

	public List<ClassBean> findSiblings(Integer ontologyVersionId,
			String conceptId, Integer level, Integer offset) throws Exception;

	public List<ClassBean> findLeaves(Integer ontologyVersionId,
			String conceptId, Integer offset, Integer limit, String delim)
			throws Exception;

	public List<ClassBean> findAllConcepts(Integer ontologyVersionId,
			Integer offset, Integer limit) throws Exception;
}
