package lucene.manager.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.StringTokenizer;

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

	private static final int MAX_NUM_CONCEPTS = Integer.MAX_VALUE;
	private LexBIGService lbs;

	public LuceneSearchManagerLexGridImpl() {
		lbs = LexBIGServiceImpl.defaultInstance();
	}

	public void indexOntology(IndexWriterWrapper writer, ResultSet rs)
			throws Exception {
		String schemeName = getLexGridCodingSchemeName(rs);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(rs);

		System.out.println("Coding Scheme: " + csvt.getTag() + " **** "
				+ csvt.getVersion());

		CodedNodeSet codeSet = lbs.getCodingSchemeConcepts(schemeName, csvt);
		ResolvedConceptReferencesIterator matchIterator = codeSet.resolve(null,
				null, null, null, true);

		while (matchIterator.hasNext()) {
			ResolvedConceptReferenceList lst = matchIterator
					.next(MAX_NUM_CONCEPTS);

			for (Iterator<ResolvedConceptReference> itr = lst
					.iterateResolvedConceptReference(); itr.hasNext();) {
				ResolvedConceptReference ref = itr.next();
				Concept entry = ref.getReferencedEntry();
				boolean isFirst = true;

				System.out.println("Concept Id: " + entry.getId());

				for (Iterator<Presentation> itr1 = entry.iteratePresentation(); itr1
						.hasNext();) {
					Presentation p = itr1.next();

					if (isFirst) {
						// preferred name

						isFirst = false;
					} else {
						// synonym
					}
				}

				// generic properties
				for (Iterator<ConceptProperty> itr1 = entry
						.iterateConceptProperty(); itr1.hasNext();) {
					ConceptProperty cp = itr1.next();

				}

				// definition properties
				for (Iterator<Definition> itr1 = entry.iterateDefinition(); itr1
						.hasNext();) {
					Definition d = itr1.next();

				}

				// comment properties
				for (Iterator<Comment> itr1 = entry.iterateComment(); itr1
						.hasNext();) {
					Comment c = itr1.next();

				}

				// instruction properties
				for (Iterator<Instruction> itr1 = entry.iterateInstruction(); itr1
						.hasNext();) {
					Instruction i = itr1.next();

				}

			}
		}
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

		CodingSchemeVersionOrTag tag = Constructors
				.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]);

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
