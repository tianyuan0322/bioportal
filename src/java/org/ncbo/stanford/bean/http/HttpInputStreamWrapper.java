package org.ncbo.stanford.bean.http;

import java.io.InputStream;
import java.net.HttpURLConnection;

public class HttpInputStreamWrapper {

	private int responseCode = HttpURLConnection.HTTP_OK;
	private InputStream inputStream = null;

	/**
	 * 
	 */
	public HttpInputStreamWrapper() {
		super();
	}

	/**
	 * @param responseCode
	 * @param inputStream
	 */
	public HttpInputStreamWrapper(int responseCode, InputStream inputStream) {
		super();
		populateInstance(responseCode, inputStream);
	}

	public void populateInstance(int responseCode, InputStream inputStream) {
		this.responseCode = responseCode;
		this.inputStream = inputStream;
	}

	public boolean isError() {
		return responseCode != HttpURLConnection.HTTP_OK;
	}

	/**
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
}
