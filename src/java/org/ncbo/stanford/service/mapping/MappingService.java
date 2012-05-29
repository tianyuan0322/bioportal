package org.ncbo.stanford.service.mapping;

import java.util.List;
import java.util.Set;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.ConceptOntologyPairBean;
import org.ncbo.stanford.bean.mapping.MappingBean;
import org.ncbo.stanford.bean.mapping.MappingConceptStatsBean;
import org.ncbo.stanford.bean.mapping.MappingOntologyStatsBean;
import org.ncbo.stanford.bean.mapping.MappingUserStatsBean;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.sparql.MappingFilterGenerator;
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
	 * @throws Exception
	 */
	public MappingBean createMapping(List<URI> source, List<URI> target,
			URI relation, URI sourceOntology, URI targetOntology,
			URI sourceOntologyVersion, URI targetOntologyVersion,
			Integer submittedBy, URI dependency, String comment,
			MappingSourceEnum mappingSource, String mappingSourceName,
			String mappingSourcecontactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType)
			throws MappingExistsException, Exception;

	/**
	 * Create a mapping by passing a mapping bean.
	 * 
	 * @param mapping
	 * @return
	 * @throws Exception
	 */
	public MappingBean createMapping(MappingBean mapping)
			throws MappingExistsException, Exception;

	/**
	 * Get a mapping with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public MappingBean getMapping(URI id) throws MappingMissingException;

	/**
	 * Update mapping with given id using a mapping bean.
	 * 
	 * @param id
	 * @param mapping
	 * @return
	 * @throws Exception
	 */
	public MappingBean updateMapping(URI id, MappingBean mapping)
			throws MappingMissingException, Exception;

	/**
	 * Delete mapping with given id.
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteMapping(URI id) throws MappingMissingException, Exception;

	/**
	 * Get all mappings from a given ontology.
	 * 
	 * @param ont
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<MappingBean> getMappingsFromOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum, MappingFilterGenerator parameters)
			throws InvalidInputException;

	/**
	 * Get all mappings to a given ontology.
	 * 
	 * @param ont
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<MappingBean> getMappingsToOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum, MappingFilterGenerator parameters)
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
	public Page<MappingBean> getMappingsBetweenOntologies(
			OntologyBean sourceOnt, OntologyBean targetOnt, Integer pageSize,
			Integer pageNum, Boolean unidirectional,
			MappingFilterGenerator parameters) throws InvalidInputException;

	/**
	 * Get ranked mappings between two ontologies. Ranked mappings look for
	 * terms that are mapped to multiple terms in the target ontology and sort
	 * these first.
	 * 
	 * @param sourceOnt
	 * @param targetOnt
	 * @param unidirectional
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<MappingBean> getRankedMappingsBetweenOntologies(
			OntologyBean sourceOnt, OntologyBean targetOnt, Integer pageSize,
			Integer pageNum, MappingFilterGenerator parameters)
			throws InvalidInputException;

	/**
	 * Get all mappings either from or to a given ontology.
	 * 
	 * @param ont
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<MappingBean> getMappingsForOntology(OntologyBean ont,
			Integer pageSize, Integer pageNum, MappingFilterGenerator parameters)
			throws InvalidInputException;

	/**
	 * Get a page of mappings based on parameters-only (not using ontology id or
	 * concept id).
	 * 
	 * @param pageSize
	 * @param pageNum
	 * @param parameters
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<MappingBean> getMappingsForParameters(Integer pageSize,
			Integer pageNum, MappingFilterGenerator parameters)
			throws InvalidInputException;

	/**
	 * Get all mappings for a given concept.
	 * 
	 * @param concept
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<MappingBean> getMappingsForConcept(OntologyBean ont,
			ClassBean concept, Integer pageSize, Integer pageNum,
			MappingFilterGenerator parameters) throws InvalidInputException;

	/**
	 * Get mappings for an arbitrary set of ontology/concept pairs.
	 * 
	 * @param conceptOntologyPairs
	 * @return
	 * @throws MappingMissingException
	 */
	public List<MappingBean> getMappingsForConceptSet(
			Set<ConceptOntologyPairBean> conceptOntologyPairs)
			throws MappingMissingException;

	/**
	 * Get all mappings from a given concept to other concepts.
	 * 
	 * @param concept
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<MappingBean> getMappingsFromConcept(OntologyBean ont,
			ClassBean concept, Integer pageSize, Integer pageNum,
			MappingFilterGenerator parameters) throws InvalidInputException;

	/**
	 * Get all mappings from other concepts to a given concept.
	 * 
	 * @param concept
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<MappingBean> getMappingsToConcept(OntologyBean ont,
			ClassBean concept, Integer pageSize, Integer pageNum,
			MappingFilterGenerator parameters) throws InvalidInputException;

	/**
	 * Get all mappings between two given concepts.
	 * 
	 * @param sourceConcept
	 * @param targetConcept
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<MappingBean> getMappingsBetweenConcepts(OntologyBean sourceOnt,
			OntologyBean targetOnt, ClassBean sourceConcept,
			ClassBean targetConcept, Boolean unidirectional, Integer pageSize,
			Integer pageNum, MappingFilterGenerator parameters)
			throws InvalidInputException;

	/*******************************************************************
	 * 
	 * Stats about mappings
	 * 
	 *******************************************************************/

	/**
	 * Get all recent mappings.
	 * 
	 * @param limit
	 * @return
	 * @throws InvalidInputException
	 */
	public List<MappingBean> getRecentMappings(Integer limit)
			throws InvalidInputException;

	/**
	 * Gets an integer representing total number of mappings in the system.
	 * 
	 * @return
	 */
	public Integer getTotalMappingsCount();

	/**
	 * Get a list of all ontologies and the number of mappings they have.
	 * 
	 * @return
	 */
	public List<MappingOntologyStatsBean> getOntologiesMappingCount();

	/**
	 * Get a list of all ontologies mapped to/from a given ontology with counts.
	 * 
	 * @param ontologyId
	 * @return
	 */
	public List<MappingOntologyStatsBean> getOntologyMappingCount(
			URI ontologyId);

	/**
	 * Get a list of concepts and their mapping counts for a given ontology.
	 * 
	 * @param ontologyId
	 * @param limit
	 * @return
	 */
	public List<MappingConceptStatsBean> getOntologyConceptsCount(
			URI ontologyId, Integer limit);

	/**
	 * Get a list of users who have created mappings for an ontology plus counts
	 * for how many mappings they've created.
	 * 
	 * @param ontologyId
	 * @return
	 */
	public List<MappingUserStatsBean> getOntologyUserCount(URI ontologyId);

	/**
	 * Get a list of users who have created mappings for an ontology plus counts
	 * for how many mappings they've created.
	 * 
	 * @param ontologyId
	 * @param targetOntology
	 * @return
	 */
	public List<MappingUserStatsBean> getOntologyUserCount(URI ontologyId,
			URI targetOntology);

}
