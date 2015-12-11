// From http://blog.jerryorr.com/2012/05/secure-password-storage-lots-of-donts.html
package org.kisst.util;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

public class PasswordEncryption {
	public static interface HasPasswordSalt {
		public String getPasswordSalt();
	}
	
	
	public static String toHexString(byte[] array) { return DatatypeConverter.printHexBinary(array); }
	public static byte[] toByteArray(String s) { return DatatypeConverter.parseHexBinary(s); }

	public static boolean authenticate(String attemptedPassword, String encryptedPassword, String salt) {
		String encryptedAttemptedPassword = encryptPassword(attemptedPassword, salt);
		return encryptedPassword!=null && encryptedPassword.equals(encryptedAttemptedPassword);
	}

	public static String encryptPassword(String password, String salt) {
		// Pick an iteration count that works for you. The NIST recommends at
		// least 1,000 iterations:
		// http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
		// iOS 4.x reportedly uses 10,000:
		// http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
		int iterations = 20000;
		return onewayEncrypt(password, salt, iterations);
	}

	public static String onewayEncrypt(String password, String salt, int iterations) {
		// PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
		// specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
		String algorithm = "PBKDF2WithHmacSHA1";
		// SHA-1 generates 160 bit hashes, so that's what makes sense here
		int derivedKeyLength = 160;
		KeySpec spec = new PBEKeySpec(password.toCharArray(), toByteArray(salt), iterations, derivedKeyLength);
		SecretKeyFactory f;
		try {
			f = SecretKeyFactory.getInstance(algorithm);
		}
		catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
		try {
			return toHexString(f.generateSecret(spec).getEncoded());
		}
		catch (InvalidKeySpecException e) {throw new RuntimeException(e); }
	}
	
	
	public static String generateSalt() {
		try {
			// VERY important to use SecureRandom instead of just Random
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

			// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
			byte[] salt = new byte[8];
			random.nextBytes(salt);
			return toHexString(salt);
		}
		catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
	}
	


}