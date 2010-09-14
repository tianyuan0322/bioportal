package org.ncbo.stanford.util.ontologyfile.pathhandler;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.manager.impl.OntologyLoaderLexGridImplTest;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.URIUploadFilePathHandlerImpl;
import static org.junit.Assert.assertTrue;

public class URIUploadFilePathHandlerTest extends AbstractBioPortalTest {

	
	@Test
	public void testURIUpload() throws Exception {
		OntologyBean ontologyBean= createDummyOntolgyBean();
		//String downloadLocation= "http://github.com/cmungall/uberon/raw/master/uberon_edit.obo";
		//String downloadLocation= "http://www.human-phenotype-ontology.org/human-phenotype-ontology.obo.gz";
		String downloadLocation= "http://www.evocontology.org/uploads/Main/evoc_v2.7_obo.tar.gz";

		URI downloadUri= new URI(downloadLocation);
		ontologyBean.setDownloadLocation(downloadLocation);
		FilePathHandler filePathHandler = new URIUploadFilePathHandlerImpl(
				CompressedFileHandlerFactory.createFileHandler(ontologyBean
						.getFormat()), downloadUri);
		List<String> list= filePathHandler.processOntologyFileUpload(ontologyBean);
		assertTrue(list!= null);
		
	}
	
	private OntologyBean createDummyOntolgyBean() {
		OntologyBean bean = new OntologyBean(false);
		bean.setOntologyId(6000);
		// OntologyId gets automatically generated.
		bean.setIsManual(ApplicationConstants.FALSE);
		bean.setFormat(ApplicationConstants.FORMAT_OBO);
		bean.setCodingScheme(OntologyLoaderLexGridImplTest.OBO_CELL_URN_VERSION);
		bean.setDisplayLabel(OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL);
		bean.setUserId(1000);
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
}
