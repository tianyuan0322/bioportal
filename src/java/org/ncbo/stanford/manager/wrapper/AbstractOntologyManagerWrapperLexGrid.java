package org.ncbo.stanford.manager.wrapper;

import java.util.StringTokenizer;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyMetadataDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;

/**
 * Abstract class to encapsulate functionality common for both LexGrid loader
 * and retrieval manager classes
 * 
 * @author Pradip Kanjamala
 * 
 */
public  abstract class AbstractOntologyManagerWrapperLexGrid
{

	protected CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	protected CustomNcboOntologyMetadataDAO ncboOntologyMetadataDAO;
	
	
	/**
	 * @return the ncboOntologyMetadataDAO
	 */
	public CustomNcboOntologyMetadataDAO getNcboOntologyMetadataDAO() {
		return ncboOntologyMetadataDAO;
	}

	/**
	 * @param ncboOntologyMetadataDAO the ncboOntologyMetadataDAO to set
	 */
	public void setNcboOntologyMetadataDAO(CustomNcboOntologyMetadataDAO ncboOntologyMetadataDAO) {
		this.ncboOntologyMetadataDAO = ncboOntologyMetadataDAO;
	}

	public CustomNcboOntologyVersionDAO getNcboOntologyVersionDAO() {
		return ncboOntologyVersionDAO;
	}

	public void setNcboOntologyVersionDAO(CustomNcboOntologyVersionDAO ncboOntologyVersionDAO) {
		this.ncboOntologyVersionDAO = ncboOntologyVersionDAO;
	}

	protected CodingSchemeRendering getCodingSchemeRendering(LexBIGService lbs, String urnAndVersion) throws Exception {
		if (urnAndVersion == null)
		{
			return null;
		}

		String array[] = splitUrnAndVersion(urnAndVersion);
		String fileUrn = array[0];
		String ver = array[1];
		CodingSchemeRenderingList schemes = lbs.getSupportedCodingSchemes();
		CodingSchemeSummary css = null;

		for (CodingSchemeRendering csr : schemes.getCodingSchemeRendering())
		{
			css = csr.getCodingSchemeSummary();
			String version = css.getRepresentsVersion();
			String urn = css.getCodingSchemeURN();

			if (urn != null && urn.equals(fileUrn))
			{
				if (version != null && version.equals(ver))
				{
					return csr;
				}
			}
		}

		return null;
	}

	protected CodingSchemeRendering getCodingSchemeRendering(LexBIGService lbs, Integer ontologyId) throws Exception {
		return getCodingSchemeRendering(lbs, getLexGridUrnAndVersion(ontologyId));
	}

	protected CodingScheme getCodingScheme(LexBIGService lbs, String urn, String version) throws Exception {
		if (StringUtils.isEmpty(urn))
		{
			return null;
		}

		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		csvt.setVersion(version);
		return lbs.resolveCodingScheme(urn, csvt);
	}

	protected CodingScheme getCodingScheme(LexBIGService lbs, Integer id) throws Exception {
		if (id == null || lbs == null)
		{
			return null;
		}
		
		String urnAndVersion= getLexGridUrnAndVersion(id);
		String array[]= splitUrnAndVersion(urnAndVersion);
		return getCodingScheme(lbs, array[0], array[1]);
	

	}
	
	/**
	 * @param ontologyId
	 * @return
	 */
	public NcboOntology getNcboOntology(Integer ontologyId) {
		return ncboOntologyVersionDAO.findOntologyVersion(ontologyId);
	}

//	public NcboOntology getNcboOntology(String codingScheme) {
//		ncboOntologyMetadataDAO.findByCodingScheme(codingScheme);
//		return null;
//	}

	/**
	 * 
	 * @param ontologyId
	 * @return The LexGrid urn and version information that is stored by concatenating the urn, "|" and version
	 */
	protected String getLexGridUrnAndVersion(Integer ontologyId) {
	
		NcboOntology ncboOntology = ncboOntologyVersionDAO.findOntologyVersion(ontologyId);
		if (ncboOntology != null)
			return (ncboOntology.getCodingScheme());
		else
			return null;

	}
	
	/**
	 * 
	 * @param urnAndVersion
	 * @return A string array of length 2, with the first element holding the urn information and 
	 * the second element holding the version
	 */
	protected String[] splitUrnAndVersion(String urnAndVersion) {
		if (StringUtils.isEmpty(urnAndVersion )){
			return null;
		}
		String array[]= {"",""};
		StringTokenizer st = new StringTokenizer(urnAndVersion, "|");
		if (st.hasMoreTokens()) {
			array[0]= st.nextToken();
		}
		if (st.hasMoreTokens()) {
			array[1]= st.nextToken();
		}
		return array;	
	}
	



}