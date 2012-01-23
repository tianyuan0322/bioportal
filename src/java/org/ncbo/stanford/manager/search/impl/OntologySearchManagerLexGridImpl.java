package org.ncbo.stanford.manager.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.Text;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Concept;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Presentation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.search.LexGridSearchProperty;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

/**
 * Implements the OntologySearchManager interface that deals specifically with
 * ontologies handled by LexGrid backend
 * 
 * @author Michael Dorf
 * 
 */
public class OntologySearchManagerLexGridImpl extends
		AbstractOntologyManagerLexGrid implements OntologySearchManager {

	private static final Log log = LogFactory
			.getLog(OntologySearchManagerLexGridImpl.class);

	private static final int MATCH_ITERATOR_BLOCK = 100;

	/**
	 * Index a given ontology
	 * 
	 * @param writer
	 * @param ontology
	 * @throws Exception
	 */
	public void indexOntology(LuceneIndexWriterWrapper writer,
			OntologyBean ontology) throws Exception {
		try {
			Integer ontologyVersionId = ontology.getId();
			Integer ontologyId = ontology.getOntologyId();
			String ontologyDisplayLabel = ontology.getDisplayLabel();
			String schemeName = getLexGridCodingSchemeName(ontology);
			CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontology);
			LexBIGService lbs = LexBIGServiceImpl.defaultInstance();
			CodedNodeSet codeSet = lbs
					.getCodingSchemeConcepts(schemeName, csvt);
			ResolvedConceptReferencesIterator matchIterator = codeSet.resolve(
					null, null, null, null, true);

			SearchIndexBean doc = new SearchIndexBean();

			while (matchIterator.hasNext()) {
				ResolvedConceptReferenceList lst = matchIterator
						.next(MATCH_ITERATOR_BLOCK);

				for (Iterator<ResolvedConceptReference> itr = lst
						.iterateResolvedConceptReference(); itr.hasNext();) {
					ResolvedConceptReference ref = itr.next();
					Concept concept = ref.getReferencedEntry();
					String preferredName = getPreferredName(concept);
					String fullId = getFullId(ontology, concept.getEntityCode());
					Byte isObsolete = new Byte(isObsolete(concept) ? (byte) 1
							: (byte) 0);

					addConceptIds(writer, doc, fullId, ontologyVersionId,
							ontologyId, ontologyDisplayLabel, preferredName,
							concept, isObsolete);
					addPresentationProperties(writer, doc, fullId,
							ontologyVersionId, ontologyId,
							ontologyDisplayLabel, preferredName, concept,
							isObsolete);
					addGenericProperties(writer, doc, fullId,
							ontologyVersionId, ontologyId,
							ontologyDisplayLabel, preferredName, concept,
							isObsolete);
					addDefinitionProperties(writer, doc, fullId,
							ontologyVersionId, ontologyId,
							ontologyDisplayLabel, preferredName, concept,
							isObsolete);
					addCommentProperties(writer, doc, fullId,
							ontologyVersionId, ontologyId,
							ontologyDisplayLabel, preferredName, concept,
							isObsolete);
				}
			}

			matchIterator.release();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new Exception("Ontology " + ontology.getDisplayLabel()
					+ " (Id: " + ontology.getId() + ", Ontology Id: "
					+ ontology.getOntologyId() + ") has generated an error: "
					+ e.getMessage());
		}
	}

	/**
	 * Adds documents to index that define "presentation" type properties in
	 * LexGrid
	 * 
	 * @param writer
	 * @param doc
	 * @param fullId
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param concept
	 * @param isObsolete
	 * @throws IOException
	 */
	private void addPresentationProperties(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, String fullId, Integer ontologyVersionId,
			Integer ontologyId, String ontologyDisplayLabel,
			String preferredName, Concept concept, Byte isObsolete)
			throws IOException {
		SearchRecordTypeEnum recType = SearchRecordTypeEnum.RECORD_TYPE_PREFERRED_NAME;
		List<SearchIndexBean> docs = new ArrayList<SearchIndexBean>(0);

		for (Iterator<Presentation> itr = concept.iteratePresentation(); itr
				.hasNext();) {
			Presentation p = itr.next();

			// if the value is not a preferred name, it is assumed to be
			// asynonym
			if (!p.getIsPreferred()) {
				recType = SearchRecordTypeEnum.RECORD_TYPE_SYNONYM;
			}

			populateIndexBean(doc, fullId, concept.getEntityCode(),
					new LexGridSearchProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel, recType, preferredName,
							isObsolete, p));
			docs.add(doc);
		}
		writer.addDocuments(docs);
	}

	/**
	 * Get the preferred name of a concept
	 * 
	 * @param concept
	 */
	private String getPreferredName(Concept concept) {
		String preferredName = "";
		EntityDescription desc = concept.getEntityDescription();

		if (desc != null) {
			preferredName = desc.getContent();
		} else {
			for (Iterator<Presentation> itr = concept.iteratePresentation(); itr
					.hasNext();) {
				Presentation p = itr.next();

				if (p.getIsPreferred()) {
					preferredName = p.getValue().getContent();
					break;
				}
			}
		}

		return preferredName;
	}

	/**
	 * Adds documents to index that define "generic" type properties in LexGrid
	 * 
	 * @param writer
	 * @param doc
	 * @param fullId
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param concept
	 * @param isObsolete
	 * @throws IOException
	 */
	private void addGenericProperties(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, String fullId, Integer ontologyVersionId,
			Integer ontologyId, String ontologyDisplayLabel,
			String preferredName, Concept concept, Byte isObsolete)
			throws IOException {
		List<SearchIndexBean> docs = new ArrayList<SearchIndexBean>(0);

		for (Iterator<Property> itr = concept.iterateProperty(); itr.hasNext();) {
			Property cp = itr.next();
			populateIndexBean(doc, fullId, concept.getEntityCode(),
					new LexGridSearchProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							SearchRecordTypeEnum.RECORD_TYPE_PROPERTY,
							preferredName, isObsolete, cp));
			docs.add(doc);
		}
		writer.addDocuments(docs);
	}

	/**
	 * Adds documents to index that define "definition" type properties in
	 * LexGrid
	 * 
	 * @param writer
	 * @param doc
	 * @param fullId
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param concept
	 * @param isObsolete
	 * @throws IOException
	 */
	private void addDefinitionProperties(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, String fullId, Integer ontologyVersionId,
			Integer ontologyId, String ontologyDisplayLabel,
			String preferredName, Concept concept, Byte isObsolete)
			throws IOException {
		List<SearchIndexBean> docs = new ArrayList<SearchIndexBean>(0);

		for (Iterator<Definition> itr = concept.iterateDefinition(); itr
				.hasNext();) {
			Definition d = itr.next();
			populateIndexBean(doc, fullId, concept.getEntityCode(),
					new LexGridSearchProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							SearchRecordTypeEnum.RECORD_TYPE_PROPERTY,
							preferredName, isObsolete, d));
			docs.add(doc);
		}
		writer.addDocuments(docs);
	}

	/**
	 * Adds documents to index that define "comment" type properties in LexGrid
	 * 
	 * @param writer
	 * @param doc
	 * @param fullId
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param concept
	 * @param isObsolete
	 * @throws IOException
	 */
	private void addCommentProperties(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, String fullId, Integer ontologyVersionId,
			Integer ontologyId, String ontologyDisplayLabel,
			String preferredName, Concept concept, Byte isObsolete)
			throws IOException {
		List<SearchIndexBean> docs = new ArrayList<SearchIndexBean>(0);

		for (Iterator<Comment> itr = concept.iterateComment(); itr.hasNext();) {
			Comment c = itr.next();
			populateIndexBean(doc, fullId, concept.getEntityCode(),
					new LexGridSearchProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							SearchRecordTypeEnum.RECORD_TYPE_PROPERTY,
							preferredName, isObsolete, c));
			docs.add(doc);
		}
		writer.addDocuments(docs);
	}

	/**
	 * Adds concept ids to the index
	 * 
	 * @param writer
	 * @param doc
	 * @param fullId
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param concept
	 * @param isObsolete
	 * @throws IOException
	 */
	private void addConceptIds(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, String fullId, Integer ontologyVersionId,
			Integer ontologyId, String ontologyDisplayLabel,
			String preferredName, Concept concept, Byte isObsolete)
			throws IOException {
		String conceptId = concept.getEntityCode();
		List<SearchIndexBean> docs = new ArrayList<SearchIndexBean>(0);

		// add concept id to index only if concept id != preferredName to avoid
		// duplication of data
		if (!conceptId.equals(preferredName)) {
			Property p = new Property();
			Text t = new Text();
			t.setContent(conceptId);
			p.setValue(t);

			populateIndexBean(doc, fullId, conceptId,
					new LexGridSearchProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							SearchRecordTypeEnum.RECORD_TYPE_CONCEPT_ID,
							preferredName, isObsolete, p));
			docs.add(doc);
		}
		writer.addDocuments(docs);
	}

	/**
	 * Populate a single record in the index
	 * 
	 * @param doc
	 * @param fullId
	 * @param conceptId
	 * @param prop
	 */
	private void populateIndexBean(SearchIndexBean doc, String fullId,
			String conceptId, LexGridSearchProperty prop) {
		doc.populateInstance(prop.getOntologyVersionId(), prop.getOntologyId(),
				prop.getOntologyDisplayLabel(), prop.getRecordType(), prop
						.getObjectType(), fullId, conceptId, prop
						.getPreferredName(), prop.getPropertyContent(), null,
				prop.getIsObsolete());
	}
}
