package org.ncbo.stanford.manager.wrapper;

import java.util.StringTokenizer;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;



public abstract class AbstractOntologyManagerWrapperLexGrid
{

	protected CodingSchemeRendering getCodingSchemeRendering(String urnAndVersion) throws Exception {
		if (urnAndVersion == null)
		{
			return null;
		}

		StringTokenizer st = new StringTokenizer(urnAndVersion, "|");
		String fileUrn = st.nextToken();
		String ver = st.nextToken();

		LexBIGService lbs = new LexBIGServiceImpl();
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

	protected CodingScheme getCodingScheme(LexBIGService lbs, String urnAndVersion) throws Exception {
		if (urnAndVersion == null)
		{
			return null;
		}

		StringTokenizer st = new StringTokenizer(urnAndVersion, "|");
		String u = st.nextToken();
		String ver = st.nextToken();
		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		csvt.setVersion(ver);

		return lbs.resolveCodingScheme(u, csvt);
	}
}