package org.ncbo.stanford.service.mapping;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.mapping.MappingParametersBean;
import org.ncbo.stanford.bean.mapping.OneToOneMappingBean;
import org.ncbo.stanford.bean.obs.ConceptBean;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.util.paginator.impl.Page;
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
	public OneToOneMappingBean createMapping(URI source, URI target,
			URI relation, Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, String comment,
			MappingSourceEnum mappingSource, String mappingSourceName,
			String mappingSourcecontactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType)
			throws MappingExistsException;

	/**
	 * Create a mapping by passing a mapping bean.
	 * 
	 * @param mapping
	 * @return
	 */
	public OneToOneMappingBean createMapping(OneToOneMappingBean mapping)
			throws MappingExistsException;

	/**
	 * Get a mapping with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public OneToOneMappingBean getMapping(URI id)
			throws MappingMissingException;

	/**
	 * Update mapping with given id using a mapping bean.
	 * 
	 * @param id
	 * @param mapping
	 * @return
	 */
	public OneToOneMappingBean updateMapping(URI id, OneToOneMappingBean mapping)
			throws MappingMissingException;

	/**
	 * Delete mapping with given id.
	 * 
	 * @param id
	 */
	public void deleteMapping(URI id) throws MappingMissingException;

	/**
	 * Get all mappings from a given ontology.
	 * 
	 * @param ont
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<OneToOneMappingBean> getMappingsFromOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum, MappingParametersBean parameters)
			throws InvalidInputException;

	/**
	 * Get all mappings to a given ontology.
	 * 
	 * @param ont
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<OneToOneMappingBean> getMappingsToOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum, MappingParametersBean parameters)
			throws InvalidInputException;

	/**
	 * Get mappings between two ontologies. Pass 'unidirectional' if you only
	 * want one-way mappings.
	 * 
	 * @param sourceOnt
	 * @param targetOnt
	 * @param unidirectional
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<OneToOneMappingBean> getMappingsBetweenOntologies(
			OntologyBean sourceOnt, OntologyBean targetOnt, Integer pageSize,
			Integer pageNum, Boolean unidirectional,
			MappingParametersBean parameters) throws InvalidInputException;

	/**
	 * Get all mappings either from or to a given ontology.
	 * 
	 * @param ont
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<OneToOneMappingBean> getMappingsForOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum, MappingParametersBean parameters)
			throws InvalidInputException;

	/**
	 * Get all mappings for a given concept.
	 * 
	 * @param concept
	 * @return
	 * @throws InvalidInputException
	 */
	public List<OneToOneMappingBean> getMappingsForConcept(ConceptBean concept,
			MappingParametersBean parameters) throws InvalidInputException;

	/**
	 * Get all mappings from a given concept to other concepts.
	 * 
	 * @param concept
	 * @return
	 * @throws InvalidInputException
	 */
	public List<OneToOneMappingBean> getMappingsFromConcept(
			ConceptBean concept, MappingParametersBean parameters)
			throws InvalidInputException;

	/**
	 * Get all mappings from other concepts to a given concept.
	 * 
	 * @param concept
	 * @return
	 * @throws InvalidInputException
	 */
	public List<OneToOneMappingBean> getMappingsToConcept(ConceptBean concept,
			MappingParametersBean parameters) throws InvalidInputException;

	/**
	 * Get all mappings between two given concepts.
	 * 
	 * @param sourceConcept
	 * @param targetConcept
	 * @return
	 * @throws InvalidInputException
	 */
	public List<OneToOneMappingBean> getMappingsBetweenConcepts(
			ConceptBean sourceConcept, ConceptBean targetConcept,
			MappingParametersBean parameters) throws InvalidInputException;

}
