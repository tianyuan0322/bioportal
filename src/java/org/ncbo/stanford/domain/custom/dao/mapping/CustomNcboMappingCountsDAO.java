package org.ncbo.stanford.domain.custom.dao.mapping;

import org.ncbo.stanford.bean.mapping.MappingParametersBean;
import org.ncbo.stanford.exception.InvalidInputException;

public class CustomNcboMappingCountsDAO extends AbstractNcboMappingDAO {

	public Integer getCountMappingsForParameters(
			MappingParametersBean parameters) throws InvalidInputException {
		return getCount(null, parameters);
	}

	public Integer getCountMappingsFromOntology(Integer ontologyId,
			MappingParametersBean parameters) throws InvalidInputException {
		String filter = generateOntologySparqlFilter(ontologyId, null, true);

		return getCount(filter, parameters);
	}

	public Integer getCountMappingsToOntology(Integer ontologyId,
			MappingParametersBean parameters) throws InvalidInputException {
		String filter = generateOntologySparqlFilter(null, ontologyId, true);

		return getCount(filter, parameters);
	}

	public Integer getCountMappingsBetweenOntologies(Integer sourceOntology,
			Integer targetOntology, Boolean unidirectional,
			MappingParametersBean parameters) throws InvalidInputException {
		String filter = generateOntologySparqlFilter(sourceOntology,
				targetOntology, unidirectional);

		return getCount(filter, parameters);
	}

	public Integer getCountMappingsForOntology(Integer ontologyId,
			MappingParametersBean parameters) throws InvalidInputException {
		String filter = generateOntologySparqlFilter(ontologyId, null, false);

		return getCount(filter, parameters);
	}

	public Integer getCountMappingsForConcept(Integer ontologyId,
			String conceptId, MappingParametersBean parameters)
			throws InvalidInputException {
		String filter = generateConceptSparqlFilter(conceptId, null, false);
		filter += " && "
				+ generateOntologySparqlFilter(ontologyId, null, false);

		return getCount(filter, parameters);
	}

	public Integer getCountMappingsFromConcept(Integer ontologyId,
			String conceptId, MappingParametersBean parameters)
			throws InvalidInputException {
		String filter = generateConceptSparqlFilter(conceptId, null, true);
		filter += " && " + generateOntologySparqlFilter(ontologyId, null, true);

		return getCount(filter, parameters);
	}

	public Integer getCountMappingsToConcept(Integer ontologyId,
			String conceptId, MappingParametersBean parameters)
			throws InvalidInputException {
		String filter = generateConceptSparqlFilter(conceptId, null, true);
		filter += " && " + generateOntologySparqlFilter(ontologyId, null, true);

		return getCount(filter, parameters);
	}

	public Integer getCountMappingsBetweenConcepts(Integer sourceOntologyId,
			Integer targetOntologyId, String fromConceptId, String toConceptId,
			Boolean unidirectional, MappingParametersBean parameters)
			throws InvalidInputException {
		String filter = generateConceptSparqlFilter(fromConceptId, toConceptId,
				unidirectional);
		filter += " && "
				+ generateOntologySparqlFilter(sourceOntologyId,
						targetOntologyId, unidirectional);

		return getCount(filter, parameters);
	}

}
