package org.ncbo.stanford.domain.custom.dao.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.bean.mapping.MappingParametersBean;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

public class CustomNcboMappingDAO extends AbstractNcboMappingDAO {

	public OneToOneMapping createMapping(URI source, URI target, URI relation,
			Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, URI dependency, String comment,
			String mappingSource, String mappingSourceName,
			String mappingSourceContactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType)
			throws MappingExistsException {

		OneToOneMapping newMapping = new OneToOneMapping();

		// Set Mapping properties
		newMapping.setSource(source);
		newMapping.setTarget(target);
		newMapping.setRelation(relation);
		newMapping.setSourceOntologyId(sourceOntologyId);
		newMapping.setTargetOntologyId(targetOntologyId);
		newMapping.setCreatedInSourceOntologyVersion(sourceOntologyVersion);
		newMapping.setCreatedInTargetOntologyVersion(targetOntologyVersion);

		// Set metadata properties
		newMapping.setDependency(dependency);
		newMapping.setSubmittedBy(submittedBy);
		newMapping.setDate(new Date());
		newMapping.setComment(comment);
		newMapping.setMappingType(mappingType);

		// Set mappingSource properties
		newMapping.setMappingSource(mappingSource);
		newMapping.setMappingSourceName(mappingSourceName);
		newMapping.setMappingSourcecontactInfo(mappingSourceContactInfo);
		newMapping.setMappingSourceSite(mappingSourceSite);
		newMapping.setMappingSourceAlgorithm(mappingSourceAlgorithm);

		createMapping(newMapping);

		OneToOneMapping mapping = null;
		try {
			mapping = getMapping(newMapping.getId());
		} catch (MappingMissingException e) {
			e.printStackTrace();
		}

		return mapping;
	}

