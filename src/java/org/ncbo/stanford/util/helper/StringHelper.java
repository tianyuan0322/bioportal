/**
 * Package Declaration
 */

package org.ncbo.stanford.util.helper;

/**
 * Import Statements
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * This gives utility methods related to String object
 * 
 * @author Michael Dorf
 * 
 */
public abstract class StringHelper {
	private final static String EMPTY_TXT = "";
	private final static String QUOTE_TXT = "\"";

	/**
	 * Formats the number
	 * 
	 * @param zeroPadded
	 * @param width
	 * @param value
	 * @return
	 */
	final public static String format(boolean zeroPadded, int width, int value) {
		return format(zeroPadded, width, String.valueOf(value), true);
	}

	/**
	 * Formats the string
	 * 
	 * @param zeroPadded
	 * @param width
	 * @param str
	 * @return
	 */
	final public static String format(boolean zeroPadded, int width, String str) {
		return format(zeroPadded, width, str, false);
	}

	/**
	 * Formats
	 * 
	 * @param zeroPadded
	 * @param width
	 * @param str
	 * @param paddingInTheFront
	 * @return
	 */
	final public static String format(boolean zeroPadded, int width,
			String str, boolean paddingInTheFront) {

		StringBuffer buffer = new StringBuffer(100);

		if (null != str && !EMPTY_TXT.equals(str)) {
			char paddingString = (zeroPadded ? '0' : ' ');

			if (!paddingInTheFront) {
				buffer.append(str.substring(Math.max(0, str.length() - width)));
			}

			for (int i = str.length(); i < width; i++) {
				buffer.append(paddingString);
			}

			if (paddingInTheFront) {
				buffer.append(str.substring(Math.max(0, str.length() - width)));
			}
		}

		return buffer.toString();
	}

	/**
	 * formats to float
	 * 
	 * @param width
	 * @param value
	 * @return
	 */
	final public static String formatToFloat(int width, int value) {
		return format(true, width, value) + ".00";
	}

	/**
	 * formats to float with zero padding
	 * 
	 * @param width
	 * @param value
	 * @return
	 */
	final public static String formatToFloatWithZeroPadding(int width, int value) {
		return format(true, width, value) + ".00";
	}

	/**
	 * formats to float without zero padding
	 * 
	 * @param width
	 * @param value
	 * @return
	 */
	final public static String formatToFloatWithoutZeroPadding(int width,
			int value) {
		return format(false, width, value) + ".00";
	}

	/**
	 * Formats
	 * 
	 * @param width
	 * @param value
	 * @return
	 */
	final public static String format(int width, int value) {
		return format(false, width, value);
	}

	/**
	 * Converts into upper case
	 * 
	 * @param value
	 * @return
	 */
	public static String toUpperCase(String value) {
		if (value == null) {
			return null;
		}

		return value.toUpperCase();
	}

	public static String escapeSpaces(String str) {
		return (str == null) ? null : str.trim().replaceAll("[\\s\\t]+",
				"\\\\ ");
	}

	/**
	 * Remove all spaces from the string passed as a parameter
	 * 
	 * @param value
	 *            string to trim
	 * @return the value without any spaces character
	 */
	public static String removeSpaces(String str) {
		return (str == null) ? null : str.trim().replaceAll("[\\s\\t]+",
				EMPTY_TXT);
	}

	/**
	 * Remove the unnecessary space characters from the string passed as a
	 * parameter.
	 * 
	 * Example: " aa aaa " --> "aa aaa"
	 * 
	 * Returns null if the parameter is null or if the trimed string is empty.
	 * 
	 * @param value
	 *            string to trim
	 * @return the value without any unnecessary space character
	 */
	public static String trimSpaces(String value) {
		int index = 0;
		char current = 0;
		boolean triming = true;
		boolean appending = false;
		StringBuffer buffer = null;
		if (value == null) {
			return null;
		}

		buffer = new StringBuffer(value.length());

		while (index < value.length()) {
			current = value.charAt(index);

			if (current == ' ') {
				triming = true;
			} else {
				if (triming && appending) {
					buffer.append(' ');
				}

				buffer.append(current);
				triming = false;
				appending = true;
			}

			++index;
		}

		if (buffer.length() == 0) {
			return null;
		}

		return buffer.toString();
	}

	public static String doubleQuoteString(String str) {
		return QUOTE_TXT + str + QUOTE_TXT;
	}

	/**
	 * removes space, removes NonAlphaCharacters, converts to lowercase
	 * 
	 * @param str
	 * @return String
	 */
	public static String toSimpleString(String str) {
		String newStr = null;

		if (null == str) {
			return str;
		}

		newStr = str;
		newStr = newStr.toLowerCase();
		newStr = removeNonAlphaCharacters(newStr);
		newStr = newStr.trim();

		return newStr;
	}

	/**
	 * Removes the given suffix from String
	 * 
	 * @param value
	 * @param suffix
	 * @return String
	 */
	public static String removeSuffix(String value, String suffix) {
		int index = value.indexOf(suffix);
		return index < 0 ? value : value.substring(0, index);
	}

