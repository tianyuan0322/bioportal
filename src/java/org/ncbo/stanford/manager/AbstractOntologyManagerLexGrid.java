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
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionMetadataDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;

/**
 * Abstract class to encapsulate functionality common for both LexGrid loader
 * and retrieval manager classes
 * 
 * @author Pradip Kanjamala
 * 
 */
public abstract class AbstractOntologyManagerLexGrid {

	protected CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	protected CustomNcboOntologyVersionMetadataDAO ncboOntologyVersionMetadataDAO;

	protected CodingSchemeRendering getCodingSchemeRendering(LexBIGService lbs,
			String urnAndVersion) throws Exception {
		if (urnAndVersion == null) {
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
			String urn = css.getCodingSchemeURN();

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
		if (StringUtils.isEmpty(urn)) {
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
	 * @param ontologyId
	 * @return
	 */
	public VNcboOntology getLatestNcboOntology(Integer ontology_id) {
		return ncboOntologyVersionDAO.findLatestOntologyVersion(ontology_id);
	}

	/**
	 * return the latest NcboOntology that has the display_label provided
	 * 
	 * @param display_label
	 * @return
	 */
	public VNcboOntology getLatestNcboOntology(String display_label) {
		List<VNcboOntology> list = ncboOntologyVersionDAO
				.findLatestOntologyVersions();
		for (VNcboOntology ncboOntology : list) {
			if (ncboOntology.getDisplayLabel().equalsIgnoreCase(display_label)) {
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
	 */
	protected String getLexGridUrnAndVersion(Integer ontologyVersionId) {
		VNcboOntology ncboOntology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);

		if (ncboOntology != null) {
			return (ncboOntology.getCodingScheme());
		}

		return null;
	}

	/**
	 * 
	 * @param ncboOntology
	 * @return The LexGrid codingScheme URN string (registered Name)
	 */
	protected String getLexGridCodingSchemeName(VNcboOntology ncboOntology) {
		String urnAndVersion = ncboOntology.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		if (urnVersionArray != null)
			return urnVersionArray[0];
		else
			return null;
	}

	/**
	 * 
	 * @param ncboOntology
	 * @return The LexGrid codingScheme URN string (registered Name)
	 */
	protected CodingSchemeVersionOrTag getLexGridCodingSchemeVersion(
			VNcboOntology ncboOntology) {
		String urnAndVersion = ncboOntology.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		if (urnVersionArray != null)
			return Constructors
					.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]);
		else
			return null;
	}

	/**
	 * 
	 * @param urnAndVersion
	 * @return A string array of length 2, with the first element holding the
	 *         urn information and the second element holding the version
	 */
	protected String[] splitUrnAndVersion(String urnAndVersion) {
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

	/**
	 * @return the ncboOntologyVersionDAO
	 */
	public CustomNcboOntologyVersionDAO getNcboOntologyVersionDAO() {
		return ncboOntologyVersionDAO;
	}

	/**
	 * @param ncboOntologyVersionDAO
	 *            the ncboOntologyVersionDAO to set
	 */
	public void setNcboOntologyVersionDAO(
			CustomNcboOntologyVersionDAO ncboOntologyVersionDAO) {
		this.ncboOntologyVersionDAO = ncboOntologyVersionDAO;
	}

	/**
	 * @return the ncboOntologyVersionMetadataDAO
	 */
	public CustomNcboOntologyVersionMetadataDAO getNcboOntologyVersionMetadataDAO() {
		return ncboOntologyVersionMetadataDAO;
	}

	/**
	 * @param ncboOntologyVersionMetadataDAO
	 *            the ncboOntologyVersionMetadataDAO to set
	 */
	public void setNcboOntologyVersionMetadataDAO(
			CustomNcboOntologyVersionMetadataDAO ncboOntologyVersionMetadataDAO) {
		this.ncboOntologyVersionMetadataDAO = ncboOntologyVersionMetadataDAO;
	}
}