package org.ncbo.stanford.service.concept.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.service.concept.RdfService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;


public class RdfServiceImpl extends ConceptServiceImpl implements RdfService {

	private static final Log log = LogFactory.getLog(ConceptServiceImpl.class);
	
	public static final String BIOPORTAL_PURL_BASE = "http://purl.bioontology.org/ontology/";
	public static final String OBO_PURL_BASE = "http://purl.obolibrary.org/";

	public static final String SKOS_NS = "skos";
	public static final String SKOS_URI = "http://www.w3.org/2004/02/skos/core#";

	public static final String RDF_NS = "rdf";
	public static final String RDF_URI = "http://www.w3.org/2004/02/skos/core#";
	
	public static final String RDFS_NS = "rdfs";
	public static final String RDFS_URI = "http://www.w3.org/2000/01/rdf-schema#";
	
	public static final String OWL_NS = "owl";
	public static final String OWL_URI = "http://www.w3.org/2002/07/owl#";
	
	public static final String XSD_NS = "xsd";
	public static final String XSD_URI = "http://www.w3.org/2001/XMLSchema#";
	
	public static final String XSP_NS = "xsp";
	public static final String XSP_URI = "http://www.owl-ontologies.com/2005/08/07/xsp.owl#";
	
	public static final String UMLS_SKOS_NS = "umls-skos";
	public static final String UMLS_SKOS_URI = "http://purl.bioontology.org/ontology/umls-skos/";
	
	public static final String OBO_REL_NS = "obo-rel";
	public static final String OBO_REL_URI = OBO_PURL_BASE + "obo/";
	
	public static final String OWL_FORMAT = "OWL";
	public static final String OBO_FORMAT = "OBO";
	public static final String RRF_FORMAT = "RRF";

	public static final String UMLS_CUI = "UMLS_CUI";
	public static final String UMLS_TUI = "TUI";
	public static final String UMLS_CODE = "CODE";
	public static final String UMLS_SEMANTIC_TYPE = "semanticType";
	public static final String OBO_ISA = "OBO_REL_0000001";
	public static final String ALT_DEFINITION = "DEFINITION";
	public static final String ALT_SYNONYM = "SYN";

	public static final String RDF_TYPE_RESOURCE = "RDF Resource";
	public static final String RDF_TYPE_LITERAL = "RDF Literal";
	
	public static final String rdfsLabel = RDFS_URI + "label";
	public static final String rdfsSubClassOf = RDFS_URI + "subClassOf";
	public static final String rdfsSubPropertyOf = RDFS_URI + "subPropertyOf";
	public static final String skosBroader = SKOS_URI + "broader";
	public static final String skosPrefLabel = SKOS_URI + "prefLabel";
	public static final String skosAltLabel = SKOS_URI + "altLabel";
	public static final String skosDefinition = SKOS_URI + "definition";
	public static final String skosNotation = SKOS_URI + "notation"; // for storing internal IDs
	public static final String oboIsa = OBO_REL_URI + OBO_ISA;
	
