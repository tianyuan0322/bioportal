package org.ncbo.stanford.sparql.dao.provisional;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.ProvisionalTermExistsException;
import org.ncbo.stanford.exception.ProvisionalTermMissingException;
import org.ncbo.stanford.manager.rdfstore.RDFStoreManager;
import org.ncbo.stanford.sparql.bean.ProvisionalTerm;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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

public class ProvisionalTermDAO {

	protected Map<String, RDFStoreManager> rdfStoreManagerMap;

	private static String MULT_IDS = "hasMultipleOntologyIds";
	private static String MULT_SYNONYMS = "hasMultipleSynonyms";

	// This is a general SPARQL query that will produce rows of results that can
	// be interpreted as provisional terms (IE it contains all provisional term
	// fields in every row). The %FILTER% token can be replaced with whatever
	// filter is need to get specific results. %OFFSET% and %LIMIT% must be
	// replaced as well.
	protected final static String provisionalTermQuery = ""
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "SELECT DISTINCT " + "?id " + "?label " + "?ontologyId " + "?"
			+ MULT_IDS
			+ " "
			+ "?synonym "
			+ "?"
			+ MULT_SYNONYMS
			+ " "
			+ "?definition"
			+ "?superClass "
			+ "?created "
			+ "?updated "
			+ "?submittedBy "
			+ "?noteId "
			+ "?status "
			+ "?permanentId "
			+ "WHERE {"
			+ "  ?id rdfs:label ?label ."
			+ "  ?id <http://purl.bioontology.org/ontology/provisional#definition> ?definition ."
			+ "  ?id <http://purl.bioontology.org/ontology/provisional#created> ?created ."
			+ "  ?id <http://purl.bioontology.org/ontology/provisional#submitted_by> ?submittedBy ."
			+ "  { SELECT ?ontologyId { ?id <http://purl.bioontology.org/ontology/provisional#ontology_id> ?ontologyId } LIMIT 1 }"
			+ "  { SELECT ?synonym { OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#synonym> ?synonym }} LIMIT 1 }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#provisional_subclass_of> ?provisionalSubclassOf }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#has_multiple_ontology_ids> ?"
			+ MULT_IDS
			+ " }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#has_multiple_synonyms> ?"
			+ MULT_SYNONYMS
			+ " }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#updated> ?updated }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#note_id> ?noteId }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#status> ?status }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#permanent_id> ?permanentId }"
			+ "  FILTER (%FILTER%) }"
			+ "%ORDERBY% LIMIT %LIMIT% OFFSET %OFFSET%";

	protected final static String provisionalTermCountQuery = ""
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "SELECT DISTINCT "
			+ "count(DISTINCT ?id) as ?provisionalTermCount WHERE {"
			+ "  ?id rdfs:label ?label ."
			+ "  ?id <http://purl.bioontology.org/ontology/provisional#definition> ?definition ."
			+ "  ?id <http://purl.bioontology.org/ontology/provisional#created> ?created ."
			+ "  ?id <http://purl.bioontology.org/ontology/provisional#submitted_by> ?submittedBy ."
			+ "  { SELECT ?ontologyId { ?id <http://purl.bioontology.org/ontology/provisional#ontology_id> ?ontologyId } LIMIT 1 }"
			+ "  { SELECT ?synonym { OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#synonym> ?synonym }} LIMIT 1 }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#provisional_subclass_of> ?provisionalSubclassOf }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#updated> ?updated }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#note_id> ?noteId }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#status> ?status }"
			+ "  OPTIONAL { ?id <http://purl.bioontology.org/ontology/provisional#permanent_id> ?permanentId }"
			+ "  FILTER (%FILTER%) }";

	protected final static String synonymsForTerm = ""
			+ "SELECT DISTINCT ?synonym {"
			+ "  ?id <http://purl.bioontology.org/ontology/provisional#synonym> ?synonym ."
			+ "  FILTER ( ?id = <%TERM_ID%> ) }";

	protected final static String ontologyIdsForTerm = "PREFIX pid: <http://purl.bioontology.org/ontology/provisional#> "
			+ "SELECT DISTINCT ?ontologyId {"
			+ "  ?id <http://purl.bioontology.org/ontology/provisional#ontology_id> ?ontologyId ."
			+ "  FILTER ( ?id = <%TERM_ID%> ) }";

