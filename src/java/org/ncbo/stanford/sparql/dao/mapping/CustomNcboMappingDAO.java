package org.ncbo.stanford.sparql.dao.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.bean.concept.ConceptOntologyPairBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.sparql.bean.Mapping;
import org.ncbo.stanford.sparql.bean.ProcessInfo;
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
import org.openrdf.repository.RepositoryResult;

public class CustomNcboMappingDAO extends AbstractNcboMappingDAO {

	public Mapping createMapping(List<URI> source, List<URI> target,
			URI relation, Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, URI dependency, String comment,
			String mappingSource, String mappingSourceName,
			String mappingSourceContactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType) throws Exception {

		Mapping newMapping = new Mapping();
		ProcessInfo processInfo = new ProcessInfo();
		newMapping.setProcessInfo(processInfo);

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
		processInfo.setSubmittedBy(submittedBy);
		processInfo.setDate(new Date());
		processInfo.setComment(comment);
		processInfo.setMappingType(mappingType);

		// Set mappingSource properties
		processInfo.setMappingSource(mappingSource);
		processInfo.setMappingSourceName(mappingSourceName);
		processInfo.setMappingSourcecontactInfo(mappingSourceContactInfo);
		processInfo.setMappingSourceSite(mappingSourceSite);
		processInfo.setMappingSourceAlgorithm(mappingSourceAlgorithm);

		createMapping(newMapping);

		Mapping mapping = getMapping(newMapping.getId());

		return mapping;
	}

