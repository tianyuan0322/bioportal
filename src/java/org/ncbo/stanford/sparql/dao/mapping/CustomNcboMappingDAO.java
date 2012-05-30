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
import org.ncbo.stanford.util.sparql.MappingFilterGenerator;
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
			URI relation, URI sourceOntology, URI targetOntology,
			URI sourceOntologyVersion, URI targetOntologyVersion,
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
		newMapping.setSourceOntology(sourceOntology);
		newMapping.setTargetOntology(targetOntology);
		newMapping.setCreatedInSourceOntologyVersion(sourceOntologyVersion);
		newMapping.setCreatedInTargetOntologyVersion(targetOntologyVersion);

		// Set metadata properties
		newMapping.setDependency(dependency);
		processInfo.setSubmittedBy(submittedBy);
		processInfo.setDate(new Date());
		newMapping.setComment(comment);
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

		String unionBlockTemplate = "{ ?id maps:source_ontology <%SOURCE_ONT%> . "
				+ "?id maps:source <%SOURCE_TERM%> ."
				+ "?id maps:target_ontology <%TARGET_ONT%> ."
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
							.replace("%SOURCE_ONT%",
									Integer.toString(opbA.getOntologyId()))
							.replace("%SOURCE_TERM%", opbA.getConceptId())
							.replace("%TARGET_ONT%",
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
			URI relation, URI sourceOntology, URI targetOntology,
			URI sourceOntologyVersion, URI targetOntologyVersion,
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
				relation, sourceOntology, targetOntology,
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
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology> <%source_ont%> ."
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology> <%target_ont%> ."
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#has_process_info> ?procInf ."
				+ "  ?procInf <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#submitted_by> %submitted_by% ."
				+ "}";
		queryString = queryString
				.replace("%source%", mapping.getTarget().get(0).toString())
				.replace("%target%", mapping.getSource().get(0).toString())
				.replace("%source_ont%",
						mapping.getTargetOntology().toString())
				.replace("%target_ont%",
						mapping.getSourceOntology().toString())
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
			Integer offset, MappingFilterGenerator parameters)
			throws InvalidInputException {
		return getMappings(limit, offset, null, parameters);
	}

	/*******************************************************************
	 * 
	 * Mappings for ontologies
	 * 
	 *******************************************************************/

	public List<Mapping> getMappingsFromOntology(URI ontology,
			Integer limit, Integer offset, MappingFilterGenerator parameters)
			throws InvalidInputException {
		String filter = generateOntologySparqlFilter(ontology, null, true);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<Mapping> getMappingsToOntology(URI ontology,
			Integer limit, Integer offset, MappingFilterGenerator parameters)
			throws InvalidInputException {
		String filter = generateOntologySparqlFilter(null, ontology, true);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<Mapping> getMappingsBetweenOntologies(URI sourceOntology,
			URI targetOntology, Boolean unidirectional, Integer limit,
			Integer offset, MappingFilterGenerator parameters)
			throws InvalidInputException {
		CustomNcboMappingCountsDAO mappingCountDAO = new CustomNcboMappingCountsDAO();
		mappingCountDAO.setRdfStoreManagerMap(rdfStoreManagerMap);
		Integer sourceTargetCount = mappingCountDAO.getCountMappingsBetweenOntologies(sourceOntology, targetOntology, true, parameters);
		
		List<Mapping> mappings = null;
		// This is a mess. Because we use two different queries for essentially two separate sets of mappings, we can't just 
		// query both of them with the same offset and limit because the paging gets all messed up (it returns 20 results
		// even though you set the limit to 10. So instead, we're going to serialize them and retrieve all source-to-target
		// mappings, then start retrieving all target-to-source mappings with some special handling if you end up on a 
		// page and limit combo that would have a mix of the two.
		if (sourceTargetCount > limit + offset) {
			mappings = super
					.getMappingsBetweenOntologies(sourceOntology, targetOntology,
							true, limit, offset, parameters);
		} else if (offset - sourceTargetCount < 0 && offset + limit - sourceTargetCount > 0) {
			// Get any remaining mappings, this call will return both sourceTarget and targetSource (if bidirectional)
			Integer sourceTargetLimit = (offset - sourceTargetCount) * -1;
			Integer targetSourceLimit = limit - sourceTargetLimit;
			Integer targetSourceOffset = 0;
			mappings = super
					.getMappingsBetweenOntologies(sourceOntology, targetOntology,
							true, sourceTargetLimit, offset, parameters);
			
			// We'll return these when getting bidirectional
			if (!unidirectional) {
				mappings.addAll(super.getMappingsBetweenOntologies(targetOntology,
						sourceOntology, true, targetSourceLimit, targetSourceOffset, parameters));
			}
		} else {
			// This will only respond when all the other mappings are returned and the only ones left are targetSource
			if (!unidirectional) {
				// Since we only start returning these mappings after the sourceTarget ones are finished we have to mess with the offset
				// What we want to do is figure out where we're actually at for this set of mappings, regardless of the total offset
				Integer shiftedOffset = offset - sourceTargetCount;
				mappings = super.getMappingsBetweenOntologies(targetOntology,
						sourceOntology, true, limit, shiftedOffset, parameters);
			}
		}
		return mappings;
	}

	public List<Mapping> getRankedMappingsBetweenOntologies(
			URI sourceOntology, URI targetOntology, Integer limit,
			Integer offset, MappingFilterGenerator parameters)
			throws InvalidInputException {
		String queryString = "select ?source (count(?target) as ?count) where {                                                    "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source> ?source .                   "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target> ?target .                   "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology> <%SOURCE_ONT%> .  "
				+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology> <%TARGET_ONT%> .  "
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
				generator.setParameters(parameters);
				generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY,sourceOntology);
				generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY,targetOntology);
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

	public List<Mapping> getMappingsForOntology(URI ontologyURI,
			Integer limit, Integer offset, MappingFilterGenerator parameters)
			throws InvalidInputException {
		
		CustomNcboMappingCountsDAO mappingCountDAO = new CustomNcboMappingCountsDAO();
		mappingCountDAO.setRdfStoreManagerMap(rdfStoreManagerMap);
		Integer sourceTargetCount = mappingCountDAO.getCountMappingsFromOntology(ontologyURI, parameters);
		
		if (sourceTargetCount > limit + offset) {
		/* from block */
			parameters.setSourceOntology(ontologyURI);
			List<Mapping> mappings =  getMappings(limit, offset, null, parameters, true); 
			return mappings;
		} else if (offset - sourceTargetCount < 0 && offset + limit - sourceTargetCount > 0) {
			Integer sourceTargetLimit = (offset - sourceTargetCount) * -1;
			Integer targetSourceLimit = limit - sourceTargetLimit;
			Integer targetSourceOffset = 0;
			parameters.setSourceOntology(ontologyURI);
			List<Mapping> mappings =  getMappings(sourceTargetLimit, offset, null, parameters, true);

			parameters.setSourceOntology(null);
			parameters.setTargetOntology(ontologyURI);
			List<Mapping> mappingsPageB =  getMappings(targetSourceLimit, targetSourceOffset, null, parameters, true);
			mappings.addAll(mappingsPageB);
			return mappings;
		} else {
			Integer shiftedOffset = offset - sourceTargetCount;
			parameters.setTargetOntology(ontologyURI);
			List<Mapping> mappingsPageB =  getMappings(limit, shiftedOffset, null, parameters, true);
			return mappingsPageB;
		}

	}

	/*******************************************************************
	 * 
	 * Mappings for concepts
	 * 
	 *******************************************************************/

	public List<Mapping> getMappingsForConcept(URI ontologyId,
			String conceptId, Integer limit, Integer offset,
			MappingFilterGenerator parameters) throws InvalidInputException {
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
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY,ontologyId);
			generator.addBindValue(ApplicationConstants.SOURCE_TERM,
					new URIImpl(conceptId));

			generator.addBidirectional(ApplicationConstants.SOURCE_TERM,
					ApplicationConstants.TARGET_TERM);
			generator.addBidirectional(ApplicationConstants.SOURCE_ONTOLOGY,
					ApplicationConstants.TARGET_ONTOLOGY);
			mappingIds = getMappingIdsFromUnion(generator);
		}
		String mappingIdFilter = generateMappingIdFilter(mappingIds);
		List<Mapping> mappings = new ArrayList<Mapping>();
		if (mappingIds.size() > 0) {
			mappings = getMappings(limit, offset, mappingIdFilter, parameters);
		}

		return mappings;
	}

	public List<Mapping> getMappingsFromConcept(URI ontologyId,
			String conceptId, Integer limit, Integer offset,
			MappingFilterGenerator parameters) throws InvalidInputException {

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
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY,ontologyId);
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

	public List<Mapping> getMappingsToConcept(URI ontologyId,
			String conceptId, Integer limit, Integer offset,
			MappingFilterGenerator parameters) throws InvalidInputException {

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
			generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY,ontologyId);
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

	public List<Mapping> getMappingsBetweenConcepts(URI sourceOntology,
			URI targetOntology, String fromConceptId, String toConceptId,
			Boolean unidirectional, Integer limit, Integer offset,
			MappingFilterGenerator parameters) throws InvalidInputException {

		Set<String> mappingIds = null;
		if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
			String filter = generateConceptSparqlFilter(fromConceptId,
					toConceptId, unidirectional);
			filter += " && "
					+ generateOntologySparqlFilter(sourceOntology,
							targetOntology, unidirectional);

			mappingIds = getMappingIdsFromFilter(limit, offset, filter);
		} else {
			SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
			generator.setLimit(limit);
			generator.setOffset(offset);
			generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY,sourceOntology);
			generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY,targetOntology);
			generator.addBindValue(ApplicationConstants.SOURCE_TERM,
					new URIImpl(fromConceptId));
			generator.addBindValue(ApplicationConstants.TARGET_TERM,
					new URIImpl(toConceptId));
			if (!unidirectional) {
				generator.addBidirectional(ApplicationConstants.SOURCE_TERM,
						ApplicationConstants.TARGET_TERM);
				generator.addBidirectional(
						ApplicationConstants.SOURCE_ONTOLOGY,
						ApplicationConstants.TARGET_ONTOLOGY);
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
