package org.ncbo.stanford.bean;

import java.util.ArrayList;

import org.ncbo.stanford.service.xml.parse.ExportBean;

/**
 * <pre>
 * Class that contains metadata for a single ontology. The
 * attribute names mirror the labels in the ontology
 * metadata descriptor file (ontologies.txt) downloaded
 * from sourceforge. Underscores are replaced with 
 * capitalization of the following letter. Ex:
 * 
 * Label                Attribute Name 
 * ---------------------------------------
 * title                title	
 * alternate_download   alternateDownload
 * </pre>
 * 
 * @author Michael Dorf
 * 
 */
public class MetadataFileBean {

	private String id;
	private String title;
	private String namespace;
	private String foundry;
	private String status;
	private String download;
	private String source;
	private String home;
	private String documentation;
	private String contact;
	private String format;
	private String description;
	private String publication;
	private String wiki;
	private String domain;
	private String relevantOrganism;
	private String subtypesOf;
	private String pathoType;
	private String granularity;
	private String application;
	private String alternateDownload;
	private String isObsolete;
	private String consider;
	private String xrefsTo;

	private ArrayList<ExportBean> export;
	private String url;
	private String pathotype;
	private String relevantorganism;

	public String getRelevantorganism() {
		return relevantorganism;
	}

	public void setRelevantorganism(String relevantorganism) {
		this.relevantorganism = relevantorganism;
	}

	public String getPathotype() {
		return pathotype;
	}

	public void setPathotype(String pathotype) {
		this.pathotype = pathotype;
	}

	public ArrayList<ExportBean> getExport() {
		return export;
	}

	public void setExport(ArrayList<ExportBean> export) {
		this.export = export;
	}

	/**
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param application
	 *            the application to set
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * @return the granularity
	 */
	public String getGranularity() {
		return granularity;
	}

	/**
	 * @param granularity
	 *            the granularity to set
	 */
	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}

	/**
	 * @return the pathoType
	 */
	public String getPathoType() {
		return pathoType;
	}

	/**
	 * @param pathoType
	 *            the pathoType to set
	 */
	public void setPathoType(String pathoType) {
		this.pathoType = pathoType;
	}

	/**
	 * @return the subtypesOf
	 */
	public String getSubtypesOf() {
		return subtypesOf;
	}

	/**
	 * @param subtypesOf
	 *            the subtypesOf to set
	 */
	public void setSubtypesOf(String subtypesOf) {
		this.subtypesOf = subtypesOf;
	}

	/**
	 * @return the contact
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * @param contact
	 *            the contact to set
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the documentation
	 */
	public String getDocumentation() {
		return documentation;
	}

	/**
	 * @param documentation
	 *            the documentation to set
	 */
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	/**
	 * @return the download
	 */
	public String getDownload() {
		return download;
	}

	/**
	 * @param download
	 *            the download to set
	 */
	public void setDownload(String download) {
		this.download = download;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the foundry
	 */
	public String getFoundry() {
		return foundry;
	}

	/**
	 * @param foundry
	 *            the foundry to set
	 */
	public void setFoundry(String foundry) {
		this.foundry = foundry;
	}

	/**
	 * @return the home
	 */
	public String getHome() {
		return home;
	}

	/**
	 * @param home
	 *            the home to set
	 */
	public void setHome(String home) {
		this.home = home;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the alternateDownload
	 */
	public String getAlternateDownload() {
		return alternateDownload;
	}

	/**
	 * @param alternateDownload
	 *            the alternateDownload to set
	 */
	public void setAlternateDownload(String alternateDownload) {
		this.alternateDownload = alternateDownload;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain
	 *            the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return the relevantOrganism
	 */
	public String getRelevantOrganism() {
		return relevantOrganism;
	}

	/**
	 * @param relevantOrganism
	 *            the relevantOrganism to set
	 */
	public void setRelevantOrganism(String relevantOrganism) {
		this.relevantOrganism = relevantOrganism;
	}

	/**
	 * @return the isObsolete
	 */
	public String getIsObsolete() {
		return isObsolete;
	}

	/**
	 * @param isObsolete
	 *            the isObsolete to set
	 */
	public void setIsObsolete(String isObsolete) {
		this.isObsolete = isObsolete;
	}

	/**
	 * @return the consider
	 */
	public String getConsider() {
		return consider;
	}

	/**
	 * @param consider
	 *            the consider to set
	 */
	public void setConsider(String consider) {
		this.consider = consider;
	}

	/**
	 * @return the publication
	 */
	public String getPublication() {
		return publication;
	}

	/**
	 * @param publication
	 *            the publication to set
	 */
	public void setPublication(String publication) {
		this.publication = publication;
	}

	/**
	 * @return the wiki
	 */
	public String getWiki() {
		return wiki;
	}

	/**
	 * @param wiki
	 *            the wiki to set
	 */
	public void setWiki(String wiki) {
		this.wiki = wiki;
	}

	/**
	 * @return the xrefsTo
	 */
	public String getXrefsTo() {
		return xrefsTo;
	}

	/**
	 * @param xrefsTo
	 *            the xrefsTo to set
	 */
	public void setXrefsTo(String xrefsTo) {
		this.xrefsTo = xrefsTo;
	}

	/**
	 * output of the class
	 */
	public String toString() {
		return "" + "id: " + id + "\n" + "title: " + title + "\n"
				+ "namespace: " + namespace + "\n" + "foundry: " + foundry
				+ "\n" + "status: " + status + "\n" + "download: " + download
				+ "\n" + "source: " + source + "\n" + "home: " + home + "\n"
				+ "documentation: " + documentation + "\n" + "contact: "
				+ contact + "\n" + "format: " + format + "\n" + "description: "
				+ description + "\n" + "publication: " + publication + "\n"
				+ "wiki: " + wiki + "\n" + "domain: " + domain + "\n"
				+ "relevantOrganism: " + relevantOrganism + "\n"
				+ "subtypesOf: " + subtypesOf + "\n" + "pathoType: "
				+ pathoType + "\n" + "granularity: " + granularity + "\n"
				+ "application: " + application + "\n" + "isObsolete: "
				+ isObsolete + "\n" + "consider: " + consider + "\n"
				+ "alternateDownload: " + alternateDownload + "\n"
				+ "xrefsTo: " + xrefsTo;
	}
}
