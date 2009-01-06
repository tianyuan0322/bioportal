package org.ncbo.stanford.service.encryption;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

public class EncryptionServiceTest extends AbstractBioPortalTest {

	@Autowired
	EncryptionService encryptionService;

	@Test
	public void testEncryptDecrypt() throws Exception {
		String plain = "BioPortal";
		String encrypted = encryptionService.encrypt(plain);
		String decrypted = encryptionService.decrypt(encrypted);
		String password = encryptionService.encodePassword(plain);

		System.out.println("Password: " + password + ", Decrypted: "
				+ decrypted + ", Encrypted: " + encrypted);

		assertNotNull(encrypted);
		assertEquals(plain, decrypted);
	}
}
