package org.ncbo.stanford.sparql.dao.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.sparql.bean.Mapping;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class CustomNcboMappingDAO extends AbstractNcboMappingDAO {

	public Mapping createMapping(List<URI> source, List<URI> target,
			URI relation, Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, URI dependency, String comment,
			String mappingSource, String mappingSourceName,
			String mappingSourceContactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType)
			throws MappingExistsException {

		Mapping newMapping = new Mapping();

		// Set Mapping properties
		newMapping.setSource(source);
		newMapping.setTarget(target);
		newMapping.setRelation(relation);
		newMapping.setSourceOntologyId(sourceOntologyId);
		newMapping.setTargetOntologyId(targetOntologyId);
		newMapping.setCreatedInSourceOntologyVersion(sourceOntologyVersion);
		newMapping.setCreatedInTargetOntologyVersion(targetOntologyVersion);

		// Set metadata properties
		newMapping.setDependency(dependency);
		newMapping.setSubmittedBy(submittedBy);
		newMapping.setDate(new Date());
		newMapping.setComment(comment);
		newMapping.setMappingType(mappingType);

		// Set mappingSource properties
		newMapping.setMappingSource(mappingSource);
		newMapping.setMappingSourceName(mappingSourceName);
		newMapping.setMappingSourcecontactInfo(mappingSourceContactInfo);
		newMapping.setMappingSourceSite(mappingSourceSite);
		newMapping.setMappingSourceAlgorithm(mappingSourceAlgorithm);

		createMapping(newMapping);

		Mapping mapping = null;
		try {
			mapping = getMapping(newMapping.getId());
		} catch (MappingMissingException e) {
			e.printStackTrace();
		}

		return mapping;
	}

	public Mapping createMapping(Mapping newMapping)
			throws MappingExistsException {
		ValueFactory vf = getRdfStoreManager().getValueFactory();

		ArrayList<Statement> statements = newMapping.toStatements(vf);

		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		for (Statement statement : statements) {
			try {
				con.add(statement, ApplicationConstants.MAPPING_CONTEXT_URI);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		// For mappings that are many-to-many, we add a triple to indicate this.
		// It helps with the lookups while we are retrieving.
		if (newMapping.getSource().size() > 1
				|| newMapping.getTarget().size() > 1) {
			URI predicate = new URIImpl(ApplicationConstants.MAPPING_PREFIX
					+ "is_many_to_many");
			Statement statement = new StatementImpl(newMapping.getId(),
					predicate, vf.createLiteral(true));

			try {
				con.add(statement, ApplicationConstants.MAPPING_CONTEXT_URI);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		Mapping mapping = null;
		try {
			mapping = getMapping(newMapping.getId());
		} catch (MappingMissingException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}

		return mapping;
	}

	public Mapping getMapping(URI id) throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		// Attempt mapping retrieval, return null if failure
		Mapping mapping = null;
		try {
			if (hasMapping(id, con)) {
				List<Mapping> mappings = getMappings(null, 0, "?mappingId = <"
						+ id + ">", null);
				if (mappings != null && !mappings.isEmpty()) {
					mapping = mappings.get(0);
				} else {
					throw new MappingMissingException();
				}
			} else {
				throw new MappingMissingException();
			}
		} catch (InvalidInputException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}

		return mapping;
	}

	public Mapping updateMapping(URI id, List<URI> source, List<URI> target,
			URI relation, Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, URI dependency, String comment,
			String mappingSource, String mappingSourceName,
			String mappingSourcecontactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType)
			throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		if (!hasMapping(id, con)) {
			throw new MappingMissingException();
		}

		Mapping mapping = getMapping(id);

		Mapping updatedMapping = updateMappingEntity(mapping, source, target,
				relation, sourceOntologyId, targetOntologyId,
				sourceOntologyVersion, targetOntologyVersion, submittedBy,
				dependency, comment, mappingSource, mappingSourceName,
				mappingSourcecontactInfo, mappingSourceSite,
				mappingSourceAlgorithm, mappingType);

		updatedMapping = updateMapping(id, updatedMapping);

		cleanup(con);

		return updatedMapping;
	}

	public Mapping updateMapping(URI id, Mapping mapping)
			throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		if (!hasMapping(id, con)) {
			throw new MappingMissingException();
		}

		deleteMappingForUpdate(id);

		try {
			Mapping updatedMapping = createMapping(mapping);

			return updatedMapping;
		} catch (MappingExistsException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}

		return null;
	}

	public void deleteMapping(URI id) throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();
		try {
			if (!hasMapping(id, con)) {
				throw new MappingMissingException();
			}

			Mapping mapping = getMapping(id);

			if (mapping.getDependency() != null) {
				deleteFromTripleStore(con, mapping.getDependency());
			}

			try {
				// Check for dependencies that might not be specified. We do
				// this because the 6mm mappings that we got from the UI
				// database don't contain dependency information and inverse
				// mappings should be removed when their counterpart is removed.
				if (mapping.getDependency() == null) {
					String filter = "?source = <" + mapping.getTarget() + ">";
					filter += " && ?target = <" + mapping.getSource() + ">";
					filter += " && ?sourceOntologyId = "
							+ mapping.getTargetOntologyId();
					filter += " && ?targetOntologyId = "
							+ mapping.getSourceOntologyId();
					filter += " && ?mappingSource = \""
							+ mapping.getMappingSource() + "\"";
					filter += " && ?submittedBy = " + mapping.getSubmittedBy();
					filter += " && ?relation = <" + mapping.getRelation() + ">";

					List<Mapping> inferredDependents = getMappings(null, null,
							filter, null);

					if (inferredDependents != null) {
						for (Mapping dependent : inferredDependents) {
							deleteFromTripleStore(con, dependent.getId());
						}
					}
				}
			} catch (Exception e) {
				// Quash all exceptions for potential dependencies
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			try {
				deleteFromTripleStore(con, id);

				cleanup(con);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteMappingForUpdate(URI id) throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		if (!hasMapping(id, con)) {
			throw new MappingMissingException();
		}

		try {
			deleteFromTripleStore(con, id);

			cleanup(con);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	/*******************************************************************
	 *
	 * Mappings for parameters
	 *
	 *******************************************************************/

	public List<Mapping> getMappingsForParameters(Integer limit,
			Integer offset, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		return getMappings(limit, offset, null, parameters);
	}

	/*******************************************************************
	 *
	 * Mappings for ontologies
	 *
	 *******************************************************************/

	public List<Mapping> getMappingsFromOntology(Integer ontologyId,
			Integer limit, Integer offset, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		String filter = generateOntologySparqlFilter(ontologyId, null, true);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<Mapping> getMappingsToOntology(Integer ontologyId,
			Integer limit, Integer offset, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		String filter = generateOntologySparqlFilter(null, ontologyId, true);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<Mapping> getMappingsBetweenOntologies(Integer sourceOntology,
			Integer targetOntology, Boolean unidirectional, Integer limit,
			Integer offset, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		String filter = generateOntologySparqlFilter(sourceOntology,
				targetOntology, unidirectional);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<Mapping> getMappingsForOntology(Integer ontologyId,
			Integer limit, Integer offset, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		String filter = generateOntologySparqlFilter(ontologyId, null, false);

		return getMappings(limit, offset, filter, parameters);
	}

	/*******************************************************************
	 *
	 * Mappings for concepts
	 *
	 *******************************************************************/

	public List<Mapping> getMappingsForConcept(Integer ontologyId,
			String conceptId, Integer limit, Integer offset,
			SPARQLFilterGenerator parameters) throws InvalidInputException {
		String filter = "("
				+ generateConceptSparqlFilter(conceptId, null, false) + ")";
		filter += " && ("
				+ generateOntologySparqlFilter(ontologyId, null, false) + ")";

		return getMappings(limit, offset, filter, parameters);
	}

	public List<Mapping> getMappingsFromConcept(Integer ontologyId,
			String conceptId, Integer limit, Integer offset,
			SPARQLFilterGenerator parameters) throws InvalidInputException {
		String filter = generateConceptSparqlFilter(conceptId, null, true);
		filter += " && " + generateOntologySparqlFilter(ontologyId, null, true);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<Mapping> getMappingsToConcept(Integer ontologyId,
			String conceptId, Integer limit, Integer offset,
			SPARQLFilterGenerator parameters) throws InvalidInputException {
		String filter = generateConceptSparqlFilter(conceptId, null, true);
		filter += " && " + generateOntologySparqlFilter(ontologyId, null, true);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<Mapping> getMappingsBetweenConcepts(Integer sourceOntologyId,
			Integer targetOntologyId, String fromConceptId, String toConceptId,
			Boolean unidirectional, Integer limit, Integer offset,
			SPARQLFilterGenerator parameters) throws InvalidInputException {
		String filter = generateConceptSparqlFilter(fromConceptId, toConceptId,
				unidirectional);
		filter += " && "
				+ generateOntologySparqlFilter(sourceOntologyId,
						targetOntologyId, unidirectional);

		return getMappings(limit, offset, filter, parameters);
	}

}