	/*******************************************************************
	 * 
	 * Generic SPARQL methods
	 * 
	 *******************************************************************/

	public List<ProvisionalTerm> getProvisionalTerms(Integer limit,
			Integer offset, String filter, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		return getProvisionalTerms(limit, offset, filter, null, parameters);
	}

	/**
	 * Generic getProvisionalTerms call. Must provide a valid SPARQL filter
	 * (generated via helper methods or elsewhere).
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
	public List<ProvisionalTerm> getProvisionalTerms(Integer limit,
			Integer offset, String filter, String orderBy,
			SPARQLFilterGenerator parameters) throws InvalidInputException {
		// Safety check
		if (limit == null || limit >= 50000) {
			limit = 50000;
		}

		if (offset == null) {
			offset = 0;
		}

		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		// Combine filters
		String combinedFilters = "";
		if (filter != null) {
			combinedFilters = (parameters != null) ? filter + " "
					+ parameters.toFilter() : filter;
		} else {
			combinedFilters = (parameters != null) ? parameters.toFilter() : "";
		}

		// Substitute tokens in the generic query string
		String queryString = provisionalTermQuery.replaceAll("%FILTER%",
				combinedFilters).replaceAll("%LIMIT%", limit.toString())
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

		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString, ApplicationConstants.PROVISIONAL_TERM_CONTEXT);

			TupleQueryResult result = query.evaluate();

			HashMap<String, ProvisionalTerm> termResults = new HashMap<String, ProvisionalTerm>();

			while (result.hasNext()) {
				BindingSet bs = result.next();

				ProvisionalTerm term = new ProvisionalTerm();

				String termId = bs.getValue("id").stringValue();

				// Set ProvisionalTerm properties (required)

				term.setId(new URIImpl(termId));

				term.setLabel(bs.getValue("label").stringValue());

				term.setCreated(convertValueToDate(bs.getValue("created")));

				term.setDefinition(bs.getValue("definition").stringValue());

				term.setSubmittedBy(convertValueToInteger(bs
						.getValue("submittedBy")));

				term.addOntologyIds(convertValueToInteger(bs
						.getValue("ontologyId")));

				// Optional values
				if (isValidValue(bs.getValue("provisionalSubclassOf")))
					term.setProvisionalSubclassOf(new URIImpl(bs.getValue(
							"provisionalSubclassOf").stringValue()));

				if (isValidValue(bs.getValue("updated")))
					term.setUpdated(convertValueToDate(bs.getValue("updated")));

				if (isValidValue(bs.getValue("noteId")))
					term.setNoteId(bs.getValue("noteId").stringValue());

				if (isValidValue(bs.getValue("status")))
					term.setStatus(bs.getValue("status").stringValue());

				if (isValidValue(bs.getValue("permanentId")))
					term.setPermanentId(new URIImpl(bs.getValue("permanentId")
							.stringValue()));

				if (isValidValue(bs.getValue("permanentId")))
					term.addSynonyms(bs.getValue("synonym").stringValue());

				// Fill in lists
				if (isValidValue(bs.getValue(MULT_SYNONYMS))) {
					String getSynonyms = synonymsForTerm.replaceAll(
							"%TERM_ID%", termId);

					TupleQuery getSynonymsQuery = con.prepareTupleQuery(
							QueryLanguage.SPARQL, getSynonyms,
							ApplicationConstants.PROVISIONAL_TERM_CONTEXT);
					TupleQueryResult getSynonymsResult = getSynonymsQuery
							.evaluate();

					while (getSynonymsResult.hasNext()) {
						BindingSet bs1 = getSynonymsResult.next();
						String synonym = bs1.getValue("synonym").stringValue();
						if (!term.getSynonyms().contains(synonym)) {
							term.addSynonyms(bs.getValue("synonym")
									.stringValue());
						}
					}
				}

				if (isValidValue(bs.getValue(MULT_IDS))) {
					String getOntologyIds = ontologyIdsForTerm.replaceAll(
							"%TERM_ID%", termId);

					TupleQuery getOntologyIdsQuery = con.prepareTupleQuery(
							QueryLanguage.SPARQL, getOntologyIds,
							ApplicationConstants.PROVISIONAL_TERM_CONTEXT);
					TupleQueryResult getOntologyIdsResult = getOntologyIdsQuery
							.evaluate();

					while (getOntologyIdsResult.hasNext()) {
						BindingSet bs2 = getOntologyIdsResult.next();
						Integer ontologyId = convertValueToInteger(bs2
								.getValue("ontologyId"));
						if (!term.getOntologyIds().contains(ontologyId)) {
							term.addOntologyIds(ontologyId);
						}
					}
				}

				termResults.put(term.getId().toString(), term);
			}

			result.close();

			return new ArrayList<ProvisionalTerm>(termResults.values());
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		return null;
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
	public Integer getCount(String filter, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		// Combine filters
		String combinedFilters = "";
		if (filter != null) {
			combinedFilters = (parameters != null) ? filter + " "
					+ parameters.toFilter() : filter;
		} else {
			combinedFilters = (parameters != null) ? parameters.toFilter() : "";
		}

		String queryString = provisionalTermCountQuery.replaceAll("%FILTER%",
				combinedFilters);

		// Remove filter if it's not used
		if (filter == null || filter.isEmpty()) {
			queryString = queryString.replaceAll("FILTER \\(\\) ", "");
		}

		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString, ApplicationConstants.PROVISIONAL_TERM_CONTEXT);
			TupleQueryResult result = query.evaluate();
			while (result.hasNext()) {
				BindingSet bs = result.next();
				return convertValueToInteger(bs
						.getValue("provisionalTermCount"));
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ProvisionalTerm getProvisionalTerm(URI id)
			throws ProvisionalTermMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		// Attempt term retrieval, return null if failure
		ProvisionalTerm term = null;
		try {
			if (hasProvisionalTerm(id, con)) {
				List<ProvisionalTerm> terms = getProvisionalTerms(null, 0,
						"?id = <" + id + ">", null);
				if (terms != null && !terms.isEmpty()) {
					term = terms.get(0);
				} else {
					throw new ProvisionalTermMissingException();
				}
			} else {
				throw new ProvisionalTermMissingException();
			}
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}

		return term;
	}

	public ProvisionalTerm createProvisionalTerm(ProvisionalTerm newTerm)
			throws ProvisionalTermExistsException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		// Fail if term exists
		if (newTerm.getId() != null && hasProvisionalTerm(newTerm.getId(), con)) {
			throw new ProvisionalTermExistsException();
		}

		ValueFactory vf = getRdfStoreManager().getValueFactory();

		ArrayList<Statement> statements = newTerm.toStatements(vf);

		for (Statement statement : statements) {
			try {
				con.add(statement,
						ApplicationConstants.PROVISIONAL_TERM_CONTEXT_URI);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		// Add triples indicating we should look up additional items
		if (newTerm.getSynonyms().size() > 1) {
			URI predicate = new URIImpl(
					ApplicationConstants.PROVISIONAL_TERM_PREFIX
							+ "has_multiple_synonyms");
			Statement statement = new StatementImpl(newTerm.getId(), predicate,
					vf.createLiteral(true));
			try {
				con.add(statement,
						ApplicationConstants.PROVISIONAL_TERM_CONTEXT_URI);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		if (newTerm.getOntologyIds().size() > 1) {
			URI predicate = new URIImpl(
					ApplicationConstants.PROVISIONAL_TERM_PREFIX
							+ "has_multiple_ontology_ids");
			Statement statement = new StatementImpl(newTerm.getId(), predicate,
					vf.createLiteral(true));
			try {
				con.add(statement,
						ApplicationConstants.PROVISIONAL_TERM_CONTEXT_URI);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		ProvisionalTerm term = null;
		try {
			term = getProvisionalTerm(newTerm.getId());
		} catch (ProvisionalTermMissingException e) {
			e.printStackTrace();
		}

		return term;
	}

	public ProvisionalTerm updateProvisionalTerm(URI id,
			List<Integer> ontologyIds, String label, List<String> synonyms,
			String definition, URI provisionalSubclassOf, Date created,
			Date updated, Integer submittedBy, String noteId, String status,
			URI permanentId) throws ProvisionalTermMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		if (!hasProvisionalTerm(id, con)) {
			throw new ProvisionalTermMissingException();
		}

		ProvisionalTerm term = getProvisionalTerm(id);

		ProvisionalTerm updatedTerm = updateProvisionalTermEntity(term,
				ontologyIds, label, synonyms, definition,
				provisionalSubclassOf, created, updated, submittedBy, noteId,
				status, permanentId);

		updatedTerm = updateProvisionalTerm(id, updatedTerm);

		return updatedTerm;
	}

	private ProvisionalTerm updateProvisionalTerm(URI id, ProvisionalTerm term)
			throws ProvisionalTermMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();

		if (!hasProvisionalTerm(id, con)) {
			throw new ProvisionalTermMissingException();
		}

		deleteProvisionalTerm(id);

		try {
			// Update date
			term.setUpdated(new Date());

			ProvisionalTerm updatedProvisionalTerm = createProvisionalTerm(term);

			return updatedProvisionalTerm;
		} catch (ProvisionalTermExistsException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void deleteProvisionalTerm(URI id)
			throws ProvisionalTermMissingException {
		RepositoryConnection con = getRdfStoreManager()
				.getRepositoryConnection();
		try {
			if (!hasProvisionalTerm(id, con)) {
				throw new ProvisionalTermMissingException();
			}

			deleteFromTripleStore(con, id);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	public List<ProvisionalTerm> getProvisionalTermsForParameters(
			Integer limit, Integer offset, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		return getProvisionalTerms(limit, offset, null, parameters);
	}

	/*******************************************************************
	 * 
	 * protected methods
	 * 
	 *******************************************************************/

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
	 * Checks the repository for a provisional term using provided id.
	 * 
	 * @param id
	 * @param con
	 * @return
	 */
	protected Boolean hasProvisionalTerm(URI id, RepositoryConnection con) {
		try {
			Statement statement = new StatementImpl(id,
					ApplicationConstants.RDF_TYPE_URI,
					ApplicationConstants.PROVISIONAL_TERM_URI);
			return con.hasStatement(statement, false,
					ApplicationConstants.PROVISIONAL_TERM_CONTEXT_URI);
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
	 * This method does the actual delete action for removing provisional terms
	 * from the triplestore. It removes all triples with a subject matching the
	 * provisional term id.
	 * 
	 * @param con
	 * @param id
	 * @throws RepositoryException
	 */
	protected void deleteFromTripleStore(RepositoryConnection con, URI id)
			throws RepositoryException {
		// Remove all triples matching the given id
		con.remove(id, null, null,
				ApplicationConstants.PROVISIONAL_TERM_CONTEXT_URI);
	}

	/**
	 * Look for valid values and update the provided provisional term bean with
	 * them. For use in re-creating a provisional term as part of an update.
	 * 
	 * @param term
	 * @param ontologyIds
	 * @param label
	 * @param synonyms
	 * @param definition
	 * @param provisionalSubclassOf
	 * @param created
	 * @param updated
	 * @param submittedBy
	 * @param noteId
	 * @param status
	 * @param permanentId
	 * @return
	 */
	protected ProvisionalTerm updateProvisionalTermEntity(ProvisionalTerm term,
			List<Integer> ontologyIds, String label, List<String> synonyms,
			String definition, URI provisionalSubclassOf, Date created,
			Date updated, Integer submittedBy, String noteId, String status,
			URI permanentId) {

		if (ontologyIds != null && ontologyIds.size() > 0)
			term.setOntologyIds(ontologyIds);
		if (label != null)
			term.setLabel(label);
		if (synonyms != null && synonyms.size() > 0)
			term.setSynonyms(synonyms);
		if (definition != null)
			term.setDefinition(definition);
		if (provisionalSubclassOf != null)
			term.setProvisionalSubclassOf(provisionalSubclassOf);
		if (created != null)
			term.setCreated(created);
		if (updated != null)
			term.setUpdated(updated);
		if (submittedBy != null)
			term.setSubmittedBy(submittedBy);
		if (noteId != null)
			term.setNoteId(noteId);
		if (status != null)
			term.setStatus(status);
		if (permanentId != null)
			term.setPermanentId(permanentId);

		return term;
	}

	protected boolean isValidValue(Value value) {
		return value != null && !value.stringValue().isEmpty();
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
