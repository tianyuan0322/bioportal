package org.ncbo.stanford.service.xml.impl;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.bean.GroupBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.bean.SubscriptionsBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.acl.OntologyAcl;
import org.ncbo.stanford.bean.acl.UserAcl;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.bean.concept.PropertyBean;
import org.ncbo.stanford.bean.http.HttpInputStreamWrapper;
import org.ncbo.stanford.bean.logging.UsageLoggingBean;
import org.ncbo.stanford.bean.mapping.MappingBean;
import org.ncbo.stanford.bean.mapping.MappingConceptStatsBean;
import org.ncbo.stanford.bean.mapping.MappingOntologyStatsBean;
import org.ncbo.stanford.bean.mapping.MappingUserStatsBean;
import org.ncbo.stanford.bean.notes.AppliesToBean;
import org.ncbo.stanford.bean.notes.NoteBean;
import org.ncbo.stanford.bean.notes.ProposalNewRelationshipBean;
import org.ncbo.stanford.bean.notes.ProposalNewTermBean;
import org.ncbo.stanford.bean.notes.ProposalPropertyValueChangeBean;
import org.ncbo.stanford.bean.response.AbstractResponseBean;
import org.ncbo.stanford.bean.response.ErrorBean;
import org.ncbo.stanford.bean.response.ErrorStatusBean;
import org.ncbo.stanford.bean.response.SuccessBean;
import org.ncbo.stanford.bean.search.OntologyHitBean;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.user.OntologyLicense;
import org.ncbo.stanford.enumeration.ConceptTypeEnum;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.enumeration.ViewingRestrictionEnum;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.service.xml.converters.ClassBeanListConverter;
import org.ncbo.stanford.service.xml.converters.ClassBeanResultListBeanConverter;
import org.ncbo.stanford.service.xml.converters.InstanceBeanResultListBeanConverter;
import org.ncbo.stanford.service.xml.converters.MappingResultListBeanConverter;
import org.ncbo.stanford.service.xml.converters.OntologyAclConverter;
import org.ncbo.stanford.service.xml.converters.OntologyHitMapConverter;
import org.ncbo.stanford.service.xml.converters.OntologyLicenseConverter;
import org.ncbo.stanford.service.xml.converters.SearchResultListBeanConverter;
import org.ncbo.stanford.service.xml.converters.UserAclConverter;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.security.SecurityContextHolder;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;
import com.thoughtworks.xstream.io.xml.TraxSource;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * A default implementation of the XMLSerializationService
 * 
 * @author Michael Dorf
 * 
 */
public class XMLSerializationServiceImpl implements XMLSerializationService {

	private static final Log log = LogFactory
			.getLog(XMLSerializationServiceImpl.class);

	private HashMap<String, Transformer> transformers = new HashMap<String, Transformer>(
			0);
	private XStream xmlSerializer = null;

	/**
	 * Generate an XML representation of a specific error This is going to
	 * retire when ErrorTypeEnum is replaced with Restlet.Status object - cyoun
	 * 
	 * @param errorType
	 * @param accessedResource
	 * @return
	 */
	public String getErrorAsXML(ErrorTypeEnum errorType, String accessedResource) {
		omitField(ErrorBean.class, "errorType");
		ErrorBean errorBean = new ErrorBean(errorType);

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			errorBean.setAccessedResource(accessedResource);
		}

