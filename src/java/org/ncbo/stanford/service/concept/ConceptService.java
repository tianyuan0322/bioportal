/**
 * 
 */
package org.ncbo.stanford.service.concept;

import java.util.List;

import org.ncbo.stanford.bean.OntologyIdBean;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.bean.concept.ClassBean;

/**
 * @author nickgriffith
 * 
 */
public interface ConceptService {

	public ClassBean findRootConcept(Integer ontologyVersionId)
			throws Exception;

	public ClassBean findConcept(Integer ontologyVersionId, String conceptId)
			throws Exception;

	public ClassBean findPathFromRoot(Integer ontologyVersionId,
			String conceptId, boolean light) throws Exception;

	
	
	
	public List<ClassBean> findParents(OntologyVersionIdBean ontologyVesionId,
			String conceptId) throws Exception;

	public List<ClassBean> findParents(OntologyIdBean ontologyId,
			String conceptId) throws Exception;

	public List<ClassBean> findChildren(OntologyVersionIdBean ontologyVesionId,
			String conceptId) throws Exception;
}
