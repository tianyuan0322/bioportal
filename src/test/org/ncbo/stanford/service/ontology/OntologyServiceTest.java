/**
 * 
 */
package org.ncbo.stanford.service.ontology;

import java.util.List;

import junit.framework.TestCase;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyVersionDAO;
import org.ncbo.stanford.manager.impl.OntologyAndConceptManagerImpl;
import org.ncbo.stanford.service.ontology.impl.OntologyServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;


/**
 * @author nickgriffith
 *
 */
public class OntologyServiceTest extends AbstractDependencyInjectionSpringContextTests{
	
	protected String[] getConfigLocations() {
		return new String[] {"classpath:applicationContext-datasources.xml",
			"classpath:applicationContext-services.xml",
			"classpath:applicationContext-rest.xml",
			"classpath:applicationContext-security.xml"};
	}

	
	 public void testfindLatestOntologyVersions(){
		
			OntologyService service = (OntologyService)applicationContext.getBean("ontologyService", OntologyService.class);
		 List<OntologyBean> ontologies = service.findLatestOntologyVersions();
		 
		 for(OntologyBean ontology : ontologies){
			 
			 System.out.println(ontology.toString());
		 }
		 
	 }
	 
	 public void testUploadOntologies(){

		 
	 }
	 
	 

}
