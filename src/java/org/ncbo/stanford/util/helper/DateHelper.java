package org.ncbo.stanford.util.helper;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Convenience and helper methods relating to the {@link Date} class.
 */
public class DateHelper {

	// Try 4-digit years *before* 2 digit years. This is to circumvent
	// weaknesses in the date parsing subroutines.
	private static final String dateFormats[] = { "yyyy-MM-dd", "dd-MM-yyyy",
			"dd-MMM-yyyy", "dd-MMMM-yyyy", "M/d/yyyy", "MMMM d yyyy",
			"EEEE MMMM d yyyy", "dd-MM-yy", "dd-MMM-yy", "dd-MMMM-yy",
			"M/d/yy", "MMMM d yy", "EEEE MMMM d yy" };
	private static final String DEFAULT_FORMAT = "MM/dd/yyyy";

	/**
	 * DateHelper constructor comment.
	 */
	public DateHelper() {
		super();
	}

	/**
	 * Similar to comapreTo on date except that 'time' is ignored in the
	 * calculation.
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static int compareTo(Date firstDate, Date secondDate) {
		Calendar first = Calendar.getInstance();
		first.setTime(firstDate);
		Calendar second = Calendar.getInstance();
		second.setTime(secondDate);
		int firstDays = first.get(Calendar.YEAR) * 365
				+ first.get(Calendar.DAY_OF_YEAR);
		int secondDays = second.get(Calendar.YEAR) * 365
				+ second.get(Calendar.DAY_OF_YEAR);

		return new Integer(firstDays).compareTo(new Integer(secondDays));
	}

	/**
	 * Recognize a date from a string using a number of different formats.
	 */
	public static Date getDateFrom(String text) {
		if (text == null || text.trim().length() == 0 || text.charAt(0) == 'u'
				|| text.charAt(0) == 'U') {
			return null;
		}

		SimpleDateFormat formatter = new SimpleDateFormat();

		for (int i = 0; i < dateFormats.length; ++i) {
			formatter.applyPattern(dateFormats[i]);

			try {
				Date d = formatter.parse(text);

				if (d != null) {
					// There's a problem with the DateFormat object whereby
					// it will succeed in matching, say, "1/1/1998" to
					// pattern "M/d/yy", and adds "1900" to the year. This
					// results in dates like "1/1/3898". We specifically
					// look for this and correct it here.
					//
					if (dateFormats[i].indexOf("yyyy") > -1) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(d);

						if (cal.get(Calendar.YEAR) < 100) {
							continue; // don't recognize the date string.
						}
					}

					return d;
				}
			} catch (ParseException e) {
			}
		}

		return null;
	}

	public static String getFormattedDate(Date dt, String formatPattern) {
		String formatted = null;

		if (dt != null) {
			formatted = new SimpleDateFormat(formatPattern).format(dt)
					.toString();
		}

		return formatted;
	}

	public static String getFormattedDate(Date dt) {
		return getFormattedDate(dt, DEFAULT_FORMAT);
	}

	// Decodes a time value in "hh:mm:ss" format and returns it as milliseconds
	// since midnight.
	public static synchronized int decodeTime(String s) throws Exception {
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
		f.setTimeZone(utcTimeZone);
		f.setLenient(false);
		ParsePosition p = new ParsePosition(0);
		Date d = f.parse(s, p);
		if (d == null || !isRestOfStringBlank(s, p.getIndex()))
			throw new Exception("Invalid time value (hh:mm:ss): \"" + s + "\".");
		
		return (int) d.getTime();
	}

	// Returns todays date without the time component.
	public static Date getTodaysDate() {
		return truncateDate(new Date());
	}

	// Truncates the time component from a date/time value.
	// (The default time zone is used to determine the begin of the day).
	public static Date truncateDate(Date d) {
		GregorianCalendar gc1 = new GregorianCalendar();
		gc1.clear();
		gc1.setTime(d);
		int year = gc1.get(Calendar.YEAR), month = gc1.get(Calendar.MONTH), day = gc1
				.get(Calendar.DAY_OF_MONTH);
		GregorianCalendar gc2 = new GregorianCalendar(year, month, day);
		
		return gc2.getTime();
	}

	// Returns true if string s is blank from position p to the end.
	public static boolean isRestOfStringBlank(String s, int p) {
		while (p < s.length() && Character.isWhitespace(s.charAt(p)))
			p++;
		
		return p >= s.length();
	}
}