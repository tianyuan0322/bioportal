package org.ncbo.stanford.service.concept.impl;

import java.util.ArrayList;
import java.util.Iterator;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.service.concept.DumpRDFService;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * This service generates an RDF dump (N3 format) for a given ontology.
 * 
 * @author <a href="mailto:tony@loeser.name>Tony Loeser</a>
 */
public class DumpRDFServiceImpl extends ConceptServiceImpl implements DumpRDFService {
	
	public static final String XREF_RELATION_NAME = "xref";

	public String generateRDFDump(Integer ontologyVersionID) throws Exception {
		OntologyBean ontoBean = 
			ontologyMetadataManager.findOntologyOrViewVersionById(ontologyVersionID);
		OntologyRetrievalManager orm = getRetrievalManager(ontoBean);
		// Set up the output content and write the header
		StringBuilder output = new StringBuilder();
		String prefix = ontoBean.getAbbreviation();
		addNamespace(output, prefix);
		// Get the list of classes from OBS
		// For each class, get the local ClassBean content and add it to the output
		for (Iterator<ClassBean> classIter = orm.listAllClasses(ontoBean); classIter.hasNext(); ) {
			addClassToOutput(classIter.next(), prefix, output);
		}
		return output.toString();
	}
	
	// Pick the name, synonyms, subclasses, etc. off of the ClassBean and 
	// add them to the RDF file content.
	@SuppressWarnings("unchecked")
	private void addClassToOutput(ClassBean classBean, String idPrefix, StringBuilder output) {
		
		// Id
		// Note that the ClassBean is inconsistent.  Sometimes getId returns a string with
		// the ontology abbreviation prepended; sometimes it does not.
		String id = classBean.getId();
		if (!id.startsWith(idPrefix)) {
			id = idPrefix+":"+id;
		}
		String n3Id = "<http://bio2rdf.org/"+id+">";
		
		// Name
		String name = classBean.getLabel();
		output.append(n3Id+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/obo#term> .\n");
		output.append(n3Id+" <http://bio2rdf.org/ns/obo_resource:name> \""+name+"\" .\n");
		output.append(n3Id+" rdfs:label \""+name+" ["+id+"]\" .\n");

		// Definition
		ArrayList<String> defns = (ArrayList<String>)classBean.getDefinitions();
		if (defns != null) {
			for (String defn: defns) {
				output.append(n3Id+" <http://bio2rdf.org/ns/obo#definition> \""+defn+"\" .\n");
			}
		}
		
		// Synonyms
		ArrayList<String> synonyms = (ArrayList<String>)classBean.getSynonyms();
		if (synonyms != null) {
			for (String synonym: synonyms) {
				if (!synonym.equals(name)) {
					output.append(n3Id+" <http://bio2rdf.org/ns/obo#synonym> \""+synonym+"\" .\n");
				}
			}
		}
		
		// Cross references
		ArrayList<String> xrefs = (ArrayList<String>)classBean.getRelation((Object)XREF_RELATION_NAME);
		if (xrefs != null) {
			for (String xref: xrefs) {
				output.append(n3Id+" <http://bio2rdf.org/ns/obo#xref> \""+xref+"\" .\n");				
			}
		}
		
		// Subclasses
		ArrayList<ClassBean> subClasses = (ArrayList<ClassBean>)classBean.getRelation((Object) ApplicationConstants.SUB_CLASS);
		if (subClasses != null) {
			for (ClassBean subClass: subClasses) {
				String subclassId = subClass.getId();
				output.append("<http://bio2rdf.org/"+subclassId+"> <http://bio2rdf.org/ns/obo#is_a> "+n3Id+" .\n");
			}
		}
	}
	
	private void addNamespace(StringBuilder output, String nsId) {
		output.append("<http://bio2rdf.org/ns/"+nsId+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/bio2rdf#Namespace> .\n");
		output.append("<http://bio2rdf.org/ns/"+nsId+"> <http://purl.org/dc/elements/1.1/identifier> \""+nsId+"\" .\n");
		output.append("<http://bio2rdf.org/ns/"+nsId+"> <http://www.w3.org/2000/01/rdf-schema#label> \"Bio2RDF namespace "+nsId+"\" .\n");
		output.append("<http://bio2rdf.org/ns/"+nsId+"> <http://bio2rdf.org/bio2rdf#namespace> \""+nsId+"\" .\n");
	}
}