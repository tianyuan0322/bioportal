package org.ncbo.stanford.aop.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyAclDAO;

@Aspect
public class OntologyMetadataAdvice {
	private static final Log log = LogFactory
			.getLog(OntologyMetadataAdvice.class);

//	private CustomNcboOntologyAclDAO ncboOntologyAclDAO;

	@AfterReturning("execution(* org.ncbo.stanford.manager.metadata.OntologyMetadataManager.fill*(..))")
    public void anotherAspect1() throws Throwable {
    	
		//System.out.println("anotherAspect1 advice ran: ");
		//log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ anotherAspect1 advice ran @@@@@@@@@@@@@@@@@@@@@@@@@@@");
    	
    	
    }
	
//	/**
//	 * @param ncboOntologyAclDAO
//	 *            the ncboOntologyAclDAO to set
//	 */
//	public void setNcboOntologyAclDAO(
//			CustomNcboOntologyAclDAO ncboOntologyAclDAO) {
//		this.ncboOntologyAclDAO = ncboOntologyAclDAO;
//	}
}
