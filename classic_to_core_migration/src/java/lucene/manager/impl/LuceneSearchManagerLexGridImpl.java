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
import org.LexGrid.concepts.PropertyLink;
import org.apache.commons.lang.StringUtils;

public class LuceneSearchManagerLexGridImpl implements LuceneSearchManager {

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

		// Analyze the result ...
		ResolvedConceptReferencesIterator matchIterator = codeSet.resolve(null,
				null, null, null, true);
		ResolvedConceptReferenceList lst = matchIterator.next(10000);

		for (Iterator<ResolvedConceptReference> itr = lst
				.iterateResolvedConceptReference(); itr.hasNext();) {
			ResolvedConceptReference ref = itr.next();
			Concept entry = ref.getReferencedEntry();

			System.out.println("Id: " + entry.getId());

			
			for (Iterator<Comment> itr1 = entry.iterateComment(); itr1
					.hasNext();) {
				Comment c = itr1.next();
				System.out.println("Comment: " + c.getText().getContent());
			}
			for (Iterator<ConceptProperty> itr1 = entry
					.iterateConceptProperty(); itr1.hasNext();) {
				ConceptProperty cp = itr1.next();
				System.out.println("ConceptProprty: " + cp.getText().getContent());
			}
			for (Iterator<Definition> itr1 = entry.iterateDefinition(); itr1
					.hasNext();) {
				Definition d = itr1.next();
				System.out.println("Definition: " + d.getText().getContent());
			}
			for (Iterator<Instruction> itr1 = entry.iterateInstruction(); itr1
					.hasNext();) {
				Instruction i = itr1.next();
				System.out.println("Instruction: " + i.getText().getContent());
			}
			for (Iterator<Presentation> itr1 = entry.iteratePresentation(); itr1
					.hasNext();) {
				Presentation p = itr1.next();
				System.out.println("Presentation: " + p.getText().getContent());
			}
			for (Iterator<PropertyLink> itr1 = entry.iteratePropertyLink(); itr1
					.hasNext();) {
				PropertyLink pl = itr1.next();
				System.out.println("PropertyLink: " + pl.getLink());
			}

		}

		System.out.println("Count: " + lst.getResolvedConceptReferenceCount());
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
