package org.ncbo.stanford.view.rest.restlet;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class OntologyRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(OntologyRestlet.class);

	private OntologyService ontologyService;

	@Override
	public void handle(Request request, Response response) {
		List<OntologyBean> ontList = ontologyService.findLatestOntologyVersions();
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("OntologyBean", OntologyBean.class);
		response.setEntity(xstream.toXML(ontList), MediaType.APPLICATION_XML);
	}

	/**
	 * @return the ontologyService
	 */
	public OntologyService getOntologyService() {
		return ontologyService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}
}
