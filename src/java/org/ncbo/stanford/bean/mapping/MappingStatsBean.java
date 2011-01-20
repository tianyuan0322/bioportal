/**
 * 
 */
package org.ncbo.stanford.bean.mapping;

import java.util.List;

/**
 * @author g.prakash
 * 
 */
public class MappingStatsBean {
	private Integer importedMappingCount;
	private Integer errorMappingCount;
	private List<Integer> errorLineNumber;

	/**
	 * 
	 * @return
	 */
	public Integer getImportedMappingCount() {
		return importedMappingCount;
	}

	/**
	 * 
	 * @param importedMappingCount
	 */
	public void setImportedMappingCount(Integer importedMappingCount) {
		this.importedMappingCount = importedMappingCount;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getErrorMappingCount() {
		return errorMappingCount;
	}

	/**
	 * 
	 * @param errorMappingCount
	 */
	public void setErrorMappingCount(Integer errorMappingCount) {
		this.errorMappingCount = errorMappingCount;
	}

	/**
	 * 
	 * @return
	 */
	public List<Integer> getErrorLineNumber() {
		return errorLineNumber;
	}

	/**
	 * 
	 * @param errorLineNumber
	 */
	public void setErrorLineNumber(List<Integer> errorLineNumber) {
		this.errorLineNumber = errorLineNumber;
	}

}
