package org.ncbo.stanford.domain.custom.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.manager.rdfstore.RDFStoreManager;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
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

	public OneToOneMapping createMapping(URI source, URI target, URI relation,
			Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, String comment, String mappingSource,
			String mappingSourceName, String mappingSourcecontactInfo,
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
		mapping.setMappingSourcecontactInfo(mappingSourcecontactInfo);
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

	public ArrayList<OneToOneMapping> getMappingsForOntology(
			Integer ontologyId, Integer limit, Integer offset) {
		// Safety check
		if (limit == null || limit >= 50000) {
			limit = 50000;
		}

		if (offset == null) {
			offset = 0;
		}

		ObjectConnection con = getRdfStoreManager().getObjectConnection();
		ValueFactory vf = getRdfStoreManager().getValueFactory();

		try {
			String queryString = "";

			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString, ApplicationConstants.MAPPING_CONTEXT);
			
			Long start = System.currentTimeMillis();
			TupleQueryResult result = query.evaluate();
			System.out.println("Evaluate query time: " + (System.currentTimeMillis() - start));

			ArrayList<OneToOneMapping> mappings = new ArrayList<OneToOneMapping>();
			start = System.currentTimeMillis();
			while (result.hasNext()) {
				BindingSet bs = result.next();
				URI mappingId = (URI) bs.getValue("mappingId");
				URI object = (URI) bs.getValue("o");
				URI predicate = (URI) bs.getValue("p");
				
				OneToOneMapping mapping = new OneToOneMapping();

				// Set Mapping properties
//				mapping.setSource((URI) bs.getValue("source"));
//				mapping.setTarget((URI) bs.getValue("target"));
//				mapping.setRelation((URI) bs.getValue("relation"));
//				mapping.setSourceOntologyId(bs.getValue("sourceOntologyId");
//				mapping.setTargetOntologyId(bs.getValue("targetOntologyId"));
//				mapping.setCreatedInSourceOntologyVersion(bs.getValue("createdInSourceOntologyVersion"));
//				mapping.setCreatedInTargetOntologyVersion(bs.getValue("createdInTargetOntologyVersion"));
//
//				// Set metadata properties
//				mapping.setSubmittedBy(bs.getValue("submittedBy"));
//				mapping.setDependency(bs.getValue("mappingId"));
//				mapping.setDate(bs.getValue("date"));
//				mapping.setComment(bs.getValue("comment"));
//				mapping.setMappingType(bs.getValue("mappingType"));
//
//				// Set mappingSource properties
//				mapping.setMappingSource(bs.getValue("mappingSource"));
//				mapping.setMappingSourceName(bs.getValue("mappingSourceName"));
//				mapping.setMappingSourcecontactInfo(bs.getValue("mappingSourcecontactInfo"));
//				mapping.setMappingSourceSite(bs.getValue("mappingSourceSite"));
//				mapping.setMappingSourceAlgorithm(bs.getValue("mappingSourceAlgorithm"));

				mappings.add(mapping);
			}
			System.out.println("Create mapping id list time: " + (System.currentTimeMillis() - start));
			
			// Get all mapping triples for the above query
			

//			start = System.currentTimeMillis();
//			while (result.hasNext()) {
//				BindingSet bs = result.next();
//				URI mappingId = (URI) bs.getValue("mapping");
//				if (con.hasStatement(mappingId,
//						ApplicationConstants.RDF_TYPE_URI,
//						ApplicationConstants.MAPPING_ONE_TO_ONE_URI,
//						ApplicationConstants.MAPPING_CONTEXT_URI)) {
//					mappings.add(con.getObject(OneToOneMapping.class, mappingId));
//				}
//			}
//			System.out.println("Get objects time: " + (System.currentTimeMillis() - start));

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

	/**
	 * @param rdfStoreManagerMap
	 *            the rdfStoreManagerMap to set
	 */
	public void setRdfStoreManagerMap(
			Map<String, RDFStoreManager> rdfStoreManagerMap) {
		this.rdfStoreManagerMap = rdfStoreManagerMap;
	}

}
