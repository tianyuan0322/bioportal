package org.ncbo.stanford.manager;

import java.util.List;
import java.util.StringTokenizer;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;

/**
 * Abstract class to encapsulate functionality common for both LexGrid loader
 * and retrieval manager classes
 * 
 * @author Pradip Kanjamala
 * 
 */
public abstract class AbstractOntologyManagerLexGrid {

	protected OntologyMetadataManager ontologyMetadataManager;

	protected CodingSchemeRendering getCodingSchemeRendering(LexBIGService lbs,
			String urnAndVersion) throws Exception {
		if (StringUtils.isBlank(urnAndVersion)) {
			return null;
		}

		String array[] = splitUrnAndVersion(urnAndVersion);
		String fileUrn = array[0];
		String ver = array[1];
		CodingSchemeRenderingList schemes = lbs.getSupportedCodingSchemes();
		CodingSchemeSummary css = null;

		for (CodingSchemeRendering csr : schemes.getCodingSchemeRendering()) {
			css = csr.getCodingSchemeSummary();
			String version = css.getRepresentsVersion();
			String urn = css.getCodingSchemeURI();

			if (urn != null && urn.equals(fileUrn)) {
				if (version != null && version.equals(ver)) {
					return csr;
				}
			}
		}

		return null;
	}

	protected CodingSchemeRendering getCodingSchemeRendering(LexBIGService lbs,
			Integer ontologyId) throws Exception {
		return getCodingSchemeRendering(lbs,
				getLexGridUrnAndVersion(ontologyId));
	}

	protected CodingScheme getCodingScheme(LexBIGService lbs, String urn,
			String version) throws Exception {
		if (StringUtils.isBlank(urn)) {
			return null;
		}

		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		csvt.setVersion(version);

		return lbs.resolveCodingScheme(urn, csvt);
	}

	protected CodingScheme getCodingScheme(LexBIGService lbs, Integer id)
			throws Exception {
		if (id == null || lbs == null) {
			return null;
		}

		String urnAndVersion = getLexGridUrnAndVersion(id);
		String array[] = splitUrnAndVersion(urnAndVersion);

		return getCodingScheme(lbs, array[0], array[1]);
	}

	/**
	 * return the latest NcboOntology that has the display_label provided
	 * 
	 * @param displayLabel
	 * @return
	 */
	public OntologyBean getLatestNcboOntology(String displayLabel) {
		List<OntologyBean> list = ontologyMetadataManager
				.findLatestOntologyVersions();

		for (OntologyBean ncboOntology : list) {
			if (ncboOntology.getDisplayLabel().equalsIgnoreCase(displayLabel)) {
				return ncboOntology;
			}
		}

		return null;
	}

	/**
	 * 
	 * @param ontologyVersionId
	 * @return The LexGrid urn and version information that is stored by
	 *         concatenating the urn, "|" and version
	 * @throws Exception 
	 */
	protected String getLexGridUrnAndVersion(Integer ontologyVersionId) throws Exception {
		OntologyBean ncboOntology = ontologyMetadataManager
				.findOntologyOrViewVersionById(ontologyVersionId);

		if (ncboOntology != null) {
			return (ncboOntology.getCodingScheme());
		}

		return null;
	}

	/**
	 * @param ontology
	 * @return The LexGrid codingScheme URN string (registered Name)
	 */
	protected String getLexGridCodingSchemeName(OntologyBean ontology) {
		String urnAndVersion = ontology.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		return (urnVersionArray != null && urnVersionArray.length > 0) ? urnVersionArray[0]
				: null;
	}

	/**
	 * @param ontology
	 * @return The LexGrid codingScheme URN string (registered Name)
	 */
	protected CodingSchemeVersionOrTag getLexGridCodingSchemeVersion(
			OntologyBean ontology) {
		String urnAndVersion = ontology.getCodingScheme();
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
	protected String[] splitUrnAndVersion(String urnAndVersion) {
		if (StringUtils.isBlank(urnAndVersion)) {
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

	/**
	 * @param ontologyMetadataManager the ontologyMetadataManager to set
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}

}