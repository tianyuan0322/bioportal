package org.ncbo.stanford.sparql.dao.mapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.bean.mapping.MappingBean;
import org.ncbo.stanford.bean.mapping.MappingConceptStatsBean;
import org.ncbo.stanford.bean.mapping.MappingOntologyStatsBean;
import org.ncbo.stanford.bean.mapping.MappingUserStatsBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.sparql.bean.Mapping;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import edu.emory.mathcs.backport.java.util.Collections;

public class CustomNcboMappingStatsDAO extends AbstractNcboMappingDAO {

	private static final String mostRecentMappings = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " 
			+ "SELECT DISTINCT ?mappingId where { "
			+ "?procInfo map:date ?date . ?mappingId map:has_process_info ?procInfo } ORDER BY DESC(?date) LIMIT %LIMIT%";

	private static final String totalMappings = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " 
			+ "SELECT (count(?mappingId) as ?mappings) "
			+ "WHERE { ?mappingId a map:One_To_One_Mapping . }";

	private static final String ontologySourceCount = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " 
			+ "SELECT ?sourceOntologyId (count(?sourceOntologyId) as ?count) "
			+ "WHERE { ?mappingId map:source_ontology ?sourceOntologyId . }" +
			" GROUP BY ?sourceOntologyId";
	
	private static final String ontologyTargetCount = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " 
			+ "SELECT ?targetOntologyId (count(?targetOntologyId) as ?count) "
			+ "WHERE { ?mappingId map:target_ontology ?targetOntologyId . }" +
			" GROUP BY ?targetOntologyId";

	private static final String sourceMappings = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " 
			+ "SELECT ?sourceOntologyId "
			+ "(count(?sourceOntologyId) as ?count) "
			+ "WHERE { ?mappingId map:target_ontology <%ONT%> . "
			+ "?mappingId map:source_ontology ?sourceOntologyId . "
			+ "} GROUP BY ?sourceOntologyId ORDER BY DESC(?count)";

	private static final String targetMappings = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " 
			+ "SELECT ?targetOntologyId "
			+ "(count(?targetOntologyId) as ?count) "
			+ "WHERE { ?mappingId map:source_ontology <%ONT%> . "
			+ "?mappingId map:target_ontology ?targetOntologyId . "
			+ "} GROUP BY ?targetOntologyId ORDER BY DESC(?count)";

	private static final String conceptCount = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " 
			+ "SELECT ?conceptId (count(?conceptId) as ?count) "
			+ "WHERE { { ?mappingId map:source_ontology <%ONT%> . "
			+ "?mappingId map:source ?conceptId . } "
			+ "UNION { ?mappingId map:target_ontology <%ONT%> . "
			+ "?mappingId map:target ?conceptId . } "
			+ "} GROUP BY ?conceptId ORDER BY DESC(?count) LIMIT %LIMIT%";

	private static final String userCount = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " 
			+ "SELECT ?userId (count(?mappingId) as ?count) "
			+ "WHERE { { ?mappingId map:source_ontology <%ONT%> . "
			+ "?mappingId map:has_process_info [ map:submitted_by ?userId ] . } "
			+ "UNION { ?mappingId map:target_ontology <%ONT%> . "
			+ "?mappingId map:has_process_info [ map:submitted_by ?userId ] . } "
			+ "} GROUP BY ?userId ORDER BY DESC(?count)";

	private static final String userCountWithTarget = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> " 
			+ "SELECT ?userId (count(?mappingId) as ?count) "
			+ "WHERE { "
			+ "{ ?mappingId map:source_ontology <%ONT%> . "
			+ " ?mappingId map:target_ontology <%TARG%> . "
			+ "?mappingId map:has_process_info [ map:submitted_by ?userId ] . } "
			+ "UNION { "
			+ "?mappingId map:target_ontology <%ONT%> . "
			+ "?mappingId map:source_ontology <%TARG%> . "
			+ "?mappingId map:has_process_info [ map:submitted_by ?userId ] . } "
			+ "} GROUP BY ?userId ORDER BY DESC(?count)";