	public Mapping createMapping(Mapping newMapping) throws Exception {
		ValueFactory vf = getRdfStoreManager().getValueFactory();

		List<Statement> statements = newMapping.toStatements(vf);
		statements.addAll(newMapping.getProcessInfo().toStatements(vf));

		getRdfStoreManager().addTriples(statements,
				ApplicationConstants.MAPPING_CONTEXT);

		// For mappings that are many-to-many, we add a triple to indicate this.
		// It helps with the lookups while we are retrieving.
		if (newMapping.getSource().size() > 1
				|| newMapping.getTarget().size() > 1) {
			URI predicate = new URIImpl(ApplicationConstants.MAPPING_PREFIX
					+ "is_many_to_many");
			Statement statement = new StatementImpl(newMapping.getId(),
					predicate, vf.createLiteral(true));

			try {
				getRdfStoreManager().addTriple(statement,
						ApplicationConstants.MAPPING_CONTEXT);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		Mapping mapping = null;
		try {
			mapping = getMapping(newMapping.getId());
		} catch (MappingMissingException e) {
			e.printStackTrace();
		}

		return mapping;
	}

	public List<Mapping> getMappingsByConceptOntologyPairs(
			List<ConceptOntologyPairBean> listConceptOntologyPairBean)
			throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		String unionBlockTemplate = "{ ?id maps:source_ontology_id %SOURCE_ONT_ID% . "
				+ "?id maps:source <%SOURCE_TERM%> ."
				+ "?id maps:target_ontology_id %TARGET_ONT_ID% ."
				+ "?id maps:target <%TARGET_TERM%> . }";

		List<Mapping> mappings = new ArrayList<Mapping>();
		try {
			List<String> unionBlocks = new ArrayList<String>();
			for (ConceptOntologyPairBean opbA : listConceptOntologyPairBean) {
				for (ConceptOntologyPairBean opbB : listConceptOntologyPairBean) {
					if (opbA.getConceptId().equals(opbB.getConceptId())
							&& opbA.getOntologyId()
									.equals(opbB.getOntologyId()))
						continue;

					String unionBlock = unionBlockTemplate
							.replace("%SOURCE_ONT_ID%",
									Integer.toString(opbA.getOntologyId()))
							.replace("%SOURCE_TERM%", opbA.getConceptId())
							.replace("%TARGET_ONT_ID%",
									Integer.toString(opbB.getOntologyId()))
							.replace("%TARGET_TERM%", opbB.getConceptId());
					unionBlocks.add(unionBlock);
				}
			}
			String queryString = StringUtils.join(unionBlocks.toArray(),
					"\nUNION\n");
			queryString = "PREFIX maps: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> "
					+ "SELECT ?id WHERE { " + queryString + "}";
			TupleQuery query;
			try {
				query = con
						.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

				TupleQueryResult result = query.evaluate();

				while (result.hasNext()) {
					BindingSet bs = result.next();

					String mappingId = bs.getValue("id").stringValue();
					mappings.add(getMapping(new URIImpl(mappingId)));

				}
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			cleanup(con);
		}

		return mappings;
	}

	public Mapping getMapping(URI id) throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		// Attempt mapping retrieval, return null if failure
		Mapping mapping = null;
		try {
			if (hasMapping(id)) {
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
			String mappingSourceAlgorithm, String mappingType) throws Exception {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		if (!hasMapping(id)) {
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

	public Mapping updateMapping(URI id, Mapping mapping) throws Exception {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		if (!hasMapping(id)) {
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

	public void deleteMapping(URI id) throws Exception {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();
		try {
			if (!hasMapping(id)) {
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
					List<String> inferredDependents = getInferredDependentIds(mapping);

					if (!inferredDependents.isEmpty()) {
						for (String dependent : inferredDependents) {
							deleteMapping(new URIImpl(dependent));
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

	public void deleteMappingForUpdate(URI id) throws Exception {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		if (!hasMapping(id)) {
			throw new MappingMissingException();
		}

		try {
			deleteFromTripleStore(con, id);

			cleanup(con);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method does the actual delete action for removing mappings from the
	 * triplestore. It removes all triples with a subject matching the mapping
	 * id.
	 * 
	 * @param con
	 * @param id
	 * @throws Exception
	 */
	private void deleteFromTripleStore(RepositoryConnection con, URI id)
			throws Exception {
		RepositoryResult<Statement> results = con.getStatements(id, null, null,
				false);

		// Remove all those triples
		getRdfStoreManager().deleteTriples(results.asList());
	}

	private List<String> getInferredDependentIds(Mapping mapping) {
		String queryString = "select ?mappingId where { "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source> <%source%> ."
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target> <%target%> ."
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id> %source_ont% ."
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id> %target_ont% ."
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#has_process_info> ?procInf ."
				+ "  ?procInf <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#submitted_by> %submitted_by% ."
				+ "}";
		queryString = queryString
				.replace("%source%", mapping.getTarget().get(0).toString())
				.replace("%target%", mapping.getSource().get(0).toString())
				.replace("%source_ont%",
						mapping.getTargetOntologyId().toString())
				.replace("%target_ont%",
						mapping.getSourceOntologyId().toString())
				.replace("%submitted_by%",
						mapping.getProcessInfo().getSubmittedBy().toString());

		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		List<String> mappingIds = new ArrayList<String>();
		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);

			TupleQueryResult result = query.evaluate();

			while (result.hasNext()) {
				BindingSet bs = result.next();
				mappingIds.add(bs.getValue("mappingId").stringValue());
			}

			result.close();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}

		return mappingIds;
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
		List<Mapping> mappings = super
				.getMappingsBetweenOntologies(sourceOntology, targetOntology,
						true, limit, offset, parameters);
		if (!unidirectional) {
			mappings.addAll(super.getMappingsBetweenOntologies(targetOntology,
					sourceOntology, true, limit, offset, parameters));
		}
		return mappings;
	}

	public List<Mapping> getRankedMappingsBetweenOntologies(
			Integer sourceOntology, Integer targetOntology, Integer limit,
			Integer offset, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		String queryString = "select ?source (count(?target) as ?count) where {                                                    "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source> ?source .                   "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target> ?target .                   "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id> %SOURCE_ONT% .  "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id> %TARGET_ONT% .  "
				+ "  %TRIPLES_FOR_PARAMS% FILTER (%FILTER%) } GROUP BY ?source ORDER BY DESC(?count) LIMIT %LIMIT% OFFSET %OFFSET% ";

		// Replace triples placeholder with triples pattern generated from the
		// user-provided parameters
		if (parameters != null && !parameters.isEmpty()) {
			List<String> triples = parameters.generateTriplePatterns();
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
				generator.setLimit(Integer.MAX_VALUE);
				generator.setOffset(0);
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

			String mappingIdFilter = generateMappingIdFilter(mappingIds);

			if (mappingIds.size() > 0) {
				mappings.addAll(getMappings(Integer.MAX_VALUE, 0,
						mappingIdFilter, null));
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
		String mappingIdFilter = generateMappingIdFilter(mappingIds);
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
		String mappingIdFilter = generateMappingIdFilter(mappingIds);
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
		String mappingIdFilter = generateMappingIdFilter(mappingIds);
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

		String mappingIdFilter = generateMappingIdFilter(mappingIds);
		List<Mapping> mappings = new ArrayList<Mapping>();
		if (mappingIds.size() > 0) {
			mappings = getMappings(limit, offset, mappingIdFilter, parameters);
		}
		return mappings;
	}

}
