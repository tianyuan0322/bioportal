package org.ncbo.stanford.domain.custom.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.manager.rdfstore.RDFStoreManager;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;

public class CustomNcboMappingDAO {

	private Map<String, RDFStoreManager> rdfStoreManagerMap;

	// This is a general SPARQL query that will produce rows of results that can
	// be interpreted as mappings (IE it contains all mappings fields in every
	// row). The %FILTER% token can be replaced with whatever filter is need to
	// get specific results. %OFFSET% and %LIMIT% must be replaced as well.
	private final static String mappingQuery = "SELECT  "
			+ "?mappingId "
			+ "?source "
			+ "?target "
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
			+ "WHERE {"
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source> ?source ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target> ?target ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#relation> ?relation ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id> ?sourceOntologyId ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id> ?targetOntologyId ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#created_in_source_ontology_version> ?createdInSourceOntologyVersion ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#created_in_target_ontology_version> ?createdInTargetOntologyVersion ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#comment> ?comment ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#date> ?date ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#submitted_by> ?submittedBy ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#dependency> ?dependency ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_type> ?mappingType ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source> ?mappingSource ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_name> ?mappingSourceName ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_contact_info> ?mappingSourceContactInfo ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_site> ?mappingSourceSite ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_algorithm> ?mappingSourceAlgorithm ."
			+ "  FILTER (%FILTER%) } LIMIT %LIMIT% OFFSET %OFFSET%";

	private final static String mappingCountQuery = "SELECT  "
			+ "count(?mappingId) as ?mappingCount " + "WHERE {"
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source> ?source ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target> ?target ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#relation> ?relation ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id> ?sourceOntologyId ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id> ?targetOntologyId ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#created_in_source_ontology_version> ?createdInSourceOntologyVersion ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#created_in_target_ontology_version> ?createdInTargetOntologyVersion ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#comment> ?comment ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#date> ?date ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#submitted_by> ?submittedBy ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#dependency> ?dependency ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_type> ?mappingType ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source> ?mappingSource ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_name> ?mappingSourceName ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_contact_info> ?mappingSourceContactInfo ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_site> ?mappingSourceSite ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_algorithm> ?mappingSourceAlgorithm ."
			+ "  FILTER (%FILTER%) }";

	public OneToOneMapping createMapping(URI source, URI target, URI relation,
			Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, String comment, String mappingSource,
			String mappingSourceName, String mappingSourceContactInfo,
			URI mappingSourceSite, String mappingSourceAlgorithm,
			String mappingType) throws MappingExistsException {

		OneToOneMapping mapping = new OneToOneMapping();

		// Set Mapping properties
		mapping.setSource(source);
		mapping.setTarget(target);
		mapping.setRelation(relation);
		mapping.setSourceOntologyId(sourceOntologyId);
		mapping.setTargetOntologyId(targetOntologyId);
		mapping.setCreatedInSourceOntologyVersion(sourceOntologyVersion);
		mapping.setCreatedInTargetOntologyVersion(targetOntologyVersion);

		// Set metadata properties
		mapping.setSubmittedBy(submittedBy);
		mapping.setDependency(mapping.getId());
		mapping.setDate(new Date());
		mapping.setComment(comment);
		mapping.setMappingType(mappingType);

		// Set mappingSource properties
		mapping.setMappingSource(mappingSource);
		mapping.setMappingSourceName(mappingSourceName);
		mapping.setMappingSourcecontactInfo(mappingSourceContactInfo);
		mapping.setMappingSourceSite(mappingSourceSite);
		mapping.setMappingSourceAlgorithm(mappingSourceAlgorithm);

		createMapping(mapping);

		return mapping;
	}