	/**
	 * Gets a list of recent mappings up to size of limit.
	 * 
	 * @param limit
	 * @return
	 * @throws InvalidInputException
	 */
	public List<Mapping> getRecentMappings(Integer limit)
			throws InvalidInputException {
		String orderBy = "?date";

		String queryString = mostRecentMappings.replaceAll("%LIMIT%",
				limit.toString());

		ArrayList<String> mappingIds = new ArrayList<String>();

		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		TupleQuery query;
		try {
			query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = query.evaluate();
			while (result.hasNext()) {
				BindingSet bs = result.next();

				mappingIds.add("?mappingId = <"
						+ bs.getValue("mappingId").stringValue() + ">");
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

		// Create a filter
		String filter = StringUtils.join(mappingIds, " || ");

		List<Mapping> mappings = getMappings(limit, 0, filter, orderBy, null);

		Collections.sort(mappings, new Comparator<Mapping>() {
			public int compare(Mapping map1, Mapping map2) {
				return map2.getProcessInfo().getDate().compareTo(map1.getProcessInfo().getDate());
			}
		});

		return mappings;
	}

	/**
	 * Returns the total number of mappings in the triplestore.
	 * 
	 * @return
	 */
	public Integer getTotalMappingsCount() {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		TupleQuery query;
		Integer totalMappingsCount = 0;
		try {
			query = con.prepareTupleQuery(QueryLanguage.SPARQL, totalMappings);
			TupleQueryResult result = query.evaluate();

			while (result.hasNext()) {
				BindingSet bs = result.next();

				totalMappingsCount = convertValueToInteger(bs
						.getValue("sourceOntologyId"));
			}

			result.close();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}

		return totalMappingsCount;
	}

	/**
	 * Returns an object with virtual ids and their source/target mapping
	 * counts.
	 * 
	 * @return
	 */
	public List<MappingOntologyStatsBean> getOntologiesMappingCount() {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		TupleQuery sourceQuery = null;
		TupleQuery targetQuery = null;
		List<MappingOntologyStatsBean> statsBean = null;
		try {
			sourceQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					ontologySourceCount);
			targetQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					ontologyTargetCount);

			statsBean = getOntologyCountList(con, sourceQuery, targetQuery);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}

		return statsBean;
	}

	/**
	 * Returns an object with virtual ids and their source/target mapping counts
	 * that correspond to a particular ontology.
	 * 
	 * @return
	 */
	public List<MappingOntologyStatsBean> getOntologyMappingCount(
			URI ontologyURI) {
		String sourceMappingsQuery = sourceMappings.replaceAll("%ONT%",
				ontologyURI.toString());
		String targetMappingsQuery = targetMappings.replaceAll("%ONT%",
				ontologyURI.toString());

		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		TupleQuery sourceQuery = null;
		TupleQuery targetQuery = null;
		List<MappingOntologyStatsBean> statsBean = null;
		try {
			// Source processing
			sourceQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					sourceMappingsQuery);
			targetQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					targetMappingsQuery);

