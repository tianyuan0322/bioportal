package org.ncbo.stanford.service.mapping;

import org.castor.mapping.MappingSource;
import org.ncbo.stanford.bean.mapping.OneToOneMappingBean;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.openrdf.model.URI;

public interface MappingService {

	/**
	 * Create a mapping by passing in individual properties.
	 * 
	 * @param source
	 * @param target
	 * @param relation
	 * @param sourceOntologyId
	 * @param targetOntologyId
	 * @param sourceOntologyVersion
	 * @param targetOntologyVersion
	 * @param submittedBy
	 * @param comment
	 * @param mappingSource
	 * @param mappingType
	 * @return
	 */
	public OneToOneMapping createMapping(URI source, URI target, URI relation,
			Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, String comment, MappingSource mappingSource,
			String mappingType);
	
	/**
	 * Create a mapping by passing a mapping bean.
	 * 
	 * @param mapping
	 * @return
	 */
	public OneToOneMappingBean createMapping(OneToOneMappingBean mapping);
	
	/**
	 * Get a mapping with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public OneToOneMappingBean getMapping(URI id);
	
	/**
	 * Update mapping with given id using a mapping bean.
	 * @param id
	 * @param mapping
	 * @return
	 */
	public OneToOneMappingBean updateMapping(URI id, OneToOneMappingBean mapping);
	
	/**
	 * Delete mapping with given id.
	 * 
	 * @param id
	 */
	public void deleteMapping(URI id);
	
}