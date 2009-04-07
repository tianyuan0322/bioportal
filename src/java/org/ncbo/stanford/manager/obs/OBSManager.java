package org.ncbo.stanford.manager.obs;

import java.util.List;

import org.ncbo.stanford.bean.concept.ClassBean;

public interface OBSManager {

	public List<ClassBean> findParents(Integer ontologyVersionId,
			String conceptId) throws Exception;

	public List<ClassBean> findChildren(Integer ontologyVersionId,
			String conceptId) throws Exception;

	public List<ClassBean> findRootPaths(Integer ontologyVersionId,
			String conceptId) throws Exception;
}
