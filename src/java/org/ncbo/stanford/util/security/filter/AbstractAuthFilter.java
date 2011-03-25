package org.ncbo.stanford.util.security.filter;

import java.util.List;

import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.restlet.Context;
import org.restlet.Filter;
import org.restlet.data.Reference;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class AbstractAuthFilter extends Filter {

	protected XMLSerializationService xmlSerializationService;
	protected List<String> exceptionPaths;

	public AbstractAuthFilter(Context context,
			WebApplicationContext springAppContext) {
		super(context);
		xmlSerializationService = (XMLSerializationService) springAppContext
				.getBean("xmlSerializationService",
						XMLSerializationService.class);
	}

	protected boolean isException(Reference ref) {
		String part = ref.getRemainingPart();

		for (String path : exceptionPaths) {
			if (part.startsWith(path)) {
				return true;
			}
		}
		return false;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(xmlSerializationService,
				"xmlSerializationService must be specified");
		Assert.notNull(exceptionPaths, "exceptionPaths must be specified");
	}
}
