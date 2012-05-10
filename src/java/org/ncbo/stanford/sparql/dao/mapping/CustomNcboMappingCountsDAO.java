package org.ncbo.stanford.sparql.dao.mapping;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.sparql.MappingFilterGenerator;
import org.ncbo.stanford.util.sparql.SPARQLUnionGenerator;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class CustomNcboMappingCountsDAO extends AbstractNcboMappingDAO {

	public Integer getCountMappingsForParameters(
			MappingFilterGenerator parameters) throws InvalidInputException {
		return getCount(null, parameters);
	}

	public Integer getCountMappingsFromOntology(Integer ontologyId,
			MappingFilterGenerator parameters) throws InvalidInputException {
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateOntologySparqlFilter(ontologyId, null, true);
			return getCount(filter, parameters);
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setCount(true);
			generator.setParameters(parameters);
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(ontologyId),
							ApplicationConstants.XSD_INTEGER));

			return getMappingCountFromUnion(generator);
		}
	}

	public Integer getCountMappingsToOntology(Integer ontologyId,
			MappingFilterGenerator parameters) throws InvalidInputException {
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateOntologySparqlFilter(null, ontologyId, true);
			return getCount(filter, parameters);
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setCount(true);
			generator.setParameters(parameters);
			generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(ontologyId),
							ApplicationConstants.XSD_INTEGER));

			return getMappingCountFromUnion(generator);
		}
	}

	public Integer getCountMappingsBetweenOntologies(Integer sourceOntology,
			Integer targetOntology, Boolean unidirectional,
			MappingFilterGenerator parameters) throws InvalidInputException {
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateOntologySparqlFilter(sourceOntology,
					targetOntology, unidirectional);
			return getCount(filter, parameters);
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setCount(true);
			generator.setParameters(parameters);
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(sourceOntology),
							ApplicationConstants.XSD_INTEGER));
			generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(targetOntology),
							ApplicationConstants.XSD_INTEGER));

			if (!unidirectional) {
				generator.addBidirectional(
						ApplicationConstants.SOURCE_ONTOLOGY_ID,
						ApplicationConstants.TARGET_ONTOLOGY_ID);
			}
			return getMappingCountFromUnion(generator);
		}
	}

	public Integer getCountRankedMappingsBetweenOntologies(
			Integer sourceOntology, Integer targetOntology,
			MappingFilterGenerator parameters) {
		String rankedSourceCount = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> "
				+ "SELECT DISTINCT (count(?source) as ?c) {{ "
				+ "  ?mappingId map:source ?source ."
				+ "  ?mappingId map:target_ontology_id \"%TARGET_ONT%\"^^<http://www.w3.org/2001/XMLSchema#integer> ."
				+ "  ?mappingId map:source_ontology_id \"%SOURCE_ONT%\"^^<http://www.w3.org/2001/XMLSchema#integer> ."
				+ "  %TRIPLES_FOR_PARAMS% FILTER (%FILTER%) "
				+ "}} GROUP BY ?source";

		// Replace triples placeholder with triples pattern generated from the
		// user-provided parameters
		if (parameters != null && !parameters.isEmpty()) {
			List<String> triples = parameters.generateTriplePatterns();
			rankedSourceCount = rankedSourceCount.replaceAll(
					"%TRIPLES_FOR_PARAMS%", StringUtils.join(triples, " . "));
		} else {
			rankedSourceCount = rankedSourceCount.replaceAll(
					"%TRIPLES_FOR_PARAMS%", "");
		}

		// Create filter
		String filter = (parameters != null && !parameters.isEmpty()) ? parameters
				.toFilter() : "";

		// Substitute tokens in the generic query string
		rankedSourceCount = rankedSourceCount.replaceAll("%FILTER%", filter)
				.replaceAll("%SOURCE_ONT%", sourceOntology.toString())
				.replaceAll("%TARGET_ONT%", targetOntology.toString());

		// Remove filter if it's not used
		if (filter == null || filter.isEmpty()) {
			rankedSourceCount = rankedSourceCount.replaceAll("FILTER \\(\\)", "");
		}

		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					rankedSourceCount);

			TupleQueryResult result = query.evaluate();

			Integer count = 0;
			while (result.hasNext()) {
				result.next();
				count += 1;
			}
			return count;
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}
		return null;

	}

	public Integer getCountMappingsForOntology(Integer ontologyId,
			MappingFilterGenerator parameters) throws InvalidInputException {
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateOntologySparqlFilter(ontologyId, null,
					false);
			return getCount(filter, parameters);
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setCount(true);
			generator.setParameters(parameters);
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(ontologyId),
							ApplicationConstants.XSD_INTEGER));

			generator.addBidirectional(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					ApplicationConstants.TARGET_ONTOLOGY_ID);
			return getMappingCountFromUnion(generator);
		}
	}

	public Integer getCountMappingsForConcept(Integer ontologyId,
			String conceptId, MappingFilterGenerator parameters)
			throws InvalidInputException {
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = "("
					+ generateConceptSparqlFilter(conceptId, null, false) + ")";
			filter += " && ("
					+ generateOntologySparqlFilter(ontologyId, null, false)
					+ ")";
			filter += " " + parameters.toFilter();

			Set<String> mappingIds = getMappingIdsFromFilter(null, null, filter);

			return mappingIds.size();
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setCount(true);
			generator.setParameters(parameters);
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(ontologyId),
							ApplicationConstants.XSD_INTEGER));
			generator.addBindValue(ApplicationConstants.SOURCE_TERM,
					new URIImpl(conceptId));

			generator.addBidirectional(ApplicationConstants.SOURCE_TERM,
					ApplicationConstants.TARGET_TERM);
			generator.addBidirectional(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					ApplicationConstants.TARGET_ONTOLOGY_ID);

			return getMappingCountFromUnion(generator);
		}
	}

	public Integer getCountMappingsFromConcept(Integer ontologyId,
			String conceptId, MappingFilterGenerator parameters)
			throws InvalidInputException {

		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateConceptSparqlFilter(conceptId, null, true);
			filter += " && "
					+ generateOntologySparqlFilter(ontologyId, null, true);
			filter += " " + parameters.toFilter();

			Set<String> mappingIds = getMappingIdsFromFilter(null, null, filter);

			return mappingIds.size();
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setCount(true);
			generator.setParameters(parameters);
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(ontologyId),
							ApplicationConstants.XSD_INTEGER));
			generator.addBindValue(ApplicationConstants.SOURCE_TERM,
					new URIImpl(conceptId));
			return getMappingCountFromUnion(generator);
		}
	}

	public Integer getCountMappingsToConcept(Integer ontologyId,
			String conceptId, MappingFilterGenerator parameters)
			throws InvalidInputException {

		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateConceptSparqlFilter(conceptId, null, true);
			filter += " && "
					+ generateOntologySparqlFilter(ontologyId, null, true);
			filter += " " + parameters.toFilter();

			Set<String> mappingIds = getMappingIdsFromFilter(null, null, filter);
			return mappingIds.size();
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setCount(true);
			generator.setParameters(parameters);
			generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY_ID,
					new LiteralImpl(Integer.toString(ontologyId),
							ApplicationConstants.XSD_INTEGER));
			generator.addBindValue(ApplicationConstants.TARGET_TERM,
					new URIImpl(conceptId));
			return getMappingCountFromUnion(generator);
		}
	}

	public Integer getCountMappingsBetweenConcepts(Integer sourceOntologyId,
			Integer targetOntologyId, String fromConceptId, String toConceptId,
			Boolean unidirectional, MappingFilterGenerator parameters)
			throws InvalidInputException {
		Set<String> mappingIds = null;
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateConceptSparqlFilter(fromConceptId,
					toConceptId, unidirectional);
			filter += " && "
					+ generateOntologySparqlFilter(sourceOntologyId,
							targetOntologyId, unidirectional);
			filter += " " + parameters.toFilter();

			mappingIds = getMappingIdsFromFilter(null, null, filter);
			return mappingIds.size();
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setCount(true);
			generator.setParameters(parameters);
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
			return getMappingCountFromUnion(generator);
		}
	}

}
