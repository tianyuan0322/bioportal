/**
 * 
 */
package org.ncbo.stanford.service.xmlparsing;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.ExportBean;
import org.ncbo.stanford.bean.MetadataFileBean;
import org.ncbo.stanford.bean.OntologyMetadataList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import edu.stanford.smi.protege.util.Assert;

/**
 * @author s.reddy
 * 
 */
public class OntologyXMLMetaDataFileParsingTest extends AbstractBioPortalTest {


	@Test
	public void testParse() {
		try {
			XStream xmlSerializer = new XStream(new DomDriver());

			xmlSerializer.alias("obo_metadata", OntologyMetadataList.class);
			xmlSerializer.alias("ont", MetadataFileBean.class);
			xmlSerializer.alias("export", ExportBean.class);

			
			// xstream.aliasField("ont", OntologyMetadataList.class,
			// "metadataFileBean");

			// set alias name for required fields.
			xmlSerializer.aliasField("time_started",
					OntologyMetadataList.class, "timestarted");
			xmlSerializer.aliasField("ontologies_indexed",
					OntologyMetadataList.class, "ontologiesindexed");
			xmlSerializer.aliasField("ontologies_not_indexed",
					OntologyMetadataList.class, "ontologiesnotindexed");

			xmlSerializer.aliasField("subtypes_of", MetadataFileBean.class,
					"subtypesOf");
			xmlSerializer.aliasField("patho_type", MetadataFileBean.class,
					"pathotype");
			xmlSerializer.aliasField("relevant_organism",
					MetadataFileBean.class, "relevantorganism");
			xmlSerializer.aliasField("xrefs_to", MetadataFileBean.class,
					"xrefsTo");
			xmlSerializer.aliasField("time_completed",
					OntologyMetadataList.class, "timecompletedvalue");

			// set all alias attribute names.
			xmlSerializer.aliasAttribute(MetadataFileBean.class, "id", "id");
			xmlSerializer.aliasAttribute(ExportBean.class, "format", "format");
			xmlSerializer.aliasAttribute(ExportBean.class, "path", "path");
			xmlSerializer.aliasAttribute(ExportBean.class, "md5", "md5");
			xmlSerializer.aliasAttribute(ExportBean.class, "timestamp",
					"timestamp");
			xmlSerializer.aliasAttribute(ExportBean.class, "timegenerated",
					"time_generated");
			xmlSerializer.aliasAttribute(ExportBean.class,
					"timetakentogenerate", "time_taken_to_generate");
			xmlSerializer.aliasAttribute(ExportBean.class, "size", "size");

			xmlSerializer.addImplicitCollection(OntologyMetadataList.class,
					"ont", MetadataFileBean.class);

			xmlSerializer.addImplicitCollection(MetadataFileBean.class,
					"export", ExportBean.class);

			String currentDir = System.getProperty("user.dir");

			FileInputStream input = new FileInputStream(currentDir
					+ "/src/test/ontology_index.xml");
			OntologyMetadataList ontologyMetadataList = (OntologyMetadataList) xmlSerializer
					.fromXML(input);

			System.out.println(ontologyMetadataList.getTimecompletedvalue());
			ArrayList<MetadataFileBean> list = ontologyMetadataList.getOnt();

			// assert list size
			Assert.assertEquals(3, list.size());

			for (MetadataFileBean metadataFileBean : list) {
				ArrayList<ExportBean> list2 = metadataFileBean.getExport();
				System.out.println("download :"
						+ metadataFileBean.getDownload());
				for (ExportBean exportBean : list2) {
					System.out.println("format :" + exportBean.getFormat());
					System.out.println("path :" + exportBean.getPath());
					System.out.println("md5 :" + exportBean.getMd5());
					System.out.println("size :" + exportBean.getSize());
					System.out.println("time gen:"
							+ exportBean.getTimegenerated());
					System.out.println("tome stamp :"
							+ exportBean.getTimestamp());
					System.out.println("time taken gen :"
							+ exportBean.getTimetakentogenerate());
					System.out.println("====================");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
