package lucene.manager.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.StringTokenizer;

import lucene.bean.LuceneLexGridProperty;
import lucene.bean.LuceneSearchDocument;
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

public class LuceneSearchManagerLexGridImpl implements LuceneSearchManager {

	public void indexOntology(IndexWriterWrapper writer, ResultSet rs)
			throws Exception {
		Integer ontologyId = rs.getInt("ontology_id");
		String displayLabel = rs.getString("display_label");

		System.out.println("Adding ontology to index: " + displayLabel
				+ " (Id: " + rs.getInt("id") + ", Ontology Id: "
				+ rs.getInt("ontology_id") + ", Format: "
				+ rs.getString("format") + ")");
		long start = System.currentTimeMillis();

		String schemeName = getLexGridCodingSchemeName(rs);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(rs);

		System.out.println("Coding Scheme: " + csvt.getTag() + " **** "
				+ csvt.getVersion());

		LexBIGService lbs = LexBIGServiceImpl.defaultInstance();
		CodedNodeSet codeSet = lbs.getCodingSchemeConcepts(schemeName, csvt);
		ResolvedConceptReferencesIterator matchIterator = codeSet.resolve(null,
				null, null, null, true);

		LuceneSearchDocument doc = new LuceneSearchDocument();

		while (matchIterator.hasNext()) {
			ResolvedConceptReferenceList lst = matchIterator
					.next(Integer.MAX_VALUE);

			for (Iterator<ResolvedConceptReference> itr = lst
					.iterateResolvedConceptReference(); itr.hasNext();) {
				ResolvedConceptReference ref = itr.next();
				Concept concept = ref.getReferencedEntry();

				setPresentationProperties(doc, ontologyId, concept);
				writer.addDocument(doc);
				setGenericProperties(doc, ontologyId, concept);
				writer.addDocument(doc);
				setDefinitionProperties(doc, ontologyId, concept);
				writer.addDocument(doc);
				setCommentProperties(doc, ontologyId, concept);
				writer.addDocument(doc);
				setInstructionProperties(doc, ontologyId, concept);
				writer.addDocument(doc);
			}
		}

		long stop = System.currentTimeMillis(); // stop timing
		System.out.println("Finished indexing ontology: " + displayLabel
				+ " in " + (double) (stop - start) / 1000 + " seconds.");
	}

	private void setPresentationProperties(LuceneSearchDocument doc,
			Integer ontologyId, Concept concept) {
		LuceneRecordTypeEnum recType = LuceneRecordTypeEnum.RECORD_TYPE_PREFERRED_NAME;

		for (Iterator<Presentation> itr1 = concept.iteratePresentation(); itr1
				.hasNext();) {
			Presentation p = itr1.next();
			setLuceneSearchDocument(doc, concept.getId(),
					new LuceneLexGridProperty(ontologyId, recType, p));
			recType = LuceneRecordTypeEnum.RECORD_TYPE_SYNONYM;
		}
	}

	private void setGenericProperties(LuceneSearchDocument doc,
			Integer ontologyId, Concept concept) {
		for (Iterator<ConceptProperty> itr1 = concept.iterateConceptProperty(); itr1
				.hasNext();) {
			ConceptProperty cp = itr1.next();
			setLuceneSearchDocument(doc, concept.getId(),
					new LuceneLexGridProperty(ontologyId,
							LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY, cp));
		}
	}

	private void setDefinitionProperties(LuceneSearchDocument doc,
			Integer ontologyId, Concept concept) {
		for (Iterator<Definition> itr1 = concept.iterateDefinition(); itr1
				.hasNext();) {
			Definition d = itr1.next();
			setLuceneSearchDocument(doc, concept.getId(),
					new LuceneLexGridProperty(ontologyId,
							LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY, d));
		}
	}

	private void setCommentProperties(LuceneSearchDocument doc,
			Integer ontologyId, Concept concept) {
		for (Iterator<Comment> itr1 = concept.iterateComment(); itr1.hasNext();) {
			Comment c = itr1.next();
			setLuceneSearchDocument(doc, concept.getId(),
					new LuceneLexGridProperty(ontologyId,
							LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY, c));
		}
	}

	private void setInstructionProperties(LuceneSearchDocument doc,
			Integer ontologyId, Concept concept) {
		for (Iterator<Instruction> itr1 = concept.iterateInstruction(); itr1
				.hasNext();) {
			Instruction i = itr1.next();
			setLuceneSearchDocument(doc, concept.getId(),
					new LuceneLexGridProperty(ontologyId,
							LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY, i));
		}
	}

	private void setLuceneSearchDocument(LuceneSearchDocument doc,
			String conceptId, LuceneLexGridProperty prop) {
		doc.setOntologyId(prop.getOntologyId().toString());
		doc.setRecordType(prop.getPropertyType());
		doc.setConceptId(conceptId);
		doc.setConceptIdShort(conceptId);
		doc.setContents(prop.getPropertyContent());
		doc.setLiteralContents(prop.getPropertyContent());
	}

	/**
	 * 
	 * @param ncboOntology
	 * @return The LexGrid codingScheme URN string (registered Name)
	 * @throws SQLException
	 */
	private String getLexGridCodingSchemeName(ResultSet rs) throws SQLException {
		String urnAndVersion = rs.getString("coding_scheme");
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);

		return (urnVersionArray != null && urnVersionArray.length > 0) ? urnVersionArray[0]
				: null;
	}

	/**
	 * 
	 * @param ncboOntology
	 * @return The LexGrid codingScheme URN string (registered Name)
	 * @throws SQLException
	 */
	private CodingSchemeVersionOrTag getLexGridCodingSchemeVersion(ResultSet rs)
			throws SQLException {
		String urnAndVersion = rs.getString("coding_scheme");
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
