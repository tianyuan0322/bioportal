package org.ncbo.stanford.util.security.filter;

import java.util.Set;
import java.util.TreeSet;

import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;
import org.restlet.routing.Filter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class AbstractAuthFilter extends Filter implements InitializingBean {

	protected XMLSerializationService xmlSerializationService;
	private Set<String> exceptionPaths;
	private Set<String> exceptionReferrers;

	@SuppressWarnings("unchecked")
	public AbstractAuthFilter(Context context,
			WebApplicationContext springAppContext) {
		super(context);
		xmlSerializationService = (XMLSerializationService) springAppContext
				.getBean("xmlSerializationService",
						XMLSerializationService.class);
		exceptionPaths = new TreeSet<String>((TreeSet<String>) springAppContext.getBean(
				"authenticationExceptionPaths", TreeSet.class));
		exceptionReferrers = (TreeSet<String>) springAppContext.getBean(
				"authenticationExceptionReferrers", TreeSet.class);		
	}

	protected void addExceptionPath(String path) {
		exceptionPaths.add(path);
	}
	
	protected void addExceptionPaths(Set<String> paths) {
		exceptionPaths.addAll(paths);
	}

	protected boolean isException(Request request) {
		Reference resourceRef = request.getResourceRef();
		Reference referrerRef = request.getReferrerRef();	
		
		String part = resourceRef.getRemainingPart();		

		for (String path : exceptionPaths) {
			if (part.startsWith(path)) {
				return true;
			}
		}
		
		if (referrerRef != null) {
			String referrer = referrerRef.toString();
			
			for (String path : exceptionReferrers) {
				if (referrer.startsWith(path)) {
					return true;
				}
			}
		}	
		
		return false;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(xmlSerializationService,
				"xmlSerializationService must be specified");
		Assert.notNull(exceptionPaths, "exceptionPaths must be specified");
		Assert.notNull(exceptionReferrers, "exceptionReferrers must be specified");
	}
}
