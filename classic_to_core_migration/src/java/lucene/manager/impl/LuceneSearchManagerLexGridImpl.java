package lucene.manager.impl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.StringTokenizer;

import lucene.bean.LuceneIndexBean;
import lucene.bean.LuceneLexGridProperty;
import lucene.enumeration.LuceneRecordTypeEnum;
import lucene.manager.LuceneSearchManager;
import lucene.wrapper.IndexWriterWrapper;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Concept;
import org.LexGrid.concepts.ConceptProperty;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Instruction;
import org.LexGrid.concepts.Presentation;
import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.util.helper.StringHelper;

public class LuceneSearchManagerLexGridImpl implements LuceneSearchManager {

	public void indexOntology(IndexWriterWrapper writer, ResultSet rs)
			throws Exception {
		Integer ontologyVersionId = rs.getInt("id");
		Integer ontologyId = rs.getInt("ontology_id");
		String ontologyDisplayLabel = rs.getString("display_label");

		System.out.println("Adding ontology to index: " + ontologyDisplayLabel
				+ " (Id: " + ontologyVersionId + ", Ontology Id: "
				+ rs.getInt("ontology_id") + ", Format: "
				+ rs.getString("format") + ")");
		long start = System.currentTimeMillis();

		String schemeName = getLexGridCodingSchemeName(rs);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(rs);
		LexBIGService lbs = LexBIGServiceImpl.defaultInstance();
		CodedNodeSet codeSet = lbs.getCodingSchemeConcepts(schemeName, csvt);
		ResolvedConceptReferencesIterator matchIterator = codeSet.resolve(null,
				null, null, null, true);

		LuceneIndexBean doc = new LuceneIndexBean();

		while (matchIterator.hasNext()) {
			ResolvedConceptReferenceList lst = matchIterator
					.next(Integer.MAX_VALUE);

			for (Iterator<ResolvedConceptReference> itr = lst
					.iterateResolvedConceptReference(); itr.hasNext();) {
				ResolvedConceptReference ref = itr.next();
				Concept concept = ref.getReferencedEntry();

				String preferredName = setPresentationProperties(writer, doc,
						ontologyVersionId, ontologyId, ontologyDisplayLabel,
						concept);
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

		long stop = System.currentTimeMillis(); // stop timing
		System.out.println("Finished indexing ontology: "
				+ ontologyDisplayLabel + " in " + (double) (stop - start)
				/ 1000 / 60 + " minutes.\n");
	}

	private String setPresentationProperties(IndexWriterWrapper writer,
			LuceneIndexBean doc, Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, Concept concept) throws IOException {
		LuceneRecordTypeEnum recType = LuceneRecordTypeEnum.RECORD_TYPE_PREFERRED_NAME;
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
				recType = LuceneRecordTypeEnum.RECORD_TYPE_SYNONYM;
			}

			setLuceneIndexBean(doc, concept.getId(),
					new LuceneLexGridProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel, recType, preferredName, p));
			writer.addDocument(doc);
		}

		return preferredName;
	}

	private void setGenericProperties(IndexWriterWrapper writer,
			LuceneIndexBean doc, Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, String preferredName, Concept concept)
			throws IOException {
		for (Iterator<ConceptProperty> itr = concept.iterateConceptProperty(); itr
				.hasNext();) {
			ConceptProperty cp = itr.next();
			setLuceneIndexBean(doc, concept.getId(),
					new LuceneLexGridProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY,
							preferredName, cp));
			writer.addDocument(doc);
		}
	}

	private void setDefinitionProperties(IndexWriterWrapper writer,
			LuceneIndexBean doc, Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, String preferredName, Concept concept)
			throws IOException {
		for (Iterator<Definition> itr = concept.iterateDefinition(); itr
				.hasNext();) {
			Definition d = itr.next();
			setLuceneIndexBean(doc, concept.getId(),
					new LuceneLexGridProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY,
							preferredName, d));
			writer.addDocument(doc);
		}
	}

	private void setCommentProperties(IndexWriterWrapper writer,
			LuceneIndexBean doc, Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, String preferredName, Concept concept)
			throws IOException {
		for (Iterator<Comment> itr = concept.iterateComment(); itr.hasNext();) {
			Comment c = itr.next();
			setLuceneIndexBean(doc, concept.getId(),
					new LuceneLexGridProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY,
							preferredName, c));
			writer.addDocument(doc);
		}
	}

	private void setInstructionProperties(IndexWriterWrapper writer,
			LuceneIndexBean doc, Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, String preferredName, Concept concept)
			throws IOException {
		for (Iterator<Instruction> itr = concept.iterateInstruction(); itr
				.hasNext();) {
			Instruction i = itr.next();
			setLuceneIndexBean(doc, concept.getId(),
					new LuceneLexGridProperty(ontologyVersionId, ontologyId,
							ontologyDisplayLabel,
							LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY,
							preferredName, i));
			writer.addDocument(doc);
		}
	}

	private void setLuceneIndexBean(LuceneIndexBean doc, String conceptId,
			LuceneLexGridProperty prop) {
		doc.populateInstance(prop.getOntologyVersionId(), prop.getOntologyId(),
				prop.getOntologyDisplayLabel(), prop.getRecordType(),
				conceptId, conceptId, prop.getPreferredName(), prop
						.getPropertyContent(), prop.getPropertyContent());
	}

	/**
	 * @param rs
	 * @return The LexGrid codingScheme URN string (registered Name)
	 * @throws SQLException
	 */
	private String getLexGridCodingSchemeName(ResultSet rs) throws SQLException {
		String urnAndVersion = rs.getString("coding_scheme");

		if (StringHelper.isNullOrNullString(urnAndVersion)) {
			urnAndVersion = rs.getString("urn");
		}

		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);

		return (urnVersionArray != null && urnVersionArray.length > 0) ? urnVersionArray[0]
				: null;
	}

	/**
	 * @param rs
	 * @return The LexGrid codingScheme URN string (registered Name)
	 * @throws SQLException
	 */
	private CodingSchemeVersionOrTag getLexGridCodingSchemeVersion(ResultSet rs)
			throws SQLException {
		String urnAndVersion = rs.getString("coding_scheme");

		if (StringHelper.isNullOrNullString(urnAndVersion)) {
			urnAndVersion = rs.getString("urn");
		}

		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);

		return (urnVersionArray != null && urnVersionArray.length > 1) ? Constructors
				.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1])
				: null;
	}

	/**
	 * 
	 * @param urnAndVersion
	 * @return A string array of length 2, with the first element holding the
	 *         urn information and the second element holding the version
	 */
	private String[] splitUrnAndVersion(String urnAndVersion) {
		if (StringUtils.isEmpty(urnAndVersion)) {
			return null;
		}

		String array[] = { "", "" };
		StringTokenizer st = new StringTokenizer(urnAndVersion, "|");

		if (st.hasMoreTokens()) {
			array[0] = st.nextToken();
		}

		if (st.hasMoreTokens()) {
			array[1] = st.nextToken();
		}

		return array;
	}
}
