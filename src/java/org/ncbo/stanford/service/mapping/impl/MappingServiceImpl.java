package org.ncbo.stanford.service.mapping.impl;

import java.util.ArrayList;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.mapping.MappingBeanResultListBean;
import org.ncbo.stanford.bean.mapping.OneToOneMappingBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboMappingDAO;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.util.paginator.Paginator;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.paginator.impl.PaginatorImpl;
import org.openrdf.model.URI;

public class MappingServiceImpl implements MappingService {

	private CustomNcboMappingDAO mappingDAO;

	public OneToOneMappingBean createMapping(URI source, URI target,
			URI relation, Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, String comment,
			MappingSourceEnum mappingSource, String mappingSourceName,
			String mappingSourcecontactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType)
			throws MappingExistsException {
		return convertToMappingBean(mappingDAO.createMapping(source, target,
				relation, sourceOntologyId, targetOntologyId,
				sourceOntologyVersion, targetOntologyVersion, submittedBy,
				comment, mappingSource.toString(), mappingSourceName,
				mappingSourcecontactInfo, mappingSourceSite,
				mappingSourceAlgorithm, mappingType));
	}

	public OneToOneMappingBean createMapping(OneToOneMappingBean mapping)
			throws MappingExistsException {
		OneToOneMapping newMapping = mappingDAO
				.createMapping(convertToMappingEntity(mapping));
		return convertToMappingBean(newMapping);
	}

	public OneToOneMappingBean getMapping(URI id)
			throws MappingMissingException {
		OneToOneMapping retrievedMapping = mappingDAO.getMapping(id);
		return convertToMappingBean(retrievedMapping);
	}

	public Page<OneToOneMappingBean> getMappingsFromOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum) throws InvalidInputException {
		MappingBeanResultListBean pageMappings = new MappingBeanResultListBean(
				0);
		Integer totalResults = mappingDAO.getCountMappingsFromOntology(ont
				.getOntologyId());

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		ArrayList<OneToOneMapping> mappings = mappingDAO
				.getMappingsFromOntology(ont.getOntologyId(), limit, offset);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsToOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum) throws InvalidInputException {
		MappingBeanResultListBean pageMappings = new MappingBeanResultListBean(
				0);
		Integer totalResults = mappingDAO.getCountMappingsToOntology(ont
				.getOntologyId());

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		ArrayList<OneToOneMapping> mappings = mappingDAO.getMappingsToOntology(
				ont.getOntologyId(), limit, offset);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsBetweenOntologies(
			OntologyBean sourceOnt, OntologyBean targetOnt, Integer pageSize,
			Integer pageNum, Boolean unidirectional)
			throws InvalidInputException {
		MappingBeanResultListBean pageMappings = new MappingBeanResultListBean(
				0);
		Integer totalResults = mappingDAO.getCountMappingsBetweenOntologies(
				sourceOnt.getOntologyId(), targetOnt.getOntologyId(),
				unidirectional);

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		ArrayList<OneToOneMapping> mappings = mappingDAO
				.getMappingsBetweenOntologies(sourceOnt.getOntologyId(),
						targetOnt.getOntologyId(), unidirectional, limit,
						offset);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsForOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum) throws InvalidInputException {
		MappingBeanResultListBean pageMappings = new MappingBeanResultListBean(
				0);
		Integer totalResults = mappingDAO.getCountMappingsForOntology(ont
				.getOntologyId());

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		ArrayList<OneToOneMapping> mappings = mappingDAO
				.getMappingsForOntology(ont.getOntologyId(), limit, offset);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public OneToOneMappingBean updateMapping(URI id, OneToOneMappingBean mapping)
			throws MappingMissingException {
		OneToOneMapping updatedMapping = mappingDAO.updateMapping(id,
				convertToMappingEntity(mapping));
		return convertToMappingBean(updatedMapping);
	}

	public void deleteMapping(URI id) throws MappingMissingException {
		mappingDAO.deleteMapping(id);
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
		mb.setMappingSource(MappingSourceEnum.valueOf(mapping
				.getMappingSource().toUpperCase()));
		mb.setMappingSourceName(mapping.getMappingSourceName());
		mb.setMappingSourceAlgorithm(mapping.getMappingSourceAlgorithm());
		mb.setMappingSourcecontactInfo(mapping.getMappingSourcecontactInfo());
		mb.setMappingSourceSite(mapping.getMappingSourceSite());
		mb.setMappingType(mapping.getMappingType());
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
		otom.setMappingSource(mapping.getMappingSource().toString());
		otom.setMappingSourceAlgorithm(mapping.getMappingSourceAlgorithm());
		otom.setMappingSourcecontactInfo(mapping.getMappingSourcecontactInfo());
		otom.setMappingSourceSite(mapping.getMappingSourceSite());
		otom.setMappingType(mapping.getMappingType());
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
