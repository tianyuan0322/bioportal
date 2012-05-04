package org.ncbo.stanford.util;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.logging.Log;

public class LoggingUtils {

	/**
	 * This method will log progress as you do process every class in an
	 * ontology. This could be used, for example, to log progress calculating
	 * metrics or indexing all classes in an ontology. The method will
	 * automatically figure out how often to log to the console based on how
	 * many classes you are processing.
	 * 
	 * @param classCount
	 * @param origProgressCount
	 * @param ontologyVersionId
	 * @param message
	 * @param log
	 */
	public static void logProgress(int classCount,
			MutableInt origProgressCount, int ontologyVersionId,
			String message, Log log) {
		int progressCount = origProgressCount.toInteger();

		// Possible intervals
		int[] intervals = new int[] { 1000, 100, 50, 10, 1 };

		// Find the interval that works with our ont size
		int interval = 0;
		int position = 0;
		while (interval == 0) {
			int testCalc = classCount / intervals[position];

			// Second test avoids endless loop
			if (testCalc > 0 && classCount / intervals[position] >= 5
					|| position == intervals.length - 1) {
				interval = intervals[position];
			}
			position += 1;
		}

		// Only output on the appropriate interval
		if (progressCount % interval == 0) {
			log.info(progressCount + "/" + classCount + " | Ontology Version: "
					+ ontologyVersionId + " | " + message + " ");
		}

		// Mutate the int to update it in the calling class
		origProgressCount.add(1);
	}

}
