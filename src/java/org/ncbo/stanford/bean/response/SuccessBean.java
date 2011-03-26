package org.ncbo.stanford.bean.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Success response container for RESTful responses
 * 
 * @author Michael Dorf
 * 
 */
public class SuccessBean extends AbstractResponseBean {

	private List<Object> data = new ArrayList<Object>(0);
	private String dataXml;

	/**
	 * Default constructor
	 */
	public SuccessBean() {
		super();
	}

	@Override
	public boolean isResponseError() {
		return false;
	}

	@Override
	public boolean isResponseSuccess() {
		return true;
	}

	/**
	 * @return the data
	 */
	public List<Object> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(List<Object> data) {
		this.data = data;
	}

	/**
	 * @return the dataXml
	 */
	public String getDataXml() {
		return dataXml;
	}

	/**
	 * @param dataXml
	 *            the dataXml to set
	 */
	public void setDataXml(String dataXml) {
		this.dataXml = dataXml;
	}
}
