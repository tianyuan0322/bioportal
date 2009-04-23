
package org.ncbo.stanford.util.diff;

import java.io.*;
import java.net.*;
import java.util.*;

import edu.stanford.bmir.protegex.chao.*;
import edu.stanford.bmir.protegex.chao.change.api.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.prompt.promptDiff.structures.*;
import edu.stanford.smi.protegex.prompt.util.*;
import edu.stanford.smi.protegex.server_changes.*;

/**
 * Utils to write out a diff between two ontologies to files in text and rdf format
 * 
 * This class contains mostly old utils copied from the Prompt code and updated.
 * 
 * @author Natasha Noy
 *
 */
public class DiffUtils {
	public static int TAB_DELIMITED = 0;

	public static void saveToTextFile (ResultTable resultTable, String fileName,
			boolean printFrameDifferences) {
		try {
			if (fileName == null || fileName.equals ("")) return;

			PrintStream log = null;
			log = new PrintStream(new BufferedOutputStream(new FileOutputStream(fileName)));

			TableRowComparator comparator = new TableRowComparator ();

			TableRow[] directlyChanged = resultTable.getRowsWithMappingLevel(TableRow.MAPPING_LEVEL_DIRECTLY_CHANGED).toArray(new TableRow[0]);
			Arrays.sort(directlyChanged, comparator);


			TableRow[] added = resultTable.getRowsWithOperation(TableRow.OPERATION_ADD).toArray(new TableRow[0]);
			Arrays.sort(added, comparator);

			TableRow[] deleted = resultTable.getRowsWithOperation(TableRow.OPERATION_DELETE).toArray(new TableRow[0]);
			Arrays.sort(deleted, comparator);

			writeArray (added, log, false);
			writeArray (deleted, log, false);
			writeArray (directlyChanged, log, false);



			// flush the log if this is our native format, otherwise fire the plugin event
			log.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	// This code is an old code from Prompt. It is updated sligtly to work with the new change management in Protege
	// But mostly it is the old stuff that hasn't been looked at for a while

	public  static void saveToRDFFile(ResultTable resultsTable, String fileName) {

		KnowledgeBase chaoKb = ChAOKbManager.createRDFFileChAOKb(resultsTable.getKb2(), URIUtilities.createURI(fileName));
		chaoKb.getProject().setProjectURI(URIUtilities.createURI(fileName));
		ChangesProject.initialize(resultsTable.getKb2().getProject());
		PostProcessorManager chao_db = ChangesProject.getPostProcessorManager (resultsTable.getKb2());
		ChangeFactory changeFactory = new ChangeFactory(chaoKb);

		Iterator<TableRow> i = resultsTable.values().iterator();
		while (i.hasNext()) {
			TableRow next = (TableRow)i.next();
			if (!shouldBeSaved (next)) continue;
			Frame nextf1 = next.getF1Value();
			Frame nextf2 = next.getF2Value();

			if (next.getOperationValue() == TableRow.OPERATION_ADD)  {
				String clsCreated = nextf2.getName();
				ServerChangesUtil.createCreatedChange(chao_db, changeFactory.createCreated_Change(null), nextf2, clsCreated);
				continue;
			}

			if (next.getOperationValue() == TableRow.OPERATION_DELETE) {
				String clsDeleted = nextf1.getName();
				ServerChangesUtil.createDeletedChange(chao_db, changeFactory.createDeleted_Change(null), nextf1, clsDeleted);
				continue;
			}

			if ((next.getOperationValue() == TableRow.OPERATION_MAP) && (next.getRenameValue()== TableRow.RENAME_PLUS)) {
				String oldName = nextf1.getName();
				String newName = nextf2.getName();
				ServerChangesUtil.createNameChange(chao_db, nextf1, oldName, newName);
			}

			if (next.getOperationValue()== TableRow.OPERATION_MAP && next.getMappingLevel()==TableRow.MAPPING_LEVEL_DIRECTLY_CHANGED) {
				Collection explanation = next.getOperationExplanation();
				for(Iterator iter = explanation.iterator();iter.hasNext();)
				{
					FrameDifferenceElement diff = (FrameDifferenceElement)iter.next();
					if(OWLUtil.getChangeDescription(null, next, diff).equals("restriction added")) {
						ServerChangesUtil.createChangeStd(chao_db,
								changeFactory.createComposite_Change(null),
								nextf1,
								"Restriction Added: " +
								((Frame)diff.getO2Value()).getBrowserText() +
								" to class " +
								nextf1.getName());
					}
					else if(OWLUtil.getChangeDescription(null, next, diff).equals("restriction deleted")){
						ServerChangesUtil.createChangeStd(chao_db, changeFactory.createComposite_Change(null),
								nextf1,  ((Frame)diff.getO1Value()).getBrowserText());
					}
				}
			}

		}

		Collection errors = new ArrayList ();
		chaoKb.getProject().save(errors);

		displayErrors (errors);

	}

	private static boolean shouldBeSaved (TableRow row) {
		Frame nextf1 = row.getF1Value();
		Frame nextf2 = row.getF2Value();

		if ((nextf1 != null) && ! (nextf1 instanceof Cls)) return false;
		if ((nextf2 != null) && ! (nextf2 instanceof Cls)) return false;
		if ((nextf1 != null) && !OWLUtil.isOWLNamedClass((Cls)nextf1)) return false;
		if ((nextf2 != null) && !OWLUtil.isOWLNamedClass((Cls)nextf2)) return false;
		return true;
	}

	public static void displayErrors(Collection errors) {
		Iterator i = errors.iterator();
		while (i.hasNext()) {
			System.out.println("Error: " + i.next());
		}
	} 

	private static void writeArray (TableRow[] array, PrintStream log, boolean printFrameDifferences) {
		for (int i = 0; i < array.length; i++) {
			TableRow nextRow = (TableRow) array[i];
			if (!shouldBeSaved (nextRow)) continue;
			saveToFile (nextRow, log, printFrameDifferences);
		}
	}

	private static void saveToFile(TableRow row, PrintStream log, boolean printFrameDifferences) {
		Frame f1 = row.getF1Value();
		Frame f2 = row.getF2Value();
		String operationValue = (row.getOperationValue() == TableRow.OPERATION_MAP) ? "Change" : row.getOperationValue();

		log.println("" + ((f1 == null) ? "\t" : f1.getBrowserText()) + "\t" + ((f2 == null) ? "\t" : f2.getBrowserText()) + "\t" + 
				operationValue);

		if (!printFrameDifferences) return;

		Collection explanation = row.getOperationExplanation();
		if (explanation == null) return;

		Iterator i = explanation.iterator();
		while (i.hasNext()) {
			FrameDifferenceElement next = (FrameDifferenceElement)i.next();
			saveToFile (row, next, log);
		}

	}

	private static void saveToFile(TableRow row, FrameDifferenceElement diff, PrintStream log) {
		log.println("\t"  + OWLUtil.getChangeDescription(null, row, diff) + "\t" + 
				((diff.getSlotValue() == null) ? "\t" : diff.getSlotValue().getBrowserText())  + "\t" + 
				((diff.getFacetValue() == null) ? "\t" : diff.getFacetValue().getBrowserText())  + "\t" + 
				((diff.getO1Value() == null || !(diff.getO1Value() instanceof Frame)) ? "\t" : ((Frame)diff.getO1Value()).getBrowserText())  + "\t" + 
				((diff.getO2Value() == null || !(diff.getO2Value() instanceof Frame)) ? "\t" : ((Frame)diff.getO2Value()).getBrowserText()) );
	}



}
