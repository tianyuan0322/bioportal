package org.ncbo.stanford.sparql.dao.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.manager.rdfstore.RDFStoreManager;
import org.ncbo.stanford.sparql.bean.Mapping;
import org.ncbo.stanford.sparql.bean.ProcessInfo;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.ncbo.stanford.util.sparql.SPARQLUnionGenerator;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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

public class AbstractNcboMappingDAO {

	protected Map<String, RDFStoreManager> rdfStoreManagerMap;

	// This is a general SPARQL query that will produce rows of results that can
	// be interpreted as mappings (IE it contains all mappings fields in every
	// row). The %FILTER% token can be replaced with whatever filter is need to
	// get specific results. %OFFSET% and %LIMIT% must be replaced as well.

	protected final static String mappingQueryBetweenOntolgies = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> "
			+ "SELECT DISTINCT "
			+ "?mappingId "
			+ "?relation "
			+ "?createdInSourceOntologyVersion "
			+ "?createdInTargetOntologyVersion "
			+ "?comment "
			+ "?date "
			+ "?submittedBy "
			+ "?dependency "
			+ "?mappingType "
			+ "?mappingSource "
			+ "?mappingSourceName "
			+ "?mappingSourceContactInfo "
			+ "?mappingSourceSite "
			+ "?mappingSourceAlgorithm "
			+ "?isManyToMany {"
			+ "  %ONTOLOGIES_MATCH_PATTERN% "
			+ "  ?mappingId map:relation ?relation ."
			+ "  ?mappingId map:created_in_source_ontology_version ?createdInSourceOntologyVersion ."
			+ "  ?mappingId map:created_in_target_ontology_version ?createdInTargetOntologyVersion ."
			+ "  ?mappingId map:has_process_info ?procInf ."
			+ "  ?procInf map:date ?date ."
			+ "  ?procInf map:mapping_type ?mappingType ."			
			+ "  ?procInf map:submitted_by ?submittedBy ."
			+ "  OPTIONAL { ?mappingId map:is_many_to_many ?isManyToMany .}"
			+ "  OPTIONAL { ?procInf map:comment ?comment .}"
			+ "  OPTIONAL { ?mappingId map:dependency ?dependency .}"
			+ "  OPTIONAL { ?procInf map:mapping_source ?mappingSource .}"
			+ "  OPTIONAL { ?procInf map:mapping_source_name ?mappingSourceName .}"
			+ "  OPTIONAL { ?procInf map:mapping_source_contact_info ?mappingSourceContactInfo .}"
			+ "  OPTIONAL { ?procInf map:mapping_source_site ?mappingSourceSite .}"
			+ "  OPTIONAL { ?procInf map:mapping_source_algorithm ?mappingSourceAlgorithm .}"
			+ "  FILTER (%FILTER%) } LIMIT %LIMIT% OFFSET %OFFSET%";

	protected final static String mappingQuery = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> "
			+ "SELECT DISTINCT "
			+ "?mappingId "
			+ "?relation "
			+ "?sourceOntologyId "
			+ "?targetOntologyId "
			+ "?createdInSourceOntologyVersion "
			+ "?createdInTargetOntologyVersion "
			+ "?comment "
			+ "?date "
			+ "?submittedBy "
			+ "?dependency "
			+ "?mappingType "
			+ "?mappingSource "
			+ "?mappingSourceName "
			+ "?mappingSourceContactInfo "
			+ "?mappingSourceSite "
			+ "?mappingSourceAlgorithm "
			+ "?isManyToMany "
			+ " {"
			+ "  ?mappingId map:relation ?relation ."
			+ "  ?mappingId map:source_ontology_id ?sourceOntologyId ."
			+ "  ?mappingId map:target_ontology_id ?targetOntologyId ."
			+ "  ?mappingId map:created_in_source_ontology_version ?createdInSourceOntologyVersion ."
			+ "  ?mappingId map:created_in_target_ontology_version ?createdInTargetOntologyVersion ."
			+ "  ?mappingId map:has_process_info ?procInf ."
			+ "  ?procInf map:date ?date ."
			+ "  ?procInf map:mapping_type ?mappingType ."			
			+ "  ?procInf map:submitted_by ?submittedBy ."
			+ "  OPTIONAL { ?mappingId map:is_many_to_many ?isManyToMany .}"
			+ "  OPTIONAL { ?procInf map:comment ?comment .}"
			+ "  OPTIONAL { ?mappingId map:dependency ?dependency .}"
			+ "  OPTIONAL { ?procInf map:mapping_source ?mappingSource .}"
			+ "  OPTIONAL { ?procInf map:mapping_source_name ?mappingSourceName .}"
			+ "  OPTIONAL { ?procInf map:mapping_source_contact_info ?mappingSourceContactInfo .}"
			+ "  OPTIONAL { ?procInf map:mapping_source_site ?mappingSourceSite .}"
			+ "  OPTIONAL { ?procInf map:mapping_source_algorithm ?mappingSourceAlgorithm .}"
			+ "  FILTER (%FILTER%) } %ORDERBY% LIMIT %LIMIT% OFFSET %OFFSET%";

