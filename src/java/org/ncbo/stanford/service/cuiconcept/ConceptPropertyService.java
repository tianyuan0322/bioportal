/**
 * 
 */
package org.ncbo.stanford.service.cuiconcept;

import java.util.Collection;
import java.util.List;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.enumeration.PropertyTypeEnum;

/**
 * @author s.reddy
 * 
 */
public interface ConceptPropertyService {

	public List<ClassBean> findConceptsByProperty(PropertyTypeEnum type,
			String value, Collection<Integer> ontologyIds);
}
