package org.ncbo.stanford.service.mapping.impl;

import org.castor.mapping.MappingSource;
import org.ncbo.stanford.bean.mapping.OneToOneMappingBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboMappingDAO;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.openrdf.model.URI;

public class MappingServiceImpl {

	CustomNcboMappingDAO mappingDAO;

	public OneToOneMapping createMapping(URI source, URI target, URI relation,
			Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, String comment, MappingSource mappingSource,
			String mappingType) {
		return mappingDAO.createMapping(source, target, relation,
				sourceOntologyId, targetOntologyId, sourceOntologyVersion,
				targetOntologyVersion, submittedBy, comment, mappingSource,
				mappingType);
	}

	public OneToOneMappingBean createMapping(OneToOneMappingBean mapping) {
		OneToOneMapping newMapping = mappingDAO
				.createMapping(convertToMappingEntity(mapping));
		return convertToMappingBean(newMapping);
	}

	public OneToOneMappingBean getMapping(URI id) {
		OneToOneMapping retrievedMapping = mappingDAO.getMapping(id);
		return convertToMappingBean(retrievedMapping);
	}

	public OneToOneMappingBean updateMapping(URI id, OneToOneMappingBean mapping) {
		OneToOneMapping updatedMapping = mappingDAO.updateMapping(id, convertToMappingEntity(mapping));
		return convertToMappingBean(updatedMapping);
	}

	public void deleteMapping(URI id) {

	}

	// Private methods
	private OneToOneMappingBean convertToMappingBean(OneToOneMapping mapping) {
		OneToOneMappingBean mb = new OneToOneMappingBean();

		// Set all properties
		mb.setComment(mapping.getComment());
		mb.setCreatedInSourceOntologyVersion(mapping
				.getCreatedInSourceOntologyVersion());
		mb.setCreatedInTargetOntologyVersion(mapping
				.getCreatedInSourceOntologyVersion());
		mb.setDate(mapping.getDate());
		mb.setDependency(mapping.getDependency());
		mb.setId(mapping.getId());
		mb.setRelation(mapping.getRelation());
		mb.setSource(mapping.getSource());
		mb.setSourceOntologyId(mapping.getSourceOntologyId());
		mb.setSubmittedBy(mapping.getSubmittedBy());
		mb.setTarget(mapping.getTarget());
		mb.setTargetOntologyId(mapping.getTargetOntologyId());

		return mb;
	}

	private OneToOneMapping convertToMappingEntity(OneToOneMappingBean mapping) {
		OneToOneMapping otom = new OneToOneMapping();

		// Set all properties
		otom.setComment(mapping.getComment());
		otom.setCreatedInSourceOntologyVersion(mapping
				.getCreatedInSourceOntologyVersion());
		otom.setCreatedInTargetOntologyVersion(mapping
				.getCreatedInSourceOntologyVersion());
		otom.setDate(mapping.getDate());
		otom.setDependency(mapping.getDependency());
		otom.setId(mapping.getId());
		otom.setRelation(mapping.getRelation());
		otom.setSource(mapping.getSource());
		otom.setSourceOntologyId(mapping.getSourceOntologyId());
		otom.setSubmittedBy(mapping.getSubmittedBy());
		otom.setTarget(mapping.getTarget());
		otom.setTargetOntologyId(mapping.getTargetOntologyId());

		return otom;
	}

	// Auto-generated methods

	/**
	 * @param mappingDAO
	 *            the mappingDAO to set
	 */
	public void setMappingDAO(CustomNcboMappingDAO mappingDAO) {
		this.mappingDAO = mappingDAO;
	}

}