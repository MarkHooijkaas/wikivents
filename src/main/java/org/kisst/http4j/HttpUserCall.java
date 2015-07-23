package org.kisst.http4j;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.http4j.HttpServer.HttpException;
import org.kisst.util.Base64;

public class HttpUserCall extends HttpCall{
	private static final String COOKIE_NAME = "Hooi4jUser";
	private static final int LOGIN_DURATION= 7*24*60*60; // a week in seconds

	//public final T user;
	public final String userid;
	
	public HttpUserCall(HttpServletRequest request,HttpServletResponse response) {
		super(request, response);
		this.userid=getUserId();
	}

	public HttpUserCall(HttpCall call) {
		super(call);
		this.userid=getUserId();
	}

	public void ensureUser() {
		if (userid==null)
			throw new UnauthorizedException("Not Authenticated user");
	}
	public void ensureUser(String userId) {
		if (userid== null || ! userid.equals(userId))
			throw new UnauthorizedException("Not Authorized expected "+userId+" but got "+userid);
	}
	
	public void clearCookie() {
		Cookie cookie = new Cookie(COOKIE_NAME, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);		
	}

	public void setCookie(String userid) {
		Cookie cookie = new Cookie(COOKIE_NAME, createCookieString(userid, System.currentTimeMillis()));
		cookie.setMaxAge(LOGIN_DURATION);
		response.addCookie(cookie);		
	}

	private String getUserId() {
		Cookie[] cookies = request.getCookies();
		if (cookies==null)
			return null;
		for (Cookie c: cookies) {
			if (COOKIE_NAME.equals(c.getName()))
				return decodeCookie(c.getValue());
		}
		return null;
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
	
	private static final String createCookieString(String userid, long time) {
		byte[] bytes = userid.getBytes();
		byte[] hash = Long.toHexString(time).getBytes();
		for (int i=0; i<bytes.length; i++)
			bytes[i] = (byte) (bytes[i] ^ hash[i%hash.length]);
		String signature=Base64.encodeBytes(bytes); // TODO: better signature
		return Base64.encodeBytes((userid+":"+time+":"+signature).getBytes());
	}
	public static class UnauthorizedException extends HttpException {
		private static final long serialVersionUID = 1L;
		public UnauthorizedException(String msg) {super(HttpServletResponse.SC_UNAUTHORIZED, msg); } 
	}
}
