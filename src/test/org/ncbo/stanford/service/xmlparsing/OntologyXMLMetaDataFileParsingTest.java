/**
 * 
 */
package org.ncbo.stanford.service.xmlparsing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.ExportBean;
import org.ncbo.stanford.bean.MetadataFileBean;
import org.ncbo.stanford.bean.OntologyMetadataList;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.springframework.beans.factory.annotation.Autowired;

import com.thoughtworks.xstream.XStream;

/**
 * @author s.reddy
 * 
 */
public class OntologyXMLMetaDataFileParsingTest extends AbstractBioPortalTest {

	@Autowired
	XMLSerializationService xmlSerializationService;

	@Test
	public void testParse() {
		try {
			XStream xmlSerializer = xmlSerializationService.getXmlSerializer();
			System.out.println(xmlSerializer);

			String currentDir = System.getProperty("user.dir");
			BufferedInputStream in = new BufferedInputStream(
					new URL(
							"http://www.berkeleybop.org/ontologies/obo-all/ontology_index.xml")
							.openStream());
			FileOutputStream fos = new FileOutputStream(currentDir
					+ "/src/test/ontology_index.xml");
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

			byte data[] = new byte[1024];
			int i;
			while ((i = in.read(data, 0, 1024)) != -1) {
				fos.write(data, 0, i);
			}
			bout.close();
			in.close();

			FileInputStream input = new FileInputStream(currentDir
					+ "/src/test/ontology_index.xml");

			OntologyMetadataList ontologyMetadataList = (OntologyMetadataList) xmlSerializer
					.fromXML(input);

			List<MetadataFileBean> list = null;/*ontologyMetadataList
					.getMetadataFileList();*/
			// assert list size
			// Assert.assertEquals(175, list.size());
			int j = 1;
			for (MetadataFileBean metadataFileBean : list) {
				ArrayList<ExportBean> list2 = metadataFileBean.getExport();
				System.out.println("ontology :" + j++);
				System.out.println("download :"
						+ metadataFileBean.getDownload());
				List<String> list3 = null;/*(List<String>) metadataFileBean
						.getExtend();*/
				if (list3 != null && !list3.isEmpty()) {

					if (list3.size() > 1) {
						System.out.println("size :" + list3.size());
					}
					for (String str : list3)
						System.out.println("extends :" + str);
				}
				if (list2 != null)
					for (ExportBean exportBean : list2) {
						System.out.println("format :" + exportBean.getFormat());
						System.out.println("path :" + exportBean.getPath());
						System.out.println("md5 :" + exportBean.getMd5());
						System.out.println("size :" + exportBean.getSize());
						/*System.out.println("time gen:"
								+ exportBean.getTimeGenerated());*/
						System.out.println("tome stamp :"
								+ exportBean.getTimestamp());
						/*System.out.println("time taken gen :"
								+ exportBean.getTimeTakenToGenerate());*/
					}
				System.out.println("====================");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
