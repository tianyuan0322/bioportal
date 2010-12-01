package org.ncbo.stanford.bean.metadata;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Review objects represent feedback on ontologies.
 * 
 * @author Tony Loeser
 */
public class ReviewBean extends TimedIdBean {
	
	private String text = null;
	private Integer userId = null;
	private Integer projectId = null;
	private Integer ontologyId = null;
	private Collection<RatingBean> ratings = new ArrayList<RatingBean>();
	
	public ReviewBean() {
		super();
	}
	
	public ReviewBean(Integer id) {
		super(id);
	}
	
	//Override
	public String toString() {
		return "ReviewBean(id: "+getId()+")";
	}

	
	// ============================================================
	// Standard getters/setters

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	public Collection<RatingBean> getRatings() {
		return ratings;
	}

	public void setRatings(Collection<RatingBean> ratings) {
		this.ratings = ratings;
	}
	
}
