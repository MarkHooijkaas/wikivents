package org.kisst.http4j;

import java.io.IOException;

import org.kisst.util.Base64;
import org.kisst.util.PasswordEncryption;

public class SecureToken {
	public static interface SaltFactory { public String getSalt(SecureToken key); }
	
	public static String globalSecret="a3sikmnu5ds75vkk";

	public final String data;
	public final long timestamp;
	public final String signature;

	public SecureToken(SaltFactory factory, String data) {
		this.data=data;
		this.timestamp=System.currentTimeMillis();
		this.signature=calcSignature(data, timestamp, factory.getSalt(this));
	}
	
	private SecureToken(String data, long timestamp, String signature) {
		this.data=data;
		this.timestamp=timestamp;
		this.signature=signature;
	}

	public static SecureToken of(String token) {
		if (token==null)
			return null;
		String decode;
		try {
			decode = new String(Base64.decode(token),"UTF-8");
		}
		catch (IOException e) { throw new RuntimeException(e);   } // ignore invalid cookie
		
		String[] parts = decode.split("[:]");
		
		return new SecureToken(parts[0], Long.parseLong(parts[1]), parts[2]);
	}
	
	public String getToken() { return Base64.encodeBytes((data+":"+timestamp+":"+signature).getBytes()); }

	public boolean isValid(SaltFactory factory, int loginDuration) {
		if ((System.currentTimeMillis()-timestamp)/1000>loginDuration)
			return false;
		String expectedSignature = calcSignature(data, timestamp, factory.getSalt(this));
		return signature.equals(expectedSignature);
	}

	private static String calcSignature(String data, long timestamp, String salt) {
		 return PasswordEncryption.onewayEncrypt(data+timestamp+globalSecret, salt, 1);
	}
}
