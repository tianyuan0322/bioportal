package org.ncbo.stanford.domain.custom.dao.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.bean.mapping.MappingParametersBean;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.ncbo.stanford.exception.InvalidInputException;
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
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;

public class AbstractNcboMappingDAO {

	protected Map<String, RDFStoreManager> rdfStoreManagerMap;

	// This is a general SPARQL query that will produce rows of results that can
	// be interpreted as mappings (IE it contains all mappings fields in every
	// row). The %FILTER% token can be replaced with whatever filter is need to
	// get specific results. %OFFSET% and %LIMIT% must be replaced as well.
	protected final static String mappingQuery = "SELECT  "
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
			+ "  FILTER (%FILTER%) } %ORDERBY% LIMIT %LIMIT% OFFSET %OFFSET%";

	protected final static String mappingCountQuery = "SELECT  "
			+ "count(?mappingId) as ?mappingCount WHERE {"
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
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_type> ?mappingType ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source> ?mappingSource ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_name> ?mappingSourceName ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_contact_info> ?mappingSourceContactInfo ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_site> ?mappingSourceSite ."
			+ "  ?mappingId <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#mapping_source_algorithm> ?mappingSourceAlgorithm ."
			+ "  FILTER (%FILTER%) }";

	/*******************************************************************
	 * 
	 * Generic SPARQL methods
	 * 
	 *******************************************************************/

	protected ArrayList<OneToOneMapping> getMappings(Integer limit,
			Integer offset, String filter, MappingParametersBean parameters)
			throws InvalidInputException {
		return getMappings(limit, offset, filter, null, parameters);
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
	protected ArrayList<OneToOneMapping> getMappings(Integer limit,
			Integer offset, String filter, String orderBy,
			MappingParametersBean parameters) throws InvalidInputException {
		// Safety check
		if (limit == null || limit >= 50000) {
			limit = 50000;
		}

		if (offset == null) {
			offset = 0;
		}

		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		// Combine filters
		String combinedFilters = "";
		if (filter != null) {
			combinedFilters = (parameters != null) ? filter + " "
					+ parameters.toFilter() : filter;
		} else {
			combinedFilters = (parameters != null) ? parameters.toFilter() : "";
		}

		// Substitute tokens in the generic query string
		String queryString = mappingQuery.replaceAll("%FILTER%",
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
				mapping.setId(new URIImpl(bs.getValue("mappingId")
						.stringValue()));
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
				if (!bs.getValue("dependency").stringValue().isEmpty()) {
					mapping.setDependency(new URIImpl(bs.getValue("dependency")
							.stringValue()));
				}
				mapping.setSubmittedBy(convertValueToInteger(bs
						.getValue("submittedBy")));
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
				mapping.setMappingSourceAlgorithm(bs.getValue(
						"mappingSourceAlgorithm").stringValue());

				// Because this can be blank we have to deal with empty strings
				String mappingSourceSite = bs.getValue("mappingSourceSite")
						.stringValue();
				URI mappingSourceSiteURI = null;
				if (!mappingSourceSite.isEmpty()) {
					mappingSourceSiteURI = new URIImpl(mappingSourceSite);
				}
				mapping.setMappingSourceSite(mappingSourceSiteURI);

				mappings.add(mapping);
			}
			System.out.println("Create mappings list time: "
					+ (System.currentTimeMillis() - start)
					+ " || Result size: " + mappings.size());

			result.close();

			return mappings;
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
	protected Integer getCount(String filter, MappingParametersBean parameters)
			throws InvalidInputException {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		// Combine filters
		String combinedFilters = "";
		if (filter != null) {
			combinedFilters = (parameters != null) ? filter + " "
					+ parameters.toFilter() : filter;
		} else {
			combinedFilters = (parameters != null) ? parameters.toFilter() : "";
		}

		String queryString = mappingCountQuery.replaceAll("%FILTER%",
				combinedFilters);

		try {
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString, ApplicationConstants.MAPPING_CONTEXT);
			TupleQueryResult result = query.evaluate();
			while (result.hasNext()) {
				BindingSet bs = result.next();
				return convertValueToInteger(bs.getValue("mappingCount"));
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

	/*******************************************************************
	 * 
	 * protected methods
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
	 * @param con
	 * @return
	 */
	protected Boolean hasMapping(URI id, ObjectConnection con) {
		try {
			return con.hasStatement(id, ApplicationConstants.RDF_TYPE_URI,
					ApplicationConstants.MAPPING_ONE_TO_ONE_URI,
					ApplicationConstants.MAPPING_CONTEXT_URI);
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
	 * This method does the actual delete action for removing mappings from the
	 * triplestore. It removes all triples with a subject matching the mapping
	 * id.
	 * 
	 * @param con
	 * @param id
	 * @throws RepositoryException
	 */
	protected void deleteFromTripleStore(RepositoryConnection con, URI id)
			throws RepositoryException {
		RepositoryResult<Statement> results = con.getStatements(id, null, null,
				false, ApplicationConstants.MAPPING_CONTEXT_URI);
		// Remove all those triples
		con.remove(results, ApplicationConstants.MAPPING_CONTEXT_URI);
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
	protected OneToOneMapping updateMappingEntity(OneToOneMapping mapping,
			URI source, URI target, URI relation, Integer sourceOntologyId,
			Integer targetOntologyId, Integer sourceOntologyVersion,
			Integer targetOntologyVersion, Integer submittedBy, URI dependency,
			String comment, String mappingSource, String mappingSourceName,
			String mappingSourcecontactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType) {
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
			mapping.setSubmittedBy(submittedBy);
		if (dependency != null)
			mapping.setDependency(dependency);
		if (comment != null)
			mapping.setComment(comment);
		if (mappingSource != null)
			mapping.setMappingSource(mappingSource);
		if (mappingSourceName != null)
			mapping.setMappingSourceName(mappingSourceName);
		if (mappingSourcecontactInfo != null)
			mapping.setMappingSourcecontactInfo(mappingSourcecontactInfo);
		if (mappingSourceSite != null)
			mapping.setMappingSourceSite(mappingSourceSite);
		if (mappingSourceAlgorithm != null)
			mapping.setMappingSourceAlgorithm(mappingSourceAlgorithm);
		if (mappingType != null)
			mapping.setMappingType(mappingType);

		return mapping;
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
