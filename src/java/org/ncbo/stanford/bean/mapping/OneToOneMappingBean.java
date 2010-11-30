package org.ncbo.stanford.bean.mapping;

import org.ncbo.stanford.service.xml.converters.URIConverter;
import org.openrdf.model.URI;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("mapping")
public class OneToOneMappingBean extends AbstractMappingBean {

	@XStreamAlias("source")
	@XStreamConverter(URIConverter.class)
	private URI source;

	@XStreamAlias("target")
	@XStreamConverter(URIConverter.class)
	private URI target;

	@XStreamAlias("relation")
	@XStreamConverter(URIConverter.class)
	private URI relation;
	
	@XStreamAlias("sourceOntologyId")
	private Integer sourceOntologyId;
	
	@XStreamAlias("targetOntologyId")
	private Integer targetOntologyId;
	
	@XStreamAlias("createdInSourceOntologyVersion")
	private Integer createdInSourceOntologyVersion;
	
	@XStreamAlias("createdInTargetOntologyVersion")
	private Integer createdInTargetOntologyVersion;

	/**
	 * Default no-arg constructor.
	 * */
	public OneToOneMappingBean() {
	}

	/**
	 * @return the source
	 */
	public URI getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(URI source) {
		this.source = source;
	}

	/**
	 * @return the target
	 */
	public URI getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(URI target) {
		this.target = target;
	}

	/**
	 * @return the relation
	 */
	public URI getRelation() {
		return relation;
	}

	/**
	 * @param relation
	 *            the relation to set
	 */
	public void setRelation(URI relation) {
		this.relation = relation;
	}

	/**
	 * @return the sourceOntologyId
	 */
	public Integer getSourceOntologyId() {
		return sourceOntologyId;
	}

	/**
	 * @param sourceOntologyId
	 *            the sourceOntologyId to set
	 */
	public void setSourceOntologyId(Integer sourceOntologyId) {
		this.sourceOntologyId = sourceOntologyId;
	}

	/**
	 * @return the targetOntologyId
	 */
	public Integer getTargetOntologyId() {
		return targetOntologyId;
	}

	/**
	 * @param targetOntologyId
	 *            the targetOntologyId to set
	 */
	public void setTargetOntologyId(Integer targetOntologyId) {
		this.targetOntologyId = targetOntologyId;
	}

	/**
	 * @return the createdInSourceOntologyVersion
	 */
	public Integer getCreatedInSourceOntologyVersion() {
		return createdInSourceOntologyVersion;
	}

	/**
	 * @param createdInSourceOntologyVersion
	 *            the createdInSourceOntologyVersion to set
	 */
	public void setCreatedInSourceOntologyVersion(
			Integer createdInSourceOntologyVersion) {
		this.createdInSourceOntologyVersion = createdInSourceOntologyVersion;
	}

	/**
	 * @return the createdInTargetOntologyVersion
	 */
	public Integer getCreatedInTargetOntologyVersion() {
		return createdInTargetOntologyVersion;
	}

	/**
	 * @param createdInTargetOntologyVersion
	 *            the createdInTargetOntologyVersion to set
	 */
	public void setCreatedInTargetOntologyVersion(
			Integer createdInTargetOntologyVersion) {
		this.createdInTargetOntologyVersion = createdInTargetOntologyVersion;
	}

}
