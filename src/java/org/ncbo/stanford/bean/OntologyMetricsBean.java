package org.ncbo.stanford.bean;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class that contains metadata about ontology metrics
 * for a single ontology.
 * </pre>
 * 
 * @author Csongor Nyulas
 * 
 */
public class OntologyMetricsBean {

	private Integer id;
	private Integer numberOfAxioms;
	private Integer numberOfClasses;
	private Integer numberOfIndividuals;
	private Integer numberOfProperties;
	private Integer maximumDepth;
	private Integer maximumNumberOfSiblings;
	private Integer averageNumberOfSiblings;
	private List<String> classesWithOneSubclass;
	private Map<String, Integer> classesWithMoreThanXSubclasses;
	private List<String> classesWithNoDocumentation;
	private List<String> classesWithNoAuthor;
	private List<String> classesWithMoreThanOnePropertyValue;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the numberOfAxioms
	 */
	public Integer getNumberOfAxioms() {
		return numberOfAxioms;
	}

	/**
	 * @param numberOfAxioms
	 *            the numberOfAxioms to set
	 */
	public void setNumberOfAxioms(Integer numberOfAxioms) {
		this.numberOfAxioms = numberOfAxioms;
	}

	/**
	 * @return the numberOfClasses
	 */
	public Integer getNumberOfClasses() {
		return numberOfClasses;
	}

	/**
	 * @param numberOfClasses
	 *            the numberOfClasses to set
	 */
	public void setNumberOfClasses(Integer numberOfClasses) {
		this.numberOfClasses = numberOfClasses;
	}

	/**
	 * @return the numberOfIndividuals
	 */
	public Integer getNumberOfIndividuals() {
		return numberOfIndividuals;
	}

	/**
	 * @param numberOfIndividuals
	 *            the numberOfIndividuals to set
	 */
	public void setNumberOfIndividuals(Integer numberOfIndividuals) {
		this.numberOfIndividuals = numberOfIndividuals;
	}

	/**
	 * @return the numberOfProperties
	 */
	public Integer getNumberOfProperties() {
		return numberOfProperties;
	}

	/**
	 * @param numberOfProperties
	 *            the numberOfProperties to set
	 */
	public void setNumberOfProperties(Integer numberOfProperties) {
		this.numberOfProperties = numberOfProperties;
	}

	/**
	 * @return the maximumDepth
	 */
	public Integer getMaximumDepth() {
		return maximumDepth;
	}

	/**
	 * @param maximumDepth
	 *            the maximumDepth to set
	 */
	public void setMaximumDepth(Integer maximumDepth) {
		this.maximumDepth = maximumDepth;
	}

	/**
	 * @return the maximumNumberOfSiblings
	 */
	public Integer getMaximumNumberOfSiblings() {
		return maximumNumberOfSiblings;
	}

	/**
	 * @param maximumNumberOfSiblings
	 *            the maximumNumberOfSiblings to set
	 */
	public void setMaximumNumberOfSiblings(Integer maximumNumberOfSiblings) {
		this.maximumNumberOfSiblings = maximumNumberOfSiblings;
	}

	/**
	 * @return the averageNumberOfSiblings
	 */
	public Integer getAverageNumberOfSiblings() {
		return averageNumberOfSiblings;
	}

	/**
	 * @param averageNumberOfSiblings
	 *            the averageNumberOfSiblings to set
	 */
	public void setAverageNumberOfSiblings(Integer averageNumberOfSiblings) {
		this.averageNumberOfSiblings = averageNumberOfSiblings;
	}

	/**
	 * @return the classesWithOneSubclass
	 */
	public List<String> getClassesWithOneSubclass() {
		return classesWithOneSubclass;
	}

	/**
	 * @param classesWithOneSubclass the classesWithOneSubclass to set
	 */
	public void setClassesWithOneSubclass(List<String> classesWithOneSubclass) {
		this.classesWithOneSubclass = classesWithOneSubclass;
	}

	/**
	 * @return the classesWithMoreThanXSubclasses
	 */
	public Map<String, Integer> getClassesWithMoreThanXSubclasses() {
		return classesWithMoreThanXSubclasses;
	}

	/**
	 * @param classesWithMoreThanXSubclasses the classesWithMoreThanXSubclasses to set
	 */
	public void setClassesWithMoreThanXSubclasses(
			Map<String, Integer> classesWithMoreThanXSubclasses) {
		this.classesWithMoreThanXSubclasses = classesWithMoreThanXSubclasses;
	}

	/**
	 * @return the classesWithNoDocumentation
	 */
	public List<String> getClassesWithNoDocumentation() {
		return classesWithNoDocumentation;
	}

	/**
	 * @param classesWithNoDocumentation the classesWithNoDocumentation to set
	 */
	public void setClassesWithNoDocumentation(
			List<String> classesWithNoDocumentation) {
		this.classesWithNoDocumentation = classesWithNoDocumentation;
	}

	/**
	 * @return the classesWithNoAuthor
	 */
	public List<String> getClassesWithNoAuthor() {
		return classesWithNoAuthor;
	}

	/**
	 * @param classesWithNoAuthor the classesWithNoAuthor to set
	 */
	public void setClassesWithNoAuthor(List<String> classesWithNoAuthor) {
		this.classesWithNoAuthor = classesWithNoAuthor;
	}

	/**
	 * @return the classesWithMoreThanOnePropertyValue
	 */
	public List<String> getClassesWithMoreThanOnePropertyValue() {
		return classesWithMoreThanOnePropertyValue;
	}

	/**
	 * @param classesWithMoreThanOnePropertyValue the classesWithMoreThanOnePropertyValue to set
	 */
	public void setClassesWithMoreThanOnePropertyValue(
			List<String> classesWithMoreThanOnePropertyValue) {
		this.classesWithMoreThanOnePropertyValue = classesWithMoreThanOnePropertyValue;
	}

	/**
	 * output of the class
	 */
	public String toString() {
		return "" + "id: " + id + "\n" + "numberOfAxioms: " + numberOfAxioms
				+ "\n" + "numberOfClasses: " + numberOfClasses + "\n"
				+ "numberOfIndividuals: " + numberOfIndividuals + "\n"
				+ "numberOfProperties: " + numberOfProperties + "\n"
				+ "maximumDepth: " + maximumDepth + "\n"
				+ "maximumNumberOfSiblings: " + maximumNumberOfSiblings + "\n"
				+ "averageNumberOfSiblings: " + averageNumberOfSiblings + "\n"
				+ "classesWithOneSubclass: " + classesWithOneSubclass + "\n"
				+ "classesWithMoreThanXSubclasses: "
				+ classesWithMoreThanXSubclasses + "\n"
				+ "classesWithNoDocumentation: " + classesWithNoDocumentation
				+ "\n" + "classesWithNoAuthor: " + classesWithNoAuthor + "\n"
				+ "classesWithMoreThanOnePropertyValue: "
				+ classesWithMoreThanOnePropertyValue;
	}
}
