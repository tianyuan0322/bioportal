package org.ncbo.stanford.aop.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyAclDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyAcl;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class OntologyMetadataAdvice {
	private static final Log log = LogFactory
			.getLog(OntologyMetadataAdvice.class);

	@Autowired
	private CustomNcboOntologyAclDAO ncboOntologyAclDAO;

	@SuppressWarnings("unchecked")
	@AfterReturning(pointcut = "addAclPointcut()", returning = "ob")
	public void addAclInfo(OntologyBean ob) throws Throwable {
		List<NcboOntologyAcl> aclList = ncboOntologyAclDAO.findByOntologyId(ob
				.getOntologyId());

		for (NcboOntologyAcl acl : aclList) {
			ob.addUserToAcl(acl.getNcboUser().getId(), acl.getIsOwner());
		}
	}

	@Pointcut("execution(* org.ncbo.stanford.manager.metadata.OntologyMetadataManager.fillInOntologyBeanFromInstance(..))")
	public void addAclPointcut() {
	}
}
