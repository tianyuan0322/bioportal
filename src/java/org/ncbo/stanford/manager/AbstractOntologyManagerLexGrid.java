package org.ncbo.stanford.manager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	
	private final Set<Integer> ownUriSchemeOntologies = new HashSet<Integer>(
			Arrays.asList(
					1072, 1073, 1074, 1075));
	
	private final Set<Integer> newPurlOntologies = new HashSet<Integer>(
			Arrays.asList(
				1007, 1062));

	private final Set<Integer> legacyPurlOntologies = new HashSet<Integer>(
			Arrays.asList(
				1090, 1370, 1222, 1114, 1023, 1005, 1049,
				1048, 1067, 1006, 1047, 1001, 1037, 1063, 1144, 1008, 1016,
				1015, 1069, 1012, 1013, 1064, 1017, 1019, 1397, 1070, 1021,
				1022, 1009, 1125, 1362, 1050, 1311, 1025, 1105, 1395, 1027,
				1152, 1029, 1030, 1077, 1000, 1010, 1031, 1026, 1132, 1328,
				1094, 1035, 1107, 1014, 1043, 1036, 1038, 1493, 1108, 1492,
				1490, 1041, 1040, 1500, 1044, 1109, 1078, 1091, 1224, 1046,
				1419, 1110, 1081, 1065, 1111, 1404, 1112, 1095, 1115, 1051));	
	
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
		OntologyBean ob = null;
		for (OntologyBean ncboOntology : list) {
			if (ncboOntology.getDisplayLabel().equalsIgnoreCase(displayLabel)) {
				if (ob == null || ncboOntology.getId() > ob.getId()) {
					ob = ncboOntology;
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
	public OntologyBean getOntologyBeanByDisplayNameAndOntologyId(
			String displayLabel, Integer ontologyId) throws Exception {
		List<OntologyBean> list = ontologyMetadataManager
				.findAllOntologyOrViewVersionsById(ontologyId, false);
		OntologyBean ob = null;
		for (OntologyBean ncboOntology : list) {
			if (ncboOntology.getDisplayLabel().equalsIgnoreCase(displayLabel)) {
				if (ob == null || ncboOntology.getId() > ob.getId()) {
					ob = ncboOntology;
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
				fullId = getOBOFullId(ontologyBean, code);
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

	private String getOBOFullId(OntologyBean ontologyBean, String code) {
		String prefix = "";
		String fullId ="";
		String modCode = code.replace(':', '_');
		String codeSplitArray[] = code.split(":");
		Integer ontologyId = ontologyBean.getOntologyId();
		
		if (codeSplitArray.length >= 2) {
			prefix = codeSplitArray[0];
		}

		// The OBO FullId is generated based on which four categories the
		// ontology id falls under.
		
		if (ownUriSchemeOntologies.contains(ontologyId)) {
			// Category 1: Ontologies that have their own URI scheme
			fullId = "http://www.cellcycleontology.org/ontology/owl/" + prefix
					+ "#" + modCode;
		} else if (newPurlOntologies.contains(ontologyId)) {
			// Category 2: The ontologies have transitioned to the new scheme such
			// as (CHEBI and PRO)
			// The url will be in the form:
			// http://purl.obolibrary.org/obo/prefix_xxxxx
			fullId = "http://purl.obolibrary.org/obo/" + modCode;
		} else if (legacyPurlOntologies.contains(ontologyId)) {
			// Category 3: Some ontologies use the "legacy" purls of the form:
			// http://purl.org/obo/owl/<prefix>#<prefix>_xxxxx
			fullId = "http://purl.org/obo/owl/" + prefix + "#" + modCode;
		} else {
			// Category 4: The ontologies have no URIs. We create a default URI of
			// the form: http://purl.bioontology.org/ontology/PREFIX/PREFIX_xxxx
			if (StringUtils.isNotBlank(ontologyBean.getAbbreviation())) {
				fullId = "http://purl.bioontology.org/ontology/"
						+ ontologyBean.getAbbreviation() + "/" + modCode;
			} else {
				fullId = "http://purl.bioontology.org/ontology/"
						+ prefix + "/" + modCode;
			}			
		}

		return fullId;
	}
}