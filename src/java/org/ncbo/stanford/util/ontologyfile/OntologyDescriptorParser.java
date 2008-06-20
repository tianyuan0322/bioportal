package org.ncbo.stanford.util.ontologyfile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ncbo.stanford.bean.ContactTypeBean;
import org.ncbo.stanford.bean.MetadataFileBean;
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
//			OntologyDescriptorParser po = new OntologyDescriptorParser();
//			String temp = "";
//			temp = "SEP developers via the PSI and MSI mailing lists|psidev-gps-dev@lists.sourceforge.net, msi-workgroups-ontology@lists.sourceforge.net,  mdorf@lists.sourceforge.net";
//			temp = "George Gkoutos	obo-phe_notype lists.sourceforge.net";
//			temp = "George Gkoutos obo-phe_notype lists.sourceforge.net, obo-anotherthype lists.bioportal.org";
//			List<ContactTypeBean> contacts = po.getContactList(temp);
//			System.out.println(contacts);
//			String filename = po
//					.getFileName("http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/developmental/animal_development/parasite/PLO.ontology");
//			List<MetadataFileBean> ol = po.parseOntologyFile();
//
//			for (MetadataFileBean mfb : ol) {
//				System.out.println(mfb);
//				System.out.println();
//			}
//
//			System.out.println("filename: " + filename);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private static final String LINE_PATTERN = "^(\\w+)(\\t|\\s)*(.*)";
	private static final String UNDERSCORE_LETTER_PATTERN = "_+(\\w)";
	private static final String LAST_SLASH_PATTERN = ".*/(.*)$";
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

			// eof has been reached
			if (temp == null) {
				break;
			}

			// end of one ontology record
			if (StringHelper.isNullOrNullString(temp)) {
				ontologyList.add(mfb);
				mfb = new MetadataFileBean();
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
	 * Parses a contact string in the following format://		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	 * &quot;Person Name|email1@domain.com,email2@domain.com&quot;
	 * and returns the list of ContactTypeBeans for 
	 * each email in the list
	 * </pre>
	 * 
	 * @param the
	 *            value in the above format
	 * @return list of ContactTypeBeans
	 */
	public static List<ContactTypeBean> getContactList(String temp) {
		ArrayList<ContactTypeBean> contactList = new ArrayList<ContactTypeBean>(
				1);
		String name = "";
		String email = "";

		try {
			if (!StringHelper.isNullOrNullString(temp)) {
				String contactPattern = "^([\\w\\s]+)([\\s\\t\\|]+)(.*)";
				Pattern pat = Pattern.compile(contactPattern);
				Matcher m = pat.matcher(temp);
				m.find();
				
				try {
					name = m.group(1);
				} catch (Exception e) {
					name = "";
				}
				
				String rest = m.group(3);

				if (rest != null) {
					if (rest.indexOf('@') < 0) {
						rest = rest.replaceFirst("[\\s\\t]", "@");
					}

					rest = rest.replaceFirst("[,\\s\\t\\\r]+", "###");

					pat = Pattern.compile("^(.*)###(.*)$");
					m = pat.matcher(rest);
					m.find();

					if (m.matches()) {
						email = m.group(1);
					} else {
						email = rest;
					}
				}

				if (!StringHelper.isNullOrNullString(email)) {
					ContactTypeBean contact = new ContactTypeBean();
					
					if (StringHelper.isNullOrNullString(name)) {
						name = email;
					}
					
					contact.setName(name);
					contact.setEmail(email);
					contactList.add(contact);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			contactList = new ArrayList<ContactTypeBean>(1);
		}

		return contactList;
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