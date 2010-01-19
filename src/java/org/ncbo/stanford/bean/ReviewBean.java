package org.ncbo.stanford.bean;

/**
 * Boilerplate bean for reviews of ontologies.  Primary definition of datastructure is
 * in the metadata ontology OWL file.
 *
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 */
public class ReviewBean {
	
	private Integer id;
	private String body;
	
	public ReviewBean() {
		id = null;
		body = null;
	}
	
//	public ReviewBean(Integer id, String body) {
//		this.id = id;
//		this.body = body;
//	}

	@Override
	public String toString() {
		return "ReviewBean(id: "+id+")";
	}
	
	// Boilerplate getters and setters
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
}
