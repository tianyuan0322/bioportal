package org.ncbo.stanford.util.loader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.manager.impl.OntologyLoaderLexGridImplTest;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.ontologyfile.OntologyDescriptorParser;


public class LoaderUtilsTest extends AbstractBioPortalTest {

	
	@Test
	public void testStoreMd5File() throws Exception {
		OntologyBean ontologyBean= createDummyOntolgyBean();
		LoaderUtils.storeMd5ToFile(ontologyBean);		
	}
	
	@Test
	public void testStoreMd5InPullPath() throws Exception {
		OntologyBean ontologyBean= createDummyOntolgyBean();
		File inputFile = new File("C:/apps/bmir.apps/bioportal_resources/uploads/5070/7/mosquito_insecticide_resistance.obo");

		LoaderUtils.storeMd5InPullPath(ontologyBean, inputFile);		
	}
	
	@Test
	public void testStoreMd5FileUsingFile() throws Exception {
		OntologyBean ontologyBean= createDummyOntolgyBean();
		File inputFile = new File("C:/apps/bmir.apps/bioportal_resources/uploads/5070/7/mosquito_insecticide_resistance.obo");

		LoaderUtils.storeMd5ToFile(ontologyBean, inputFile);		
	}
		
	@Test
	public void testFetchMd5File() throws Exception {
		OntologyBean ontologyBean= createDummyOntolgyBean();
		String str= LoaderUtils.fetchMd5FromFile(ontologyBean);	
		System.out.println("MD5 value= "+str);
		assertTrue(str!= null);
		
	}
	
	@Test
	public void testComputeMd5() throws Exception {
		OntologyBean ontologyBean= createDummyOntolgyBean();
		String str= LoaderUtils.computeMD5(ontologyBean);	
		System.out.println("MD5 value= "+str);
		str= LoaderUtils.computeMD5(ontologyBean);	
		System.out.println("MD5 value= "+str);
		str= LoaderUtils.computeMD5(ontologyBean);	
		System.out.println("MD5 value= "+str);
		assertTrue(str!= null);
		
	}
	
	@Test
	public void testComputeMd5FromFile() throws Exception {
		//File inputFile = new File(OntologyLoaderLexGridImplTest.OBO_CELL_PATHNAME);
		File inputFile = new File("C:/apps/bmir.apps/bioportal_resources/uploads/5070/7/mosquito_insecticide_resistance.obo");
		String str= LoaderUtils.computeMD5(inputFile);	
		System.out.println("MD5 value= "+str);
		inputFile = new File("C:/apps/bmir.apps/bioportal_resources/cvscheckout/obofoundry/obo/ontology/phenotype/mosquito_insecticide_resistance.obo");
		str= LoaderUtils.computeMD5(inputFile);	
		System.out.println("MD5 value= "+str);
		OntologyBean ontologyBean= createDummyOntolgyBean();
		str= LoaderUtils.computeMD5(ontologyBean);	
		System.out.println("MD5 value= "+str);
	}
	