	public OneToOneMapping createMapping(OneToOneMapping newMapping)
			throws MappingExistsException {
		RepositoryConnection con = getRdfStoreManager().getRepositoryConnection();
		ValueFactory vf = getRdfStoreManager().getValueFactory();

		ArrayList<Statement> statements = newMapping.toStatements(vf);

		for (Statement statement : statements) {
			try {
				con.add(statement, ApplicationConstants.MAPPING_CONTEXT_URI);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		OneToOneMapping mapping = null;
		try {
			mapping = getMapping(newMapping.getId());
		} catch (MappingMissingException e) {
			e.printStackTrace();
		}

		return mapping;
	}

	public OneToOneMapping getMapping(URI id) throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager().getRepositoryConnection();

		// Attempt mapping retrieval, return null if failure
		OneToOneMapping mapping = null;
		try {
			if (hasMapping(id, con)) {
				ArrayList<OneToOneMapping> mappings = getMappings(1, 0,
						"?mappingId = <" + id + ">", null);
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
		}

		return mapping;
	}

	public OneToOneMapping updateMapping(URI id, URI source, URI target,
			URI relation, Integer sourceOntologyId, Integer targetOntologyId,
			Integer sourceOntologyVersion, Integer targetOntologyVersion,
			Integer submittedBy, URI dependency, String comment,
			String mappingSource, String mappingSourceName,
			String mappingSourcecontactInfo, URI mappingSourceSite,
			String mappingSourceAlgorithm, String mappingType)
			throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager().getRepositoryConnection();

		if (!hasMapping(id, con)) {
			throw new MappingMissingException();
		}

		OneToOneMapping mapping = getMapping(id);

		OneToOneMapping updatedMapping = updateMappingEntity(mapping,
				source, target, relation, sourceOntologyId,
				targetOntologyId, sourceOntologyVersion,
				targetOntologyVersion, submittedBy, dependency, comment,
				mappingSource, mappingSourceName, mappingSourcecontactInfo,
				mappingSourceSite, mappingSourceAlgorithm, mappingType);

		updatedMapping = updateMapping(id, updatedMapping);
		
		return updatedMapping;
	}

	public OneToOneMapping updateMapping(URI id, OneToOneMapping mapping)
			throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager().getRepositoryConnection();

		if (!hasMapping(id, con)) {
			throw new MappingMissingException();
		}

		deleteMappingForUpdate(id);

		try {
			OneToOneMapping updatedMapping = createMapping(mapping);

			return updatedMapping;
		} catch (MappingExistsException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void deleteMapping(URI id) throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager().getRepositoryConnection();
		try {
			if (!hasMapping(id, con)) {
				throw new MappingMissingException();
			}
			System.out.println("Deleting mapping: " + id.toString());

			OneToOneMapping mapping = getMapping(id);

			if (mapping.getDependency() != null) {
				deleteFromTripleStore(con, mapping.getDependency());
			}

			try {
				// Check for dependencies that might not be specified. We do
				// this because the 6mm mappings that we got from the UI
				// database don't contain dependency information and inverse
				// mappings should be removed when their counterpart is removed.
				if (mapping.getDependency() == null) {
					String filter = "?source = <" + mapping.getTarget() + ">";
					filter += " && ?target = <" + mapping.getSource() + ">";
					filter += " && ?sourceOntologyId = "
							+ mapping.getTargetOntologyId();
					filter += " && ?targetOntologyId = "
							+ mapping.getSourceOntologyId();
					filter += " && ?mappingSource = \""
							+ mapping.getMappingSource() + "\"";
					filter += " && ?submittedBy = " + mapping.getSubmittedBy();
					filter += " && ?relation = <" + mapping.getRelation() + ">";

					ArrayList<OneToOneMapping> inferredDependents = getMappings(
							null, null, filter, null);

					if (inferredDependents != null) {
						for (OneToOneMapping dependent : inferredDependents) {
							deleteFromTripleStore(con, dependent.getId());
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
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteMappingForUpdate(URI id) throws MappingMissingException {
		RepositoryConnection con = getRdfStoreManager().getRepositoryConnection();
		if (!hasMapping(id, con)) {
			throw new MappingMissingException();
		}
		System.out.println("Deleting mapping: " + id.toString());
		
		try {
			deleteFromTripleStore(con, id);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	/*******************************************************************
	 * 
	 * Mappings for parameters
	 * 
	 *******************************************************************/

	public List<OneToOneMapping> getMappingsForParameters(Integer limit,
			Integer offset, MappingParametersBean parameters)
			throws InvalidInputException {
		return getMappings(limit, offset, null, parameters);
	}

	/*******************************************************************
	 * 
	 * Mappings for ontologies
	 * 
	 *******************************************************************/

	public List<OneToOneMapping> getMappingsFromOntology(Integer ontologyId,
			Integer limit, Integer offset, MappingParametersBean parameters)
			throws InvalidInputException {
		String filter = generateOntologySparqlFilter(ontologyId, null, true);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<OneToOneMapping> getMappingsToOntology(Integer ontologyId,
			Integer limit, Integer offset, MappingParametersBean parameters)
			throws InvalidInputException {
		String filter = generateOntologySparqlFilter(null, ontologyId, true);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<OneToOneMapping> getMappingsBetweenOntologies(
			Integer sourceOntology, Integer targetOntology,
			Boolean unidirectional, Integer limit, Integer offset,
			MappingParametersBean parameters) throws InvalidInputException {
		String filter = generateOntologySparqlFilter(sourceOntology,
				targetOntology, unidirectional);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<OneToOneMapping> getMappingsForOntology(Integer ontologyId,
			Integer limit, Integer offset, MappingParametersBean parameters)
			throws InvalidInputException {
		String filter = generateOntologySparqlFilter(ontologyId, null, false);

		return getMappings(limit, offset, filter, parameters);
	}

	/*******************************************************************
	 * 
	 * Mappings for concepts
	 * 
	 *******************************************************************/

	public List<OneToOneMapping> getMappingsForConcept(Integer ontologyId,
			String conceptId, Integer limit, Integer offset,
			MappingParametersBean parameters) throws InvalidInputException {
		String filter = generateConceptSparqlFilter(conceptId, null, false);
		filter += " && "
				+ generateOntologySparqlFilter(ontologyId, null, false);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<OneToOneMapping> getMappingsFromConcept(Integer ontologyId,
			String conceptId, Integer limit, Integer offset,
			MappingParametersBean parameters) throws InvalidInputException {
		String filter = generateConceptSparqlFilter(conceptId, null, true);
		filter += " && " + generateOntologySparqlFilter(ontologyId, null, true);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<OneToOneMapping> getMappingsToConcept(Integer ontologyId,
			String conceptId, Integer limit, Integer offset,
			MappingParametersBean parameters) throws InvalidInputException {
		String filter = generateConceptSparqlFilter(conceptId, null, true);
		filter += " && " + generateOntologySparqlFilter(ontologyId, null, true);

		return getMappings(limit, offset, filter, parameters);
	}

	public List<OneToOneMapping> getMappingsBetweenConcepts(
			Integer sourceOntologyId, Integer targetOntologyId,
			String fromConceptId, String toConceptId, Boolean unidirectional,
			Integer limit, Integer offset, MappingParametersBean parameters)
			throws InvalidInputException {
		String filter = generateConceptSparqlFilter(fromConceptId, toConceptId,
				unidirectional);
		filter += " && "
				+ generateOntologySparqlFilter(sourceOntologyId,
						targetOntologyId, unidirectional);

		return getMappings(limit, offset, filter, parameters);
	}

}