	protected final static String mappingCountQuery = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> "
			+ "SELECT DISTINCT "
			+ "count(DISTINCT ?mappingId) as ?mappingCount WHERE {"
			+ "  ?mappingId map:source_ontology_id ?sourceOntologyId ."
			+ "  ?mappingId map:target_ontology_id ?targetOntologyId ."
			+ " %TRIPLES_FOR_PARAMS% FILTER (%FILTER%) }";

	protected final static String sourcesAndTargetsForMappingIds = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> "
			+ "SELECT DISTINCT ?mappingId ?source ?target {"
			+ "  ?mappingId map:source ?source ."
			+ "  ?mappingId map:target ?target ."
			+ "  FILTER ( ?mappingId = %MAPPING_IDS% ) }";

	protected final static String mappingIdFromSourceOrTarget = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> "
			+ "SELECT DISTINCT ?mappingId {"
			+ "  ?mappingId map:source ?source ."
			+ "  ?mappingId map:target ?target ."
			+ "  ?mappingId map:source_ontology_id ?sourceOntologyId ."
			+ "  ?mappingId map:target_ontology_id ?targetOntologyId ."
			+ "  FILTER ( %FILTER% ) } LIMIT %LIMIT% OFFSET %OFFSET%";

	/*******************************************************************
	 * 
	 * Generic SPARQL methods
	 * 
	 *******************************************************************/

	protected List<Mapping> getMappings(Integer limit, Integer offset,
			String filter, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		return getMappings(limit, offset, filter, null, parameters);
	}

	protected List<Mapping> getMappingsFromSPARQLQuery(String queryString) {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);

			TupleQueryResult result = query.evaluate();

			HashMap<String, Mapping> mappingResults = new HashMap<String, Mapping>();

			while (result.hasNext()) {
				BindingSet bs = result.next();

				Mapping mapping = new Mapping();

				String mappingId = bs.getValue("mappingId").stringValue();

				// Set Mapping properties (required)

				mapping.setId(new URIImpl(mappingId));

				mapping.setRelation(new URIImpl(bs.getValue("relation")
						.stringValue()));

				if (bs.getValue("sourceOntologyId") != null) {
					mapping.setSourceOntologyId(convertValueToInteger(bs
							.getValue("sourceOntologyId")));
				}
				if (bs.getValue("targetOntologyId") != null) {
					mapping.setTargetOntologyId(convertValueToInteger(bs
							.getValue("targetOntologyId")));
				}

				mapping.setCreatedInSourceOntologyVersion(convertValueToInteger(bs
						.getValue("createdInSourceOntologyVersion")));

				mapping.setCreatedInTargetOntologyVersion(convertValueToInteger(bs
						.getValue("createdInTargetOntologyVersion")));

				ProcessInfo processInfo = new ProcessInfo();
				mapping.setProcessInfo(processInfo);
				processInfo.setSubmittedBy(convertValueToInteger(bs
						.getValue("submittedBy")));

				processInfo.setMappingType(bs.getValue("mappingType").stringValue());

				processInfo.setDate(convertValueToDate(bs.getValue("date")));

				// Set mapping properties (optional)
				if (isValidValue(bs.getValue("dependency")))
					mapping.setDependency(new URIImpl(bs.getValue("dependency")
							.stringValue()));

				if (isValidValue(bs.getValue("comment")))
					processInfo.setComment(bs.getValue("comment").stringValue());

				if (isValidValue(bs.getValue("mappingSource")))
					processInfo.setMappingSource(bs.getValue("mappingSource")
							.stringValue());

				if (isValidValue(bs.getValue("mappingSourceName")))
					processInfo.setMappingSourceName(bs.getValue(
							"mappingSourceName").stringValue());

				if (isValidValue(bs.getValue("mappingSourceContactInfo")))
					processInfo.setMappingSourcecontactInfo(bs.getValue(
							"mappingSourceContactInfo").stringValue());

				if (isValidValue(bs.getValue("mappingSourceAlgorithm")))
					processInfo.setMappingSourceAlgorithm(bs.getValue(
							"mappingSourceAlgorithm").stringValue());

				if (isValidValue(bs.getValue("mappingSourceSite")))
					processInfo.setMappingSourceSite(new URIImpl(bs.getValue(
							"mappingSourceSite").stringValue()));

				mappingResults.put(mapping.getId().toString(), mapping);
			}