		return getResponseAsXML(errorBean);
	}

	/**
	 * Generate an XML representation of a specific error.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public String getErrorAsXML(Request request, Response response) {
		String accessedResource = request.getResourceRef().getPath();
		ErrorStatusBean errorStatusBean = new ErrorStatusBean(response
				.getStatus());

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			errorStatusBean.setAccessedResource(accessedResource);
		}

		return getResponseAsXML(errorStatusBean);
	}

	/**
	 * Generate an XML representation of a successfully processed request. This
	 * should only be used when no other XML response is expected (i.e.
	 * authentication).
	 * 
	 * @param errorStatusBean
	 * @return String
	 */
	public String getErrorAsXML(ErrorStatusBean errorStatusBean) {
		return getResponseAsXML(errorStatusBean);
	}

	/**
	 * Generate an XML representation of a successfully processed request. This
	 * should only be used when no other XML response is expected (i.e.
	 * authentication).
	 * 
	 * @param successBean
	 * @return String
	 */
	public String getSuccessAsXML(SuccessBean successBean) {
		return getResponseAsXML(successBean);
	}

	/**
	 * returns ErrorStatusBean
	 */
	public ErrorStatusBean getErrorBean(Request request, Response response) {
		String accessedResource = request.getResourceRef().getPath();
		ErrorStatusBean errorStatusBean = new ErrorStatusBean(response
				.getStatus());

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			errorStatusBean.setAccessedResource(accessedResource);
		}

		return errorStatusBean;
	}

	/**
	 * returns SuccessBean with apiKey, accessedResource populated
	 */
	public SuccessBean getSuccessBean(Request request) {
		String accessedResource = request.getResourceRef().getPath();
		SuccessBean successBean = new SuccessBean();

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			successBean.setAccessedResource(accessedResource);
		}

		return successBean;
	}

	/**
	 * returns SuccessBean with apiKey, accessedResource and data populated
	 */
	public SuccessBean getSuccessBean(Request request, Object data) {
		SuccessBean successBean = getSuccessBean(request);

		if (data != null) {
			successBean.getData().add(data);
		}

		return successBean;
	}

	/**
	 * Generate an XML representation of a successfully processed request with
	 * XSL Transformation.
	 * 
	 * @param request
	 * @param data
	 * @param xsltFile
	 * @return String
	 * @throws TransformerException
	 */
	public String applyXSL(Request request, Object data, String xsltFile)
			throws TransformerException {
		SuccessBean sb = getSuccessBean(request, data);
		// create source
		TraxSource traxSource = new TraxSource(sb, xmlSerializer);
		// create buffer for XML output
		Writer buffer = new StringWriter();
		getTransformerInstance(xsltFile).transform(traxSource,
				new StreamResult(buffer));

		return buffer.toString();
	}

	/**
	 * Generates Generic XML response which contains status info whether success
	 * or fail. session id and access resource info is included.
	 * 
	 * @param request
	 * @param response
	 */
	public void generateStatusXMLResponse(Request request, Response response) {
		if (!response.getStatus().isError()) {
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML,
					getSuccessAsXML(getSuccessBean(request)));
		} else {
			RequestUtils.setHttpServletResponse(response, response.getStatus(),
					MediaType.TEXT_XML, getErrorAsXML(getErrorBean(request,
							response)));
		}
	}

	/**
	 * Generates XML response. If SUCCESS - Entity info is displayed. else -
	 * Error info is displayed.
	 * 
	 * @param request
	 * @param response
	 * @param data
	 */

	public void generateXMLResponse(Request request, Response response,
			Object data) {
		// SUCCESS, include the bean info
		if (!response.getStatus().isError()) {
			RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
					MediaType.TEXT_XML, getSuccessAsXML(getSuccessBean(request,
							data)));
			// if ERROR, just status, no bean info
		} else {
			generateStatusXMLResponse(request, response);
		}
	}

	/**
	 * Generates XML response then apply XSL transformation. This is useful to
	 * filter huge XML response such as findAll() Ontologies.
	 * 
	 * If SUCCESS - Entity info is displayed. else - Error info is displayed.
	 * 
	 * @param request
	 * @param response
	 * @param data
	 * @param xsltFile
	 */
	public void generateXMLResponse(Request request, Response response,
			Object data, String xsltFile) {
		// if SUCCESS, include the bean info
		if (!response.getStatus().isError()) {
			try {
				RequestUtils.setHttpServletResponse(response,
						Status.SUCCESS_OK, MediaType.TEXT_XML, applyXSL(
								request, data, xsltFile));
			} catch (TransformerException e) {
				// XML parse ERROR
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				generateStatusXMLResponse(request, response);
				e.printStackTrace();
				log.error(e);
			}
			// if ERROR, just status, no bean info
		} else {
			generateStatusXMLResponse(request, response);
		}
	}

	/**
	 * Process a get request and return a response
	 * 
	 * @param baseUrl
	 * @param getParams
	 * @throws Exception
	 */
	public AbstractResponseBean processGet(String baseUrl,
			HashMap<String, String> getParams) throws Exception {
		AbstractResponseBean responseBean = null;
		HttpInputStreamWrapper inputStreamWrapper = RequestUtils.doHttpGet(
				baseUrl, getParams);
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(inputStreamWrapper.getInputStream());
		removeWhitespaceNodes(doc.getDocumentElement());

		if (inputStreamWrapper.isError()) {
			responseBean = populateErrorBean(doc, inputStreamWrapper
					.getResponseCode());
		} else {
			responseBean = populateSuccessBean(doc);
		}

		return responseBean;
	}

	private void removeWhitespaceNodes(Element e) {
		NodeList children = e.getChildNodes();

		for (int i = children.getLength() - 1; i >= 0; i--) {
			Node child = children.item(i);

			if (child instanceof Text
					&& ((Text) child).getData().trim().length() == 0) {
				e.removeChild(child);
			} else if (child instanceof Element) {
				removeWhitespaceNodes((Element) child);
			}
		}
	}

	private ErrorStatusBean populateErrorBean(Document doc, int responseCode)
			throws TransformerException {
		String node = getNodeAsXML(doc);
		ErrorStatusBean errorBean = (ErrorStatusBean) xmlSerializer
				.fromXML(node);
		errorBean.setStatus(new Status(responseCode));

		return errorBean;
	}

	private SuccessBean populateSuccessBean(Document doc)
			throws TransformerException {
		SuccessBean sb = new SuccessBean();
		String node = getNodeAsXML(doc.getElementsByTagName(
				ApplicationConstants.DATA_XML_TAG_NAME).item(0).getFirstChild());
		sb.setDataXml(node);

		return sb;
	}

	private String getNodeAsXML(Node node) throws TransformerException {
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(node);
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		trans.transform(source, result);

		return sw.toString();
	}

	/**
	 * returns a singleton transformer instance for a XSL file specified. do not
	 * use synchronized since it is expensive.
	 */
	public Transformer getTransformerInstance(String xslFile)
			throws TransformerException {
		Transformer transformer = (Transformer) transformers.get(xslFile);

		if (transformer == null) {
			File ontologyXSLT = new File(xslFile);
			transformer = TransformerFactory.newInstance().newTransformer(
					new StreamSource(ontologyXSLT));
			transformers.put(xslFile, transformer);
		}

		return transformer;
	}

	@SuppressWarnings("unchecked")
	public void addImplicitCollection(Class ownerType, String fieldName) {
		xmlSerializer.addImplicitCollection(ownerType, fieldName);
	}

	@SuppressWarnings("unchecked")
	public void omitField(Class definedIn, String fieldName) {
		xmlSerializer.omitField(definedIn, fieldName);
	}

	@SuppressWarnings("unchecked")
	public void alias(String name, Class type) {
		xmlSerializer.alias(name, type);
	}

	@SuppressWarnings("unchecked")
	public void aliasField(String alias, Class definedIn, String fieldName) {
		xmlSerializer.aliasField(alias, definedIn, fieldName);
	}

	public Object fromXML(String xml) {
		return xmlSerializer.fromXML(xml);
	}

	public XStream getXmlSerializer() {
		return xmlSerializer;
	}

	/**
	 * Generate an XML representation of a request.
	 * 
	 * @param responseBean
	 * @return String
	 */
	private String getResponseAsXML(AbstractResponseBean responseBean) {
		StringBuffer sb = new StringBuffer(ApplicationConstants.XML_DECLARATION);
		sb.append('\n');
		sb.append(xmlSerializer.toXML(responseBean));

		return sb.toString();
	}

	/**
	 * @param xmlSerializer
	 *            the xmlSerializer to set
	 */
	public void setXmlSerializer(XStream xmlSerializer) {
		this.xmlSerializer = xmlSerializer;
		this.xmlSerializer.setMode(XStream.NO_REFERENCES);
		setAliases(this.xmlSerializer);
		registerConverters(this.xmlSerializer);
	}

	/**
	 * set aliases for xmlSerializer
	 */
	private void registerConverters(XStream xmlSerializer) {
		Mapper mapper = xmlSerializer.getMapper();
		xmlSerializer.registerConverter(new OntologyHitMapConverter(mapper));
		xmlSerializer.registerConverter(new SearchResultListBeanConverter(
				mapper));
		xmlSerializer.registerConverter(new EnumSingleValueConverter(
				SearchRecordTypeEnum.class));
		xmlSerializer.registerConverter(new EnumSingleValueConverter(
				ConceptTypeEnum.class));
		xmlSerializer.registerConverter(new ClassBeanResultListBeanConverter(
				mapper));
		xmlSerializer.registerConverter(new ClassBeanListConverter(mapper));
		xmlSerializer
				.registerConverter(new InstanceBeanResultListBeanConverter(
						mapper));
		xmlSerializer.registerConverter(new MappingResultListBeanConverter(
				mapper));
		xmlSerializer.registerConverter(new UserAclConverter(mapper));
		xmlSerializer.registerConverter(new OntologyAclConverter(mapper));
		xmlSerializer.registerConverter(new OntologyLicenseConverter(mapper));
		xmlSerializer.registerConverter(new EnumSingleValueConverter(
				ViewingRestrictionEnum.class));
	}

	/**
	 * set aliases for xmlSerializer
	 */
	private void setAliases(XStream xmlSerializer) {
		xmlSerializer.alias(MessageUtils.getMessage("entity.ontologybean"),
				OntologyBean.class);
		xmlSerializer.alias(MessageUtils
				.getMessage("entity.ontologymetricsbean"),
				OntologyMetricsBean.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.userbean"),
				UserBean.class);
		xmlSerializer.alias(
				MessageUtils.getMessage("entity.subscriptionsbean"),
				SubscriptionsBean.class);

		xmlSerializer.alias(MessageUtils.getMessage("entity.classbean"),
				ClassBean.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.categorybean"),
				CategoryBean.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.groupbean"),
				GroupBean.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.propertybean"),
				PropertyBean.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.instancebean"),
				InstanceBean.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.searchbean"),
				SearchBean.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.usageloggingbean"),
				UsageLoggingBean.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.page"), Page.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.ontologyhitbean"),
				OntologyHitBean.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.restfulsession"),
				RESTfulSession.class);
		xmlSerializer.alias(MessageUtils
				.getMessage("entity.securitycontextholder"),
				SecurityContextHolder.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.notebean"),
				NoteBean.class);
		xmlSerializer.alias(MessageUtils.getMessage("entity.appliesto"),
				AppliesToBean.class);
		xmlSerializer.alias(MessageUtils
				.getMessage("entity.proposalforchangepropertyvalue"),
				ProposalPropertyValueChangeBean.class);
		xmlSerializer.alias(MessageUtils
				.getMessage("entity.proposalforcreateentity"),
				ProposalNewTermBean.class);
		xmlSerializer.alias(MessageUtils
				.getMessage("entity.proposalforchangehierarchy"),
				ProposalNewRelationshipBean.class);

		xmlSerializer.alias(ApplicationConstants.RESPONSE_XML_TAG_NAME,
				SuccessBean.class);
		xmlSerializer.alias(ApplicationConstants.ERROR_XML_TAG_NAME,
				ErrorBean.class);
		xmlSerializer.alias(ApplicationConstants.ERROR_STATUS_XML_TAG_NAME,
				ErrorStatusBean.class);
		xmlSerializer.alias(ApplicationConstants.SUCCESS_XML_TAG_NAME,
				SuccessBean.class);
		String aclAlias = MessageUtils.getMessage("entity.acl");
		xmlSerializer.alias(aclAlias, OntologyAcl.class);
		xmlSerializer.alias(aclAlias, UserAcl.class);

		String licenseAlias = MessageUtils
				.getMessage("entity.ontologylicenses");
		xmlSerializer.alias(licenseAlias, OntologyLicense.class);

		xmlSerializer.omitField(UserBean.class, "password");
		xmlSerializer.omitField(UserBean.class, "apiKey");
		xmlSerializer.omitField(ErrorStatusBean.class, "status");

		// Mapping aliases using annotations
		xmlSerializer.processAnnotations(MappingBean.class);
		xmlSerializer.processAnnotations(MappingOntologyStatsBean.class);
		xmlSerializer.processAnnotations(MappingConceptStatsBean.class);
		xmlSerializer.processAnnotations(MappingUserStatsBean.class);
		xmlSerializer.addDefaultImplementation(URIImpl.class, URI.class);
		xmlSerializer.addDefaultImplementation(ArrayList.class, List.class);
	}
}
