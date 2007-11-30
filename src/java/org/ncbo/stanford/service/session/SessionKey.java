package org.ncbo.stanford.service.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Generates a globally unique string key
 * 
 * @author Michael Dorf
 */
public class SessionKey {

	private static Random randomNumberGenerator;
	private String uniqueKey;

	static {
		try {
			randomNumberGenerator = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Generating insecure session keys");
			randomNumberGenerator = new Random();
		}
	}

	public SessionKey() {
		uniqueKey = generateUniqueString();
	}

	public String getKey() {
		return uniqueKey;
	}

	public int hashCode() {
		return uniqueKey.hashCode();
	}

	public boolean equals(Object object) {
		if (!(object instanceof SessionKey)) {
			return false;
		}
		
		SessionKey otherSessionKey = (SessionKey) object;
		
		return uniqueKey.equals(otherSessionKey.uniqueKey);
	}

	public String toString() {
		return uniqueKey;
	}
	
	private static String generateUniqueString() {
		String randomNum = new Integer(randomNumberGenerator.nextInt())
				.toString();
		String uniqueString = null;

		try {
			MessageDigest sha;
			sha = MessageDigest.getInstance("SHA-1");
			byte[] result = sha.digest(randomNum.getBytes());
			uniqueString = hexEncode(result);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return uniqueString;
	}

	/**
	 * The byte[] returned by MessageDigest does not have a nice textual
	 * representation, so some form of encoding is usually performed.
	 * 
	 * This implementation follows the example of David Flanagan's book "Java In
	 * A Nutshell", and converts a byte array into a String of hex characters.
	 * 
	 * Another popular alternative is to use a "Base64" encoding.
	 */
	private static String hexEncode(byte[] aInput) {
		StringBuffer result = new StringBuffer();
		char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };

		for (int idx = 0; idx < aInput.length; ++idx) {
			byte b = aInput[idx];
			result.append(digits[(b & 0xf0) >> 4]);
			result.append(digits[b & 0x0f]);
		}

		return result.toString();
	}
}
