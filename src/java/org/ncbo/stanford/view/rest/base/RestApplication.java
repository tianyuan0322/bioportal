package org.ncbo.stanford.view.rest.base;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.ext.spring.SpringContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Michael Dorf
 */
public class RestApplication extends Application {

	public RestApplication(Context context) {
	}

	@Override
	public Restlet createRoot() {
		Router router = new Router(getContext());

		SpringContext springContext = new SpringContext(getContext());
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(springContext);
        
		ClassPathResource[] resources = {
				new ClassPathResource("applicationContext-datasources.xml"),
				new ClassPathResource("applicationContext-services.xml"),
				new ClassPathResource("applicationContext-rest.xml"), 
				new ClassPathResource("applicationContext-security.xml") };
		xmlReader.loadBeanDefinitions(resources);		
		springContext.refresh();
		
		RestManager manager = (RestManager)springContext.getBean("restManager");
		manager.init(router);

		return router;
	}

	public void handle(Request request, Response response) {
		try {
			start();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		super.handle(request, response);
	}

}
