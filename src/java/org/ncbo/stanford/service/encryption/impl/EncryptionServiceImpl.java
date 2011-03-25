package org.ncbo.stanford.service.encryption.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.spring.security2.PasswordEncoder;
import org.ncbo.stanford.service.encryption.EncryptionService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;

/**
 * Implementation of Encryption service
 * 
 * @author Michael Dorf
 */
public class EncryptionServiceImpl implements EncryptionService {

	private StandardPBEStringEncryptor stringEncryptor = null;
	private PasswordEncoder passwordEncoder = null;

	/**
	 * Encodes passwords using a one-way encryption algorithm
	 * 
	 * @param password
	 * @return
	 */
	public String encodePassword(String password) {
		return passwordEncoder.encodePassword(password, null);
	}

	/**
	 * Checks whether a given password is valid
	 * 
	 * @param encPass
	 * @param rawPass
	 * @return
	 */
	public boolean isPasswordValid(String encPass, String rawPass) {
		return passwordEncoder.isPasswordValid(encPass, rawPass, null);
	}

	/**
	 * Encrypts a specified string
	 * 
	 * @param decrypted
	 * @return
	 */
	public String encrypt(String decrypted) {
		return stringEncryptor.encrypt(decrypted);
	}

	/**
	 * Decrypts a specified string
	 * 
	 * @param encrypted
	 * @return
	 */
	public String decrypt(String encrypted) {
		return stringEncryptor.decrypt(encrypted);
	}

	/**
	 * Returns a decrypted query string from an encrypted one
	 * 
	 * @param encrypted
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String decryptQueryString(String encrypted)
			throws UnsupportedEncodingException {
		return decrypt(URLDecoder.decode(encrypted, MessageUtils
				.getMessage("default.encoding")));
	}

	/**
	 * Encrypts and URLEncodes a given query string
	 * 
	 * @param queryString
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String encryptQueryString(String queryString)
			throws UnsupportedEncodingException {
		return URLEncoder.encode(encrypt(queryString), MessageUtils
				.getMessage("default.encoding"));
	}

	/**
	 * Returns parameter value based on its name from the encrypted query string
	 * 
	 * @param encrypted
	 * @param paramName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getParamFromEncryptedQueryString(String encrypted,
			String paramName) throws UnsupportedEncodingException {
		Map<String, Object> params = new HashMap<String, Object>(0);
		String decrypted = decryptQueryString(encrypted);
		RequestUtils.parseQueryString(decrypted, params);

		return (String) params.get(paramName);
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