	/**
	 * Removes the given prefix from String
	 * 
	 * @param value
	 * @param prefix
	 * @return String
	 */
	public static String removePrefix(String value, String prefix) {
		return value.startsWith(prefix) ? value.substring(prefix.length())
				: value;
	}

	/**
	 * replaces the string with passed value
	 * 
	 * @param str
	 * @param replaceWhat
	 * @param withWhat
	 * @return
	 */
	public static String replaceAll(String str, String replaceWhat,
			String withWhat) {
		if (null != str) {

			int foundAtIndex = str.indexOf(replaceWhat);

			while (foundAtIndex >= 0) {
				str = str.substring(0, foundAtIndex) + withWhat
						+ str.substring(foundAtIndex + replaceWhat.length());
				foundAtIndex = str.indexOf(replaceWhat);
			}
		}

		return str;
	}

	/**
	 * splits the string with passed delimeter
	 * 
	 * @param str
	 * @param delimiter
	 * @return String[]
	 */
	public static String[] split(String str, String delimiter) {
		if (str != null) {
			StringTokenizer tokenizer = new StringTokenizer(str, delimiter);
			String[] tokens = new String[tokenizer.countTokens()];
			int i = 0;

			while (tokenizer.hasMoreElements()) {
				tokens[i++] = tokenizer.nextToken();
			}

			return tokens;
		}

		return null;
	}

	/**
	 * 
	 * @param str
	 * @param delimiter
	 * @param considerSpaceAsToken -
	 *            StringTokenizer by default doesnot consider empty string as a
	 *            token. pass considerSpaceAsToken = true for empty string to be
	 *            considered as a token
	 * @return
	 */
	public static String[] split(String str, String delimiter,
			boolean considerSpaceAsToken) {
		if (null != str) {
			if (considerSpaceAsToken) {
				if (str.startsWith(delimiter)) {
					str = ' ' + delimiter;
				}

				if (str.endsWith(delimiter)) {
					str = str + ' ';
				}

				str = replaceAll(str, delimiter + delimiter, delimiter + ' '
						+ delimiter);
			}

			return split(str, delimiter);
		}

		return null;
	}

	/**
	 * compares 2 String Arrays
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean compareTwoStringArrays(String str1[], String str2[]) {
		int j = 0;
		Collections.sort(Arrays.asList(str1));
		Collections.sort(Arrays.asList(str2));

		if (str1.length == str2.length) {
			for (int i = 0; i < str1.length; i++) {
				if (!str1[i].equals(str2[j])) {
					j = 1;
					break;
				}
				j++;
			}

			return !(j == 1);
		} else {
			return false;
		}
	}

	/**
	 * checks for string array contains all null in it
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isStringArrayContainsAllNull(String[] str) {
		int flag = 0;

		if (null != str) {
			for (int i = 0; i < str.length; i++) {
				if (str[i] != null) {
					flag = 1;
				}
			}

			return !(flag == 1);
		} else {
			return true;
		}
	}

	/**
	 * checks for null or null string
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrNullString(String str) {
		return (null == str) || EMPTY_TXT.equals(str.trim());
	}

	/**
	 * Undouble all the chs in a string.
	 */
	public static String undoubleChars(String str, char ch) {
		StringBuffer strbuf = new StringBuffer();
		char prevChar = (char) 0;

		for (char curChar : str.toCharArray()) {
			if (curChar != ch) {
				strbuf.append(curChar);
				prevChar = curChar;
			} else if (curChar == ch) {
				if (prevChar == ch) {
					strbuf.append(ch);
					prevChar = (char) 0;
				} else {
					prevChar = ch;
				}
			}
		}

		return (strbuf.toString());
	}

	/**
	 * Removes leading and trailing double-quotes from a string (only if both
	 * are present). The rest of double-quotes remain intact.
	 */
	public static String unquote(String string) {
		if (string == null)
			return (null);
		if (string.length() <= 0)
			return ("");
		if (string.length() == 1)
			return (string); // You can't unquote a single quote!
		if (string.charAt(0) == '"'
				&& string.charAt(string.length() - 1) == '"') {
			return (string.substring(1, string.length() - 1));
		} else {
			return (string);
		}
	}

	/**
	 * removes NonAlphaCharacters
	 * 
	 * @param str
	 * @return
	 */
	private static String removeNonAlphaCharacters(String str) {
		char[] chars = str.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			char oldChar = chars[i];
			char newChar = oldChar;

			if ((oldChar == '-') || (oldChar == '/') || (oldChar == '\\')
					|| (oldChar == '&') || (oldChar == '_') || (oldChar == '+')
					|| (oldChar == '=') || (oldChar == ':') || (oldChar == '.')
					|| (oldChar == ',') || (oldChar == '\'')
					|| (oldChar == '*') || (oldChar == '|') || (oldChar == '(')
					|| (oldChar == ')')) {
				newChar = ' ';
			}

			chars[i] = newChar;
		}

		return new String(chars);
	}

	/**
	 * Protected default constructor
	 * 
	 */
	protected StringHelper() {
		// do nothing
	}
}