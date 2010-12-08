package org.ncbo.stanford.domain.custom.dao.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ncbo.stanford.bean.mapping.MappingConceptStatsBean;
import org.ncbo.stanford.bean.mapping.MappingOntologyStatsBean;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

public class CustomNcboMappingStatsDAO extends AbstractNcboMappingDAO {

	private static final String totalMappings = "SELECT count(?mappingId) as ?mappings "
			+ "WHERE { ?mappingId a <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#One_To_One_Mapping> . }";

	private static final String ontologySourceCount = "SELECT ?sourceOntologyId count(?sourceOntologyId) as ?count "
			+ "WHERE { ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id> ?sourceOntologyId . }";
	private static final String ontologyTargetCount = "SELECT ?targetOntologyId count(?targetOntologyId) as ?count "
			+ "WHERE { ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id> ?targetOntologyId . }";

	private static final String sourceMappings = "SELECT ?sourceOntologyId "
			+ "count(?sourceOntologyId) as ?count "
			+ "WHERE { ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id> %ONT% . "
			+ "?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id> ?sourceOntologyId . "
			+ "} ORDER BY DESC(?count)";
	private static final String targetMappings = "SELECT ?targetOntologyId "
		+ "count(?targetOntologyId) as ?count "
		+ "WHERE { ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id> %ONT% . "
		+ "?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id> ?targetOntologyId . "
		+ "} ORDER BY DESC(?count)";

	private static final String conceptCount = "SELECT ?conceptId count(?conceptId) as ?count "
			+ "WHERE { { ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id> %ONT% . "
			+ "?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source> ?conceptId . } "
			+ "UNION { ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id> %ONT% . "
			+ "?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target> ?conceptId . } "
			+ "} ORDER BY DESC(?count) LIMIT %LIMIT%";

	/**
	 * Gets a list of recent mappings up to size of limit.
	 * 
	 * @param limit
	 * @return
	 * @throws InvalidInputException
	 */
	public List<OneToOneMapping> getRecentMappings(Integer limit)
			throws InvalidInputException {
		String orderBy = "?date";

		return getMappings(limit, 0, null, orderBy, null);
	}

	/**
	 * Returns the total number of mappings in the triplestore.
	 * 
	 * @return
	 */
	public Integer getTotalMappingsCount() {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		TupleQuery query;
		Integer totalMappingsCount = 0;
		try {
			query = con.prepareTupleQuery(QueryLanguage.SPARQL, totalMappings,
					ApplicationConstants.MAPPING_CONTEXT);
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
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		TupleQuery sourceQuery = null;
		TupleQuery targetQuery = null;
		try {
			sourceQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					ontologySourceCount, ApplicationConstants.MAPPING_CONTEXT);
			targetQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					ontologyTargetCount, ApplicationConstants.MAPPING_CONTEXT);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		return getOntologyCountList(con, sourceQuery, targetQuery);
	}

	/**
	 * Returns an object with virtual ids and their source/target mapping counts
	 * that correspond to a particular ontology.
	 * 
	 * @return
	 */
	public List<MappingOntologyStatsBean> getOntologyMappingCount(
			Integer ontologyId) {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		String sourceMappingsQuery = sourceMappings.replaceAll("%ONT%",
				ontologyId.toString());
		String targetMappingsQuery = targetMappings.replaceAll("%ONT%",
				ontologyId.toString());

		TupleQuery sourceQuery = null;
		TupleQuery targetQuery = null;
		try {
			// Source processing
			sourceQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					sourceMappingsQuery, ApplicationConstants.MAPPING_CONTEXT);
			targetQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					targetMappingsQuery, ApplicationConstants.MAPPING_CONTEXT);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		return getOntologyCountList(con, sourceQuery, targetQuery);
	}

	public List<MappingConceptStatsBean> getOntologyConceptsCount(
			Integer ontologyId, Integer limit) {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();
		
		List<MappingConceptStatsBean> concepts = new ArrayList<MappingConceptStatsBean>();

		String sourceMappingsQuery = conceptCount.replaceAll("%ONT%",
				ontologyId.toString()).replaceAll("%LIMIT%", limit.toString());

		TupleQuery query = null;
		try {
			// Source processing
			query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					sourceMappingsQuery, ApplicationConstants.MAPPING_CONTEXT);
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
		}

		return concepts;
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

				Integer sourceOntologyId = convertValueToInteger(bs
						.getValue("sourceOntologyId"));
				Integer count = convertValueToInteger(bs.getValue("count"));

				if (!countsMap.containsKey(sourceOntologyId)) {
					MappingOntologyStatsBean stats = new MappingOntologyStatsBean();
					stats.setOntologyId(sourceOntologyId);
					stats.setTargetMappings(count);
					countsMap.put(sourceOntologyId, stats);
				} else {
					MappingOntologyStatsBean stats = countsMap
							.get(sourceOntologyId);
					stats.setTargetMappings(count);
					countsMap.put(sourceOntologyId, stats);
				}
			}

			result.close();

			// Target processing
			result = targetQuery.evaluate();

			while (result.hasNext()) {
				BindingSet bs = result.next();

				Integer targetOntologyId = convertValueToInteger(bs
						.getValue("targetOntologyId"));
				Integer count = convertValueToInteger(bs.getValue("count"));

				if (!countsMap.containsKey(targetOntologyId)) {
					MappingOntologyStatsBean stats = new MappingOntologyStatsBean();
					stats.setOntologyId(targetOntologyId);
					stats.setSourceMappings(count);
					countsMap.put(targetOntologyId, stats);
				} else {
					MappingOntologyStatsBean stats = countsMap
							.get(targetOntologyId);
					stats.setSourceMappings(count);
					countsMap.put(targetOntologyId, stats);
				}
			}

			result.close();
			
			// Totals
			for (Integer ontology : countsMap.keySet()) {
				MappingOntologyStatsBean stats = countsMap.get(ontology);
				Integer total = stats.getSourceMappings() + stats.getTargetMappings();
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