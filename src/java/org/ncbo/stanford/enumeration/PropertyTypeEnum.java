/**
 * 
 */
package org.ncbo.stanford.enumeration;

/**
 * @author s.reddy
 * 
 */
public enum PropertyTypeEnum {
	CUI("cui");
	
	private final String type;

	private PropertyTypeEnum(String sts) {
		type = sts;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
}
