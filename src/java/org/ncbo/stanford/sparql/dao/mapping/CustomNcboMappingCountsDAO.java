package org.ncbo.stanford.sparql.dao.mapping;

import java.util.Set;

import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.impl.URIImpl;
import org.ncbo.stanford.util.sparql.SPARQLUnionGenerator;
import org.openrdf.model.impl.LiteralImpl;
import org.ncbo.stanford.sparql.bean.Mapping;

public class CustomNcboMappingCountsDAO extends AbstractNcboMappingDAO {

	public Integer getCountMappingsForParameters(
			SPARQLFilterGenerator parameters) throws InvalidInputException {
		return getCount(null, parameters);
	}

	public Integer getCountMappingsFromOntology(Integer ontologyId,
			SPARQLFilterGenerator parameters) throws InvalidInputException {
        if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
            String filter = generateOntologySparqlFilter(ontologyId, null, true);
            return getCount(filter, parameters);
        } else {
            SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
            generator.setCount(true);
            generator.setParameters(parameters);
            generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
                    new LiteralImpl(Integer.toString(ontologyId), ApplicationConstants.XSD_INTEGER));

            return getMappingCountFromUnion(generator);
        }
	}

	public Integer getCountMappingsToOntology(Integer ontologyId,
			SPARQLFilterGenerator parameters) throws InvalidInputException {
        if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
            String filter = generateOntologySparqlFilter(null, ontologyId, true);
            return getCount(filter, parameters);
        }
        else {
            SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
            generator.setCount(true);
            generator.setParameters(parameters);
            generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY_ID,
                    new LiteralImpl(Integer.toString(ontologyId), ApplicationConstants.XSD_INTEGER));

            return getMappingCountFromUnion(generator);
        }
	}

	public Integer getCountMappingsBetweenOntologies(Integer sourceOntology,
			Integer targetOntology, Boolean unidirectional,
			SPARQLFilterGenerator parameters) throws InvalidInputException {
        if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
            String filter = generateOntologySparqlFilter(sourceOntology,
				targetOntology, unidirectional);
            return getCount(filter, parameters);
        } else {
            SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
            generator.setCount(true);
            generator.setParameters(parameters);
            generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
                    new LiteralImpl(Integer.toString(sourceOntology), ApplicationConstants.XSD_INTEGER));
            generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY_ID,
                    new LiteralImpl(Integer.toString(targetOntology), ApplicationConstants.XSD_INTEGER));

            if (!unidirectional) {
                generator.addBidirectional(ApplicationConstants.SOURCE_ONTOLOGY_ID,
                           ApplicationConstants.TARGET_ONTOLOGY_ID);
            }
            return getMappingCountFromUnion(generator);
        }
	}

	public Integer getCountMappingsForOntology(Integer ontologyId,
			SPARQLFilterGenerator parameters) throws InvalidInputException {
        if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
            String filter = generateOntologySparqlFilter(ontologyId, null, false);
            return getCount(filter, parameters);
        } else {
            SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
            generator.setCount(true);
            generator.setParameters(parameters);
            generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
                    new LiteralImpl(Integer.toString(ontologyId), ApplicationConstants.XSD_INTEGER));

            generator.addBidirectional(ApplicationConstants.SOURCE_ONTOLOGY_ID,
                           ApplicationConstants.TARGET_ONTOLOGY_ID);
            return getMappingCountFromUnion(generator);
        }
	}

	public Integer getCountMappingsForConcept(Integer ontologyId,
			String conceptId, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
        if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
            String filter = "(" + generateConceptSparqlFilter(conceptId, null, false) + ")";
            filter += " && ("
                    + generateOntologySparqlFilter(ontologyId, null, false) + ")";
            filter += " " + parameters.toFilter();

            Set<String> mappingIds = getMappingIdsFromFilter(null, null, filter);

            return mappingIds.size();
        } else {
            SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
            generator.setCount(true);
            generator.setParameters(parameters);
            generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
                    new LiteralImpl(Integer.toString(ontologyId), ApplicationConstants.XSD_INTEGER));
            generator.addBindValue(ApplicationConstants.SOURCE_TERM,
                    new URIImpl(conceptId));

            generator.addBidirectional(ApplicationConstants.SOURCE_TERM,
                           ApplicationConstants.TARGET_TERM);
            generator.addBidirectional(ApplicationConstants.SOURCE_ONTOLOGY_ID,
                           ApplicationConstants.TARGET_ONTOLOGY_ID);

            return getMappingCountFromUnion(generator);
        }
	}

	public Integer getCountMappingsFromConcept(Integer ontologyId,
			String conceptId, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
        
        if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
            String filter = generateConceptSparqlFilter(conceptId, null, true);
            filter += " && " + generateOntologySparqlFilter(ontologyId, null, true);
            filter += " " + parameters.toFilter();

            Set<String> mappingIds = getMappingIdsFromFilter(null, null, filter);

            return mappingIds.size();
        } else {
            SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
            generator.setCount(true);
            generator.setParameters(parameters);
            generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
                    new LiteralImpl(Integer.toString(ontologyId), ApplicationConstants.XSD_INTEGER));
            generator.addBindValue(ApplicationConstants.SOURCE_TERM,
                    new URIImpl(conceptId));
            return getMappingCountFromUnion(generator);
        }
	}

	public Integer getCountMappingsToConcept(Integer ontologyId,
			String conceptId, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
        
        if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
            String filter = generateConceptSparqlFilter(conceptId, null, true);
            filter += " && " + generateOntologySparqlFilter(ontologyId, null, true);
            filter += " " + parameters.toFilter();

            Set<String> mappingIds = getMappingIdsFromFilter(null, null, filter);
            return mappingIds.size();
        } else {
            SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
            generator.setCount(true);
            generator.setParameters(parameters);
            generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY_ID,
                    new LiteralImpl(Integer.toString(ontologyId), ApplicationConstants.XSD_INTEGER));
            generator.addBindValue(ApplicationConstants.TARGET_TERM,
                    new URIImpl(conceptId));
            return getMappingCountFromUnion(generator);
        }
	}

	public Integer getCountMappingsBetweenConcepts(Integer sourceOntologyId,
			Integer targetOntologyId, String fromConceptId, String toConceptId,
			Boolean unidirectional, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
        Set<String> mappingIds = null;
        if (!ApplicationConstants.GENERATE_UNION_SPARQL) {
            String filter = generateConceptSparqlFilter(fromConceptId, toConceptId,
                    unidirectional);
            filter += " && "
                    + generateOntologySparqlFilter(sourceOntologyId,
                            targetOntologyId, unidirectional);
            filter += " " + parameters.toFilter();

            mappingIds = getMappingIdsFromFilter(null, null, filter);
            return mappingIds.size();
        } 
        else {
            SPARQLUnionGenerator generator = new SPARQLUnionGenerator();
            generator.setCount(true);
            generator.setParameters(parameters);
            generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
                    new LiteralImpl(Integer.toString(sourceOntologyId), ApplicationConstants.XSD_INTEGER));
            generator.addBindValue(ApplicationConstants.TARGET_ONTOLOGY_ID,
                    new LiteralImpl(Integer.toString(targetOntologyId), ApplicationConstants.XSD_INTEGER));
            generator.addBindValue(ApplicationConstants.SOURCE_TERM,
                    new URIImpl(fromConceptId));
            generator.addBindValue(ApplicationConstants.TARGET_TERM,
                    new URIImpl(toConceptId));
            if (!unidirectional) {
                generator.addBidirectional(ApplicationConstants.SOURCE_TERM,
                               ApplicationConstants.TARGET_TERM);
                generator.addBidirectional(ApplicationConstants.SOURCE_ONTOLOGY_ID,
                               ApplicationConstants.TARGET_ONTOLOGY_ID);
            }
            return getMappingCountFromUnion(generator);
        }
	}

}