			result.close();

			if (!mappingResults.isEmpty()) {
				// This query will get all sources and targets for the mapping
				// that were generated above
				Set<String> mappingIds = mappingResults.keySet();

				List<String> mappingIdsSparql = new ArrayList<String>();
				for (String mappingId : mappingIds) {
					mappingIdsSparql.add("<" + mappingId + ">");
				}

				String queryString1 = sourcesAndTargetsForMappingIds
						.replaceAll("%MAPPING_IDS%", StringUtils.join(
								mappingIdsSparql, " || ?mappingId = "));

				TupleQuery query1 = con.prepareTupleQuery(QueryLanguage.SPARQL,
						queryString1);
				TupleQueryResult result1 = query1.evaluate();

				while (result1.hasNext()) {
					BindingSet bs1 = result1.next();

					String mappingId = bs1.getValue("mappingId").stringValue();
					URI sourceURI = new URIImpl(bs1.getValue("source")
							.stringValue());
					URI targetURI = new URIImpl(bs1.getValue("target")
							.stringValue());

					Mapping updatedMapping = mappingResults.get(mappingId);

					if (!updatedMapping.getSource().contains(sourceURI))
						updatedMapping.addSource(sourceURI);

					if (!updatedMapping.getTarget().contains(targetURI))
						updatedMapping.addTarget(targetURI);

					mappingResults.put(mappingId, updatedMapping);
				}

				result1.close();
			}

