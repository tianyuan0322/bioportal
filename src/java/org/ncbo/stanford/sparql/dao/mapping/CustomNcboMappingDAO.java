package org.ncbo.stanford.sparql.dao.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.sparql.bean.Mapping;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.ncbo.stanford.util.sparql.SPARQLUnionGenerator;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
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
		return super.getMappingsBetweenOntologies(sourceOntology,
				targetOntology, unidirectional, limit, offset, parameters);
	}

	public List<Mapping> getRankedMappingsBetweenOntologies(
			Integer sourceOntology, Integer targetOntology, Integer limit,
			Integer offset, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		String queryString = "select ?source (count(?target) as ?count) where {                                                     "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source> ?source .                   "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target> ?target .                   "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id> %SOURCE_ONT% .  "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id> %TARGET_ONT% .  "
				+ "  %TRIPLES_FOR_PARAMS% FILTER (%FILTER%) } GROUP BY ?source ORDER BY DESC(?count) LIMIT %LIMIT% OFFSET %OFFSET%                      ";

		// Replace triples placeholder with triples pattern generated from the
		// user-provided parameters
		if (parameters != null && !parameters.isEmpty()) {
			List<String> triples = parameters.generateTriplePatterns(
					"mappingId", new Mapping());
			queryString = queryString.replaceAll("%TRIPLES_FOR_PARAMS%",
					StringUtils.join(triples, " . "));
		} else {
			queryString = queryString.replaceAll("%TRIPLES_FOR_PARAMS%", "");
		}

		// Create filter
		String filter = (parameters != null && !parameters.isEmpty()) ? parameters
				.toFilter() : "";

		// Substitute tokens in the generic query string
		queryString = queryString.replaceAll("%FILTER%", filter)
				.replaceAll("%LIMIT%", limit.toString())
				.replaceAll("%OFFSET%", offset.toString())
				.replaceAll("%SOURCE_ONT%", sourceOntology.toString())
				.replaceAll("%TARGET_ONT%", targetOntology.toString());

		// Remove filter if it's not used
		if (filter == null || filter.isEmpty()) {
			queryString = queryString.replaceAll("FILTER \\(\\) ", "");
		}

		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		List<String> sourceIds = new ArrayList<String>();
		TupleQueryResult result = null;
		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);

			result = query.evaluate();

			while (result.hasNext()) {
				BindingSet bs = result.next();

				String sourceId = bs.getValue("source").stringValue();

				sourceIds.add(sourceId);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		}

		List<Mapping> mappings = new ArrayList<Mapping>();
		Set<String> mappingIds = null;
		for (String conceptId : sourceIds) {
			if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
				filter += (filter.isEmpty()) ? "" : " && "; 
				filter += generateConceptSparqlFilter(conceptId, null, true);
				filter += " && "
						+ generateOntologySparqlFilter(sourceOntology,
								targetOntology, true);
				mappingIds = getMappingIdsFromFilter(limit, offset, filter);
			} else {
				SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
				generator.setLimit(limit);
				generator.setOffset(offset);
				generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
						new LiteralImpl(Integer.toString(sourceOntology),
								ApplicationConstants.XSD_INTEGER));
				generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY_ID,
						new LiteralImpl(Integer.toString(targetOntology),
								ApplicationConstants.XSD_INTEGER));
				generator.addBindValue(ApplicationConstants.SOURCE_TERM,
						new URIImpl(conceptId));
				mappingIds = getMappingIdsFromUnion(generator);
			}

			String mappingIdFilter = generateMappingIdINFilter(mappingIds);

			if (mappingIds.size() > 0) {
				mappings.addAll(getMappings(limit, offset, mappingIdFilter,
						null));
			}
		}

		return mappings;
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
		Set<String> mappingIds = null;
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = "("
					+ generateConceptSparqlFilter(conceptId, null, false);
			filter += ") && ("
					+ generateOntologySparqlFilter(ontologyId, null, false)
					+ ")";
			mappingIds = getMappingIdsFromFilter(limit, offset, filter);
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setLimit(limit);
			generator.setOffset(offset);
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(ontologyId),
							ApplicationConstants.XSD_INTEGER));
			generator.addBindValue(ApplicationConstants.SOURCE_TERM,
					new URIImpl(conceptId));

			generator.addBidirectional(ApplicationConstants.SOURCE_TERM,
					ApplicationConstants.TARGET_TERM);
			generator.addBidirectional(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					ApplicationConstants.TARGET_ONTOLOGY_ID);
			mappingIds = getMappingIdsFromUnion(generator);
		}
		String mappingIdFilter = generateMappingIdINFilter(mappingIds);
		List<Mapping> mappings = new ArrayList<Mapping>();
		if (mappingIds.size() > 0) {
			mappings = getMappings(limit, offset, mappingIdFilter, parameters);
		}

		return mappings;
	}

	public List<Mapping> getMappingsFromConcept(Integer ontologyId,
			String conceptId, Integer limit, Integer offset,
			SPARQLFilterGenerator parameters) throws InvalidInputException {

		Set<String> mappingIds = null;
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateConceptSparqlFilter(conceptId, null, true);
			filter += " && "
					+ generateOntologySparqlFilter(ontologyId, null, true);
			mappingIds = getMappingIdsFromFilter(limit, offset, filter);
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setLimit(limit);
			generator.setOffset(offset);
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(ontologyId),
							ApplicationConstants.XSD_INTEGER));
			generator.addBindValue(ApplicationConstants.SOURCE_TERM,
					new URIImpl(conceptId));
			mappingIds = getMappingIdsFromUnion(generator);
		}
		String mappingIdFilter = generateMappingIdINFilter(mappingIds);
		List<Mapping> mappings = new ArrayList<Mapping>();
		if (mappingIds.size() > 0) {
			mappings = getMappings(limit, offset, mappingIdFilter, parameters);
		}

		return mappings;
	}

	public List<Mapping> getMappingsToConcept(Integer ontologyId,
			String conceptId, Integer limit, Integer offset,
			SPARQLFilterGenerator parameters) throws InvalidInputException {

		Set<String> mappingIds = null;
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateConceptSparqlFilter(conceptId, null, true);
			filter += " && "
					+ generateOntologySparqlFilter(ontologyId, null, true);
			mappingIds = getMappingIdsFromFilter(limit, offset, filter);
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setLimit(limit);
			generator.setOffset(offset);
			generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(ontologyId),
							ApplicationConstants.XSD_INTEGER));
			generator.addBindValue(ApplicationConstants.TARGET_TERM,
					new URIImpl(conceptId));
			mappingIds = getMappingIdsFromUnion(generator);
		}
		String mappingIdFilter = generateMappingIdINFilter(mappingIds);
		List<Mapping> mappings = new ArrayList<Mapping>();
		if (mappingIds.size() > 0) {
			mappings = getMappings(limit, offset, mappingIdFilter, parameters);
		}

		return mappings;
	}

	public List<Mapping> getMappingsBetweenConcepts(Integer sourceOntologyId,
			Integer targetOntologyId, String fromConceptId, String toConceptId,
			Boolean unidirectional, Integer limit, Integer offset,
			SPARQLFilterGenerator parameters) throws InvalidInputException {

		Set<String> mappingIds = null;
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateConceptSparqlFilter(fromConceptId,
					toConceptId, unidirectional);
			filter += " && "
					+ generateOntologySparqlFilter(sourceOntologyId,
							targetOntologyId, unidirectional);

			mappingIds = getMappingIdsFromFilter(limit, offset, filter);
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setLimit(limit);
			generator.setOffset(offset);
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(sourceOntologyId),
							ApplicationConstants.XSD_INTEGER));
			generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(targetOntologyId),
							ApplicationConstants.XSD_INTEGER));
			generator.addBindValue(ApplicationConstants.SOURCE_TERM,
					new URIImpl(fromConceptId));
			generator.addBindValue(ApplicationConstants.TARGET_TERM,
					new URIImpl(toConceptId));
			if (!unidirectional) {
				generator.addBidirectional(ApplicationConstants.SOURCE_TERM,
						ApplicationConstants.TARGET_TERM);
				generator.addBidirectional(
						ApplicationConstants.SOURCE_ONTOLOGY_ID,
						ApplicationConstants.TARGET_ONTOLOGY_ID);
			}
			mappingIds = getMappingIdsFromUnion(generator);
		}

		String mappingIdFilter = generateMappingIdINFilter(mappingIds);
		List<Mapping> mappings = new ArrayList<Mapping>();
		if (mappingIds.size() > 0) {
			mappings = getMappings(limit, offset, mappingIdFilter, parameters);
		}
		return mappings;
	}

}
