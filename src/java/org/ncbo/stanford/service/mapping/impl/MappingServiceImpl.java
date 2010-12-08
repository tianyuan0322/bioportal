package org.ncbo.stanford.service.mapping.impl;

import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.mapping.MappingConceptStatsBean;
import org.ncbo.stanford.bean.mapping.MappingOntologyStatsBean;
import org.ncbo.stanford.bean.mapping.MappingParametersBean;
import org.ncbo.stanford.bean.mapping.MappingResultListBean;
import org.ncbo.stanford.bean.mapping.OneToOneMappingBean;
import org.ncbo.stanford.domain.custom.dao.mapping.CustomNcboMappingCountsDAO;
import org.ncbo.stanford.domain.custom.dao.mapping.CustomNcboMappingDAO;
import org.ncbo.stanford.domain.custom.dao.mapping.CustomNcboMappingStatsDAO;
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
	private CustomNcboMappingCountsDAO mappingCountsDAO;
	private CustomNcboMappingStatsDAO mappingStatsDAO;

	public OneToOneMappingBean createMapping(URI source, URI target,
			URI relation, Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, URI dependency, String comment,
			MappingSourceEnum mappingSource, String mappingSourceName,
			String mappingSourcecontactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType)
			throws MappingExistsException {
		// Verify that there's a string provided to avoid null pointer exception
		String mappingSourceStr = "";
		if (mappingSource != null)
			mappingSourceStr = mappingSource.toString();

		return convertToMappingBean(mappingDAO.createMapping(source, target,
				relation, sourceOntologyId, targetOntologyId,
				sourceOntologyVersion, targetOntologyVersion, submittedBy,
				dependency, comment, mappingSourceStr, mappingSourceName,
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
			Integer pageSize, Integer pageNum, MappingParametersBean parameters)
			throws InvalidInputException {
		MappingResultListBean pageMappings = new MappingResultListBean(0);
		Integer totalResults = mappingCountsDAO.getCountMappingsFromOntology(
				ont.getOntologyId(), parameters);

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		List<OneToOneMapping> mappings = mappingDAO.getMappingsFromOntology(ont
				.getOntologyId(), limit, offset, parameters);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsToOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum, MappingParametersBean parameters)
			throws InvalidInputException {
		MappingResultListBean pageMappings = new MappingResultListBean(0);
		Integer totalResults = mappingCountsDAO.getCountMappingsToOntology(ont
				.getOntologyId(), parameters);

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		List<OneToOneMapping> mappings = mappingDAO.getMappingsToOntology(ont
				.getOntologyId(), limit, offset, parameters);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsBetweenOntologies(
			OntologyBean sourceOnt, OntologyBean targetOnt, Integer pageSize,
			Integer pageNum, Boolean unidirectional,
			MappingParametersBean parameters) throws InvalidInputException {
		MappingResultListBean pageMappings = new MappingResultListBean(0);
		Integer totalResults = mappingCountsDAO
				.getCountMappingsBetweenOntologies(sourceOnt.getOntologyId(),
						targetOnt.getOntologyId(), unidirectional, parameters);

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		List<OneToOneMapping> mappings = mappingDAO
				.getMappingsBetweenOntologies(sourceOnt.getOntologyId(),
						targetOnt.getOntologyId(), unidirectional, limit,
						offset, parameters);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsForOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum, MappingParametersBean parameters)
			throws InvalidInputException {
		MappingResultListBean pageMappings = new MappingResultListBean(0);
		Integer totalResults = mappingCountsDAO.getCountMappingsForOntology(ont
				.getOntologyId(), parameters);

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		List<OneToOneMapping> mappings = mappingDAO.getMappingsForOntology(ont
				.getOntologyId(), limit, offset, parameters);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsForParameters(Integer pageSize,
			Integer pageNum, MappingParametersBean parameters)
			throws InvalidInputException {
		MappingResultListBean pageMappings = new MappingResultListBean(0);
		Integer totalResults = mappingCountsDAO
				.getCountMappingsForParameters(parameters);

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		List<OneToOneMapping> mappings = mappingDAO.getMappingsForParameters(
				limit, offset, parameters);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsForConcept(OntologyBean ont,
			ClassBean concept, Integer pageSize, Integer pageNum,
			MappingParametersBean parameters) throws InvalidInputException {
		MappingResultListBean pageMappings = new MappingResultListBean(0);
		Integer totalResults = mappingCountsDAO.getCountMappingsForConcept(ont
				.getOntologyId(), concept.getFullId(), parameters);

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		List<OneToOneMapping> mappings = mappingDAO.getMappingsForConcept(ont
				.getOntologyId(), concept.getFullId(), limit, offset,
				parameters);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsFromConcept(OntologyBean ont,
			ClassBean concept, Integer pageSize, Integer pageNum,
			MappingParametersBean parameters) throws InvalidInputException {
		MappingResultListBean pageMappings = new MappingResultListBean(0);
		Integer totalResults = mappingCountsDAO.getCountMappingsFromConcept(ont
				.getOntologyId(), concept.getFullId(), parameters);

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		List<OneToOneMapping> mappings = mappingDAO.getMappingsFromConcept(ont
				.getOntologyId(), concept.getFullId(), limit, offset,
				parameters);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsToConcept(OntologyBean ont,
			ClassBean concept, Integer pageSize, Integer pageNum,
			MappingParametersBean parameters) throws InvalidInputException {
		MappingResultListBean pageMappings = new MappingResultListBean(0);
		Integer totalResults = mappingCountsDAO.getCountMappingsToConcept(ont
				.getOntologyId(), concept.getFullId(), parameters);

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		List<OneToOneMapping> mappings = mappingDAO.getMappingsToConcept(ont
				.getOntologyId(), concept.getFullId(), limit, offset,
				parameters);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public Page<OneToOneMappingBean> getMappingsBetweenConcepts(
			OntologyBean sourceOnt, OntologyBean targetOnt,
			ClassBean sourceConcept, ClassBean targetConcept,
			Boolean unidirectional, Integer pageSize, Integer pageNum,
			MappingParametersBean parameters) throws InvalidInputException {
		MappingResultListBean pageMappings = new MappingResultListBean(0);
		Integer totalResults = mappingCountsDAO
				.getCountMappingsBetweenConcepts(sourceOnt.getOntologyId(),
						targetOnt.getOntologyId(), sourceConcept.getFullId(),
						targetConcept.getFullId(), unidirectional, parameters);

		Paginator<OneToOneMappingBean> p = new PaginatorImpl<OneToOneMappingBean>(
				pageMappings, pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;
		int limit = (offset + pageSize > totalResults) ? totalResults : offset
				+ pageSize;

		List<OneToOneMapping> mappings = mappingDAO.getMappingsBetweenConcepts(
				sourceOnt.getOntologyId(), targetOnt.getOntologyId(),
				sourceConcept.getFullId(), targetConcept.getFullId(),
				unidirectional, limit, offset, parameters);

		for (OneToOneMapping mapping : mappings) {
			pageMappings.add(convertToMappingBean(mapping));
		}

		return p.getCurrentPage(pageNum);
	}

	public List<OneToOneMappingBean> getRecentMappings(Integer limit)
			throws InvalidInputException {
		ArrayList<OneToOneMappingBean> mappings = new ArrayList<OneToOneMappingBean>();

		for (OneToOneMapping mapping : mappingStatsDAO.getRecentMappings(limit)) {
			mappings.add(convertToMappingBean(mapping));
		}

		return mappings;
	}
	
	public Integer getTotalMappingsCount() {
		return mappingStatsDAO.getTotalMappingsCount();
	}
	
	public List<MappingOntologyStatsBean> getOntologiesMappingCount() {
		return mappingStatsDAO.getOntologiesMappingCount();
	}
	
	public List<MappingOntologyStatsBean> getOntologyMappingCount(Integer ontologyId) {
		return mappingStatsDAO.getOntologyMappingCount(ontologyId);
	}
	
	public List<MappingConceptStatsBean> getOntologyConceptsCount(Integer ontologyId, Integer limit) {
		return mappingStatsDAO.getOntologyConceptsCount(ontologyId, limit);
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
				.getCreatedInTargetOntologyVersion());
		mb.setDate(mapping.getDate());
		mb.setDependency(mapping.getDependency());
		mb.setId(mapping.getId());
		if (mapping.getMappingSource() != null
				&& mapping.getMappingSource().length() > 0) {
			mb.setMappingSource(MappingSourceEnum.valueOf(mapping
					.getMappingSource().toUpperCase()));
		}
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
				.getCreatedInTargetOntologyVersion());
		otom.setDate(mapping.getDate());
		otom.setDependency(mapping.getDependency());
		otom.setId(mapping.getId());
		if (mapping.getMappingSource() != null
				&& !mapping.getMappingSource().toString().isEmpty()) {
			otom.setMappingSource(mapping.getMappingSource().toString());
		}
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

	/**
	 * @param mappingCountsDAO
	 *            the mappingCountsDAO to set
	 */
	public void setMappingCountsDAO(CustomNcboMappingCountsDAO mappingCountsDAO) {
		this.mappingCountsDAO = mappingCountsDAO;
	}

	/**
	 * @param mappingStatsDAO
	 *            the mappingStatsDAO to set
	 */
	public void setMappingStatsDAO(CustomNcboMappingStatsDAO mappingStatsDAO) {
		this.mappingStatsDAO = mappingStatsDAO;
	}

}
