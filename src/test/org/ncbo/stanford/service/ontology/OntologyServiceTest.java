/**
 * 
 */
package org.ncbo.stanford.service.ontology;

import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;

/**
 * @author nickgriffith
 * 
 */
public class OntologyServiceTest extends AbstractBioPortalTest {

	public void testfindLatestOntologyVersions() {

		OntologyService service = (OntologyService) applicationContext.getBean(
				"ontologyService", OntologyService.class);
		List<OntologyBean> ontologies = service.findLatestOntologyVersions();

		for (OntologyBean ontology : ontologies) {

			System.out.println(ontology.toString());
		}

	}

	public void testUploadOntologies() {

	}

}
