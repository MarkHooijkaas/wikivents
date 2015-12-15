package org.kisst.http4j;

import java.io.IOException;

import javax.servlet.http.Cookie;

import org.kisst.util.Base64;
import org.kisst.util.PasswordEncryption;

public class SecureCookie{
	public static String globalSecret="a3sikmnu5ds75vkk";

	public final String data;
	public final long timestamp;
	public final String signature;

	private SecureCookie(String data, long timestamp, String signature) {
		this.data=data;
		this.timestamp=timestamp;
		this.signature=signature;
	}
	
	public static void set(HttpCall call, String cookieName, String data, int cookieAge, String salt) {
		long timestamp=System.currentTimeMillis();
		String signature=calcSignature(data, timestamp, salt);
		Cookie cookie = new Cookie(cookieName, Base64.encodeBytes((data+":"+timestamp+":"+signature).getBytes()));
		cookie.setMaxAge(cookieAge);
		//cookie.setSecure(true); // TODO: does not work with http: development
		cookie.setPath("/");
		call.response.addCookie(cookie);
	}
	
	public static void clear(HttpCall call, String cookieName) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		call.response.addCookie(cookie);		
	}
	
	public static SecureCookie of(HttpCall call, String cookieName) {
		Cookie cookie=call.getNamedCookie(cookieName);
		if (cookie==null)
			return null;

		String decode;
		try {
			decode = new String(Base64.decode(cookie.getValue()),"UTF-8");
		}
		catch (IOException e) {  return null; } // ignore invalid cookie
		//System.out.println("decoded to:"+decode);
		
		
		String[] parts = decode.split("[:]");
		if (parts.length!=3)
			return null;
		
		return new SecureCookie(parts[0], Long.parseLong(parts[1]), parts[2]);
	}
	
	public boolean isValid(int loginDuration, String salt) {
		if ((System.currentTimeMillis()-timestamp)>(loginDuration*1000))
			return false;
		String expectedSignature = calcSignature(data, timestamp, salt);
		return signature.equals(expectedSignature);
	}

	private static String calcSignature(String data, long timestamp, String salt) {
		 return PasswordEncryption.onewayEncrypt(data+timestamp+globalSecret, salt, 1);
	}
}
