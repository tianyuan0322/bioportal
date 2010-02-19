/**
 * @author: Pradip Kanjamala kanjamala.pradipnoy@mayo.edu
 */
package org.ncbo.stanford.manager.diff.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.Text;
import org.LexGrid.concepts.Entity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.diff.OntologyDiffManager;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.util.difffile.pathhandler.DiffFilePathHandler;
import org.ncbo.stanford.util.difffile.pathhandler.impl.DiffFilePathHandlerImpl;

import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.bmir.protegex.chao.change.api.Class_Created;
import edu.stanford.bmir.protegex.chao.change.api.Deleted_Change;
import edu.stanford.bmir.protegex.chao.change.api.Property_Change;
import edu.stanford.bmir.protegex.chao.change.api.Property_Created;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyComponentFactory;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Class;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.storage.rdf.RDFBackend;

public class OntologyDiffManagerLexGridImpl extends
		AbstractOntologyManagerLexGrid implements OntologyDiffManager {
	public static final String FORMAT_RDF = "rdf";
	public static final String FORMAT_TXT = "txt";
	public static final String FORMAT_DEFAULT = "txt";

	private static final Log log = LogFactory
			.getLog(OntologyDiffManagerLexGridImpl.class);
	private KnowledgeBase changesKb;
	private ChangeFactory changeFactory;
	private OntologyComponentFactory componentFactory;
	private OntologyDiffManagerProtegeImpl ontologyDiffManagerProtege;

	public OntologyDiffManagerLexGridImpl() throws Exception {
		lbs = LexBIGServiceImpl.defaultInstance();
		lbscm = (LexBIGServiceConvenienceMethods) lbs
				.getGenericExtension("LexBIGServiceConvenienceMethods");

	}

	public void createDiff(OntologyBean ontologyVersionOld,
			OntologyBean ontologyVersionNew) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("In create diff for " + ontologyVersionOld.getId()
					+ " and " + ontologyVersionNew.getId());
		}

		List<ChangeHolder> changeList = processOntologyDiff(ontologyVersionOld,
				ontologyVersionNew);
		saveDiffToFiles(ontologyVersionOld, ontologyVersionNew, changeList);

	}

	public void createDiffForTwoLatestVersions(Integer ontologyId)
			throws Exception {
		List<OntologyBean> allVersions = ontologyMetadataManager
				.findAllOntologyOrViewVersionsById(ontologyId, false);

		// get a list of version ids, filtering out ontologies that are not
		// active (want to compare only active ontologies
		List<Integer> versionIds = new ArrayList<Integer>(allVersions.size());

		for (OntologyBean ontologyVersion : allVersions) {
			if (ontologyVersion.getStatusId().equals(
					StatusEnum.STATUS_READY.getStatus())) {
				versionIds.add(ontologyVersion.getId());
			}
		}

		// there are fewer than two active versions -- nothing to compare
		if (versionIds.size() < 2)
			return;

		Collections.sort(versionIds);
		// latest version
		Integer newVersionId = versionIds.get(versionIds.size() - 1);
		// previous version
		Integer oldVersionId = versionIds.get(versionIds.size() - 2);

		OntologyBean newVersion = ontologyMetadataManager
				.findOntologyOrViewVersionById(newVersionId);
		OntologyBean oldVersion = ontologyMetadataManager
				.findOntologyOrViewVersionById(oldVersionId);

		if (newVersion == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + newVersionId + ")");
		}
		if (oldVersion == null) {
			throw new OntologyNotFoundException(
					OntologyNotFoundException.DEFAULT_MESSAGE
							+ " (Version Id: " + oldVersionId + ")");
		}
		createDiff(oldVersion, newVersion);
	}

	public List<ArrayList<String>> getAllDiffsForOntology(Integer ontologyId) {
		return ontologyDiffManagerProtege.getAllDiffsForOntology(ontologyId);
	}

	public File getDiffFileForOntologyVersions(Integer ontologyVerisonId1,
			Integer ontologyVersionId2, String format)
			throws FileNotFoundException, Exception {
		return ontologyDiffManagerProtege.getDiffFileForOntologyVersions(
				ontologyVerisonId1, ontologyVersionId2, format);
	}

	public boolean diffExists(OntologyBean ontologyVersionOld,
			OntologyBean ontologyVersionNew) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Saves the ResultTable in different file formats
	 */
	private void saveDiffToFiles(OntologyBean ontologyVersionOld,
			OntologyBean ontologyVersionNew, List<ChangeHolder> changeList)
			throws Exception {
		String diffFileName = getDiffFileName(ontologyVersionOld
				.getOntologyId(), ontologyVersionOld.getId(),
				ontologyVersionNew.getId(), true);

		saveToRDFFile(changeList, diffFileName);
		saveToTabDelimitedTextFile(changeList, diffFileName);
	}

	private void saveToTabDelimitedTextFile(List<ChangeHolder> changeList,
			String diffFileName) throws Exception {
		String fullDiffFileName = diffFileName
				+ getFileExtensionFromFormat(FORMAT_TXT);
		System.out.println("TabDelimited File location=" + fullDiffFileName);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				fullDiffFileName));
		bufferedWriter.write(ChangeHolder.toTabbedHeader() + "\n");
		for (ChangeHolder ch : changeList) {
			bufferedWriter.write(ch.toTabbedString() + "\n");
		}

	}

	private String getFileExtensionFromFormat(String format) {
		return "." + format;
	}

	/**
	 * Will return the file name for the diff (without the extension) Returns
	 * null if the directory for that diff does not exist
	 * 
	 * @param ontologyId
	 * @param ontologyVersionIdOld
	 * @param ontologyVersionIdNew
	 * @param createDir
	 * @return
	 */

	private String getDiffFileName(Integer ontologyId,
			Integer ontologyVersionIdOld, Integer ontologyVersionIdNew,
			boolean createDir) {
		DiffFilePathHandler diffPathFileHandler = new DiffFilePathHandlerImpl();
		String diffDirName = diffPathFileHandler
				.getDiffDirNameForOntologyVersions(ontologyId,
						ontologyVersionIdOld, ontologyVersionIdNew, createDir);

		if (createDir) // the directory was created in getDiffDirName if it
			// didn't exist
			return diffPathFileHandler.getFileName(diffDirName,
					ontologyVersionIdOld, ontologyVersionIdNew);

		// did not want to create a directory (createDir == null)
		if (diffDirName == null) // the directory does not exist
			return null;

		return diffPathFileHandler.getFileName(diffDirName,
				ontologyVersionIdOld, ontologyVersionIdNew);
	}

	public void printOntologyDiffs(OntologyBean ontologyVersionOld,
			OntologyBean ontologyVersionNew) throws Exception {
		List<ChangeHolder> changeList = processOntologyDiff(ontologyVersionOld,
				ontologyVersionNew);
		for (ChangeHolder ch : changeList) {
			System.out.println(ch);
		}
		saveToRDFFile(changeList, "C:/temp/cellDiffFile");
	}

	private List<ChangeHolder> processOntologyDiff(
			OntologyBean ontologyVersionOld, OntologyBean ontologyVersionNew)
			throws Exception {

		String schemeNameOld = getLexGridCodingSchemeName(ontologyVersionOld);
		CodingSchemeVersionOrTag csvtOld = getLexGridCodingSchemeVersion(ontologyVersionOld);
		String schemeNameNew = getLexGridCodingSchemeName(ontologyVersionNew);
		CodingSchemeVersionOrTag csvtNew = getLexGridCodingSchemeVersion(ontologyVersionNew);

		return processOntologyDiff(schemeNameNew, csvtNew, schemeNameOld,
				csvtOld);

	}

	private List<ChangeHolder> processOntologyDiff(String schemeNameNew,
			CodingSchemeVersionOrTag csvtNew, String schemeNameOld,
			CodingSchemeVersionOrTag csvtOld) throws Exception {

		List<ChangeHolder> changeList = new ArrayList<ChangeHolder>();
		if (StringUtils.isBlank(schemeNameNew)
				|| StringUtils.isBlank(schemeNameOld)) {
			log
					.warn("Can not process request when the codingSchemeURI is blank for either the new CodingScheme Name or the old codingScheme Name");
		} else {
			List<ChangeHolder> addChangeList = computeConceptAdds(
					schemeNameNew, csvtNew, schemeNameOld, csvtOld);
			List<ChangeHolder> deleteChangeList = computeConceptDeletes(
					schemeNameNew, csvtNew, schemeNameOld, csvtOld);
			List<ChangeHolder> changeChangeList = computeConceptChanges(
					schemeNameNew, csvtNew, schemeNameOld, csvtOld);
			changeList.addAll(addChangeList);
			changeList.addAll(deleteChangeList);
			changeList.addAll(changeChangeList);
		}
		return changeList;

	}

	private List<ChangeHolder> computeConceptAdds(String schemeNameNew,
			CodingSchemeVersionOrTag csvtNew, String schemeNameOld,
			CodingSchemeVersionOrTag csvtOld) throws Exception {
		List<ChangeHolder> changeList = new ArrayList<ChangeHolder>();

		CodedNodeSet cnsNew = lbs.getCodingSchemeConcepts(schemeNameNew,
				csvtNew);
		CodedNodeSet cnsOld = lbs.getCodingSchemeConcepts(schemeNameOld,
				csvtOld);
		CodedNodeSet diffCS = cnsNew.difference(cnsOld);
		ResolvedConceptReference[] rcrArray = diffCS.resolveToList(null, null,
				null, null, false, -1).getResolvedConceptReference();
		for (ResolvedConceptReference rcr : rcrArray) {
			ChangeHolder ch = new ChangeHolder(rcr.getConceptCode(), rcr
					.getEntityDescription().getContent(),
					ChangeHolder.CHANGETYPE.ADD);
			changeList.add(ch);

		}

		return changeList;

	}

	private List<ChangeHolder> computeConceptDeletes(String schemeNameNew,
			CodingSchemeVersionOrTag csvtNew, String schemeNameOld,
			CodingSchemeVersionOrTag csvtOld) throws Exception {
		List<ChangeHolder> changeList = new ArrayList<ChangeHolder>();

		CodedNodeSet cnsNew = lbs.getCodingSchemeConcepts(schemeNameNew,
				csvtNew);
		CodedNodeSet cnsOld = lbs.getCodingSchemeConcepts(schemeNameOld,
				csvtOld);
		cnsOld.difference(cnsNew);
		ResolvedConceptReference[] rcrArray = cnsOld.resolveToList(null, null,
				null, null, false, -1).getResolvedConceptReference();
		for (ResolvedConceptReference rcr : rcrArray) {
			ChangeHolder ch = new ChangeHolder(rcr.getConceptCode(), rcr
					.getEntityDescription().getContent(),
					ChangeHolder.CHANGETYPE.DELETE);
			changeList.add(ch);

		}
		return changeList;
	}

	private List<ChangeHolder> computeConceptChanges(String schemeNameNew,
			CodingSchemeVersionOrTag csvtNew, String schemeNameOld,
			CodingSchemeVersionOrTag csvtOld) throws Exception {
		List<ChangeHolder> changeList = new ArrayList<ChangeHolder>();

		CodedNodeSet cnsNew = lbs.getCodingSchemeConcepts(schemeNameNew,
				csvtNew);
		CodedNodeSet cnsOld = lbs.getCodingSchemeConcepts(schemeNameOld,
				csvtOld);
		cnsNew.intersect(cnsOld);
		ResolvedConceptReferencesIterator rcrIt = cnsNew.resolve(null, null,
				null, null, false);
		while (rcrIt.hasNext()) {
			ResolvedConceptReference rcrNew = null;
			ResolvedConceptReference rcrOld = null;

			ResolvedConceptReference rcr = rcrIt.next();

			ResolvedConceptReferenceList rcrlNew = lbs.getNodeGraph(
					schemeNameNew, csvtNew, null).resolveAsList(rcr, true,
					true, 0, 1, null, null, null, -1);
			if (rcrlNew.getResolvedConceptReferenceCount() > 0) {
				rcrNew = (ResolvedConceptReference) rcrlNew
						.enumerateResolvedConceptReference().nextElement();
			}

			ResolvedConceptReferenceList rcrlOld = lbs.getNodeGraph(
					schemeNameOld, csvtOld, null).resolveAsList(rcr, true,
					true, 0, 1, null, null, null, -1);
			if (rcrlOld.getResolvedConceptReferenceCount() > 0) {
				rcrOld = (ResolvedConceptReference) rcrlOld
						.enumerateResolvedConceptReference().nextElement();
			}
			if (rcrNew != null && rcrOld != null) {
				computeResolvedConceptReferenceChanges(rcrNew, rcrOld,
						changeList);
			} else {
				log.warn("Problem resolving either the code "
						+ rcr.getConceptCode() + " for either codingSchemeNew="
						+ schemeNameNew + " or codingSchemeOld= "
						+ schemeNameOld);
			}

		}
		return changeList;
	}

	void computeResolvedConceptReferenceChanges(
			ResolvedConceptReference rcrNew, ResolvedConceptReference rcrOld,
			List<ChangeHolder> changeList) {
		// Compare entity description
		String rcrNewEntityDesc = rcrNew.getEntityDescription().getContent()
				.trim();
		String rcrOldEntityDesc = rcrOld.getEntityDescription().getContent()
				.trim();
		if (!rcrNewEntityDesc.equals(rcrOldEntityDesc)) {
			ChangeHolder ch = new ChangeHolder(rcrNew.getCode(),
					rcrNewEntityDesc, ChangeHolder.CHANGETYPE.MODIFY,
					ChangeHolder.FIELDTYPE.LABEL,
					ChangeHolder.CHANGETYPE.MODIFY, "EntityDescription",
					rcrOldEntityDesc, rcrNewEntityDesc);
			changeList.add(ch);
		}

		// Compare the entities
		computeEntityChanges(rcrNew.getEntity(), rcrOld.getEntity(), changeList);
		computeAssociationListChanges(rcrNew, rcrNew.getSourceOf(), rcrOld
				.getSourceOf(), changeList);

	}

	void computeAssociationListChanges(ResolvedConceptReference rcrNew,
			AssociationList assocListNew, AssociationList assocListOld,
			List<ChangeHolder> changeList) {
		Set<String> processedAssocNames = new HashSet<String>();
		if (assocListNew == null) {
			assocListNew = new AssociationList();
		}
		if (assocListOld == null) {
			assocListOld = new AssociationList();
		}
		for (Association assocNew : assocListNew.getAssociation()) {
			for (Association assocOld : assocListOld.getAssociation()) {
				if (assocNew.getAssociationName().equals(
						assocOld.getAssociationName())) {
					computeAssociationChanges(rcrNew, assocNew, assocOld,
							changeList);
					processedAssocNames.add(assocNew.getAssociationName());
				}
			}

			// The association was not found in the old version, process as adds
			if (!processedAssocNames.contains(assocNew.getAssociationName())) {
				Association dummyAssoc = new Association();
				dummyAssoc.setAssociationName(assocNew.getAssociationName());
				computeAssociationChanges(rcrNew, assocNew, dummyAssoc,
						changeList);
			}

		}

		// Check if there are associations in the previous version that isn't in
		// the new
		for (Association assocOld : assocListOld.getAssociation()) {
			if (!processedAssocNames.contains(assocOld.getAssociationName())) {
				Association dummyAssoc = new Association();
				dummyAssoc.setAssociationName(assocOld.getAssociationName());
				computeAssociationChanges(rcrNew, dummyAssoc, assocOld,
						changeList);
			}
		}

	}

	// The association name is assumed to be the same for both the assocNew and
	// and assocOld based on processing logic
	void computeAssociationChanges(ResolvedConceptReference rcrNew,
			Association assocNew, Association assocOld,
			List<ChangeHolder> changeList) {
		Set<String> processedConcept = new HashSet<String>();
		if (assocNew.getAssociatedConcepts() != null) {
			for (AssociatedConcept acNew : assocNew.getAssociatedConcepts()
					.getAssociatedConcept()) {
				if (assocOld.getAssociatedConcepts() != null) {
					for (AssociatedConcept acOld : assocOld
							.getAssociatedConcepts().getAssociatedConcept()) {
						if (acNew.getConceptCode().equals(
								acOld.getConceptCode())) {
							processedConcept.add(acNew.getConceptCode());
						}
					}
				}

				if (!processedConcept.contains(acNew.getConceptCode())) {
					ChangeHolder ch = new ChangeHolder(rcrNew.getConceptCode(),
							rcrNew.getEntityDescription().getContent(),
							ChangeHolder.CHANGETYPE.MODIFY,
							ChangeHolder.FIELDTYPE.ASSOCIATION,
							ChangeHolder.CHANGETYPE.ADD, assocNew
									.getAssociationName(), null, acNew
									.getConceptCode());
					changeList.add(ch);

				}
			}
		}

		if (assocOld.getAssociatedConcepts() != null) {
			for (AssociatedConcept acOld : assocOld.getAssociatedConcepts()
					.getAssociatedConcept()) {
				if (!processedConcept.contains(acOld.getConceptCode())) {
					ChangeHolder ch = new ChangeHolder(rcrNew.getConceptCode(),
							rcrNew.getEntityDescription().getContent(),
							ChangeHolder.CHANGETYPE.MODIFY,
							ChangeHolder.FIELDTYPE.ASSOCIATION,
							ChangeHolder.CHANGETYPE.DELETE, assocNew
									.getAssociationName(), acOld
									.getConceptCode(), null);
					changeList.add(ch);

				}
			}
		}
	}

	void computeEntityChanges(Entity entityNew, Entity entityOld,
			List<ChangeHolder> changeList) {
		// Compare the presentations
		computePropertyChanges(entityNew, (Property[]) entityNew
				.getPresentation(), (Property[]) entityOld.getPresentation(),
				ChangeHolder.FIELDTYPE.PRESENTATION, changeList);
		// Compare the definitions
		computePropertyChanges(entityNew, (Property[]) entityNew
				.getDefinition(), (Property[]) entityOld.getDefinition(),
				ChangeHolder.FIELDTYPE.DEFINITION, changeList);
		// Compare the comments
		computePropertyChanges(entityNew, (Property[]) entityNew.getComment(),
				(Property[]) entityOld.getComment(),
				ChangeHolder.FIELDTYPE.COMMENT, changeList);
		// Compare the rest of the properties
		computePropertyChanges(entityNew, (Property[]) entityNew.getProperty(),
				(Property[]) entityOld.getProperty(),
				ChangeHolder.FIELDTYPE.PROPERTY, changeList);

	}

	String getNotNullPropertyValue(Property p) {
		Text t = p.getValue();
		if (t != null) {
			return t.getContent();
		}
		return "";
	}

	void computePropertyChanges(Entity entityNew, Property[] propArrayNew,
			Property[] propArrayOld, ChangeHolder.FIELDTYPE fieldType,
			List<ChangeHolder> changeList) {
		String entityCode = entityNew.getEntityCode();
		String entityDescription = entityNew.getEntityDescription()
				.getContent();
		if (propArrayNew == null) {
			propArrayNew = new Property[0];
		}
		if (propArrayOld == null) {
			propArrayOld = new Property[0];
		}
		// Use a map to identify how a new property value maps to a old property
		// value. We assume the property names should be the same for them to
		// map, else they are new.
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < propArrayNew.length; i++) {
			Property propNew = propArrayNew[i];
			String propNewName = propNew.getPropertyName();
			String propNewValue = getNotNullPropertyValue(propNew);
			int minLevenshteinDistance = Integer.MAX_VALUE;
			for (int j = 0; j < propArrayOld.length; j++) {
				Property propOld = propArrayOld[j];
				String propOldName = propOld.getPropertyName();
				String propOldValue = getNotNullPropertyValue(propOld);
				// Compare the propertyNames to ensure they are the same
				if (propNewName.equals(propOldName)) {
					int levenshteinDistance = StringUtils
							.getLevenshteinDistance(propNewValue, propOldValue);
					if (levenshteinDistance < minLevenshteinDistance) {
						minLevenshteinDistance = levenshteinDistance;
						map.put(i, j);
					}

				}
			}
		}
		// Process adds
		for (int i = 0; i < propArrayNew.length; i++) {
			if (!map.containsKey(i)) {
				Property propNew = propArrayNew[i];
				String propNewName = propNew.getPropertyName();
				String propNewValue = getNotNullPropertyValue(propNew);
				ChangeHolder ch = new ChangeHolder(entityCode,
						entityDescription, ChangeHolder.CHANGETYPE.MODIFY,
						fieldType, ChangeHolder.CHANGETYPE.ADD, propNewName,
						null, propNewValue);
				changeList.add(ch);
			}
		}
		// Process deletes
		for (int i = 0; i < propArrayOld.length; i++) {
			if (!map.containsValue(i)) {
				Property propOld = propArrayOld[i];
				String propOldName = propOld.getPropertyName();
				String propOldValue = getNotNullPropertyValue(propOld);
				ChangeHolder ch = new ChangeHolder(entityCode,
						entityDescription, ChangeHolder.CHANGETYPE.MODIFY,
						fieldType, ChangeHolder.CHANGETYPE.DELETE, propOldName,
						propOldValue, null);
				changeList.add(ch);
			}
		}
		// Process changes
		for (int key : map.keySet()) {
			int value = map.get(key);
			Property propNew = propArrayNew[key];
			String propNewName = propNew.getPropertyName();
			String propNewValue = getNotNullPropertyValue(propNew).trim();
			Property propOld = propArrayOld[value];
			String propOldName = propOld.getPropertyName();
			String propOldValue = getNotNullPropertyValue(propOld).trim();
			if (!propNewValue.equals(propOldValue)) {
				ChangeHolder ch = new ChangeHolder(entityCode,
						entityDescription, ChangeHolder.CHANGETYPE.MODIFY,
						fieldType, ChangeHolder.CHANGETYPE.MODIFY, propNewName,
						propOldValue, propNewValue);
				changeList.add(ch);

			}
		}
	}

	static class ChangeHolder {
		String conceptCode;
		String entityDescription;
		CHANGETYPE changeType;
		FIELDTYPE fieldType;
		CHANGETYPE fieldChangeType;
		String fieldName;
		String oldValue;
		String newValue;

		public enum CHANGETYPE {
			ADD, DELETE, MODIFY
		};

		public enum FIELDTYPE {
			LABEL, PRESENTATION, DEFINITION, COMMENT, PROPERTY, ASSOCIATION
		};

		ChangeHolder(String conceptCode, String entityDescription,
				CHANGETYPE changeType, FIELDTYPE fieldType,
				CHANGETYPE fieldChangeType, String fieldName, String oldValue,
				String newValue) {
			this.conceptCode = conceptCode;
			this.entityDescription = entityDescription;
			this.changeType = changeType;
			this.fieldType = fieldType;
			this.fieldChangeType = fieldChangeType;
			this.fieldName = fieldName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		ChangeHolder(String conceptCode, String entityDescription,
				CHANGETYPE changeType) {
			this.conceptCode = conceptCode;
			this.entityDescription = entityDescription;
			this.changeType = changeType;

		}

		public String getConceptCode() {
			return conceptCode;
		}

		public void setConceptCode(String conceptCode) {
			this.conceptCode = conceptCode;
		}

		public CHANGETYPE getChangeType() {
			return changeType;
		}

		public void setChangeType(CHANGETYPE changeType) {
			this.changeType = changeType;
		}

		public FIELDTYPE getFieldType() {
			return fieldType;
		}

		public void setfieldType(FIELDTYPE fieldType) {
			this.fieldType = fieldType;
		}

		public CHANGETYPE getFieldChangeType() {
			return fieldChangeType;
		}

		public void setFieldChangeType(CHANGETYPE fieldChangeType) {
			this.fieldChangeType = fieldChangeType;
		}

		public String getOldValue() {
			return oldValue;
		}

		public void setOldValue(String oldValue) {
			this.oldValue = oldValue;
		}

		public String getNewValue() {
			return newValue;
		}

		public void setNewValue(String newValue) {
			this.newValue = newValue;
		}

		public String toString() {
			String str = "conceptCode= " + conceptCode
					+ "  entityDescription= " + entityDescription
					+ "  changeType= " + changeType + "  fieldType= "
					+ fieldType + "  fieldChangeType= " + fieldChangeType
					+ "  fieldName= " + fieldName + "  oldValue= " + oldValue
					+ "  newValue= " + newValue;
			return str;

		}

		public static String toTabbedHeader() {
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("conceptCode").append("\t");
			strBuf.append("label").append("\t");
			strBuf.append("changeType").append("\t");
			strBuf.append("propertyName").append("\t");
			strBuf.append("propertyChangeType").append("\t");
			strBuf.append("value");
			return strBuf.toString();
		}
		
		public String toTabbedString() {
			StringBuffer strBuf = new StringBuffer();
			switch (changeType) {
			case ADD:
			case DELETE:
				strBuf.append(conceptCode).append("\t");
				strBuf.append(entityDescription).append("\t");
				strBuf.append(changeType);
				break;

			case MODIFY:
				strBuf.append(conceptCode).append("\t");
				strBuf.append(entityDescription).append("\t");
				strBuf.append(changeType).append("\t");
				strBuf.append(fieldName).append("\t");
				strBuf.append(fieldChangeType).append("\t");
				switch (fieldChangeType) {
				case ADD:
					strBuf.append(newValue);
					break;
				case DELETE:
					strBuf.append(oldValue);
					break;
				case MODIFY:
					strBuf.append("oldValue=").append(oldValue).append("\t");
					strBuf.append("newValue=").append(newValue);
					break;
				}
				
				break;
			}
			return strBuf.toString();
		}
	}

	public static void logErrors(Collection errors) {
		Iterator i = errors.iterator();
		while (i.hasNext()) {
			log.error("Error: " + i.next());
		}
	}

	void saveToRDFFile(List<ChangeHolder> changeList, String fileName)
			throws Exception {
		fileName += ".pprj";
		initChAO(fileName);
		processChangeHolderIntoChAO(changeList);
		Collection errors = new ArrayList();

		changesKb.getProject().save(errors);
		if (!errors.isEmpty()) {
			logErrors(errors);
		}

	}

	public void initChAO(String fileNameOfpprj) throws Exception {
		try {
			final String PROTEGE_NAMESPACE = "http://protege.stanford.edu/kb#";

			// run the static initializer
			Class.forName("edu.stanford.bmir.protegex.chao.ChAOKbManager");
			Collection errors = new ArrayList();
			ClassLoader cl = this.getClass().getClassLoader();
			URL url = cl.getResource("projects/changes.pprj");

			System.out.println("URL of changes.pprj is" + url);
			// load the standard changes project
			Project changesProject = new Project(url.toString(), errors);
			changesKb = changesProject.getKnowledgeBase();
			if (!errors.isEmpty()) {
				logErrors(errors);
				throw new RuntimeException(
						"Unexpected problem loading standard changes knowledge base");
			}
			// don't override the standard changes project
			URI chaoURI = URIUtilities.createURI(fileNameOfpprj);
			changesProject.setProjectURI(chaoURI);

			// The statements below are for setting the name of the rdf file
			// that is generated.
			// If these are not there, the name of the rdf file defaults to
			// change.rdf
			String rdfsFileName = FileUtilities.replaceExtension(URIUtilities
					.getName(chaoURI), ".rdfs");
			String rdfFileName = FileUtilities.replaceExtension(URIUtilities
					.getName(chaoURI), ".rdf");
			RDFBackend.setSourceFiles(changesProject.getSources(),
					rdfsFileName, rdfFileName, PROTEGE_NAMESPACE);

		} catch (ClassNotFoundException cnfe) {
			// normally wouldn't throw runtime exceptions but this shouldn't
			// happen
			throw new RuntimeException(cnfe);
		}
		changeFactory = new ChangeFactory(changesKb);
		componentFactory = new OntologyComponentFactory(changesKb);
	}

	void processChangeHolderIntoChAO(List<ChangeHolder> changeList) {
		for (ChangeHolder ch : changeList) {
			switch (ch.changeType) {
			case ADD:
				chaoClassCreated(ch.conceptCode, ch.entityDescription);
				break;
			case DELETE:
				chaoClassDeleted(ch.conceptCode, ch.entityDescription);
				break;
			case MODIFY:
				switch (ch.fieldChangeType) {
				case ADD:
					chaoPropertyAdded(ch.conceptCode, ch.entityDescription,
							ch.fieldName, ch.newValue);
					break;
				case DELETE:
					chaoPropertyDeleted(ch.conceptCode, ch.entityDescription,
							ch.fieldName, ch.oldValue);
					break;
				case MODIFY:
					chaoPropertyChange(ch.conceptCode, ch.entityDescription,
							ch.fieldName, ch.oldValue, ch.newValue);
					break;
				}
				break;

			}
		}

	}

	public Class_Created chaoClassCreated(String classId, String classLabel) {
		String description = "Created concept/class " + classId
				+ " with label " + classLabel;
		Ontology_Class ont_class = componentFactory.getOntology_Class(classId);
		if (ont_class == null) {
			ont_class = componentFactory.createOntology_Class(classId);
			ont_class.setCurrentName(classLabel);

		}

		Class_Created created = changeFactory.createClass_Created(null);
		created.setAction(description);
		created.setApplyTo(ont_class);
		created.setContext(classLabel);
		// created.setAuthor(user);
		created.setCreationName(classId);
		// created.setTimestamp(DefaultTimestamp.getTimestamp(changesKb));
		return created;
	}

	public Deleted_Change chaoClassDeleted(String classId, String classLabel) {
		String description = "Deleted concept/class " + classId
				+ " with label " + classLabel;
		Ontology_Class ont_class = componentFactory.getOntology_Class(classId);
		if (ont_class == null) {
			ont_class = componentFactory.createOntology_Class(classId);
			ont_class.setCurrentName(classLabel);
		}

		Deleted_Change deleted = changeFactory.createDeleted_Change(null);
		deleted.setAction(description);
		deleted.setApplyTo(ont_class);
		deleted.setContext(classLabel);
		// deleted.setAuthor(user);
		deleted.setDeletionName(classId);
		// deleted.setTimestamp(DefaultTimestamp.getTimestamp(changesKb));
		return deleted;
	}

	public Property_Created chaoPropertyAdded(String classId,
			String classLabel, String propertyName, String propertyValue) {
		String description = "Added property to class " + classId
				+ " with property name= " + propertyName + " and value= "
				+ propertyValue;
		Ontology_Class ont_class = componentFactory.getOntology_Class(classId);
		if (ont_class == null) {
			ont_class = componentFactory.createOntology_Class(classId);
			ont_class.setCurrentName(classLabel);
		}

		Property_Created created = changeFactory.createProperty_Created(null);
		created.setAction(description);
		created.setApplyTo(ont_class);
		created.setContext(classId + " " + propertyName + " " + propertyValue);
		// deleted.setAuthor(user);
		created.setCreationName(propertyName);
		// created.setTimestamp(DefaultTimestamp.getTimestamp(changesKb));
		return created;
	}

	public Deleted_Change chaoPropertyDeleted(String classId,
			String classLabel, String propertyName, String propertyValue) {
		String description = "Deleted property in class " + classId
				+ " with property name= " + propertyName + " and value= "
				+ propertyValue;
		Ontology_Class ont_class = componentFactory.getOntology_Class(classId);
		if (ont_class == null) {
			ont_class = componentFactory.createOntology_Class(classId);
			ont_class.setCurrentName(classLabel);
		}

		Deleted_Change deleted = changeFactory.createProperty_Deleted(null);
		deleted.setAction(description);
		deleted.setApplyTo(ont_class);
		deleted.setContext(classId + " " + propertyName + " " + propertyValue);
		// deleted.setAuthor(user);
		deleted.setDeletionName(propertyName);
		// deleted.setTimestamp(DefaultTimestamp.getTimestamp(changesKb));
		return deleted;
	}

	public Property_Change chaoPropertyChange(String classId,
			String classLabel, String propertyName, String oldPropertyValue,
			String newPropertyValue) {
		String description = "Changed property in class " + classId
				+ " with property name= " + propertyName + ". Old value= "
				+ oldPropertyValue + ", New value=" + newPropertyValue;
		Ontology_Class ont_class = componentFactory.getOntology_Class(classId);
		if (ont_class == null) {
			ont_class = componentFactory.createOntology_Class(classId);
			ont_class.setCurrentName(classLabel);
		}

		Property_Change propChange = changeFactory.createProperty_Change(null);
		propChange.setAction(description);
		propChange.setApplyTo(ont_class);

		// propChange.setAuthor(user);
		propChange.setContext(propertyName);
		// propChange.setTimestamp(DefaultTimestamp.getTimestamp(changesKb));
		return propChange;
	}

	/**
	 * @param ontologyMetadataManager
	 *            the ontologyMetadataManager to set
	 */
	public void setOntologyDiffManagerProtege(
			OntologyDiffManagerProtegeImpl ontologyDiffManagerProtege) {
		this.ontologyDiffManagerProtege = ontologyDiffManagerProtege;
	}
}
