package org.ncbo.stanford.util.ontologyfile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ncbo.stanford.bean.ContactTypeBean;
import org.ncbo.stanford.bean.MetadataFileBean;
import org.ncbo.stanford.exception.InvalidDataException;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.helper.reflection.ReflectionHelper;

/**
 * <pre>
 * Parses the ontology descriptor file (ontologies.txt)
 * and uses reflection to populate the list of 
 * MetatdataFileBean classes, each corresponding to 
 * one ontology entry in the descriptor file. The 
 * labels in the file are matched to the properties in
 * the MetadataFileBean class. Properties that don't
 * match any label are ignored. The property names must 
 * follow the below format:
 * 
 * Label in file: 					description
 * MetadataFileBean propety name:	description
 * 
 * Label in file: 					alternate_download
 * MetadataFileBean propety name:	alternateDownload
 * 
 * Label in file: 					my_fancy_label
 * MetadataFileBean propety name:	myFancyLabel
 * </pre>
 * 
 * @author Michael Dorf
 * 
 */
public class OntologyDescriptorParser {

//	public static void main(String[] args) {
//		try {
//			OntologyDescriptorParser po = new OntologyDescriptorParser("");
//			String temp = "";
//			temp = "Paul Schofield	PS	mole.bio.cam.ac.uk";
//
//			ContactTypeBean c = getContact(temp, "");
//			System.out.println(c);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private static final String LINE_PATTERN = "^(\\w+)(\\t|\\s)*(.*)";
	private static final String UNDERSCORE_LETTER_PATTERN = "_+(\\w)";
	private static final String LAST_SLASH_PATTERN = ".*/(.*)$";
	private static final String VALUE_SEPARATOR = "|";
	private String descriptorFilePath = null;

	public OntologyDescriptorParser(String descriptorFilePath) {
		this.descriptorFilePath = descriptorFilePath;
	}

	/**
	 * Parses ontology file and populates the list of MetadataFileBeans using
	 * reflection
	 * 
	 * @return list of populated MetadataFileBeans
	 * @throws IOException
	 */
	public List<MetadataFileBean> parseOntologyFile() throws IOException {
		List<MetadataFileBean> ontologyList = new ArrayList<MetadataFileBean>(1);
		RandomAccessFile rf = new RandomAccessFile(descriptorFilePath, "r");
		MetadataFileBean mfb = new MetadataFileBean();

		while (true) {
			String temp = rf.readLine();

			// end of one ontology record
			if (StringHelper.isNullOrNullString(temp)) {
				ontologyList.add(mfb);
				mfb = new MetadataFileBean();

				// eof has been reached
				if (temp == null) {
					break;
				}
			} else {
				Pattern pat = Pattern.compile(LINE_PATTERN);
				Matcher m = pat.matcher(temp);
				m.find();
				String label = m.group(1);
				String value = m.group(3);

				try {
					// convert label to property and set
					// its value using Reflection
					ReflectionHelper.setProperty(mfb, labelToProperty(label),
							value.trim());
				} catch (RuntimeException e) {
					// ignore error, property doesn't exist
				}
			}
		}

		rf.close();

		return ontologyList;
	}

	/**
	 * <pre>
	 * Parses a contact string in the following format:
	 * &quot;PersonName	email	domain.com&quot;
	 * and returns the ContactTypeBean for the contact
	 * </pre>
	 * 
	 * @param the
	 *            value in the above format
	 * @return ContactTypeBean
	 * @throws InvalidDataException
	 */
	public static ContactTypeBean getContact(String contactString,
			String oboFoundryId) throws InvalidDataException {
		ContactTypeBean contact = new ContactTypeBean();
		String emailName = "";
		String emailDomain = "";
		String email = "";
		String name = "";
		int i = 0;

		for (i = contactString.length() - 1; i > 0; i--) {
			if (contactString.charAt(i) == ' '
					|| contactString.charAt(i) == '	'
					|| contactString.charAt(i) == '|') {
				emailDomain = contactString.substring(i).trim();
				contactString = contactString.substring(0, i);
				break;
			}
		}

		for (i = contactString.length() - 1; i > 0; i--) {
			if (contactString.charAt(i) == ' '
					|| contactString.charAt(i) == '	'
					|| contactString.charAt(i) == '|') {
				emailName = contactString.substring(i).trim();
				contactString = contactString.substring(0, i);
				break;
			}
		}

		if (StringHelper.isNullOrNullString(emailName)
				|| StringHelper.isNullOrNullString(emailDomain)) {
			throw new InvalidDataException("Provided contact [" + contactString
					+ "] is invalid for ontology: " + oboFoundryId);
		}

		name = contactString = contactString.substring(0, i);
		email = emailName + "@" + emailDomain;

		if (StringHelper.isNullOrNullString(name)) {
			name = email;
		}

		contact.setName(name);
		contact.setEmail(email);

		return contact;
	}

	/**
	 * Parses out the publication url from the descriptor file string
	 * 
	 * @param publicationString
	 * @return
	 */
	public static String getPublication(String publicationString) {
		return (StringHelper.isNullOrNullString(publicationString)) ? null
				: publicationString;
	}

	/**
	 * Parses out the documentation url from the descriptor file string
	 * 
	 * @param documentationString
	 * @return
	 */
	public static String getDocumentation(String documentationString) {
		String documentation = null;

		if (!StringHelper.isNullOrNullString(documentationString)
				&& documentationString.contains(VALUE_SEPARATOR)) {
			documentation = documentationString.substring(documentationString
					.indexOf(VALUE_SEPARATOR) + 1);
		}

		return documentation;
	}

	/**
	 * Parses out the homepage url from the descriptor file string
	 * 
	 * @param homepageString
	 * @return
	 */
	public static String getHomepage(String homepageString) {
		String homepage = null;

		if (!StringHelper.isNullOrNullString(homepageString)
				&& homepageString.contains(VALUE_SEPARATOR)) {
			homepage = homepageString.substring(homepageString
					.indexOf(VALUE_SEPARATOR) + 1);
		}

		return homepage;
	}

	/**
	 * Parses out the filename from a given url string
	 * 
	 * @param full
	 *            url
	 * @return substirng of the url representing the filename
	 */
	public static String getFileName(String url) {
		String t = "";

		try {
			Pattern pat = Pattern.compile(LAST_SLASH_PATTERN);
			Matcher m = pat.matcher(url);
			m.find();
			t = m.group(1);
		} catch (Exception e) {
			t = "";
		}

		return t;
	}

	/**
	 * Converts string from format "my_first_hello_world" to
	 * "myFirstHelloWorld". All existing letters in the label are converted to
	 * lower-case.
	 * 
	 * @param label
	 *            to convert
	 * @return converted label
	 */
	public static String labelToProperty(String label) {
		StringBuffer sb = new StringBuffer();
		Pattern pat = Pattern.compile(UNDERSCORE_LETTER_PATTERN);
		Matcher m = pat.matcher(label.toLowerCase());

		while (m.find()) {
			try {
				m.appendReplacement(sb, m.group(1).toUpperCase());
			} catch (RuntimeException e) {
				// igonore exception, no match found
			}
		}

		m.appendTail(sb);

		return sb.toString();
	}
}