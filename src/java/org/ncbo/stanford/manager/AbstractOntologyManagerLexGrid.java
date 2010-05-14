package org.ncbo.stanford.manager;

import java.util.List;
import java.util.StringTokenizer;

import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * Abstract class to encapsulate functionality common for both LexGrid loader
 * and retrieval manager classes
 * 
 * @author Pradip Kanjamala
 * 
 */
public abstract class AbstractOntologyManagerLexGrid {

	protected OntologyMetadataManager ontologyMetadataManager;
	protected LexBIGService lbs;
	protected LexBIGServiceConvenienceMethods lbscm;

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
	 * @throws Exception
	 */
	public OntologyBean getLatestOntologyBean(String displayLabel)
			throws Exception {
		List<OntologyBean> list = ontologyMetadataManager
				.findLatestOntologyVersions();
		OntologyBean ob= null;
		for (OntologyBean ncboOntology : list) {
			if (ncboOntology.getDisplayLabel().equalsIgnoreCase(displayLabel)) {
				if (ob== null || ncboOntology.getId() > ob.getId()) {
					ob= ncboOntology;
				}
			}
		}

		return ob;
	}

	/**
	 * return the latest NcboOntology that has the display_label provided
	 * 
	 * @param displayLabel
	 * @return
	 * @throws Exception
	 */
	public OntologyBean getOntologyBeanByDisplayNameAndOntologyId(String displayLabel, Integer ontologyId)
			throws Exception {
		List<OntologyBean> list = ontologyMetadataManager
				.findAllOntologyOrViewVersionsById(ontologyId, false);
		OntologyBean ob= null;
		for (OntologyBean ncboOntology : list) {
			if (ncboOntology.getDisplayLabel().equalsIgnoreCase(displayLabel)) {
				if (ob== null || ncboOntology.getId() > ob.getId()) {
					ob= ncboOntology;
				}
			}
		}

		return ob;
	}
	/**
	 * 
	 * @param ontologyVersionId
	 * @return The LexGrid urn and version information that is stored by
	 *         concatenating the urn, "|" and version
	 * @throws Exception
	 */
	protected String getLexGridUrnAndVersion(Integer ontologyVersionId)
			throws Exception {
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
	 * @param ontologyMetadataManager
	 *            the ontologyMetadataManager to set
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}

	/**
	 * A helper method that returns the ResolvedConceptReferenceList of root
	 * concepts.
	 * 
	 * @param schemeName
	 * @param csvt
	 * @return
	 * @throws Exception
	 */
	protected ResolvedConceptReferenceList getHierarchyRootConcepts(
			String schemeName, CodingSchemeVersionOrTag csvt, boolean light)
			throws Exception {
		// Iterate through all hierarchies ...
		String hierarchyId = getDefaultHierarchyId(schemeName, csvt);
		ResolvedConceptReferenceList rcrl = lbscm.getHierarchyRoots(schemeName,
				csvt, hierarchyId, !light);
		return rcrl;
	}

	/**
	 * A helper method that returns the next level of the hierarchy of a
	 * conceptId
	 * 
	 * @param schemeName
	 * @param csvt
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	protected AssociationList getHierarchyLevelPrev(String schemeName,
			CodingSchemeVersionOrTag csvt, String conceptId) throws Exception {
		String hierarchyId = getDefaultHierarchyId(schemeName, csvt);
		AssociationList associations = lbscm.getHierarchyLevelPrev(schemeName,
				csvt, hierarchyId, conceptId, false, false, null);
		return associations;
	}

	protected String getDefaultHierarchyId(String schemeName,
			CodingSchemeVersionOrTag csvt) throws Exception {
		String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
		String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;

		for (String hierarchy : hierarchyIDs) {
			if (hierarchy.equalsIgnoreCase("IS_A")) {
				hierarchyId = hierarchy;
				break;
			}
		}
		return hierarchyId;
	}

	/**
	 * A helper method that returns the previous level of the hierarchy of a
	 * conceptId
	 * 
	 * @param schemeName
	 * @param csvt
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	protected AssociationList getHierarchyLevelNext(String schemeName,
			CodingSchemeVersionOrTag csvt, String conceptId) throws Exception {
		String hierarchyId = getDefaultHierarchyId(schemeName, csvt);
		AssociationList associations = lbscm.getHierarchyLevelNext(schemeName,
				csvt, hierarchyId, conceptId, false, false, null);
		return associations;
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	protected int getChildCount(AssociationList list) {
		int count = 0;
		if (list == null)
			return count;
		for (Association association : list.getAssociation()) {
			AssociatedConceptList assocConceptList = association
					.getAssociatedConcepts();
			count += assocConceptList.getAssociatedConceptCount();
		}
		return count;

	}
	
	protected String getFullId(OntologyBean ontologyBean, String code) {
		String fullId = code;
		String modCode = code.replace(':', '_');
		if (ontologyBean != null) {
			if (ApplicationConstants.FORMAT_OBO.equalsIgnoreCase(ontologyBean
					.getFormat())) {
				fullId = "http://purl.obolibrary.org/obo/" + modCode;
			}
			if (ApplicationConstants.FORMAT_UMLS_RRF
					.equalsIgnoreCase(ontologyBean.getFormat())) {
				fullId = "http://purl.bioontology.org/ontology/"
						+ ontologyBean.getAbbreviation() + "/" + code;
			}
			if (ApplicationConstants.FORMAT_LEXGRID_XML
					.equalsIgnoreCase(ontologyBean.getFormat())) {
				fullId = "http://purl.bioontology.org/ontology/"
						+ ontologyBean.getAbbreviation() + "/" + modCode;
			}

		}
		return fullId;
	}	
}