	public OneToOneMapping createMapping(OneToOneMapping newMapping)
			throws MappingExistsException {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		OneToOneMapping mapping = new OneToOneMapping();
		try {
			if (hasMapping(newMapping.getId(), con)) {
				throw new MappingExistsException();
			}

			con.addObject(newMapping.getId(), newMapping);
			mapping = con.getObject(OneToOneMapping.class, newMapping.getId());
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mapping;
	}

	public OneToOneMapping getMapping(URI id) throws MappingMissingException {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		// Attempt mapping retrieval, return null if failure
		OneToOneMapping mapping = null;
		try {
			if (hasMapping(id, con)) {
				mapping = con.getObject(OneToOneMapping.class, id);
			} else {
				throw new MappingMissingException();
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mapping;
	}

	// TODO: This method is not working currently.
	public OneToOneMapping updateMapping(URI id, URI source, URI target,
			URI relation, Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, String comment, String mappingSource,
			String mappingSourceName, String mappingSourcecontactInfo,
			String mappingSourceSite, String mappingSourceAlgorithm,
			String mappingType) throws MappingMissingException {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		try {
			if (!hasMapping(id, con)) {
				throw new MappingMissingException();
			}

			OneToOneMapping mapping = con.getObject(OneToOneMapping.class, id);

			deleteMapping(id);

			// Retrieve mapping to update and set new information
			if (sourceOntologyVersion != null)
				mapping
						.setCreatedInSourceOntologyVersion(sourceOntologyVersion);
			if (targetOntologyVersion != null)
				mapping
						.setCreatedInTargetOntologyVersion(targetOntologyVersion);
			if (relation != null)
				mapping.setRelation(relation);
			if (source != null)
				mapping.setSource(source);
			if (sourceOntologyId != null)
				mapping.setSourceOntologyId(sourceOntologyId);
			if (target != null)
				mapping.setTarget(target);
			if (targetOntologyId != null)
				mapping.setTargetOntologyId(targetOntologyId);

			// Save the new mapping
			con.addObject(id, mapping);

			return mapping;
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public OneToOneMapping updateMapping(URI id, OneToOneMapping mapping)
			throws MappingMissingException {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		deleteMapping(id);

		try {
			// Save the new mapping
			con.addObject(id, mapping);

			return mapping;
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void deleteMapping(URI id) throws MappingMissingException {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();
		try {
			/**
			 * Commenting out until we see what AliBaba can fix. Currently their
			 * recommended method for deletion doesn't work.
			 */
			// OneToOneMapping mapping = con.getObject(OneToOneMapping.class,
			// id);
			//
			// // Set properties to null values (weird delete method w/ AliBaba)
			// mapping.setId(null);
			// mapping.setSource(null);
			// mapping.setTarget(null);
			// mapping.setRelation(null);
			// mapping.setSourceOntologyId(null);
			// mapping.setTargetOntologyId(null);
			// mapping.setCreatedInSourceOntologyVersion(null);
			// mapping.setCreatedInTargetOntologyVersion(null);
			//
			// // Delete the mapping and metadata
			// con.removeDesignation(mapping, OneToOneMapping.class);

			// Alternative method for removing mapping
			// Get all triples with subject matching the id for this mapping

			if (!hasMapping(id, con)) {
				throw new MappingMissingException();
			}

			RepositoryResult<Statement> results = con.getStatements(id, null,
					null, false, new URIImpl(
							ApplicationConstants.MAPPING_CONTEXT));
			// Remove all those triples
			con.remove(results, new URIImpl(
					ApplicationConstants.MAPPING_CONTEXT));

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*******************************************************************
	 * 
	 * Count methods
	 * 
	 *******************************************************************/

	public Integer getCountMappingsFromOntology(Integer ontologyId)
			throws InvalidInputException {
		return getCount(ontologyId, null, true);
	}

	public Integer getCountMappingsToOntology(Integer ontologyId)
			throws InvalidInputException {
		return getCount(null, ontologyId, true);
	}

	public Integer getCountMappingsBetweenOntologies(Integer sourceOntology,
			Integer targetOntology, Boolean unidirectional)
			throws InvalidInputException {
		return getCount(sourceOntology, targetOntology, unidirectional);
	}

	public Integer getCountMappingsForOntology(Integer ontologyId)
			throws InvalidInputException {
		return getCount(ontologyId, null, false);
	}

	private Integer getCount(Integer sourceOntology, Integer targetOntology,
			Boolean unidirectional) throws InvalidInputException {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		String filter = generateOntologySparqlFilter(sourceOntology,
				targetOntology, unidirectional);

		String queryString = mappingCountQuery.replaceAll("%FILTER%", filter);

		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString, ApplicationConstants.MAPPING_CONTEXT);
			TupleQueryResult result = query.evaluate();
			while (result.hasNext()) {
				BindingSet bs = result.next();
				return convertValueToInteger(bs.getValue("mappingCount"));
			}
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/*******************************************************************
	 * 
	 * Mappings for ontologies
	 * 
	 *******************************************************************/

	public ArrayList<OneToOneMapping> getMappingsFromOntology(
			Integer ontologyId, Integer limit, Integer offset)
			throws InvalidInputException {
		return getMappings(ontologyId, null, true, limit, offset);
	}

	public ArrayList<OneToOneMapping> getMappingsToOntology(Integer ontologyId,
			Integer limit, Integer offset) throws InvalidInputException {
		return getMappings(null, ontologyId, true, limit, offset);
	}

	public ArrayList<OneToOneMapping> getMappingsBetweenOntologies(
			Integer sourceOntology, Integer targetOntology,
			Boolean unidirectional, Integer limit, Integer offset)
			throws InvalidInputException {
		return getMappings(sourceOntology, targetOntology, unidirectional,
				limit, offset);
	}

	public ArrayList<OneToOneMapping> getMappingsForOntology(
			Integer ontologyId, Integer limit, Integer offset)
			throws InvalidInputException {
		return getMappings(ontologyId, null, false, limit, offset);
	}

	private ArrayList<OneToOneMapping> getMappings(Integer sourceOntology,
			Integer targetOntology, Boolean unidirectional, Integer limit,
			Integer offset) throws InvalidInputException {
		// Safety check
		if (limit == null || limit >= 50000) {
			limit = 50000;
		}

		if (offset == null) {
			offset = 0;
		}

		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		// Get a filter for use in the query
		String filter = generateOntologySparqlFilter(sourceOntology,
				targetOntology, unidirectional);

		// Substitute tokens in the generic query string
		String queryString = mappingQuery.replaceAll("%FILTER%", filter)
				.replaceAll("%LIMIT%", limit.toString()).replaceAll("%OFFSET%",
						offset.toString());

		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString, ApplicationConstants.MAPPING_CONTEXT);

			Long start = System.currentTimeMillis();
			TupleQueryResult result = query.evaluate();
			System.out.println("Evaluate query time: "
					+ (System.currentTimeMillis() - start));

			ArrayList<OneToOneMapping> mappings = new ArrayList<OneToOneMapping>();
			start = System.currentTimeMillis();
			while (result.hasNext()) {
				BindingSet bs = result.next();

				OneToOneMapping mapping = new OneToOneMapping();

				// Set Mapping properties
				mapping.setSource(new URIImpl(bs.getValue("source")
						.stringValue()));
				mapping.setTarget(new URIImpl(bs.getValue("target")
						.stringValue()));
				mapping.setRelation(new URIImpl(bs.getValue("relation")
						.stringValue()));
				mapping.setSourceOntologyId(convertValueToInteger(bs
						.getValue("sourceOntologyId")));
				mapping.setTargetOntologyId(convertValueToInteger(bs
						.getValue("targetOntologyId")));
				mapping
						.setCreatedInSourceOntologyVersion(convertValueToInteger(bs
								.getValue("createdInSourceOntologyVersion")));
				mapping
						.setCreatedInTargetOntologyVersion(convertValueToInteger(bs
								.getValue("createdInTargetOntologyVersion")));

				// Set metadata properties
				mapping.setSubmittedBy(convertValueToInteger(bs
						.getValue("submittedBy")));
				mapping.setDependency(new URIImpl(bs.getValue("mappingId")
						.stringValue()));
				mapping.setDate(convertValueToDate(bs.getValue("date")));
				mapping.setComment(bs.getValue("comment").stringValue());
				mapping
						.setMappingType(bs.getValue("mappingType")
								.stringValue());

				// Set mappingSource properties
				mapping.setMappingSource(bs.getValue("mappingSource")
						.stringValue());
				mapping.setMappingSourceName(bs.getValue("mappingSourceName")
						.stringValue());
				mapping.setMappingSourcecontactInfo(bs.getValue(
						"mappingSourceContactInfo").stringValue());
				mapping.setMappingSourceSite(new URIImpl(bs.getValue(
						"mappingSourceSite").stringValue()));
				mapping.setMappingSourceAlgorithm(bs.getValue(
						"mappingSourceAlgorithm").stringValue());

				mappings.add(mapping);
			}
			System.out.println("Create mappings list time: "
					+ (System.currentTimeMillis() - start)
					+ " || Result size: " + mappings.size());

			result.close();

			return mappings;
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/*******************************************************************
	 * 
	 * Concept methods
	 * 
	 *******************************************************************/

	public List<OneToOneMapping> getMappingsFromConcept(String conceptId) {

		return null;
	}

	public List<OneToOneMapping> getMappingsToConcept(String conceptId) {

		return null;
	}

	public List<OneToOneMapping> getMappingsBetweenConcept(
			String fromConceptId, String toConceptId) {

		return null;
	}

	/*******************************************************************
	 * 
	 * Private methods
	 * 
	 *******************************************************************/

	/**
	 * Generates a SPARQL filter for the given ontology ids.
	 * 
	 * @param sourceOntology
	 * @param targetOntology
	 * @param unidirectional
	 * @return
	 * @throws InvalidInputException
	 */
	private String generateOntologySparqlFilter(Integer sourceOntology,
			Integer targetOntology, Boolean unidirectional)
			throws InvalidInputException {
		// Determine the SPARQL filter to use based on directionality
		// Default is bidirectional
		String filter = "?sourceOntologyId = " + sourceOntology
				+ " || ?targetOntologyId = " + targetOntology
				+ " || ?sourceOntologyId = " + targetOntology
				+ " || ?targetOntologyId = " + sourceOntology;
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
	 * @param sourceOntology
	 * @param targetOntology
	 * @param unidirectional
	 * @return
	 * @throws InvalidInputException
	 */
	private String generateOntologySparqlClause(Integer sourceOntology,
			Integer targetOntology, Boolean unidirectional)
			throws InvalidInputException {
		String src = " <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id> ";
		String tgt = " <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id> ";
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

	private Integer convertValueToInteger(Value val) {
		LiteralImpl integerVal = (LiteralImpl) val;
		return integerVal.intValue();
	}

	private Date convertValueToDate(Value val) {
		LiteralImpl dateVal = (LiteralImpl) val;
		return dateVal.calendarValue().toGregorianCalendar().getTime();
	}

	/**
	 * Checks the repository for a mapping using provided id.
	 * 
	 * @param id
	 * @param con
	 * @return
	 */
	private Boolean hasMapping(URI id, ObjectConnection con) {
		try {
			return con.hasStatement(id, ApplicationConstants.RDF_TYPE_URI,
					ApplicationConstants.MAPPING_ONE_TO_ONE_URI,
					ApplicationConstants.MAPPING_CONTEXT_URI);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
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
	private RDFStoreManager getRdfStoreManager() {
		String storeType = MessageUtils.getMessage("rdf.store.type");
		return rdfStoreManagerMap.get(storeType);
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