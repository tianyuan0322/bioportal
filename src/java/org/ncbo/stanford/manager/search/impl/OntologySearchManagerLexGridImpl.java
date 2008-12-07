package org.ncbo.stanford.manager.search.impl;

import java.io.IOException;
import java.util.Iterator;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Concept;
import org.LexGrid.concepts.ConceptProperty;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Instruction;
import org.LexGrid.concepts.Presentation;
import org.ncbo.stanford.bean.search.LexGridSearchProperty;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
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

	/**
	 * Index a given ontology
	 * 
	 * @param writer
	 * @param ontology
	 * @throws Exception
	 */
	public void indexOntology(LuceneIndexWriterWrapper writer,
			VNcboOntology ontology) throws Exception {
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
						.next(Integer.MAX_VALUE);

				for (Iterator<ResolvedConceptReference> itr = lst
						.iterateResolvedConceptReference(); itr.hasNext();) {
					ResolvedConceptReference ref = itr.next();
					Concept concept = ref.getReferencedEntry();

					String preferredName = setPresentationProperties(writer,
							doc, ontologyVersionId, ontologyId,
							ontologyDisplayLabel, concept);
					setGenericProperties(writer, doc, ontologyVersionId,
							ontologyId, ontologyDisplayLabel, preferredName,
							concept);
					setDefinitionProperties(writer, doc, ontologyVersionId,
							ontologyId, ontologyDisplayLabel, preferredName,
							concept);
					setCommentProperties(writer, doc, ontologyVersionId,
							ontologyId, ontologyDisplayLabel, preferredName,
							concept);
					setInstructionProperties(writer, doc, ontologyVersionId,
							ontologyId, ontologyDisplayLabel, preferredName,
							concept);
				}
			}

			matchIterator.release();
		} catch (Exception e) {
			if (e instanceof LBParameterException
					|| e instanceof LBInvocationException) {
				throw new Exception("Ontology " + ontology.getDisplayLabel()
						+ " (Id: " + ontology.getId() + ", Ontology Id: "
						+ ontology.getOntologyId()
						+ ") does not exist in the LexGrid back-end");
			}
		}
	}

	/**
	 * Adds documents to index that define "presentation" type properties in
	 * LexGrid
	 * 
	 * @param writer
	 * @param doc
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param concept
	 * @return
	 * @throws IOException
	 */
	private String setPresentationProperties(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, Concept concept) throws IOException {
		SearchRecordTypeEnum recType = SearchRecordTypeEnum.RECORD_TYPE_PREFERRED_NAME;
		boolean isFirst = true;
		String preferredName = null;

		for (Iterator<Presentation> itr = concept.iteratePresentation(); itr
				.hasNext();) {
			Presentation p = itr.next();

			// the first value is assumed to be the preferred name
			// the rest of the values are assumed to by synonyms
			if (isFirst) {
				preferredName = p.getText().getContent();
				isFirst = false;
			} else {
				recType = SearchRecordTypeEnum.RECORD_TYPE_SYNONYM;
			}

			populateIndexBean(doc, concept.getId(), new LexGridSearchProperty(
					ontologyVersionId, ontologyId, ontologyDisplayLabel,
					recType, preferredName, p));
			writer.addDocument(doc);
		}

		return preferredName;
	}

	/**
	 * Adds documents to index that define "generic" type properties in LexGrid
	 * 
	 * @param writer
	 * @param doc
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param concept
	 * @throws IOException
	 */
	private void setGenericProperties(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, String preferredName, Concept concept)
			throws IOException {
		for (Iterator<ConceptProperty> itr = concept.iterateConceptProperty(); itr
				.hasNext();) {
			ConceptProperty cp = itr.next();
			populateIndexBean(doc, concept.getId(), new LexGridSearchProperty(
					ontologyVersionId, ontologyId, ontologyDisplayLabel,
					SearchRecordTypeEnum.RECORD_TYPE_PROPERTY, preferredName,
					cp));
			writer.addDocument(doc);
		}
	}

	/**
	 * Adds documents to index that define "definition" type properties in
	 * LexGrid
	 * 
	 * @param writer
	 * @param doc
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param concept
	 * @throws IOException
	 */
	private void setDefinitionProperties(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, String preferredName, Concept concept)
			throws IOException {
		for (Iterator<Definition> itr = concept.iterateDefinition(); itr
				.hasNext();) {
			Definition d = itr.next();
			populateIndexBean(doc, concept.getId(),
					new LexGridSearchProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							SearchRecordTypeEnum.RECORD_TYPE_PROPERTY,
							preferredName, d));
			writer.addDocument(doc);
		}
	}

	/**
	 * Adds documents to index that define "comment" type properties in LexGrid
	 * 
	 * @param writer
	 * @param doc
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param concept
	 * @throws IOException
	 */
	private void setCommentProperties(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, String preferredName, Concept concept)
			throws IOException {
		for (Iterator<Comment> itr = concept.iterateComment(); itr.hasNext();) {
			Comment c = itr.next();
			populateIndexBean(doc, concept.getId(),
					new LexGridSearchProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							SearchRecordTypeEnum.RECORD_TYPE_PROPERTY,
							preferredName, c));
			writer.addDocument(doc);
		}
	}

	/**
	 * Adds documents to index that define "instruction" type properties in
	 * LexGrid
	 * 
	 * @param writer
	 * @param doc
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param concept
	 * @throws IOException
	 */
	private void setInstructionProperties(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, String preferredName, Concept concept)
			throws IOException {
		for (Iterator<Instruction> itr = concept.iterateInstruction(); itr
				.hasNext();) {
			Instruction i = itr.next();
			populateIndexBean(doc, concept.getId(),
					new LexGridSearchProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							SearchRecordTypeEnum.RECORD_TYPE_PROPERTY,
							preferredName, i));
			writer.addDocument(doc);
		}
	}

	/**
	 * Populate a single record in the index
	 * 
	 * @param doc
	 * @param conceptId
	 * @param prop
	 */
	private void populateIndexBean(SearchIndexBean doc, String conceptId,
			LexGridSearchProperty prop) {
		doc.populateInstance(prop.getOntologyVersionId(), prop.getOntologyId(),
				prop.getOntologyDisplayLabel(), prop.getRecordType(),
				conceptId, conceptId, prop.getPreferredName(), prop
						.getPropertyContent(), prop.getPropertyContent());
	}
}
