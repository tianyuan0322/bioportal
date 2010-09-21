package org.ncbo.stanford.domain.custom.dao;

import java.util.Date;
import java.util.Map;

import org.castor.mapping.MappingSource;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.ncbo.stanford.manager.rdfstore.RDFStoreManager;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;

public class CustomNcboMappingDAO {

	private Map<String, RDFStoreManager> rdfStoreManagerMap;

	public OneToOneMapping createMapping(URI source, URI target, URI relation,
			Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, String comment, MappingSource mappingSource,
			String mappingType) {

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

		createMapping(mapping);
		
		return mapping;
	}
	
	public OneToOneMapping createMapping(OneToOneMapping newMapping) {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();
		
		OneToOneMapping mapping = new OneToOneMapping();
		try {
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


	public OneToOneMapping getMapping(URI id) {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		// Attempt mapping retrieval, return null if failure
		OneToOneMapping mapping = null;
		try {
			if (con.hasStatement(id, ApplicationConstants.RDF_TYPE_URI,
					ApplicationConstants.MAPPING_ONE_TO_ONE_URI,
					ApplicationConstants.MAPPING_CONTEXT_URI)) {
				mapping = con.getObject(OneToOneMapping.class, id);
			} else {
				// TODO: Throw an appropriate exception here.
				return null;
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		return mapping;
	}

	public OneToOneMapping updateMapping(URI id, URI source, URI target,
			URI relation, Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, String comment, MappingSource mappingSource,
			String mappingType) {
		ObjectConnection con = getRdfStoreManager().getObjectConnection();

		try {
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
	
	public OneToOneMapping updateMapping(URI id, OneToOneMapping mapping) {
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

	public void deleteMapping(URI id) {
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
