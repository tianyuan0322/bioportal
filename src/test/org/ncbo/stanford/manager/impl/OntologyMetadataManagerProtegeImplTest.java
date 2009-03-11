package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.manager.metadata.impl.OntologyMetadataManagerProtegeImpl;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests ontology metadata related operations using the
 * OntologyMetadataManagerProtegeImpl
 * 
 * @author Csongor Nyulas
 */
public class OntologyMetadataManagerProtegeImplTest extends AbstractBioPortalTest {
	
	private static int ID_ONTOLOGY_VERSION_1 = 98765;
	private static int ID_ONTOLOGY_1 = 1143;
	
	@Autowired
	OntologyMetadataManagerProtegeImpl ontMetadataManagerProtege;


	@Test
	public void testSaveOntologyMetadata() {
		System.out.println("Starting testSaveOntologyMetadata");
		
		try {
			OntologyBean ontologyBean = createOntolgyBeanBase();
			ontologyBean.setId(ID_ONTOLOGY_VERSION_1);
			ontologyBean.setOntologyId(ID_ONTOLOGY_1);
			ontMetadataManagerProtege.saveOntology(ontologyBean);
		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			fail("save ontology metadata failed: " + e.getMessage());
		}
		
		System.out.println("End testSaveOntologyMetadata");
	}
	
	@Test
	public void testFindOntologyMetadataById() {
		System.out.println("Starting testFindOntologyMetadataById");
		
		try {
			OntologyBean ob = ontMetadataManagerProtege.findOntologyById(ID_ONTOLOGY_VERSION_1);
			
			System.out.println("OntologyBean: " + ob);
			assertNotNull("Retrieving ontology metadata by ID has failed", ob);
			
			System.out.println("Date Released (OMV:creationDate): " + ob.getDateReleased());
			System.out.println("Date Created (metadata:uploadDate): " + ob.getDateCreated());
		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			fail("find ontology metadata by ID failed: " + e.getMessage());
		}
		
		System.out.println("End testFindOntologyMetadataById");
	}
	
	

	private OntologyBean createOntolgyBeanBase() {
		OntologyBean bean = new OntologyBean();
		// bean.setOntologyId(3000);
		// OntologyId gets automatically generated.
		bean.setIsManual(ApplicationConstants.FALSE);
		bean.setFormat(ApplicationConstants.FORMAT_OWL);
		bean.setCodingScheme(null);
		bean.setDisplayLabel("BioPortal Metadata Ontology");
		bean.setUserId(1000);
		bean.setVersionNumber("1.0");
		bean.setStatusId(StatusEnum.STATUS_WAITING.getStatus());
		bean.setVersionStatus("pre-production");
		bean.setIsRemote(new Byte("1"));
		bean.setIsReviewed(new Byte("1"));
		bean.setDateCreated(new Date());
		bean.setDateReleased(new Date());
		bean.setContactEmail("obo@email.com");
		bean.setContactName("Max Mustermann");
		bean.setIsFoundry(new Byte("0"));
		return bean;
	}

}