	public void generateRdf(OWLOntologyManager manager, String dir, OntologyService ontologyService) throws Exception {
		List<OntologyBean> ontologies = ontologyService.findLatestActiveOntologyVersions();
		
		System.out.println("********** PAEA ***" + ontologies.size() + " ontologies are in the processing queue.");
		
		int i=0;
		for (OntologyBean ont : ontologies) {
			Integer ontologyVersionId = ont.getId();
			i++;
			
			System.out.println("********** PAEA ***" + " currently processing " + i + " (of " + ontologies.size() + ").");

			if (ontologyVersionId == null)
				throw new Exception("ontology version id cannot be null");
			
			try {
				System.out.println("********** PAEA ***" + ont.getId() + " is currently being processed.");
				File file = new File(dir + File.separator + getFileName(ont));
				
				if (StringHelper.isNullOrNullString(ont.getAbbreviation())) {
					System.out.println("********** PAEA ***" + " warning: "+ ont.getId() + " has no abbreviation.");
				}
				
				if (file.exists() 
						/*
						|| 
						(!StringHelper.isNullOrNullString(ont.getAbbreviation()))
						&& (
								ont.getAbbreviation().equals("GO") // don't re-process
								|| ont.getAbbreviation().equals("SO") // don't re-process
								|| ont.getAbbreviation().equals("CCO") // don't re-process
								|| ont.getAbbreviation().equals("SNOMED") // don't re-process
								|| ont.getAbbreviation().equals("TO") // don't re-process
								|| ont.getAbbreviation().equals("WBbt") // don't re-process
								|| ont.getAbbreviation().equals("MIRO") // don't re-process
						)
						*/
						
				) {
					System.out.println("********** PAEA ***" + ont.getAbbreviation() + " is being skipped because it was already saved.");
				} else if (ont.isMetadataOnly()) {
					System.out.println("********** PAEA ***" + ont.getAbbreviation() + " is being skipped because it is flagged as isMataDataOnly=true.");
				} else {
					// generate the RDF file!
					generateRdf(manager, dir, ont);
				}
				
				System.out.println("********** PAEA ***" + ont.getAbbreviation() + " has been successfully processed.");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("********** PAEA ***" + ont.getId() + " could not be successfully processed.");
			}
		}
	}
	
	public OWLOntology generateRdf(OWLOntologyManager manager, String dir, OntologyBean ont) throws Exception {
		// Pass an empty list of concepts. Meaning, get ALL of them.
		return generateRdf(manager, dir, ont, (List<String>)null);
	}
	
	public OWLOntology generateRdf(OWLOntologyManager manager, String dir, OntologyBean ont, String conceptId) throws Exception {
		// Pass a singleton concept list.
		ArrayList<String> conceptIds = new ArrayList<String>(1);
		conceptIds.add(conceptId);
		return generateRdf(manager, dir, ont, conceptIds);
	}

	/**
	 * Given a list (singleton, many, or NULL) of concepts, generate the RDF for a given ontology version.
	 * 
	 * @param manager
	 * @param dir
	 * @param ontologyVersionId
	 * @param conceptIds
	 * @return
	 * @throws Exception
	 */
	public OWLOntology generateRdf(OWLOntologyManager manager, String dir, OntologyBean ont, List<String> conceptIds)
			throws Exception {
		
		long startTime = System.currentTimeMillis();

		OWLOntology ontology = manager.createOntology(IRI.create(getOntologyUri(ont,conceptIds)));
		File file = new File(dir + File.separator + getFileName(ont));
		
		useOwlApi(manager, ontology, ont, conceptIds);
		
		long owlTime = System.currentTimeMillis();
		long owlElapsed = owlTime - startTime;
		System.out.println("********** PAEA ***" + "time to create owl model: " + owlElapsed);
		
		// save ontology into file in RDF/XML format
		if (file != null
				&& (conceptIds == null || 
						// TODO: if a list of IDs is passed, we should not necessarily save it.
						conceptIds.size() > 1)) { 
			manager.saveOntology(ontology, new RDFXMLOntologyFormat(), IRI.create(file.toURI()));
		}
		
		long endTime = System.currentTimeMillis();
		long elapsed = endTime - startTime;
		System.out.println("********** PAEA ***" + "time to create rdf: " + elapsed);
		
		return ontology;
	}
	
	private void useOwlApi(OWLOntologyManager manager, OWLOntology ontology, 
			OntologyBean ont, List<String>conceptIds) throws Exception {
		
		Hashtable<String,String> namedSlots = new Hashtable<String, String>();
		
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		// add ontology annotations
		OWLLiteral ontologyInfoLiteral;
		OWLAnnotation ontologyInfoAnnotation;
		
		// version
		if (ont.getId() != null) {
			ontologyInfoLiteral = factory.getOWLStringLiteral(ont.getId().toString());
			ontologyInfoAnnotation = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(IRI.create(BIOPORTAL_PURL_BASE + "BPMetadata#/id")), 
					ontologyInfoLiteral);
			manager.applyChange(new AddOntologyAnnotation(ontology,ontologyInfoAnnotation));			
		}

