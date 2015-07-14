package org.kisst.http4j;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.util.Base64;

public abstract class HttpUserPage extends HttpBasicPage {
	private static final String COOKIE_NAME = "Hooi4jUser";
	private static final long LOGIN_TIME = 7*24*60*60*1000; // a week
	
	public String getUserId(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies==null)
			return null;
		for (Cookie c: cookies) {
			if (COOKIE_NAME.equals(c.getName()))
				return decodeCookie(c.getValue());
		}
		return null;
	}

	public void setCookie(HttpServletResponse response, String userid) {
		Cookie cookie = new Cookie(COOKIE_NAME, createCookieString(userid, System.currentTimeMillis()));
		//System.out.println("setting "+userid+" to "+cookie);

		response.addCookie(cookie);		
	}

	
	
	private String decodeCookie(String cookie) {
			//System.out.println("got cookie:"+cookie);
			String decode;
			try {
				decode = new String(Base64.decode(cookie),"UTF-8");
			}
			catch (IOException e) {  return null; } // ignore invalid cookie
			//System.out.println("decoded to:"+decode);
			String[] parts = decode.split("[:]");
			String userid=parts[0];
			//System.out.println("user id:"+userid);
			long loginTime=Long.parseLong(parts[1]);
			//System.out.println("login :"+(System.currentTimeMillis()-loginTime));
			if ((System.currentTimeMillis()-loginTime)>LOGIN_TIME)
				return null; // Cookie too old
			String expectedCookie= createCookieString(userid,loginTime);
			//System.out.println("expected cookie:"+cookie);
			if (expectedCookie.equals(cookie))
				return userid;
			//System.out.println("expected cookie:"+cookie+" differs from "+cookie);
			return null; // invalid cookie
	}
	
	public static final String createCookieString(String userid, long time) {
		byte[] bytes = userid.getBytes();
		byte[] hash = Long.toHexString(time).getBytes();
		for (int i=0; i<bytes.length; i++)
			bytes[i] = (byte) (bytes[i] ^ hash[i%hash.length]);
		String signature=Base64.encodeBytes(bytes); // TODO: better signature
		return Base64.encodeBytes((userid+":"+time+":"+signature).getBytes());
	}
	
	public String ensureUserId(HttpServletRequest req, HttpServletResponse resp,String userId) {
		String id=getUserId(req);
		if (id==null) {
			sendUnauthorizedError(resp);
			throw new RuntimeException("Not Authenticated");
		}
		if (id.equals(userId))
			return id;
		sendUnauthorizedError(resp);
		throw new RuntimeException("Not Authorized");
	}

	public void sendUnauthorizedError(HttpServletResponse resp) {
		try {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "");
		} catch (IOException e) { throw new RuntimeException(e);}
	}
}