			return new ArrayList<Mapping>(mappingResults.values());
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}

		return null;
	}

	private void populateSourceAndTargetOntologyId(List<Mapping> mappings) {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();
		try {
			String queryString = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " 
					+ "SELECT * WHERE { "
					+ " ?mappingId map:target_ontology_id ?targetOntologyId . "
					+ " ?mappingId map:source_ontology_id ?sourceOntologyId . "
					+ " FILTER (?mappingId = <%mappingId%>) } LIMIT 1";
			for (Mapping m : mappings) {
				TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
						queryString.replaceAll("%mappingId%", m.getId()
								.toString()));
				TupleQueryResult result = query.evaluate();
				if (result.hasNext()) {
					BindingSet bs = result.next();
					m.setSourceOntologyId(convertValueToInteger(bs
							.getValue("sourceOntologyId")));
					m.setTargetOntologyId(convertValueToInteger(bs
							.getValue("targetOntologyId")));
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}
	}

	/**
	 * specific case to run mapping query with a union for source and target. it
	 * performs better than the generalised case
	 */
	protected List<Mapping> getMappingsBetweenOntologies(
			Integer sourceOntology, Integer targetOntology,
			Boolean unidirectional, Integer limit, Integer offset,
			SPARQLFilterGenerator parameters) throws InvalidInputException {

		// Safety check
		if (limit == null || limit >= 50000) {
			limit = 50000;
		}

		if (offset == null) {
			offset = 0;
		}

		// Combine filters
		String filter = (parameters != null && !parameters.isEmpty()) ? parameters
				.toFilter() : "";

		// Substitute tokens in the generic query string
		String queryString = mappingQueryBetweenOntolgies
				.replaceAll("%FILTER%", filter)
				.replaceAll("%LIMIT%", limit.toString())
				.replaceAll("%OFFSET%", offset.toString());

		// Remove filter if it's not used
		if (filter == null || filter.isEmpty()) {
			queryString = queryString.replaceAll("FILTER \\(\\) ", "");
		}

		String unionPattern = "{ ?mappingId map:target_ontology_id %ONTOLOGY_A% . "
				+ "?mappingId map:source_ontology_id %ONTOLOGY_B% . " + "} ";
		if (!unidirectional) {
			unionPattern += "UNION { "
					+ "?mappingId map:target_ontology_id %ONTOLOGY_B% . "
					+ "?mappingId map:source_ontology_id %ONTOLOGY_A% . "
					+ "} ";
		}

		unionPattern = unionPattern.replaceAll("%ONTOLOGY_A%",
				Integer.toString(targetOntology)).replaceAll("%ONTOLOGY_B%",
				Integer.toString(sourceOntology));
		queryString = queryString.replaceAll("%ONTOLOGIES_MATCH_PATTERN%",
				unionPattern);

		List<Mapping> mappings = getMappingsFromSPARQLQuery(queryString);

		if (!unidirectional) {
			populateSourceAndTargetOntologyId(mappings);
		} else {
			for (Mapping m : mappings) {
				m.setTargetOntologyId(targetOntology);
				m.setSourceOntologyId(sourceOntology);
			}
		}

		return mappings;
	}

	/**
	 * Generic getMappings call. Must provide a valid SPARQL filter (generated
	 * via helper methods or elsewhere).
	 * 
	 * @param limit
	 * @param offset
	 * @param filter
	 * @param parameters
	 * @param sourceOntology
	 * @param targetOntology
	 * @param unidirectional
	 * 
	 * @return
	 * @throws InvalidInputException
	 */
	protected List<Mapping> getMappings(Integer limit, Integer offset,
			String filter, String orderBy, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		// Safety check
		if (limit == null || limit >= 50000) {
			limit = 50000;
		}

		if (offset == null) {
			offset = 0;
		}

		// Combine filters
		String combinedFilters = "";
		if (filter != null) {
			combinedFilters = (parameters != null && !parameters.isEmpty()) ? filter
					+ " && " + parameters.toFilter()
					: filter;
		} else {
			combinedFilters = (parameters != null && !parameters.isEmpty()) ? parameters
					.toFilter() : "";
		}

		// Substitute tokens in the generic query string
		String queryString = mappingQuery
				.replaceAll("%FILTER%", combinedFilters)
				.replaceAll("%LIMIT%", limit.toString())
				.replaceAll("%OFFSET%", offset.toString());

		if (orderBy != null && !orderBy.isEmpty()) {
			queryString = queryString.replaceAll("%ORDERBY%", " ORDER BY DESC("
					+ orderBy + ")");
		} else {
			queryString = queryString.replaceAll("%ORDERBY%", "");
		}

		// Remove filter if it's not used
		if (filter == null || filter.isEmpty()) {
			queryString = queryString.replaceAll("FILTER \\(\\) ", "");
		}

		return getMappingsFromSPARQLQuery(queryString);
	}

	private Integer getMappingCountFromQuery(String queryString) {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);

			TupleQueryResult result = query.evaluate();

			Integer count = 0;
			while (result.hasNext()) {
				BindingSet bs = result.next();
				count = Integer.parseInt(bs.getValue("c").stringValue());
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

	private Set<String> getMappingIdsFromQuery(String queryString) {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		HashSet<String> mappingIds = new HashSet<String>();

		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);

			TupleQueryResult result = query.evaluate();

			while (result.hasNext()) {
				BindingSet bs = result.next();
				String mappingId = bs.getValue("mappingId").stringValue();
				mappingIds.add("<" + mappingId + ">");
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}
		return mappingIds;
	}

	/** from a SPARQLUnionGenerator executes the call returning the count */
	protected Integer getMappingCountFromUnion(
			SPARQLUnionGenerator unionGenerator) {
		String queryString = unionGenerator.getSPARQLQuery();
		return getMappingCountFromQuery(queryString);
	}

	/**
	 * from a SPARQLUnionGenerator executes the call and returns the set of
	 * mappgin IDs
	 */
	protected Set<String> getMappingIdsFromUnion(
			SPARQLUnionGenerator unionGenerator) {
		String queryString = unionGenerator.getSPARQLQuery();
		return getMappingIdsFromQuery(queryString);
	}

	protected Set<String> getMappingIdsFromFilter(Integer limit,
			Integer offset, String filter) {

		String queryString;
		if (limit != null && offset != null) {
			queryString = mappingIdFromSourceOrTarget
					.replaceAll("%FILTER%", filter)
					.replaceAll("%LIMIT%", limit.toString())
					.replaceAll("%OFFSET%", offset.toString());
		} else {
			queryString = mappingIdFromSourceOrTarget.replaceAll("%FILTER%",
					filter).replaceAll("LIMIT %LIMIT% OFFSET %OFFSET%", "");
		}

		return getMappingIdsFromQuery(queryString);
	}

	/**
	 * Generic getCount call. Must provide a valid SPARQL filter (generated via
	 * helper methods or elsewhere).
	 * 
	 * @param sourceOntology
	 * @param targetOntology
	 * @param unidirectional
	 * @param filter
	 * @return
	 * @throws InvalidInputException
	 */
	protected Integer getCount(String filter, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		// Combine filters
		String combinedFilters = "";
		if (filter != null) {
			combinedFilters = (parameters != null && !parameters.isEmpty()) ? filter
					+ " && " + parameters.toFilter()
					: filter;
		} else {
			combinedFilters = (parameters != null && !parameters.isEmpty()) ? parameters
					.toFilter() : "";
		}

		String queryString = mappingCountQuery.replaceAll("%FILTER%",
				combinedFilters);

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

		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();
		Integer count = null;
		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);
			TupleQueryResult result = query.evaluate();
			while (result.hasNext()) {
				BindingSet bs = result.next();
				count = convertValueToInteger(bs.getValue("mappingCount"));
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}

		return count;
	}

	/**
	 * Generates a SPARQL filter for the given ontology ids.
	 * 
	 * @param sourceOntology
	 * @param targetOntology
	 * @param unidirectional
	 * @return
	 * @throws InvalidInputException
	 */
	protected String generateOntologySparqlFilter(Integer sourceOntology,
			Integer targetOntology, Boolean unidirectional)
			throws InvalidInputException {
		// Determine the SPARQL filter to use based on directionality
		// Default is bidirectional
		String filter = "(?sourceOntologyId = " + sourceOntology
				+ " && ?targetOntologyId = " + targetOntology
				+ ") || (?sourceOntologyId = " + targetOntology
				+ " && ?targetOntologyId = " + sourceOntology + ")";
		if (sourceOntology != null && targetOntology != null) {
			if (unidirectional != null && unidirectional == true) {
				filter = "?sourceOntologyId = " + sourceOntology
						+ " && ?targetOntologyId = " + targetOntology;
			}
		} else if (sourceOntology != null && targetOntology == null) {
			if (unidirectional != null && unidirectional == true) {
				filter = "?sourceOntologyId = " + sourceOntology;
			} else {
				filter = "?sourceOntologyId = " + sourceOntology
						+ " || ?targetOntologyId = " + sourceOntology;
			}
		} else if (sourceOntology == null && targetOntology != null) {
			if (unidirectional != null && unidirectional == true) {
				filter = "?targetOntologyId = " + targetOntology;
			} else {
				filter = "?sourceOntologyId = " + targetOntology
						+ " || ?targetOntologyId = " + targetOntology;
			}
		} else {
			throw new InvalidInputException();
		}

		return filter;
	}

	/**
	 * Generates a SPARQL filter for the given ontology ids.
	 * 
	 * @param sourceConcept
	 * @param targetConcept
	 * @param unidirectional
	 * @return
	 * @throws InvalidInputException
	 */
	protected String generateConceptSparqlFilter(String sourceConcept,
			String targetConcept, Boolean unidirectional)
			throws InvalidInputException {
		// Determine the SPARQL filter to use based on directionality
		// Default is bidirectional
		String filter = "(?source = <" + sourceConcept + "> && ?target = <"
				+ targetConcept + ">) || (?source = <" + targetConcept
				+ "> && ?target = <" + sourceConcept + ">)";
		if (sourceConcept != null && targetConcept != null) {
			if (unidirectional != null && unidirectional == true) {
				filter = "?source = <" + sourceConcept + "> && ?target = <"
						+ targetConcept + ">";
			}
		} else if (sourceConcept != null && targetConcept == null) {
			if (unidirectional != null && unidirectional == true) {
				filter = "?source = <" + sourceConcept + ">";
			} else {
				filter = "?source = <" + sourceConcept + "> || ?target = <"
						+ sourceConcept + ">";
			}
		} else if (sourceConcept == null && targetConcept != null) {
			if (unidirectional != null && unidirectional == true) {
				filter = "?target = <" + targetConcept + ">";
			} else {
				filter = "?source = <" + targetConcept + "> || ?target = <"
						+ targetConcept + ">";
			}
		} else {
			throw new InvalidInputException();
		}

		return filter;
	}

	@SuppressWarnings("unused")
	protected String generateConceptSparqlFilterRegex(String sourceConcept,
			String targetConcept, Boolean unidirectional)
			throws InvalidInputException {
		// Determine the SPARQL filter to use based on directionality
		// Default is bidirectional
		String filter = "regex(str(?source), \"" + sourceConcept
				+ "\") || regex(str(?target), \"" + targetConcept
				+ "\") || regex(str(?source), \"" + targetConcept
				+ "\") || regex(str(?target), \"" + sourceConcept + "\")";
		if (sourceConcept != null && targetConcept != null) {
			if (unidirectional != null && unidirectional == true) {
				filter = "regex(str(?source), \"" + sourceConcept
						+ "\") && regex(str(?target), \"" + targetConcept
						+ "\")";
			}
		} else if (sourceConcept != null && targetConcept == null) {
			if (unidirectional != null && unidirectional == true) {
				filter = "regex(str(?source), \"" + sourceConcept + "\")";
			} else {
				filter = "regex(str(?source), \"" + sourceConcept
						+ "\") || regex(str(?target), \"" + sourceConcept
						+ "\")";
			}
		} else if (sourceConcept == null && targetConcept != null) {
			if (unidirectional != null && unidirectional == true) {
				filter = "regex(str(?target), \"" + targetConcept + "\")";
			} else {
				filter = "regex(str(?source), \"" + targetConcept
						+ "\") || regex(str(?target), \"" + targetConcept
						+ "\")";
			}
		} else {
			throw new InvalidInputException();
		}

		return filter;
	}

	/**
	 * Generates a SPARQL filter for the given ontology ids.
	 * 
	 * @param sourceOntology
	 * @param targetOntology
	 * @param unidirectional
	 * @return
	 * @throws InvalidInputException
	 */
	@SuppressWarnings("unused")
	protected String generateOntologySparqlClause(Integer sourceOntology,
			Integer targetOntology, Boolean unidirectional)
			throws InvalidInputException {
		String src = " map:source_ontology_id ";
		String tgt = " map:target_ontology_id ";
		String id = "?mappingId";

		// Determine the SPARQL filter to use based on directionality
		ArrayList<String> filter = new ArrayList<String>();

		if (sourceOntology != null && targetOntology != null) {
			if (unidirectional != null && unidirectional == true) {
				// Unidirectional
				filter.add(id + 1 + src + sourceOntology);
				filter.add(id + 2 + tgt + targetOntology);
			} else {
				// Default is bidirectional
				filter.add(id + 1 + src + sourceOntology);
				filter.add(id + 2 + tgt + targetOntology);
				filter.add(id + 3 + src + targetOntology);
				filter.add(id + 4 + tgt + sourceOntology);
			}
		} else if (sourceOntology != null && targetOntology == null) {
			if (unidirectional != null && unidirectional == true) {
				// Unidirectional source ontology
				filter.add(id + 1 + src + sourceOntology);
			} else {
				// Bidirectional one (source) ontology
				filter.add(id + 1 + src + sourceOntology);
				filter.add(id + 2 + tgt + sourceOntology);
			}
		} else if (sourceOntology == null && targetOntology != null) {
			if (unidirectional != null && unidirectional == true) {
				// Unidirectional target ontology
				filter.add(id + 1 + tgt + targetOntology);
			} else {
				// Bidirectional one (target) ontology
				filter.add(id + 1 + src + targetOntology);
				filter.add(id + 2 + tgt + targetOntology);
			}
		} else {
			throw new InvalidInputException();
		}

		return StringUtils.join(filter, " . ");
	}

	/**
	 * Given a list of mapping ids this generates a SPARQL filter using the IN
	 * clause.
	 * 
	 * @param mappingIds
	 * @return
	 */
	protected String generateMappingIdFilter(Set<String> mappingIds) {
		String mappingIdFilter = "?mappingId = %MAPPING_IDS%";

		mappingIdFilter = mappingIdFilter.replaceAll("%MAPPING_IDS%",
				StringUtils.join(mappingIds, " || ?mappingId = "));

		return mappingIdFilter;
	}

	protected Integer convertValueToInteger(Value val) {
		LiteralImpl integerVal = (LiteralImpl) val;
		return integerVal.intValue();
	}

	protected Date convertValueToDate(Value val) {
		LiteralImpl dateVal = (LiteralImpl) val;
		GregorianCalendar cal = dateVal.calendarValue().toGregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		return cal.getTime();
	}

	/**
	 * Checks the repository for a mapping using provided id.
	 * 
	 * @param id
	 * @return
	 */
	protected Boolean hasMapping(URI id) {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		try {
			Statement statement = new StatementImpl(id,
					ApplicationConstants.RDF_TYPE_URI,
					ApplicationConstants.MAPPING_ONE_TO_ONE_URI);
			return con.hasStatement(statement, false);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Get a manager for the RDF store based on the configured options in
	 * build.properties.
	 * 
	 * @return
	 */
	protected RDFStoreManager getRdfStoreManager() {
		String storeType = MessageUtils.getMessage("rdf.store.type");
		return rdfStoreManagerMap.get(storeType);
	}

	/**
	 * Look for valid values and update the provided mapping bean with them. For
	 * use in re-creating a mapping as part of an update.
	 * 
	 * @param mapping
	 * @param source
	 * @param target
	 * @param relation
	 * @param sourceOntologyId
	 * @param targetOntologyId
	 * @param sourceOntologyVersion
	 * @param targetOntologyVersion
	 * @param submittedBy
	 * @param dependency
	 * @param comment
	 * @param mappingSource
	 * @param mappingSourceName
	 * @param mappingSourcecontactInfo
	 * @param mappingSourceSite
	 * @param mappingSourceAlgorithm
	 * @param mappingType
	 * @return
	 */
	protected Mapping updateMappingEntity(Mapping mapping, List<URI> source,
			List<URI> target, URI relation, Integer sourceOntologyId,
			Integer targetOntologyId, Integer sourceOntologyVersion,
			Integer targetOntologyVersion, Integer submittedBy, URI dependency,
			String comment, String mappingSource, String mappingSourceName,
			String mappingSourcecontactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType) {
		ProcessInfo processInfo = mapping.getProcessInfo();
		if (processInfo != null) {
			processInfo = new ProcessInfo();
			mapping.setProcessInfo(processInfo);
		}
		if (source != null)
			mapping.setSource(source);
		if (target != null)
			mapping.setTarget(target);
		if (relation != null)
			mapping.setRelation(relation);
		if (sourceOntologyId != null)
			mapping.setSourceOntologyId(sourceOntologyId);
		if (targetOntologyId != null)
			mapping.setTargetOntologyId(targetOntologyId);
		if (sourceOntologyVersion != null)
			mapping.setCreatedInSourceOntologyVersion(sourceOntologyVersion);
		if (targetOntologyVersion != null)
			mapping.setCreatedInTargetOntologyVersion(targetOntologyVersion);
		if (submittedBy != null)
			processInfo.setSubmittedBy(submittedBy);
		if (dependency != null)
			mapping.setDependency(dependency);
		if (comment != null)
			processInfo.setComment(comment);
		if (mappingSource != null)
			processInfo.setMappingSource(mappingSource);
		if (mappingSourceName != null)
			processInfo.setMappingSourceName(mappingSourceName);
		if (mappingSourcecontactInfo != null)
			processInfo.setMappingSourcecontactInfo(mappingSourcecontactInfo);
		if (mappingSourceSite != null)
			processInfo.setMappingSourceSite(mappingSourceSite);
		if (mappingSourceAlgorithm != null)
			processInfo.setMappingSourceAlgorithm(mappingSourceAlgorithm);
		if (mappingType != null)
			processInfo.setMappingType(mappingType);

		return mapping;
	}

	protected boolean isValidValue(Value value) {
		return value != null && !value.stringValue().isEmpty();
	}

	/**
	 * Cleanup repositories after use.
	 * 
	 * @param con
	 */
	protected void cleanup(RepositoryConnection con) {
		try {
			con.close();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	/*******************************************************************
	 * 
	 * Auto-generated methods
	 * 
	 *******************************************************************/

	/**
	 * @param rdfStoreManagerMap
	 *            the rdfStoreManagerMap to set
	 */
	public void setRdfStoreManagerMap(
			Map<String, RDFStoreManager> rdfStoreManagerMap) {
		this.rdfStoreManagerMap = rdfStoreManagerMap;
	}

}