		// label
		if (!StringHelper.isNullOrNullString(ont.getDisplayLabel())) {
			ontologyInfoLiteral = factory.getOWLStringLiteral(ont.getDisplayLabel());
			ontologyInfoAnnotation = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(IRI.create(BIOPORTAL_PURL_BASE + "OMV/name")), 
					ontologyInfoLiteral);
			manager.applyChange(new AddOntologyAnnotation(ontology,ontologyInfoAnnotation));
		}
		
		// OMV uri
		// assert ontology URI not null
		ontologyInfoLiteral = factory.getOWLStringLiteral(getOntologyUri(ont,conceptIds));
		ontologyInfoAnnotation = factory.getOWLAnnotation(
				factory.getOWLAnnotationProperty(IRI.create(BIOPORTAL_PURL_BASE + "OMV/URI")), 
				ontologyInfoLiteral);
		manager.applyChange(new AddOntologyAnnotation(ontology,ontologyInfoAnnotation));
		
		// description
		if (!StringHelper.isNullOrNullString(ont.getDescription())) {
			ontologyInfoLiteral = factory.getOWLStringLiteral(ont.getDescription());
			ontologyInfoAnnotation = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(IRI.create(BIOPORTAL_PURL_BASE + "OMV/description")), 
					ontologyInfoLiteral);
			manager.applyChange(new AddOntologyAnnotation(ontology,ontologyInfoAnnotation));
		}
		
		// abbreviation
		if (!StringHelper.isNullOrNullString(ont.getAbbreviation())) {
			ontologyInfoLiteral = factory.getOWLStringLiteral(ont.getAbbreviation());
			ontologyInfoAnnotation = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(IRI.create(BIOPORTAL_PURL_BASE + "OMV/acronym")), 
					ontologyInfoLiteral);
			manager.applyChange(new AddOntologyAnnotation(ontology,ontologyInfoAnnotation));
		}
		
		// format
		if (!StringHelper.isNullOrNullString(ont.getFormat())) {
			ontologyInfoLiteral = factory.getOWLStringLiteral(ont.getFormat());
			ontologyInfoAnnotation = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(IRI.create(BIOPORTAL_PURL_BASE + "OMV/hasOntologyLanguage")), 
					ontologyInfoLiteral);
			manager.applyChange(new AddOntologyAnnotation(ontology,ontologyInfoAnnotation));
		}
		
		// add essential annotation properties
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI())));
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SUBCLASS_OF.getIRI())));
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SUB_PROPERTY_OF.getIRI())));
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(IRI.create(skosBroader))));
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(IRI.create(skosPrefLabel))));
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(IRI.create(skosAltLabel))));
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(IRI.create(skosDefinition))));
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(IRI.create(skosNotation))));

		// Use BioPortal metadata slots.
		String synonymSlot = ont.getSynonymSlot();
		String preferredNameSlot = ont.getPreferredNameSlot();
		String definitionSlot = ont.getDocumentationSlot();
		
		// Assert annotation sub-properties.
		 
		// NOTE: skosBroader and oboIsA are not technically sub-properties of rdfsSubClassOf!  
		// So we are not adding axioms for those and we treat them as plain annotation properties.
		
		// skos:prefLabels are rdfs:labels
		manager.addAxiom(ontology, factory.getOWLSubAnnotationPropertyOfAxiom(
				factory.getOWLAnnotationProperty(IRI.create(skosPrefLabel)), 
				factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI())));		
		
		// The following special subProperties are recorded in ontology metadata.
		
		// synonyms are skos:altLabels
		if (!StringHelper.isNullOrNullString(synonymSlot)) {
			if (!synonymSlot.equals(skosAltLabel)
					&& synonymSlot.startsWith("http://")) {
				manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(IRI.create(synonymSlot))));
				manager.addAxiom(ontology, factory.getOWLSubAnnotationPropertyOfAxiom(
						factory.getOWLAnnotationProperty(IRI.create(synonymSlot)), 
						factory.getOWLAnnotationProperty(IRI.create(skosAltLabel))));
				namedSlots.put(skosAltLabel, synonymSlot);
			}
		}
		
		// preferred names are skos:prefLabels
		if (!StringHelper.isNullOrNullString(preferredNameSlot)) {
			if (!preferredNameSlot.equalsIgnoreCase("rdfs:label")) {
				if (!preferredNameSlot.equals(skosPrefLabel)
						&& preferredNameSlot.startsWith("http://")) {
					manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(IRI.create(preferredNameSlot))));
					manager.addAxiom(ontology, factory.getOWLSubAnnotationPropertyOfAxiom(
							factory.getOWLAnnotationProperty(IRI.create(preferredNameSlot)), 
							factory.getOWLAnnotationProperty(IRI.create(skosPrefLabel))));
					namedSlots.put(skosPrefLabel, preferredNameSlot);
				}
			}
		}
		
		// definitions are skos:definitions
		if (!StringHelper.isNullOrNullString(definitionSlot)) {
			if (!definitionSlot.equals(skosDefinition)
					&& definitionSlot.startsWith("http://")) {
				manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(IRI.create(definitionSlot))));
				manager.addAxiom(ontology, factory.getOWLSubAnnotationPropertyOfAxiom(
						factory.getOWLAnnotationProperty(IRI.create(definitionSlot)), 
						factory.getOWLAnnotationProperty(IRI.create(skosDefinition))));
				namedSlots.put(skosDefinition, definitionSlot);
			}
		}
		
		int i = 0;
		// Iterate over concept beans specified for the ontology.
		if (conceptIds != null) {
			// specified list (most likely a singleton) of conceptIds
			for (String conceptId : conceptIds) {
				ClassBean classBean = findConcept(ont.getId(), conceptId, null, false, false);
				try {
					addClassUsingOwlApi(manager, ontology, ont, classBean, namedSlots);
				} catch (Exception e) {
					System.out.println("********** PAEA ***" + "Unable to add class: " + conceptId);
					e.printStackTrace();
				}
				i++;
			}
		} else {
			// all concepts from the ontology
			OntologyRetrievalManager orm = getRetrievalManager(ont);
			
			i=0; // reset to 0
			long start = System.currentTimeMillis();
			
			for (Iterator<ClassBean> classIter = orm.listAllClasses(ont); classIter.hasNext(); ) {
				if (i>0 && (i % 1000) == 0) {
					long end = System.currentTimeMillis();
					long elapsed = end - start;
					long avg = elapsed / i;
					System.out.println("********** PAEA ***" + "Avg time to process single concept (lac): " + i + ": " + avg);
				}
				ClassBean classBean = classIter.next();
				try {
					addClassUsingOwlApi(manager, ontology, ont, classBean, namedSlots);
				} catch (Exception e) {
					System.out.println("********** PAEA ***" + "Unable to add class: " + i);
					e.printStackTrace();
				}
				i++;
			}
			long end = System.currentTimeMillis();
			long elapsed = end - start;
			System.out.println("********** PAEA ***" + "time to iterate: " + elapsed);

			
			long timeToPage = System.currentTimeMillis();
			long timeToPageElapsed = timeToPage - start;
			System.out.println("********** PAEA ***" + "time to page: " + timeToPageElapsed);
		}
		System.out.println("********** PAEA ***" + "numClasses: " + i);

	}
	
	private void addClassUsingOwlApi(OWLOntologyManager manager, OWLOntology ontology, 
			OntologyBean ont, ClassBean classBean, Hashtable<String,String> namedSlots) throws Exception {
		
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		// Declare Class
		OWLClass owlClass = factory.getOWLClass(IRI.create(getClassUri(ont, classBean)));
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(owlClass));
		
		// Internal ID (skos:notation)
		String internalId = classBean.getId();
		if (!StringHelper.isNullOrNullString(internalId)){
			OWLAnnotation annotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(skosNotation)), 
					factory.getOWLStringLiteral(internalId));
			manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation));
		}
		
		// Label (rdfs:label, skos:prefLabel)
		String label = classBean.getLabel();
		if (!StringHelper.isNullOrNullString(label)) {
			OWLAnnotation annotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
					factory.getOWLStringLiteral(label));
			manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation));

			// also add skos:prefLabel
			annotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(skosPrefLabel)),
					factory.getOWLStringLiteral(label));
			manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation));
			
			// also add namedSlot:prefLabel
			if (namedSlots.containsKey(skosPrefLabel)) {
				annotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(namedSlots.get(skosPrefLabel))),
						factory.getOWLStringLiteral(label));
				manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation));
			}
		}
		
		// Definition (skos:definition)
		ArrayList<String> definitions = (ArrayList<String>)classBean.getDefinitions();
		if (definitions != null) {
			for (String definition: definitions) {
				OWLAnnotation annotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(skosDefinition)),
						factory.getOWLStringLiteral(definition));
				manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation));
				
				// also add namedSlot:definition
				if (namedSlots.containsKey(skosDefinition)) {
					annotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(namedSlots.get(skosDefinition))),
							factory.getOWLStringLiteral(definition));
					manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation));
				}
			}
		}
		
		// Synonyms (skos:altLabel)
		ArrayList<String> synonyms = (ArrayList<String>)classBean.getSynonyms();
		if (synonyms != null) {
			for (String synonym: synonyms) {
				if (!synonym.equals(label)) {
					OWLAnnotation annotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(skosAltLabel)),
							factory.getOWLStringLiteral(synonym));
					manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation));
					
					// also add namedSlot:synonym
					if (namedSlots.containsKey(skosAltLabel)) {
						annotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(namedSlots.get(skosAltLabel))),
								factory.getOWLStringLiteral(synonym));
						manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation));
					}
				}
			}
		}
		
		// Super Classes (rdfs:subClassOf)
		ArrayList<ClassBean> superClasses = (ArrayList<ClassBean>)classBean.getRelation((Object) ApplicationConstants.SUPER_CLASS);
		if (superClasses != null) {
			for (ClassBean superClass: superClasses) {
				manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(owlClass, 
						factory.getOWLClass(IRI.create(getClassUri(ont, superClass)))));
				// add skos:broader annotation
				OWLAnnotation skosBroaderAnnotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(SKOSVocabulary.BROADER.getURI())),
						owlClass.getIRI());
				manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), skosBroaderAnnotation));

				
				// add OBO_ISA annotation
				if (ont.getFormat().toUpperCase().contains(OBO_FORMAT)) {

					OWLAnnotation oboIsaAnnotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(oboIsa)),
							owlClass.getIRI());
					manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), oboIsaAnnotation));
					OWLAnnotation oboIsaAnnotationForLiteral=factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create(oboIsa)), factory.getOWLStringLiteral(superClass.getId()));
					manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), oboIsaAnnotationForLiteral));
				}
			}
		} else {
			// if orphan, superClassOf owl:Thing? NO! Don't do this.
			//manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(owlClass, factory.getOWLThing()));
		}
		
		// Other Annotations, such as UMLS CUI TUI and semanticType
		getOtherAnnotationsUsingOwlApi(manager, ontology, factory, ont, classBean, owlClass);
	}
	
	private void getOtherAnnotationsUsingOwlApi(OWLOntologyManager manager, OWLOntology ontology, OWLDataFactory factory, OntologyBean ont, ClassBean classBean, OWLClass owlClass) throws OWLOntologyChangeException {
		Set<Object> rels = classBean.getRelations().keySet();
		for (Iterator<Object> iterator = rels.iterator(); iterator.hasNext();) {
			Object relObj = iterator.next();
			if (relObj instanceof String) {
				String annotationName = (String) relObj;
				Object annotationValues = classBean.getRelation(annotationName);
				if (annotationValues instanceof List) {
					for (Object annotationValue : (List)annotationValues) {
						if (annotationValue instanceof String) {
							if (
									// include essential UMLS annotations
									annotationName.equalsIgnoreCase(UMLS_CUI)
									|| annotationName.equalsIgnoreCase(UMLS_TUI)
									|| annotationName.equalsIgnoreCase(UMLS_CODE)
									|| annotationName.equalsIgnoreCase(UMLS_SEMANTIC_TYPE)
									) 
							{
								OWLAnnotation annotation = factory.getOWLAnnotation(
										factory.getOWLAnnotationProperty(IRI.create(UMLS_SKOS_URI + annotationName)),
										factory.getOWLStringLiteral((String)annotationValue));
								manager.addAxiom(ontology, 
										factory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation));
							}
						} else if (annotationValue instanceof ClassBean) {
							// object property (not an annotation!)
						} else {
							// annotation is neither string nor class (an error?)
						}
					}
				} else {
					// annotations are not a list (an error?)
				}
			}
		}
	}
	
	/*
	 *  The specifications for these URIs is documented in the following Google Doc
	 *  after careful negotiation and deliberation among Mayo and OBOFoundry representatives:
	 *    https://docs.google.com/Doc?docid=0AaKiubd-CCXsZGRoemJoYndfMTFmNmpuN2hmdw&hl=en
	 */
	private String getClassUri(OntologyBean ont, ClassBean classBean) {
		String ontologyFormat = ont.getFormat().toUpperCase();
		String classId = null;
		String classIdPrefix = "";
		String baseUri = null;
		String fullUri = null;
		
		if (ontologyFormat.contains("OBO")) {
			baseUri = OBO_PURL_BASE + "obo/";
			if (classBean.getId().contains(":")) {
				String[] oboId = classBean.getId().split(":", 2);
				classIdPrefix = oboId[0];
				classId = oboId[1];
			} else {
				classIdPrefix = ont.getOntologyId().toString();
			}
			classIdPrefix = getIRIFriendlyName(classIdPrefix);
			classId = getIRIFriendlyName(classId);
			fullUri = baseUri + classIdPrefix + "_" + classId;
		} else if (ontologyFormat.contains("RRF") || ontologyFormat.contains("PROT")) {
			baseUri = BIOPORTAL_PURL_BASE + "ontology/";
			classIdPrefix = StringHelper.isNullOrNullString(ont.getAbbreviation()) ?
					ont.getOntologyId().toString() : ont.getAbbreviation();
			classIdPrefix = getIRIFriendlyName(classIdPrefix);
			classId = getIRIFriendlyName(classBean.getId());
			fullUri = baseUri + classIdPrefix + "/" + classId;
		} else {
			// OWL -- use fullId URI
			fullUri = classBean.getFullId();
		}
		
		return fullUri;
	}
	
	private String getIRIFriendlyName(String name) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			// TODO: is there a better way to clean up the identifiers?
			// e.g., the HL7 ontology has ids such as "%" and "<"
			buffer.append(Character.isJavaIdentifierPart(ch) ? ch : "_"); 
		}
		return buffer.toString();
	}
	
	/**
	 * For a specific concept, we give a specific URI for it.
	 * For many concepts, we return the general ontology URI for them.
	 * 
	 * @param ont
	 * @param conceptIds
	 * @return a PURLized ontology uri
	 */
	private String getOntologyUri(OntologyBean ont, List<String> conceptIds) {
		
		
		// use the ontology abbreviation, else it's virtual id
		String prefix = StringHelper.isNullOrNullString(ont.getAbbreviation()) ? ont
				.getOntologyId().toString()
				: ont.getAbbreviation();

		String uri = BIOPORTAL_PURL_BASE;
		uri = uri + prefix;

		// provide a single-concept specific uri

		return uri;


	}
	
	
	private String getFileName(OntologyBean ont) {
		// use the virtual ontology id because we are only keeping one RDF version, the latest version
		return ont.getOntologyId() + ".rdf";
	}

}