			statsBean = getOntologyCountList(con, sourceQuery, targetQuery);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}

		return statsBean;
	}

	public List<MappingConceptStatsBean> getOntologyConceptsCount(
			URI ontologyURI, Integer limit) {
		List<MappingConceptStatsBean> concepts = new ArrayList<MappingConceptStatsBean>();

		String sourceMappingsQuery = conceptCount.replaceAll("%ONT%",
				ontologyURI.toString()).replaceAll("%LIMIT%", limit.toString());

		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		TupleQuery query = null;
		try {
			// Source processing
			query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					sourceMappingsQuery);
			TupleQueryResult result = query.evaluate();

			while (result.hasNext()) {
				BindingSet bs = result.next();

				String conceptId = bs.getValue("conceptId").stringValue();
				Integer count = convertValueToInteger(bs.getValue("count"));

				MappingConceptStatsBean stats = new MappingConceptStatsBean();
				stats.setFullId(conceptId);
				stats.setCount(count);
				concepts.add(stats);
			}

			result.close();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} finally {
			cleanup(con);
		}

		return concepts;
	}

	public List<MappingUserStatsBean> getOntologyUserCount(URI ontologyURI,
			URI targetOntologyURI) {
		List<MappingUserStatsBean> users = new ArrayList<MappingUserStatsBean>();

		String queryTemplate = targetOntologyURI == null ? userCount
				: userCountWithTarget;

		String userCountQuery = queryTemplate.replaceAll("%ONT%",
				ontologyURI.toString());

		if (targetOntologyURI != null)
			userCountQuery = userCountQuery.replaceAll("%TARG%",
					targetOntologyURI.toString());

		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		TupleQuery query = null;
		try {
			// Source processing
			query = con.prepareTupleQuery(QueryLanguage.SPARQL, userCountQuery);
			TupleQueryResult result = query.evaluate();
			while (result.hasNext()) {
				BindingSet bs = result.next();

				Integer userId = convertValueToInteger(bs.getValue("userId"));
				Integer count = convertValueToInteger(bs.getValue("count"));

				MappingUserStatsBean user = new MappingUserStatsBean();
				user.setUserId(userId);
				user.setMappingCount(count);
				users.add(user);
			}

			result.close();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return users;
	}

	/**
	 * Private Methods
	 */

	private ArrayList<MappingOntologyStatsBean> getOntologyCountList(
			RepositoryConnection con, TupleQuery sourceQuery,
			TupleQuery targetQuery) {
		// Use for ontology lookup since we have to use two queries
		HashMap<Integer, MappingOntologyStatsBean> countsMap = new HashMap<Integer, MappingOntologyStatsBean>();

		try {
			TupleQueryResult result = sourceQuery.evaluate();

			while (result.hasNext()) {
				BindingSet bs = result.next();

				URI sourceOntologyURI = (URI)bs.getValue("sourceOntologyId");
				Integer count = convertValueToInteger(bs.getValue("count"));

				Integer sourceOntologyId = Mapping.ontologyURI2Id(sourceOntologyURI);
				
				if (!countsMap.containsKey(sourceOntologyId)) {
					MappingOntologyStatsBean stats = new MappingOntologyStatsBean();
					stats.setOntologyId(sourceOntologyId);
					stats.setTargetMappings(count);
					
					countsMap.put(sourceOntologyId, stats);
				} else {
					MappingOntologyStatsBean stats = countsMap
							.get(sourceOntologyId);
					stats.setTargetMappings(count + stats.getTargetMappings());
					countsMap.put(sourceOntologyId, stats);
				}
			}

			result.close();

			// Target processing
			result = targetQuery.evaluate();

			while (result.hasNext()) {
				BindingSet bs = result.next();

				URI targetOntologyURI = (URI)bs
						.getValue("targetOntologyId");
				Integer count = convertValueToInteger(bs.getValue("count"));

				Integer targetOntologyId = Mapping.ontologyURI2Id(targetOntologyURI);
				
				if (!countsMap.containsKey(targetOntologyId)) {
					MappingOntologyStatsBean stats = new MappingOntologyStatsBean();
					stats.setOntologyId(targetOntologyId);
					stats.setSourceMappings(count);
					countsMap.put(targetOntologyId, stats);
				} else {
					MappingOntologyStatsBean stats = countsMap
							.get(targetOntologyId);
					stats.setSourceMappings(count + stats.getSourceMappings());
					countsMap.put(targetOntologyId, stats);
				}
			}

			result.close();

			// Totals
			for (Integer ontology : countsMap.keySet()) {
				MappingOntologyStatsBean stats = countsMap.get(ontology);
				Integer total = stats.getSourceMappings()
						+ stats.getTargetMappings();
				stats.setTotalMappings(total);
				countsMap.put(ontology, stats);
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		// Convert map to list
		ArrayList<MappingOntologyStatsBean> counts = new ArrayList<MappingOntologyStatsBean>();
		for (Integer ontology : countsMap.keySet()) {
			counts.add(countsMap.get(ontology));
		}

		return counts;
	}
}
