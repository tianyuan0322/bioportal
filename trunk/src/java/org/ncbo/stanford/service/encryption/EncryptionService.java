package org.ncbo.stanford.service.encryption;

import java.io.UnsupportedEncodingException;

/**
 * Service that provides encryption and encoding capabilities
 * 
 * @author Michael Dorf
 * 
 */
public interface EncryptionService {

	/**
	 * Encodes passwords using a one-way encryption algorithm
	 * 
	 * @param password
	 * @return
	 */
	public String encodePassword(String password);

	/**
	 * Encrypts a specified string
	 * 
	 * @param decrypted
	 * @return
	 */
	public String encrypt(String decrypted);

	/**
	 * Decrypts a specified string
	 * 
	 * @param encrypted
	 * @return
	 */
	public String decrypt(String encrypted);

	/**
	 * Returns a decrypted query string from an encrypted one
	 * 
	 * @param encrypted
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String decryptQueryString(String encrypted)
			throws UnsupportedEncodingException;

	/**
	 * Encrypts and URLEncodes a given query string
	 * 
	 * @param queryString
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String encryptQueryString(String queryString)
			throws UnsupportedEncodingException;

	/**
	 * Returns parameter value based on its name from the encrypted query string
	 * 
	 * @param encrypted
	 * @param paramName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getParamFromEncryptedQueryString(String encrypted,
			String paramName) throws UnsupportedEncodingException;
}
