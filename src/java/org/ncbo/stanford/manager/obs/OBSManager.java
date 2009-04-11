package org.ncbo.stanford.manager.obs;

import java.util.List;

import org.ncbo.stanford.bean.concept.ClassBean;

public interface OBSManager {

	public List<ClassBean> findParents(String ontologyVersionId,
			String conceptId) throws Exception;

	public List<ClassBean> findChildren(String ontologyVersionId,
			String conceptId) throws Exception;

	public List<ClassBean> findRootPaths(String ontologyVersionId,
			String conceptId) throws Exception;

	public List<ClassBean> findSiblings(String ontologyVersionId,
			String conceptId, String level) throws Exception;

	public List<ClassBean> findLeaves(String ontologyVersionId,
			String conceptId) throws Exception;
}
