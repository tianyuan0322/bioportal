
package org.ncbo.stanford.util.diff;

import edu.stanford.smi.protegex.prompt.promptDiff.structures.ResultTable;

/**
 * 
 * @author Natasha Noy
 *
 */
public class DiffUtils {
	public static int TAB_DELIMITED = 0;

	public static void saveToTextFile (ResultTable resultTable, String fileName,
			boolean printFrameDifferences) {
//		try {
//			if (fileName == null || fileName.equals ("")) return;
//
//			PrintStream log = null;
//			log = new PrintStream(new BufferedOutputStream(new FileOutputStream(fileName)));
//
//			TableRowComparator comparator = new TableRowComparator ();
//
//			TableRow[] directlyChanged = resultTable.getRowsWithMappingLevel(TableRow.MAPPING_LEVEL_DIRECTLY_CHANGED).toArray(new TableRow[0]);
//			Arrays.sort(directlyChanged, comparator);
//			
//			
//			TableRow[] added = resultTable.getRowsWithOperation(TableRow.OPERATION_ADD).toArray(new TableRow[0]);
//			Arrays.sort(added, comparator);
//
//			TableRow[] deleted = resultTable.getRowsWithOperation(TableRow.OPERATION_DELETE).toArray(new TableRow[0]);
//			Arrays.sort(deleted, comparator);
//
//			writeArray (added, log, printFrameDifferences);
//			writeArray (deleted, log, printFrameDifferences);
//			writeArray (directlyChanged, log, printFrameDifferences);
//			
//			
//
//			// flush the log if this is our native format, otherwise fire the plugin event
//				log.flush();
//			} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

//	   private static void writeArray (TableRow[] array, PrintStream log, boolean printFrameDifferences) {
//			for (int i = 0; i < array.length; i++) {
//				TableRow nextRow = (TableRow) array[i];
//				nextRow.saveToFile(log, printFrameDifferences);
//			}
//		}
//
}