	private OntologyBean createDummyOntolgyBean() {
		OntologyBean bean = new OntologyBean(false);
		bean.setOntologyId(6000);
		// OntologyId gets automatically generated.
		bean.setIsManual(ApplicationConstants.FALSE);
		bean.setFormat(ApplicationConstants.FORMAT_OBO);
		bean.setCodingScheme(OntologyLoaderLexGridImplTest.OBO_CELL_URN_VERSION);
		bean.setDisplayLabel(OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL);
		bean.setDownloadLocation("http://github.com/cmungall/uberon/raw/master/uberon_edit.obo");
		bean.setDownloadLocation("http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/phenotype/mosquito_insecticide_resistance.obo");
		bean.setUserIds(Arrays.asList(1000));
		bean.setVersionNumber("1.0");
		bean.setInternalVersionNumber(1);
		bean.setStatusId(StatusEnum.STATUS_WAITING.getStatus());
		bean.setVersionStatus("pre-production");
		bean.setIsRemote(new Byte("0"));
		bean.setIsReviewed(new Byte("1"));
		bean.setDateCreated(new Date());
		bean.setDateReleased(new Date());
		bean.setContactEmail("obo@email.com");
		bean.setContactName("OBO Name");
		bean.setIsFoundry(new Byte("0"));
		return bean;
	}
	
	
	@Test
	public void testFileNameExtraction() {
		String url= "http://palea.cgrb.oregonstate.edu/viewsvn/Poc/trunk/ontology/OBO_format/po_anatomy.obo?view=co";
		String filename= OntologyDescriptorParser.getFileName(url);
		System.out.println(filename);
		url= "http://palea.cgrb.oregonstate.edu/viewsvn/Poc/trunk/ontology/OBO_format/po_anatomy.obo";
		filename= OntologyDescriptorParser.getFileName(url);
		System.out.println(filename);
	}
	
	@Test
	public void testGetRemoteFileSize() {
		String url= "http://obo.svn.sourceforge.net/viewvc/*checkout*/obo/fma-conversion/trunk/fma2_obo.obo";
		int size=0;
		long date_long=0;
		try {
		 size= LoaderUtils.getContentLength(url);
		 date_long= LoaderUtils.getLastModifiedDate(url);
		}catch (Exception ex) {
			
		}
		System.out.println("Size of file at url= "+url+" is : "+ size);
		System.out.println("Date of file at url= "+url+" is : "+ new Date(date_long));
		
	}	
	
	@Test
	public void testUrlExists() {
		String url= "http://obo.svn.sourceforge.net/viewvc/*checkout*/obo/fma-conversion/trunk/fma2_obo.obo";
		boolean exists;			
		exists= LoaderUtils.isValidDownloadLocation(url);			
		assertTrue(exists);
		url= "http://brebiou.cshl.edu/viewcvs/Poc/ontology/collaborators_ontology/gramene/temporal_gramene.obo";
		exists= LoaderUtils.isValidDownloadLocation(url);			
		assertFalse(exists);
		url= "http://palea.cgrb.oregonstate.edu/viewsvn/Poc/trunk/ontology/OBO_format/po_anatomy.obo?view=co";
		exists= LoaderUtils.isValidDownloadLocation(url);	
		assertTrue(exists);
		url= "http://github.com/cmungall/uberon/raw/master/uberon.obo";
		exists= LoaderUtils.isValidDownloadLocation(url);			
		assertTrue(exists);
		url= "ftp://rgd.mcw.edu/pub/data_release/pathway.obo";
		exists= LoaderUtils.isValidDownloadLocation(url);			
		assertFalse(exists);
		url= "ftp://ftp.pir.georgetown.edu/databases/ontology/pro_obo/pro.obo";
		exists= LoaderUtils.isValidDownloadLocation(url);			
		assertTrue(exists);
		
		
	}		
	
	@Test
	public void testHasOntologyBeenRefreshed() throws Exception {
		OntologyBean ontologyBean= createDummyOntolgyBean();
		String url_location= "http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/phenotype/mosquito_insecticide_resistance.obo";
		boolean isRefreshed= LoaderUtils.hasDownloadLocationContentBeenUpdated(url_location, ontologyBean);
		assertFalse(isRefreshed);
	}
	
	@Test
	public void testGetContent() {	
		String url= "http://github.com/cmungall/uberon/raw/master/uberon.obo";
		try {
		BufferedInputStream in= new BufferedInputStream(LoaderUtils.getInputStream(url));
		byte data[] = new byte[ApplicationConstants.BUFFER_SIZE];
		int count;
		while ((count = in.read(data, 0, ApplicationConstants.BUFFER_SIZE)) != -1) {
			System.out.print(new String(data, 0, count));
		}
		
		in.close();
		} catch (Exception ex) {
			
		}
	}
	
}
