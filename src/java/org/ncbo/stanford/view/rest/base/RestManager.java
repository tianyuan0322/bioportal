package org.ncbo.stanford.view.rest.base;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * @author Michael Dorf
 */
public class RestManager {

	private Map<String, Restlet> resourceMappings = new HashMap<String, Restlet>();

	public void init(Router router) {
		for (String key : resourceMappings.keySet()) {
			if (key.endsWith("/")) {
				router.attach(key.substring(0, key.length() - 1),
						resourceMappings.get(key));
				router.attach(key, resourceMappings.get(key));
			} else {
				router.attach(key, resourceMappings.get(key));
				router.attach(key + "/", resourceMappings.get(key));
			}
		}
	}

	public void setResourceMappings(HashMap<String, Restlet> resourceMappings) {
		this.resourceMappings = resourceMappings;
	}
}
