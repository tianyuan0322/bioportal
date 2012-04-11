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
import org.ncbo.stanford.bean.NamespaceBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.bean.SubscriptionsBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.acl.OntologyAcl;
import org.ncbo.stanford.bean.acl.UserAcl;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.ConceptOntologyPairBean;
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
import org.ncbo.stanford.bean.response.SuccessBean;
import org.ncbo.stanford.bean.search.OntologyHitBean;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.user.OntologyLicense;
import org.ncbo.stanford.enumeration.ConceptTypeEnum;
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
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
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
	private XStream jsonSerializer = null;

	public XMLSerializationServiceImpl() {
		initXmlSerializer();
		initJsonSerializer();
	}

	/**
	 * Generates Generic XML response which contains status info whether success
	 * or fail. session id and access resource info is included.
	 * 
	 * @param request
	 * @param response
	 */
	public void generateStatusXMLResponse(Request request, Response response) {
		generateXMLResponse(request, response, null);
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
		AbstractResponseBean respBean = null;
		Status status = response.getStatus();

		if (status.isError()) {
			respBean = getErrorBean(request, response);
		} else {
			respBean = getSuccessBean(request, data);
		}

		RequestUtils.setHttpServletResponse(response, status, respBean
				.getMediaType(), getResponseAsString(respBean));
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
		if (response.getStatus().isError()) {
			generateStatusXMLResponse(request, response);
		} else {
			MediaType prefMediaType = getPreferredMediaType(request);

			if (prefMediaType.equals(MediaType.APPLICATION_JSON)) {
				generateXMLResponse(request, response, data);
			} else {
				try {
					RequestUtils.setHttpServletResponse(response,
							Status.SUCCESS_OK, prefMediaType, applyXSL(request,
									data, xsltFile));
				} catch (TransformerException e) {
					// XML parse ERROR
					response.setStatus(Status.SERVER_ERROR_INTERNAL, e
							.getMessage());
					generateStatusXMLResponse(request, response);
					e.printStackTrace();
					log.error(e);
				}
			}
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
	private String applyXSL(Request request, Object data, String xsltFile)
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
	 * returns ErrorStatusBean
	 */
	private ErrorBean getErrorBean(Request request, Response response) {
		ErrorBean errorStatusBean = new ErrorBean(response.getStatus());
		initResponseBean(request, errorStatusBean);

		return errorStatusBean;
	}

	/**
	 * returns SuccessBean with apiKey, accessedResource populated
	 */
	private SuccessBean getSuccessBean(Request request) {
		SuccessBean successBean = new SuccessBean();
		initResponseBean(request, successBean);

		return successBean;
	}

	/**
	 * returns SuccessBean with apiKey, accessedResource and data populated
	 */
	private SuccessBean getSuccessBean(Request request, Object data) {
		SuccessBean successBean = getSuccessBean(request);

		if (data != null) {
			successBean.getData().add(data);
		}

		return successBean;
	}

	private void initResponseBean(Request request,
			AbstractResponseBean responseBean) {
		String accessedResource = request.getResourceRef().getPath();

		if (!GenericValidator.isBlankOrNull(accessedResource)) {
			responseBean.setAccessedResource(accessedResource);
		}

		responseBean.setMediaType(getPreferredMediaType(request));
	}

	private MediaType getPreferredMediaType(Request request) {
		List<MediaType> prefMediaTypes = new ArrayList<MediaType>(2);
		prefMediaTypes.add(MediaType.APPLICATION_XML);
		prefMediaTypes.add(MediaType.APPLICATION_JSON);
		MediaType prefMediaType = request.getClientInfo()
				.getPreferredMediaType(prefMediaTypes);

		return (prefMediaType == null) ? MediaType.APPLICATION_XML
				: prefMediaType;
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

	private ErrorBean populateErrorBean(Document doc, int responseCode)
			throws TransformerException {
		String node = getNodeAsXML(doc);
		ErrorBean errorBean = (ErrorBean) xmlSerializer.fromXML(node);
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
	 * Generate a String representation of a request.
	 * 
	 * @param responseBean
	 * @return String
	 */
	private String getResponseAsString(AbstractResponseBean responseBean) {
		StringBuffer sb = new StringBuffer();
		XStream serializer = null;

		if (responseBean.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			serializer = jsonSerializer;
		} else {
			serializer = xmlSerializer;
			sb.append(ApplicationConstants.XML_DECLARATION);
			sb.append('\n');
		}

		sb.append(serializer.toXML(responseBean));

		return sb.toString();
	}

	private void initXmlSerializer() {
		this.xmlSerializer = new XStream();
		initSerializer(this.xmlSerializer);
	}

	private void initJsonSerializer() {
		this.jsonSerializer = new XStream(new JsonHierarchicalStreamDriver());
		initSerializer(this.jsonSerializer);
	}

	private void initSerializer(XStream serializer) {
		serializer.setMode(XStream.NO_REFERENCES);
		setAliases(serializer);
		registerConverters(serializer);
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
		xmlSerializer.alias(ApplicationConstants.ERROR_STATUS_XML_TAG_NAME,
				ErrorBean.class);
		xmlSerializer.alias(ApplicationConstants.SUCCESS_XML_TAG_NAME,
				SuccessBean.class);

		xmlSerializer.alias("namespace", NamespaceBean.class);

		String aclAlias = MessageUtils.getMessage("entity.acl");
		xmlSerializer.alias(aclAlias, OntologyAcl.class);
		xmlSerializer.alias(aclAlias, UserAcl.class);

		String licenseAlias = MessageUtils
				.getMessage("entity.ontologylicenses");
		xmlSerializer.alias(licenseAlias, OntologyLicense.class);

		xmlSerializer.omitField(UserBean.class, "password");
		xmlSerializer.omitField(UserBean.class, "apiKey");

		xmlSerializer.omitField(AbstractResponseBean.class, "mediaType");
		xmlSerializer.omitField(AbstractResponseBean.class, "status");

		// Mapping aliases using annotations
		xmlSerializer.processAnnotations(MappingBean.class);
		xmlSerializer.processAnnotations(MappingOntologyStatsBean.class);
		xmlSerializer.processAnnotations(MappingConceptStatsBean.class);
		xmlSerializer.processAnnotations(MappingUserStatsBean.class);
		xmlSerializer.processAnnotations(ConceptOntologyPairBean.class);
		xmlSerializer.addDefaultImplementation(URIImpl.class, URI.class);
		xmlSerializer.addDefaultImplementation(ArrayList.class, List.class);
	}
}
