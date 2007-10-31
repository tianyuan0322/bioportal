package org.ncbo.stanford.service.encryption.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.ncbo.stanford.service.encryption.EncryptionService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;


/**
 * Implementation of Encryption service
 * 
 * @author Michael Dorf
 */
public class EncryptionServiceImpl implements EncryptionService {

	private StandardPBEStringEncryptor stringEncryptor = null;
	private PasswordEncoder passwordEncoder = null;

	/*
	 * (non-Javadoc)
	 * @see tsn.encryption.service.EncryptionService#encodePassword(java.lang.String)
	 */
	public String encodePassword(String password) {
		return passwordEncoder.encodePassword(
				password, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see tsn.encryption.service.EncryptionService#encrypt(java.lang.String)
	 */
	public String encrypt(String decrypted) {
		return stringEncryptor.encrypt(decrypted);
	}
	
	/*
	 * (non-Javadoc)
	 * @see tsn.encryption.service.EncryptionService#decrypt(java.lang.String)
	 */
	public String decrypt(String encrypted) {
		return stringEncryptor.decrypt(encrypted);
	}
	
	/*
	 * (non-Javadoc)
	 * @see tsn.encryption.service.EncryptionService#decryptQueryString(java.lang.String)
	 */
	public String decryptQueryString(
			String encrypted) throws UnsupportedEncodingException {
		return decrypt(URLDecoder.decode(
				encrypted, ApplicationConstants.DEFAULT_ENCODING));
	}

	/*
	 * (non-Javadoc)
	 * @see tsn.encryption.service.EncryptionService#encryptQueryString(java.lang.String)
	 */
	public String encryptQueryString(
			String queryString) throws UnsupportedEncodingException {
		return URLEncoder.encode(encrypt(queryString), 
				ApplicationConstants.DEFAULT_ENCODING);
	}
	
	/*
	 * (non-Javadoc)
	 * @see tsn.encryption.service.EncryptionService#getParamFromEncryptedQueryString(java.lang.String, java.lang.String)
	 */
	public String getParamFromEncryptedQueryString(
			String encrypted, String paramName) throws UnsupportedEncodingException {
		String decrypted = decryptQueryString(encrypted);		
		HashMap<String, String> params = 
			RequestUtils.parseQueryString(decrypted);
		
		return params.get(paramName);
	}
	
	/**
	 * @return the passwordEncoder
	 */
	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	/**
	 * @param passwordEncoder
	 *            the passwordEncoder to set
	 */
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * @return the stringEncryptor
	 */
	public StandardPBEStringEncryptor getStringEncryptor() {
		return stringEncryptor;
	}

	/**
	 * @param stringEncryptor
	 *            the stringEncryptor to set
	 */
	public void setStringEncryptor(StandardPBEStringEncryptor stringEncryptor) {
		this.stringEncryptor = stringEncryptor;
	}
}
