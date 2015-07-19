package org.kisst.http4j;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.util.Base64;

public abstract class HttpUserPage extends HttpBasicPage {
	private static final String COOKIE_NAME = "Hooi4jUser";
	private static final int LOGIN_DURATION= 7*24*60*60; // a week in seconds
	
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

	public void clearCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_NAME, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);		
	}

	public void setCookie(HttpServletResponse response, String userid) {
		Cookie cookie = new Cookie(COOKIE_NAME, createCookieString(userid, System.currentTimeMillis()));
		cookie.setMaxAge(LOGIN_DURATION);
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
			if ((System.currentTimeMillis()-loginTime)>(LOGIN_DURATION*1000))
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
	public String ensureUserId(HttpServletRequest req, HttpServletResponse resp) {
		String id=getUserId(req);
		if (id==null)
			throw new HttpServer.UnauthorizedException("Not Autheticated user");
		return id;
	}
	public String ensureUserId(HttpServletRequest req, HttpServletResponse resp,String userId) {
		String id=ensureUserId(req,resp);
		if (id.equals(userId))
			return id;
		throw new HttpServer.UnauthorizedException("Not Authorized expected "+userId+" but got "+id);
	}
}
